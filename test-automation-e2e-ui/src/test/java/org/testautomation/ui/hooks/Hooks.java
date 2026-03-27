package org.testautomation.ui.hooks;

import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.commons.config.UserPoolManager;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.commons.utils.TokenHolder;
import org.testautomation.core.context.FeatureContext;
import org.testautomation.core.context.ScenarioContext;
import org.testautomation.core.ui.PlaywrightInstanceProvider;
import org.testautomation.core.utils.ReportManager;
import org.testautomation.core.utils.ScenarioContextHolder;
import org.testautomation.core.utils.ScreenshotUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Cucumber lifecycle hooks for the UI test module.
 *
 * <p>Hook ordering mirrors the API module:
 * <ul>
 *   <li>order=0 — token retrieval (add project-specific hooks here)</li>
 *   <li>order=1 — scenario initialisation</li>
 *   <li>order=100 — teardown (screenshot on failure, browser cleanup)</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public class Hooks {

    private final ScenarioContext scenarioContext;
    private final FeatureContext featureContext;
    private final UserPoolManager userPoolManager;
    private final Page page;

    // ── Before ────────────────────────────────────────────────────────────────

    @Before(order = 1, value = "@ui")
    public void beforeScenario(Scenario scenario) {
        featureContext.clear();
        ScenarioContextHolder.setScenario(scenario);
        log.info("▶ UI Scenario '{}' starting", scenario.getName());

        UserPoolManager.UserSession session = userPoolManager.acquireUser();
        scenarioContext.set(ScenarioContextConstants.CURRENT_USERNAME, session.getUsername());
    }

    // ── After ─────────────────────────────────────────────────────────────────

    @After(order = 100, value = "@ui")
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                captureAndAttachScreenshot(scenario);
                log.error("✗ UI Scenario '{}' FAILED", scenario.getName());
            } else {
                log.info("✓ UI Scenario '{}' PASSED", scenario.getName());
            }
            attachCapturedCurls(scenario);
        } finally {
            TokenHolder.clearUserToken();
            featureContext.remove();
            ScenarioContextHolder.clear();
            userPoolManager.releaseUser(scenario);
            PlaywrightInstanceProvider.cleanupThreadResources();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void captureAndAttachScreenshot(Scenario scenario) {
        try {
            String path = ScreenshotUtil.capture(page, scenario.getName());
            if (path != null) {
                byte[] bytes = Files.readAllBytes(Paths.get(path));
                Allure.addAttachment("Screenshot on failure", "image/png",
                        new java.io.ByteArrayInputStream(bytes), ".png");
            }
        } catch (IOException e) {
            log.warn("Could not attach screenshot to Allure: {}", e.getMessage());
        }
    }

    private void attachCapturedCurls(Scenario scenario) {
        try {
            String curls = String.join("\n\n",
                    ReportManager.getInstance().getCurls(scenario.getName()));
            if (!curls.isBlank()) {
                Allure.addAttachment("Network Requests (cURL)", "text/plain", curls);
            }
            ReportManager.getInstance().clearCurlsAndResponses(scenario.getName());
        } catch (Exception e) {
            log.debug("Could not attach cURLs to Allure: {}", e.getMessage());
        }
    }
}
