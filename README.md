# amex assessment
# Amex UI Testing Framework

This project is a UI testing framework for testing the American Express credit card application flow.

## Project Structure

```
src/main/java
  - com.amex.ui.framework
    - config
      - ConfigReader.java
      - DriverManager.java
    - pages
      - BasePage.java
      - HomePage.java
      - AllCardsPage.java
      - GoldCardPage.java
      - ApplicationFormPage.java
    - utils
      - TestData.java
      - WaitUtils.java
      - ScreenshotUtils.java
src/test/java
  - com.amex.ui.tests
    - BaseTest.java
    - CardApplicationFlowTest.java
src/main/resources
  - config.properties
```

## Features

- Page Object Model design pattern
- Cross-browser testing support (Chrome, Firefox, Edge, Safari)
- Screenshot capture on test failure
- Random test data generation
- Fluent API design for test readability
- Configurable waiting strategies
- Support for headless mode

## Prerequisites

- Java 11 or higher
- Maven
- Chrome, Firefox, or Edge browser

## Running the Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=CardApplicationFlowTest

# Run with specific browser
mvn clean test -Dbrowser=firefox
```

## Configuration

Edit `src/main/resources/config.properties` to modify:
- Browser selection
- Headless mode
- Timeouts
- Base URL
