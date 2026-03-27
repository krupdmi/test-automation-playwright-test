package org.testautomation.core.utils;

import io.cucumber.java.Scenario;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Thread-local holder for the current Cucumber {@link Scenario}.
 * Used to pass the active scenario to infrastructure code (e.g. {@link ReportManager})
 * that cannot receive it via dependency injection.
 *
 * <p>Set in {@code @Before} hooks; cleared in {@code @After} hooks.</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScenarioContextHolder {

    private static final ThreadLocal<Scenario> SCENARIO = new ThreadLocal<>();

    public static void setScenario(Scenario scenario) {
        SCENARIO.set(scenario);
    }

    public static Scenario getScenario() {
        return SCENARIO.get();
    }

    public static void clear() {
        SCENARIO.remove();
    }
}
