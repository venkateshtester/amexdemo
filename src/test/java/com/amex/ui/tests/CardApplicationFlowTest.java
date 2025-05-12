package com.amex.ui.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AllCardsPage;
import pages.ApplicationFormPage;
import pages.GoldCardPage;
import pages.HomePage;

public class CardApplicationFlowTest extends BaseTest{
    @Test(description = "Verify the entire American Express Gold Card application flow")
    public void testGoldCardApplicationFlow() {
        // Step 1: Navigate to FR Homepage
        HomePage homePage = new HomePage();
        homePage.open();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page is not loaded correctly");

        // Step 2: Click on "Cartes American Express" and navigate to All Cards page
        AllCardsPage allCardsPage = homePage.clickOnCartesAmexLink();
        Assert.assertTrue(allCardsPage.isPageLoaded(), "All Cards page is not loaded correctly");

        // Step 3: Click on "En Savoir Plus" under Gold Card and navigate to Gold Card page
        GoldCardPage goldCardPage = allCardsPage.clickOnEnSavoirPlusGoldCard();
        Assert.assertTrue(goldCardPage.isPageLoaded(), "Gold Card page is not loaded correctly");

        // Step 4: Click on "Demandez Votre Carte" and navigate to Application Form page
        ApplicationFormPage applicationFormPage = goldCardPage.clickOnDemandezVotreCarte();
        Assert.assertTrue(applicationFormPage.isPageLoaded(), "Application Form page is not loaded correctly");

        // Step 5: Fill the form with junk data
        applicationFormPage.fillForm();

        // Step 6: Click on "Sauvegarder et Continuer" and verify validation errors
        applicationFormPage.clickSubmitButton();

        // Verify that validation errors are displayed
        Assert.assertTrue(applicationFormPage.hasValidationErrors(), "No validation errors are displayed");
        System.out.println("Number of validation errors: " + applicationFormPage.getValidationErrorCount());
        System.out.println("Validation error messages: " + applicationFormPage.getValidationErrorMessages());
    }

    @Test(description = "Verify UI elements on the Application Form page")
    public void testApplicationFormUIElements() {
        // Navigate directly to the application form
        HomePage homePage = new HomePage();
        homePage.open();

        AllCardsPage allCardsPage = homePage.clickOnCartesAmexLink();
        GoldCardPage goldCardPage = allCardsPage.clickOnEnSavoirPlusGoldCard();
        ApplicationFormPage applicationFormPage = goldCardPage.clickOnDemandezVotreCarte();

        // Verify page is loaded
        Assert.assertTrue(applicationFormPage.isPageLoaded(), "Application Form page is not loaded correctly");

        // Verify UI validation by submitting empty form
        applicationFormPage.clickSubmitButton();

        // Verify that validation errors are displayed
        Assert.assertTrue(applicationFormPage.hasValidationErrors(), "No validation errors are displayed for empty form");
    }
}
