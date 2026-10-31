package com.sdet.booker.listeners;

import com.sdet.booker.config.Config;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to retry.count times. Meant only for an unreliable shared demo host;
 * a retried pass still appears in Allure as a retry, so real flakiness stays visible.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < Config.retryCount()) {
            attempts++;
            return true;
        }
        return false;
    }
}
