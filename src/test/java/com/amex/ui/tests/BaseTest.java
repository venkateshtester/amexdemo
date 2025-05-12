package com.amex.ui.tests;

import com.amex.ui.framework.config.ConfigReader;
import com.amex.ui.framework.config.DriverManager;


import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ScreenshotUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Base test class with robust setup/teardown and test reporting
 */


public class BaseTest {


        // Simple logging to file
        private static final String LOG_FILE = "target/test-execution.log";
        private static FileWriter logWriter;

        static {
            try {
                // Create directory if it doesn't exist
                File logDir = new File("target");
                if (!logDir.exists()) {
                    logDir.mkdirs();
                }

                // Initialize log writer
                logWriter = new FileWriter(LOG_FILE, true);
            } catch (IOException e) {
                System.err.println("Failed to initialize log file: " + e.getMessage());
            }
        }

        /**
         * Write message to log file
         */
        protected void log(String message) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logMessage = timestamp + " - " + message + "\n";

            try {
                // Write to file
                if (logWriter != null) {
                    logWriter.write(logMessage);
                    logWriter.flush();
                }

                // Also print to console
                System.out.println(logMessage);
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }

        /**
         * Setup executed before test suite
         *
         * @param ctx TestNG context
         */
        @BeforeSuite(alwaysRun = true)
        public void setupSuite(ITestContext ctx) {
            // Load configuration
            ConfigReader.loadConfig();

            // Log suite start with environment info
            log("==================================================");
            log("TEST SUITE STARTED: " + ctx.getSuite().getName());
            log("Environment: " + ConfigReader.getProperty("environment", "N/A"));
            log("Browser: " + ConfigReader.getProperty("browser", "chrome"));
            log("Headless Mode: " + ConfigReader.getProperty("headless", "false"));
            log("==================================================");
        }

        /**
         * Setup executed before each test method
         *
         * @param method  Test method
         * @param ctx     TestNG context
         * @param browser Optional browser override parameter
         */
        @BeforeMethod(alwaysRun = true)
        @Parameters({"browser"})
        public void setup(Method method, ITestContext ctx, @Optional String browser) {
            // Set browser from parameter if provided
            if (browser != null && !browser.isEmpty()) {
                System.setProperty("browser", browser);
            }

            // Log test start
            String className = this.getClass().getSimpleName();
            String methodName = method.getName();
            log("STARTING TEST: " + className + "." + methodName);

            // Log test metadata
            org.testng.annotations.Test testAnnotation = method.getAnnotation(org.testng.annotations.Test.class);
            if (testAnnotation != null) {
                String description = testAnnotation.description();
                if (description != null && !description.isEmpty()) {
                    log("Test description: " + description);
                }

                if (testAnnotation.groups().length > 0) {
                    log("Test groups: " + String.join(", ", testAnnotation.groups()));
                }
            }

            // Initialize WebDriver
            DriverManager.initDriver();
        }

        /**
         * Cleanup executed after each test method
         *
         * @param result Test result
         */
        @AfterMethod(alwaysRun = true)
        public void tearDown(ITestResult result) {
            // Log test result
            String status = getTestResultStatus(result);
            log("Test completed with status: " + status);

            // Capture screenshot on failure
            if (result.getStatus() == ITestResult.FAILURE) {
                String screenshotPath = ScreenshotUtils.takeScreenshot(result.getName());
                log("Test failed. Screenshot saved to: " + screenshotPath);

                // Log error details
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    log("Exception: " + throwable.getMessage());
                    log("Stack trace: " + Arrays.toString(throwable.getStackTrace()));
                }
            }

            // Quit WebDriver
            try {
                DriverManager.quitDriver();
            } catch (Exception e) {
                log("Error quitting driver: " + e.getMessage());
            }
        }

        /**
         * Cleanup executed after test suite
         *
         * @param ctx TestNG context
         */
        @AfterSuite(alwaysRun = true)
        public void tearDownSuite(ITestContext ctx) {
            // Log suite completion
            int passed = ctx.getPassedTests().size();
            int failed = ctx.getFailedTests().size();
            int skipped = ctx.getSkippedTests().size();

            log("==================================================");
            log("TEST SUITE COMPLETED: " + ctx.getSuite().getName());
            log("Total tests: " + (passed + failed + skipped));
            log("Passed: " + passed);
            log("Failed: " + failed);
            log("Skipped: " + skipped);
            log("==================================================");

            // Close log writer
            try {
                if (logWriter != null) {
                    logWriter.close();
                }
            } catch (IOException e) {
                System.err.println("Failed to close log file: " + e.getMessage());
            }
        }

        /**
         * Get human-readable test result status
         *
         * @param result Test result
         * @return Status string
         */
        private String getTestResultStatus(ITestResult result) {
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    return "PASSED";
                case ITestResult.FAILURE:
                    return "FAILED";
                case ITestResult.SKIP:
                    return "SKIPPED";
                case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                    return "PARTIALLY PASSED";
                default:
                    return "UNKNOWN";
            }
        }

        /**
         * Utility method to retry failed assertions
         * Useful for flaky UI tests with timing issues
         *
         * @param assertion       Assertion lambda to retry
         * @param maxRetries      Maximum number of retries
         * @param retryIntervalMs Milliseconds between retries
         */
        protected void retryAssertion(Runnable assertion, int maxRetries, long retryIntervalMs) {
            int retryCount = 0;
            while (retryCount < maxRetries) {
                try {
                    assertion.run();
                    return; // Assertion passed, exit method
                } catch (AssertionError e) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e; // Re-throw if all retries failed
                    }

                    log("Assertion failed, retrying (" + retryCount + "/" + maxRetries + ")");
                    try {
                        Thread.sleep(retryIntervalMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry wait", ie);
                    }
                }
            }
        }
    }


