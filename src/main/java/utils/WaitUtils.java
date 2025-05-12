package utils;
import com.amex.ui.framework.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WaitUtils {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FluentWait<WebDriver> fluentWait;


    private static final int DEFAULT_TIMEOUT = 30;
    private static final int DEFAULT_POLLING_INTERVAL = 500; // ms

    /**
     * Constructor with configurable timeout
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.getProperty("explicitWait", String.valueOf(DEFAULT_TIMEOUT)));

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        // FluentWait configuration with polling interval and ignored exceptions
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(DEFAULT_POLLING_INTERVAL))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);


    }

    /**
     * Wait for element to be visible
     */
    public void waitForElementVisibility(WebElement element) {

        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be visible with custom timeout
     */
    public void waitForElementVisibility(WebElement element, int timeoutInSeconds) {

        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for all elements to be visible
     */
    public void waitForElementsVisibility(List<WebElement> elements) {

        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Wait for element to be clickable
     */
    public void waitForElementToBeClickable(WebElement element) {

        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be clickable with custom timeout
     */
    public void waitForElementToBeClickable(WebElement element, int timeoutInSeconds) {

        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for page to be fully loaded
     */
    public void waitForPageLoad() {


        // First wait for document.readyState to be complete
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));

        // Then wait for jQuery to be inactive if present
        try {
            wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript(
                        "return (typeof jQuery === 'undefined' || jQuery.active === 0)");
            });
        } catch (Exception e) {
            // jQuery might not be present on the page, which is fine

        }

        // Finally wait for any AJAX calls to complete
        try {
            wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript(
                        "return (window.XMLHttpRequest.DONE === 4 || " +
                                "Array.prototype.slice.call(window.performance.getEntriesByType('resource')" +
                                ".filter(n => n.initiatorType === 'xmlhttprequest'))" +
                                ".every(({duration}) => duration > 0))");
            });
        } catch (Exception e) {
            // This advanced check might fail in some browsers

        }
    }

    /**
     * Wait for URL to contain specific text
     */
    public void waitForUrlContains(String urlPart) {

        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Wait for URL to match a pattern
     */
    public void waitForUrlMatches(String regex) {

        wait.until(ExpectedConditions.urlMatches(regex));
    }

    /**
     * Wait for JavaScript condition to be true
     */
    public void waitForJsCondition(String jsCondition) {

        wait.until(driver -> (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return " + jsCondition));
    }

    /**
     * Wait for element to disappear/become invisible
     */
    public void waitForElementInvisibility(WebElement element) {

        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Wait for element to disappear by locator
     */
    public void waitForElementInvisibility(By locator) {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for element attribute to contain a value
     */
    public void waitForElementAttributeContains(WebElement element, String attribute, String value) {

        wait.until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    /**
     * Wait for text to be present in element
     */
    public void waitForTextPresent(WebElement element, String text) {

        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Wait for number of elements to be a specific count
     */
    public void waitForNumberOfElements(By locator, int count) {

        wait.until(ExpectedConditions.numberOfElementsToBe(locator, count));
    }

    /**
     * Wait with custom condition using fluent wait
     */
    public <T> T waitWithCustomCondition(Function<WebDriver, T> condition) {

        return fluentWait.until(condition);
    }

    /**
     * Sleep method - use sparingly, prefer explicit waits
     */
    public void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        }
    }

    /**
     * Wait for frame to be available and switch to it
     */
    public void waitAndSwitchToFrame(WebElement frameElement) {

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    /**
     * Wait for an alert to be present
     */
    public void waitForAlertPresent() {

        wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Wait for element to have a specific CSS class
     */
    public void waitForElementHasClass(WebElement element, String cssClass) {

        wait.until(driver -> element.getAttribute("class").contains(cssClass));
    }

    /**
     * Wait for any AJAX calls to complete
     */
    public void waitForAjaxComplete() {


        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            try {
                return ((Long) ((JavascriptExecutor) driver)
                        .executeScript("return jQuery.active") == 0);
            } catch (Exception e) {
                return true;
            }
        };

        ExpectedCondition<Boolean> jsLoad = driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .toString().equals("complete");

        wait.until(jQueryLoad);
        wait.until(jsLoad);
    }
}
