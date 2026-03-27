package org.testautomation.api.hooks;

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
import org.testautomation.core.utils.ReportManager;
import org.testautomation.core.utils.ScenarioContextHolder;

import java.util.List;

/**
 * Cucumber lifecycle hooks for the API test module.
 *
 * <p>Hook ordering:
 * <ol>
 *   <li>order=0 — infrastructure setup (token retrieval hooks belong here)</li>
 *   <li>order=1 — scenario initialisation (user pool, context, reporting)</li>
 *   <li>order=100 — teardown (release user, clean ThreadLocals)</li>
 * </ol>
 *
 * <p>Add token-retrieval Before hooks in your project following the pattern:
 * <pre>
 *   @Before(order = 0, value = "@userToken and @api")
 *   public void retrieveUserToken(Scenario scenario) { ... }
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class Hooks {

    private final ScenarioContext scenarioContext;
    private final FeatureContext featureContext;
    private final UserPoolManager userPoolManager;

    private static volatile boolean shutdownHookRegistered = false;

    // ── Before ────────────────────────────────────────────────────────────────

    @Before(order = 1, value = "@api")
    public void beforeScenario(Scenario scenario) {
        featureContext.clear();
        ScenarioContextHolder.setScenario(scenario);

        List<String> tags = scenario.getSourceTagNames().stream()
                .map(t -> t.replace("@", ""))
                .toList();
        ReportManager.getInstance().collectCurl(scenario.getName(), ""); // prime entry
        Allure.step("Scenario start: " + scenario.getName());
        log.info("▶ Scenario '{}' — tags: {}", scenario.getName(), tags);

        UserPoolManager.UserSession session = userPoolManager.acquireUser();
        scenarioContext.set(ScenarioContextConstants.CURRENT_USERNAME, session.getUsername());
    }

    // ── After ─────────────────────────────────────────────────────────────────

    @After(order = 100, value = "@api")
    public void afterScenario(Scenario scenario) {
        try {
            attachApiResponsesToAllure(scenario);
            registerShutdownHookOnce();

            if (scenario.isFailed()) {
                log.error("✗ Scenario '{}' FAILED", scenario.getName());
            } else {
                log.info("✓ Scenario '{}' PASSED", scenario.getName());
            }
        } finally {
            TokenHolder.clearUserToken();
            featureContext.remove();
            ScenarioContextHolder.clear();
            userPoolManager.releaseUser(scenario);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void attachApiResponsesToAllure(Scenario scenario) {
        try {
            String responses = String.join("\n\n",
                    ReportManager.getInstance().getResponses(scenario.getName()));
            if (!responses.isBlank()) {
                Allure.addAttachment("API Responses", "application/json", responses);
            }
            String curls = String.join("\n\n",
                    ReportManager.getInstance().getCurls(scenario.getName()));
            if (!curls.isBlank()) {
                Allure.addAttachment("cURL Commands", "text/plain", curls);
            }
            ReportManager.getInstance().clearCurlsAndResponses(scenario.getName());
        } catch (Exception e) {
            log.debug("Could not attach API responses to Allure: {}", e.getMessage());
        }
    }

    private void registerShutdownHookOnce() {
        if (!shutdownHookRegistered) {
            synchronized (Hooks.class) {
                if (!shutdownHookRegistered) {
                    Runtime.getRuntime().addShutdownHook(new Thread(() ->
                            ReportManager.getInstance().flush()));
                    shutdownHookRegistered = true;
                }
            }
        }
    }
}
