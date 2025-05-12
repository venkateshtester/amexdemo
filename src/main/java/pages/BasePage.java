package pages;
import com.amex.ui.framework.config.ConfigReader;
import com.amex.ui.framework.config.DriverManager;
import com.amex.ui.framework.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.testng.Assert;
import utils.ScreenshotUtils;
import utils.WaitUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * BasePage class serves as foundation for all Page Objects.
 * Contains common methods and utilities for page interactions.
 */

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WaitUtils waitUtils;
    protected Actions actions;


    // Retry mechanism configuration
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 500;

    // Cookie handling configuration
    private static final String[] COOKIE_ACCEPT_SELECTORS = {
            "//button[contains(text(), 'Accepter')]",
            "//button[contains(text(), 'Accept')]",
            "//button[contains(@id, 'cookie-accept')]",
            "//button[contains(@class, 'cookie-consent')]",
            "//button[contains(@class, 'accept-cookies')]",
            "//div[contains(@id, 'consent')]//button[contains(@id, 'accept')]",
            "//div[contains(@class, 'cookie')]//button[contains(@class, 'accept')]"
    };

    /**
     * Constructor initializes driver, waits and page factory
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        int timeout = Integer.parseInt(ConfigReader.getProperty("explicitWait", "30"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);

        // Use AjaxElementLocatorFactory for better handling of dynamic elements
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, timeout), this);

        // Log page initialization

    }

    /**
     * Clicks on an element with retry logic
     * @param element WebElement to click
     * @param elementName Name for logging
     */
    protected void click(WebElement element, String elementName) {
        waitUtils.waitForElementToBeClickable(element);
        try {

            element.click();
        } catch (Exception e) {
            // Retry with JS click if standard click fails

            clickWithJS(element, elementName);
        }
    }

    /**
     * Overloaded click method without element name
     */
    protected void click(WebElement element) {
        click(element, "unnamed element");
    }

    /**
     * Clicks on element using JavaScript
     * @param element WebElement to click
     * @param elementName Name for logging
     */
    protected void clickWithJS(WebElement element, String elementName) {
        waitUtils.waitForElementVisibility(element);

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }





    /**
     * Checks if element is displayed with error handling
     * @param element WebElement to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Waits for page to load completely
     */
    protected void waitForPageLoad() {

        waitUtils.waitForPageLoad();
    }

    /**
     * Scrolls to element
     * @param element WebElement to scroll to
     * @param elementName Name for logging
     */
    protected void scrollToElement(WebElement element, String elementName) {

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        try {
            Thread.sleep(500); // Small wait after scroll
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Overloaded scrollToElement method without element name
     */
    protected void scrollToElement(WebElement element) {
        scrollToElement(element, "unnamed element");
    }

    /**
     * Attempts to accept cookies if consent popup is present
     */
    protected void acceptCookiesIfPresent() {

        try {
            // Reduce implicit wait temporarily for faster checking
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

            for (String selector : COOKIE_ACCEPT_SELECTORS) {
                try {
                    List<WebElement> cookieButtons = driver.findElements(By.xpath(selector));
                    for (WebElement cookieButton : cookieButtons) {
                        if (cookieButton.isDisplayed()) {

                            clickWithJS(cookieButton, "Cookie accept button");
                            waitUtils.sleep(1000); // Wait for cookie banner to disappear
                            return;
                        }
                    }
                } catch (Exception ignore) {
                    // Continue to next selector if not found
                }
            }
        } finally {
            // Reset implicit wait to original value
            int implicitWait = Integer.parseInt(ConfigReader.getProperty("implicitWait", "10"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        }
    }

    /**
     * Hover over an element
     * @param element WebElement to hover over
     * @param elementName Name for logging
     */
    protected void hoverOverElement(WebElement element, String elementName) {
        waitUtils.waitForElementVisibility(element);

        actions.moveToElement(element).perform();
    }

    /**
     * Validates element text equals expected text
     * @param element WebElement to validate
     * @param expectedText Expected text
     * @param elementName Name for logging
     */
    protected void validateElementText(WebElement element, String expectedText, String elementName) {
        waitUtils.waitForElementVisibility(element);
        String actualText = element.getText().trim();

        Assert.assertEquals(actualText, expectedText, "Text validation failed for " + elementName);
    }

    /**
     * Retries an operation with exponential backoff
     * @param operation Function to retry
     * @param operationName Name for logging
     * @return Result of operation
     */
    protected <T> T retryingOperation(Function<Void, T> operation, String operationName) {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRIES) {
            try {
                return operation.apply(null);
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException e) {
                lastException = e;
                retryCount++;
                if (retryCount < MAX_RETRIES) {

                    try {
                        // Exponential backoff
                        Thread.sleep(RETRY_DELAY_MS * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // If we've exhausted retries, log and rethrow

        if (lastException instanceof RuntimeException) {
            throw (RuntimeException) lastException;
        } else {
            throw new RuntimeException("Failed operation: " + operationName, lastException);
        }
    }

    /**
     * Gets page title
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Gets current URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Takes a screenshot and returns the file path
     * @return Path to screenshot file
     */
    public String takeScreenshot() {
        return ScreenshotUtils.takeScreenshot(
                this.getClass().getSimpleName() + "_" + System.currentTimeMillis());
    }

    /**
     * Abstract method that checks if page is loaded correctly
     * Every page object must implement this to verify its unique elements
     * @return true if page is loaded correctly, false otherwise
     */
    public abstract boolean isPageLoaded();
}
