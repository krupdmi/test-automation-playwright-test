package org.testautomation.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton that accumulates cURL commands and API responses per scenario name,
 * enabling a consolidated Allure attachment at the end of each scenario.
 *
 * <p>Call {@link #flush()} in a JVM shutdown hook to ensure the report is finalised.</p>
 */
@Slf4j
public final class ReportManager {

    private static final ReportManager INSTANCE = new ReportManager();

    private final Map<String, List<String>> curlsByScenario = new ConcurrentHashMap<>();
    private final Map<String, List<String>> responsesByScenario = new ConcurrentHashMap<>();

    private ReportManager() {}

    public static ReportManager getInstance() {
        return INSTANCE;
    }

    public void collectCurl(String scenarioName, String curl) {
        curlsByScenario.computeIfAbsent(scenarioName, k -> new ArrayList<>()).add(curl);
    }

    public void collectResponse(String scenarioName, String response) {
        responsesByScenario.computeIfAbsent(scenarioName, k -> new ArrayList<>()).add(response);
    }

    public List<String> getCurls(String scenarioName) {
        return curlsByScenario.getOrDefault(scenarioName, List.of());
    }

    public List<String> getResponses(String scenarioName) {
        return responsesByScenario.getOrDefault(scenarioName, List.of());
    }

    public void clearCurlsAndResponses(String scenarioName) {
        curlsByScenario.remove(scenarioName);
        responsesByScenario.remove(scenarioName);
    }

    /** Flushes any in-progress data. Called from a JVM shutdown hook. */
    public void flush() {
        log.info("ReportManager flushed — Allure results ready.");
        curlsByScenario.clear();
        responsesByScenario.clear();
    }
}
