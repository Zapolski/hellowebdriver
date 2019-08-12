import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnzheroScrab {

    private static WebDriver driver;
    private static int WAIT_TIMEOUT_SECONDS = 10;

    //private static By xpathLinksToViewDocuments = By.xpath("//a[@class='cTitleLinkN']");
    //private static By xpathLinkForDownload = By.xpath("//a[contains(text(),'качать')]");

    private static By xpathLinksToViewDocuments = By.xpath("//a[contains(text(),'б утв')]");
    private static By xpathLinkForDownload = By.xpath("//a[contains(text(),'б утв')]");

    //private static String mainUrl = "https://www.anzhero.ru/pages/sport/viewpolog.asp?Id=199";
    //private static String mainUrl = "https://www.anzhero.ru/pages/sport/viewspart.asp?Id=199";
    private static String mainUrl = "https://www.anzhero.ru/pages/culture/viewdocs.asp?Id=193";

    public static void main(String[] args) throws IOException {

        driver = new ChromeDriver();

        driver.get(mainUrl);
        new WebDriverWait(driver, 10).until(CustomConditions.jQueryAJAXsCompleted());

        List<WebElement> documentList = waitForAllElementsLoacatedBy(xpathLinksToViewDocuments);

        List<String> documentLinksList = new ArrayList<>();
        for (WebElement element: documentList){
            documentLinksList.add(element.getAttribute("href"));
        }

        int index = 29;
        //for (String link: documentLinksList){
        for (int i=index; i<=documentLinksList.size();i++){
            try {
                driver.get(documentLinksList.get(i-1));
                WebElement pageWithPdfLink = waitForElementLoacatedBy(xpathLinkForDownload);
                String fileLink = pageWithPdfLink.getAttribute("href");

                String fileName = "./documents/provision_"+String.format("%04d",i)+fileLink.substring(fileLink.lastIndexOf("."));

                System.out.println(String.format("%04d %s %s",i,fileLink,fileName));
                downloadUsingStream(fileLink,fileName);
            }catch (NoSuchElementException|TimeoutException|StringIndexOutOfBoundsException e){
                System.out.println("-----> don't file for page: "+documentLinksList.get(i-1)+"\n"+e.getMessage());
            }
        }
    }

    private static List<WebElement> waitForAllElementsLoacatedBy(By by) {
        return new WebDriverWait(driver,WAIT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    private static WebElement waitForElementLoacatedBy(By by) {
        return new WebDriverWait(driver,WAIT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private static void downloadUsingStream(String urlStr, String file){
        try {
            URL url = new URL(urlStr);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count=0;
            while((count = bis.read(buffer,0,1024)) != -1){
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
