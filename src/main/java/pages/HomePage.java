package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class HomePage extends BasePage{
    private static final String PAGE_URL = "https://www.americanexpress.com/fr-fr/?inav=NavLogo";

    @FindBy(xpath = "//a[contains(text(), 'Cartes Particuliers') or contains(@aria-label, 'Cartes Particuliers')]")
    private WebElement cartesAmexLink;

    public HomePage() {
        super();
    }

    public HomePage open() {
        driver.get(PAGE_URL);
        waitForPageLoad();
        acceptCookiesIfPresent();
        return this;
    }

    public AllCardsPage clickOnCartesAmexLink() {
        waitUtils.waitForElementVisibility(cartesAmexLink);
        click(cartesAmexLink);
        return new AllCardsPage();
    }

    public boolean isPageLoaded() {
        return driver.getCurrentUrl().contains("Cartes Particuliers") &&
                isElementDisplayed(cartesAmexLink);
    }
}
