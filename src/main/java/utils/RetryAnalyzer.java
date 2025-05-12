package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
     * RetryAnalyzer inner class for handling test retries
     */
    public class RetryAnalyzer implements IRetryAnalyzer {
        private int retryCount = 0;
        private static final int MAX_RETRY_COUNT = 2; // Can be configured

        @Override
        public boolean retry(ITestResult result) {
            if (retryCount < MAX_RETRY_COUNT) {
                System.out.println("Retrying test: " + result.getName() + " - Retry #" + (retryCount + 1));
                retryCount++;
                return true;
            }
            return false;
        }
    }
