package by.zapolski.capture;

import by.zapolski.database.dao.ConnectorDB;
import by.zapolski.database.dao.RecordDao;
import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Record;
import by.zapolski.database.model.Word;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DefinitionGetter {
    private static WebDriver driver;

    private static By xpathExamples = By.xpath("//*[ (./*[@data-src-mp3]) and contains(@class,'EXAMPLE')]");
    private static By xpathMp3Link = By.xpath("./*[@data-src-mp3]");
    private static By xpathDefinition = By.xpath("./ancestor::span[@class='Sense']//span[@class='DEF']");

    private static final String DIR_WITH_SOUNDS = "d:/Test/EnglishPhrases/words/";

    private static final int WAIT_TIMEOUT_SECONDS = 2;
    private static final int MAX_RANK = 100_000;

    public static void main(String[] args) {

        ConnectorDB connectorDB = new ConnectorDB();
        WordDao wordDao = new WordDao(connectorDB);
        RecordDao recordDao = new RecordDao(connectorDB);

        List<String> words = Arrays.asList("float");

//        List<String> words = wordDao.getAll().stream()
//                .map(Word::getValue)
//                .collect(Collectors.toList());

        try {
            driver = new ChromeDriver();
            for (String word1 : words) {
                String word = word1.toLowerCase();

                Map<Long, String> map = new HashMap<>();

                //если фразовый глагол, то через черточку в адресной строке
                driver.get("https://www.ldoceonline.com/dictionary/" + word.replaceAll(" ", "-"));
                new WebDriverWait(driver, 10).until(CustomConditions.jQueryAJAXsCompleted());
                try {
                    List<WebElement> examplesList = waitForAllElementsLocatedBy(xpathExamples);
                    for (WebElement element : examplesList) {

                        WebElement mp3Link = element.findElement(xpathMp3Link);
                        String mp3Url = mp3Link.getAttribute("data-src-mp3");
                        log.info("URL: {}", mp3Url);

                        Long fileSize = getFileSizeInBytes(mp3Url);
                        log.info("FILE SIZE: {}", fileSize);

                        String definition = "";
                        try {
                            WebElement definitionElement = element.findElement(xpathDefinition);
                            definition = definitionElement.getText();
                        } catch (NoSuchElementException e) {
                            log.error("Can not find definition for example: {}", mp3Link);
                        }
                        log.info("DEFINITION: {}", definition);

                        map.put(fileSize, definition);
                    }
                    log.info("Found [{}] record(s)", examplesList.size());

                    List<Record> records = recordDao.getRecordsByWord(word, 0, MAX_RANK);
                    for (Record record : records) {
                        String fileName = DIR_WITH_SOUNDS + record.getWord() + "/" + record.getSoundPath();
                        log.info("FILE NAME: {}", fileName);
                        Long fileSize = new File(fileName).length();
                        log.info("FILE SIZE: {}", fileSize);
                        String definition = map.get(fileSize);
                        if (definition != null) {
                            record.setRule(definition);
                            //recordDao.update(record);
                            log.info("The definition was updated for phrase: {}", record.getEnglish());
                        }
                    }
                } catch (TimeoutException | NoSuchElementException e) {
                    log.error("Longman Dictionary failed with examples for word [{}]", word, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectorDB.close();
            driver.quit();
        }
    }

    private static List<WebElement> waitForAllElementsLocatedBy(By by) {
        return new WebDriverWait(driver, WAIT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    private static long getFileSizeInBytes(String path) {
        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            return urlConnection.getContentLength();
        } catch (IOException e) {
            log.error("Can not get file size for URL [{}]", path, e);
        }
        return -1;
    }

}
