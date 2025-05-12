package utils;
import com.amex.ui.framework.config.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    public static String takeScreenshot(String testName) {
        WebDriver driver = DriverManager.getDriver();

        // Create directory if it doesn't exist
        createScreenshotDirIfNeeded();

        // Generate unique filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";

        // Take screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            Files.copy(screenshot.toPath(), Paths.get(filename));
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

    private static void createScreenshotDirIfNeeded() {
        Path dirPath = Paths.get(SCREENSHOT_DIR);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.err.println("Failed to create screenshot directory: " + e.getMessage());
            }
        }
    }
}
