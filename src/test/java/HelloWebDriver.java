import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class HelloWebDriver {

    private WebDriver driver;
    private static By searchInputBy = By.id("q");
    private static By searchResultsBy = By.xpath("//div[contains(@class,'gsc-webResult') and contains(.,'selenium') and contains(.,'java')]");

    @BeforeMethod (alwaysRun = true)
    public void browserSetup(){
        driver = new ChromeDriver();
    }

    @AfterMethod (alwaysRun = true)
    public void browserTearDown(){
        driver.quit();
        driver=null;
    }

    @Test (description = "Some description")
    public void commonSearchTermResultsNotEmpty() {

        // зависит от стратегии, по умолчанию notrmal
        //driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        // для выполнения скриптов executeAsyncScript()
        //driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        //driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        driver.get("http://seleniumhq.org");
        new WebDriverWait(driver,10).until(CustomConditions.jQueryAJAXsCompleted());

        WebElement searchInput = waitForElementLoacatedBy(driver,searchInputBy);
        searchInput.sendKeys("selenium java");

        WebElement searchBtn = driver.findElement(By.xpath("//*[@id=\"submit\"]"));
        searchBtn.click();

//        new WebDriverWait(driver,10)
//                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(searchResultsBy));

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timeout for waiting search result list was exceeded!");

        List<WebElement> searchResults = wait.until(new Function<WebDriver, List<WebElement>>() {
            public List<WebElement> apply(WebDriver webDriver) {
                return driver.findElements(searchResultsBy);
            }
        });

        //List<WebElement> searchResults = driver.findElements(searchResultsBy);
        //System.out.println("Search results number for requested term: "+searchResults.size());

        Assert.assertTrue(searchResults.size()>0,"search results are empty");

//        driver = new EdgeDriver();
//        driver.get("http://seleniumhq.org");
//        Thread.sleep(2000);
//        driver.quit();
//
//        driver = new FirefoxDriver();
//        driver.get("http://seleniumhq.org");
//        Thread.sleep(2000);
//        driver.quit();

        ////*[@data-src-mp3]/..[@class='EXAMPLE']


    }

    private static WebElement waitForElementLoacatedBy(WebDriver driver, By by) {
        return new WebDriverWait(driver,10)
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }
}
