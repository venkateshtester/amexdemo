package pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GoldCardPage extends BasePage{
    @FindBy(xpath = "//div[@class='sc_paddingTop_20 sc_paddingBottom_20']//a[@class='sc_at_button_btn sc_at_button_isMinMax sc_textBody_3 sc_horizontallyFluid'][normalize-space()='Demandez votre Carte']")
    private WebElement demandezVotreCarteButton;

    public GoldCardPage() {
        super();
        waitForPageLoad();
    }

    public ApplicationFormPage clickOnDemandezVotreCarte() {
        waitUtils.waitForElementToBeClickable(demandezVotreCarteButton);
        scrollToElement(demandezVotreCarteButton);
        click(demandezVotreCarteButton);
        return new ApplicationFormPage();
    }

    public boolean isPageLoaded() {
        return driver.getCurrentUrl().contains("Demandez votre Carte") &&
                isElementDisplayed(demandezVotreCarteButton);
    }
}
