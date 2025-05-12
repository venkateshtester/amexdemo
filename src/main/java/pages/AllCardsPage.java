package pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AllCardsPage extends BasePage{
    @FindBy(xpath = "//a[@href='carte-de-paiement/carte-platinum-americanexpress/?linknav=fr-amex-cardshop-allcards-learn-CartePlatinumAmericanExpress-fc']")
    private WebElement enSavoirPlusGoldCard;

    public AllCardsPage() {
        super();
        waitForPageLoad();
    }

    public GoldCardPage clickOnEnSavoirPlusGoldCard() {
        waitUtils.waitForElementToBeClickable(enSavoirPlusGoldCard);
        scrollToElement(enSavoirPlusGoldCard);
        click(enSavoirPlusGoldCard);
        return new GoldCardPage();
    }

    public boolean isPageLoaded() {
        return driver.getCurrentUrl().contains("En savoir plus") &&
                isElementDisplayed(enSavoirPlusGoldCard);
    }
}
