package org.testautomation.ui.listeners;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;

/**
 * Remaps Allure BROKEN status to FAILED so all test failures appear
 * in a single bucket in the Allure report (avoids the broken/failed split
 * that happens when Playwright throws unexpected exceptions).
 */
public class BrokenToFailedLifecycleListener implements TestLifecycleListener {

    @Override
    public void beforeTestStop(TestResult result) {
        if (Status.BROKEN.equals(result.getStatus())) {
            result.setStatus(Status.FAILED);
        }
    }
}
