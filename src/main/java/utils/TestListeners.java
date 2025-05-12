package utils;

import com.amex.ui.framework.config.DriverManager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import org.testng.IAnnotationTransformer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * TestNG listener for enhanced test reporting and retry handling
 */

public class TestListeners implements ITestListener, IAnnotationTransformer{

    private static final String REPORT_DIR = "target/test-reports/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Called when test suite starts
     */
    @Override
    public void onStart(ITestContext context) {

        createReportDirectory();
    }

    /**
     * Called when test suite finishes
     */
    @Override
    public void onFinish(ITestContext context) {

        // Generate summary report
        generateSummaryReport(context);
    }

    /**
     * Called when test method starts
     */
    @Override
    public void onTestStart(ITestResult result) {

    }

    /**
     * Called when test method succeeds
     */
    @Override
    public void onTestSuccess(ITestResult result) {

    }

    /**
     * Called when test method fails
     */
    @Override
    public void onTestFailure(ITestResult result) {

        // Capture detailed failure information
        captureFailureDetails(result);
    }

    /**
     * Called when test method is skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {


        // Check if it was skipped due to dependencies
        if (result.getThrowable() != null) {

        }
    }

    /**
     * Apply RetryAnalyzer to all test methods automatically
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Set retry analyzer for all test methods
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    /**
     * Capture failure details for reporting
     */
    private void captureFailureDetails(ITestResult result) {
        try {
            // Take screenshot
            String screenshotPath = ScreenshotUtils.takeScreenshot(getTestMethodName(result));

            // Log exception details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {

                throwable.printStackTrace();

                // Save stack trace to file
                String stackTraceFilePath = saveStackTraceToFile(throwable, getTestMethodName(result));

            }

            // Capture browser console logs if possible
            captureBrowserLogs(getTestMethodName(result));

            // Capture DOM snapshot
            captureDOMSnapshot(getTestMethodName(result));


        } catch (Exception e) {

        }
    }

    /**
     * Capture browser console logs
     */
    private void captureBrowserLogs(String testName) {
        // Implementation depends on browser driver capabilities
        // This is a placeholder for when needed
    }

    /**
     * Capture DOM snapshot
     */
    private void captureDOMSnapshot(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            String pageSource = driver.getPageSource();

            // Save DOM to file
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            Path domFilePath = Paths.get(REPORT_DIR, testName + "_" + timestamp + "_dom.html");
            Files.write(domFilePath, pageSource.getBytes());


        } catch (Exception e) {

        }
    }

    /**
     * Save stack trace to file
     */
    private String saveStackTraceToFile(Throwable throwable, String testName) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            Path stackTraceFilePath = Paths.get(REPORT_DIR, testName + "_" + timestamp + "_stacktrace.txt");

            StringBuilder sb = new StringBuilder();
            sb.append("Exception: ").append(throwable.getMessage()).append("\n\n");
            sb.append("Stack Trace:\n");

            for (StackTraceElement element : throwable.getStackTrace()) {
                sb.append("\tat ").append(element.toString()).append("\n");
            }

            Files.write(stackTraceFilePath, sb.toString().getBytes());
            return stackTraceFilePath.toString();
        } catch (IOException e) {

            return "Failed to save";
        }
    }

    /**
     * Generate summary report
     */
    private void generateSummaryReport(ITestContext context) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            Path summaryFilePath = Paths.get(REPORT_DIR, "summary_" + timestamp + ".txt");

            StringBuilder sb = new StringBuilder();
            sb.append("Test Execution Summary\n");
            sb.append("======================\n\n");
            sb.append("Suite: ").append(context.getName()).append("\n");
            sb.append("Start Time: ").append(new Date(context.getStartDate().getTime())).append("\n");
            sb.append("End Time: ").append(new Date(context.getEndDate().getTime())).append("\n");
            sb.append("Duration: ").append(context.getEndDate().getTime() - context.getStartDate().getTime()).append("ms\n\n");

            sb.append("Results:\n");
            sb.append("- Passed: ").append(context.getPassedTests().size()).append("\n");
            sb.append("- Failed: ").append(context.getFailedTests().size()).append("\n");
            sb.append("- Skipped: ").append(context.getSkippedTests().size()).append("\n\n");



            Files.write(summaryFilePath, sb.toString().getBytes());

        } catch (IOException e) {

        }
    }

    /**
     * Create report directory
     */
    private void createReportDirectory() {
        try {
            Path reportDirPath = Paths.get(REPORT_DIR);
            if (!Files.exists(reportDirPath)) {
                Files.createDirectories(reportDirPath);
            }
        } catch (IOException e) {

        }
    }

    /**
     * Get test method name
     */
    private String getTestMethodName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getName();
    }

}
