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

public class VeidadmScrab {

    private static WebDriver driver;
    private static int WAIT_TIMEOUT_SECONDS = 10;


    //private static By xpathTypeDisposalCheckbox = By.xpath("//*[@id=\"fl-rubric-9\"]/li[2]/div/label"); //распоряжения
    private static By xpathTypeDisposalCheckbox = By.xpath("//*[@id=\"fl-rubric-9\"]/li[3]/div/label"); //решения

    private static By xpathLinkForDownload = By.xpath("//*[@class='link-block hidden-xs-down']//a");

    private static By xpathPageLink = By.xpath("//a[@class='page-link paginate']");

    private static By xpathNextPageLink = By.xpath("//a[contains(text(),'Следующая')]");

    private static String mainUrl = "http://veidadm.ru/dokumenty/vse-dokumenty/";

    public static void main(String[] args) throws IOException {

        driver = new ChromeDriver();

        driver.get(mainUrl);
        new WebDriverWait(driver, 10).until(CustomConditions.jQueryAJAXsCompleted());

        waitForElementLoacatedBy(xpathTypeDisposalCheckbox).click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        int index = 1;

        int countPages = waitForAllElementsLoacatedBy(xpathPageLink).size();

        for (int i=0; i<countPages;i++){  // last link doesn't fit

            System.out.println("-------------> process page: "+(i+1)+"; all pages: "+countPages);

            List<WebElement> documentList = waitForAllElementsLoacatedBy(xpathLinkForDownload);
            List<String> documentLinksList = new ArrayList<>();
            for (WebElement element: documentList){
                documentLinksList.add(element.getAttribute("href"));
            }

            for (int j=1; j<=documentLinksList.size();j++){
                try {
                    String fileLink = documentLinksList.get(j-1);

                    String fileName = "./documents/decision_"+String.format("%04d",index++)+fileLink.substring(fileLink.lastIndexOf("."));

                    System.out.println(String.format("%04d %s %s",j,fileLink,fileName));
                    downloadUsingStream(fileLink,fileName);
                }catch (NoSuchElementException|TimeoutException|StringIndexOutOfBoundsException e){
                    System.out.println("-----> don't file for page: "+documentLinksList.get(j-1)+"\n"+e.getMessage());
                }
            }

            if (!waitForElementLoacatedBy(xpathNextPageLink).isEnabled()){
                waitForElementLoacatedBy(xpathNextPageLink).click();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
