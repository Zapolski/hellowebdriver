package by.zapolski.capture;

import by.zapolski.capture.model.Translate;
import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.WordDao;
import com.google.gson.Gson;
import javazoom.jl.player.Player;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LongmanParseWithDbCheck {

    private static WebDriver driver;
    private static final int WAIT_TIMEOUT_SECONDS = 10;

    private static By xpathExamples = By.xpath("//*[ (./*[@data-src-mp3]) and contains(@class,'EXAMPLE')]");
    private static By xpathMp3Link = By.xpath("./*[@data-src-mp3]");

    private static final String DATABASE_FILE = "d:\\Test\\EnglishPhrases\\info\\new\\newWords.xls";
    private static final String LIST_WORDS = "d:\\Test\\EnglishPhrases\\info\\new\\newWords.txt";
    private static final String DIR_WITH_SOUNDS = "d:/Test/EnglishPhrases/words/";

    public static void main(String[] args) throws IOException {

        driver = new ChromeDriver();
        File file = new File(DATABASE_FILE);
        FileInputStream inputStream = new FileInputStream(file);

        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum() + 1;
        Cell cell;
        Row row;

        ConnectorDB connectorDB = new ConnectorDB();
        WordDao wordDao = new WordDao(connectorDB);

        List<String> words = Files.readAllLines(Paths.get(LIST_WORDS));
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).toLowerCase();
            if (wordDao.getByValue(word) != null) {
                System.out.println("Word [" + word + "] has already existed in DB. Skipped.");
                continue;
            }
            if (word.startsWith("+")) {
                continue;
            }

            String fileDir = DIR_WITH_SOUNDS + word + "/";
            if (!new File(fileDir).exists()) {
                new File(fileDir).mkdir();
                System.out.printf("%04d --------> %s%n", i, word);
            } else {
                System.out.printf("%04d --------> %s (directory has already existed). Skipped.%n", i, word);
                continue;
            }

            //если фразовый глагол, то через черточку в адресной строке
            driver.get("https://www.ldoceonline.com/dictionary/" + word.replace(" ", "-"));
            new WebDriverWait(driver, 10).until(CustomConditions.jQueryAJAXsCompleted());

            try {
                List<WebElement> examplesList = waitForAllElementsLocatedBy(xpathExamples);
                int index = 1;
                for (WebElement element : examplesList) {
                    String englishString = element.getText().trim();
                    WebElement mp3Link = element.findElement(xpathMp3Link);
                    String russianString = getRussianSentence(englishString);
                    String mp3Url = mp3Link.getAttribute("data-src-mp3");
                    String fileName = word + "-" + String.format("%04d.mp3", index++);
                    downloadUsingStream(mp3Url, fileDir + fileName);

                    row = sheet.createRow(rowNum++);
                    cell = row.createCell(0);
                    cell.setCellValue(word);
                    cell = row.createCell(1);
                    cell.setCellValue(russianString);
                    cell = row.createCell(2);
                    cell.setCellValue(englishString);
                    cell = row.createCell(3);
                    cell.setCellValue(fileName);
                }

            } catch (TimeoutException | NoSuchElementException e) {
                System.out.println("---> Longman fail for word: " + word);
            }

        }
        connectorDB.close();

        // Write File
        File tempFile = new File(DATABASE_FILE);
        FileOutputStream out = new FileOutputStream(tempFile);
        workbook.write(out);
        out.flush();
        out.close();
        driver.quit();
        workbook.close();
    }

    private static String getRussianSentence(String englishString) {
        String result = "";
        try {
            String translatedJson = getTranslate(englishString);
            Gson g = new Gson();
            Translate translate = g.fromJson(translatedJson, Translate.class);
            result = translate.text.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
    *   https://translate.yandex.net/api/v1.5/tr.json/translate
    *   ? [key=<API-ключ>]
    *   & [text=<переводимый текст>]
    *   & [lang=<направление перевода>]
    *   & [format=<формат текста>]
    *   & [options=<опции перевода>]
    *   & [callback=<имя callback-функции>]
    *   trnsl.1.1.20190711T105344Z.5d3d73ddd6ee8245.446444e44948bcf43a6909469fa9bfb94b8c7e11
    **/
    private static String getTranslate(String sourceText) throws IOException {

        String key = "trnsl.1.1.20190711T105344Z.5d3d73ddd6ee8245.446444e44948bcf43a6909469fa9bfb94b8c7e11";
        String text = sourceText.trim().replace(" ", "%20");
        String lang = "en-ru";

        String url = String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s", key, text, lang);
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static List<WebElement> waitForAllElementsLocatedBy(By by) {
        return new WebDriverWait(driver, WAIT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    private static WebElement waitForElementLocatedBy(By by) {
        return new WebDriverWait(driver, WAIT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private static void sound(String url) {
        try {
            InputStream fis = new URL(url).openStream();
            Player playMP3 = new Player(fis);
            playMP3.play();
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.println("Failed to play the file.");
        }
    }

    private static void downloadUsingStream(String urlStr, String file) {
        try {
            URL url = new URL(urlStr);
            try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
                 FileOutputStream fis = new FileOutputStream(file);) {
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = bis.read(buffer, 0, 1024)) != -1) {
                    fis.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
