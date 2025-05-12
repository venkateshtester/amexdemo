package com.amex.ui.framework.config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    private ConfigReader() {
        // Private constructor to prevent instantiation
    }

    public static void loadConfig() {
        if (!isLoaded) {
            try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
                properties.load(fis);
                isLoaded = true;
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config.properties", e);
            }
        }
    }

    public static String getProperty(String key) {
        if (!isLoaded) {
            loadConfig();
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property " + key + " not found in config.properties");
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        if (!isLoaded) {
            loadConfig();
        }
        return properties.getProperty(key, defaultValue);
    }
}
