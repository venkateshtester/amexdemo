package pages;
import utils.TestData;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ApplicationFormPage extends BasePage{
    // Form fields - These XPaths need to be updated based on actual form structure
    @FindBy(xpath = "//input[@id='fieldControl-input-firstName']")
    private WebElement firstNameField;

    @FindBy(xpath = "//input[@id='fieldControl-input-lastName']")
    private WebElement lastNameField;

    @FindBy(xpath = "//input[@id='fieldControl-input-email']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@id='fieldControl-input-mobilePhoneNumber']")
    private WebElement phoneField;

    @FindBy(xpath = "//input[@id='fieldControl-input-dateOfBirth']")
    private WebElement date;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submitButton;

    // Validation error elements
    @FindBy(xpath = "//div[contains(@class, 'error') or contains(@class, 'validation')]")
    private List<WebElement> validationErrors;

    public ApplicationFormPage() {
        super();
        waitForPageLoad();
    }


    public ApplicationFormPage fillForm() {
        // Simply use sendKeys directly for each field
        firstNameField.sendKeys("Jean");
        lastNameField.sendKeys("Dupont");
        date.sendKeys("29/02/1996");
        emailField.sendKeys("jean.dupont@example.com");
        phoneField.sendKeys("0612345678");




        return this;
    }


    public ApplicationFormPage clickSubmitButton() {
        scrollToElement(submitButton);
        click(submitButton);
        return this;
    }

    public boolean isPageLoaded() {
        return driver.getCurrentUrl().contains("apply") &&
                isElementDisplayed(firstNameField);
    }

    public boolean hasValidationErrors() {
        try {
            waitUtils.waitForElementsVisibility(validationErrors);
            return !validationErrors.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getValidationErrorCount() {
        return validationErrors.size();
    }

    public List<String> getValidationErrorMessages() {
        return validationErrors.stream()
                .map(WebElement::getText)
                .collect(java.util.stream.Collectors.toList());
    }
}
