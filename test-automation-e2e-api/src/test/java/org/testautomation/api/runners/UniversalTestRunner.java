package org.testautomation.api.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Universal Cucumber test runner for the API module.
 *
 * <p>Run all scenarios:
 * <pre>mvn test -pl test-automation-e2e-api</pre>
 *
 * <p>Filter by tag:
 * <pre>mvn test -pl test-automation-e2e-api -Dcucumber.filter.tags="@smoke"</pre>
 *
 * <p>Parallel threads:
 * <pre>mvn test -pl test-automation-e2e-api -Dparallel.threads=8</pre>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, " +
                "html:target/cucumber-reports/cucumber.html, " +
                "json:target/cucumber-reports/cucumber.json, " +
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm, " +
                "org.testautomation.api.listeners.AllureStepLogger")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "org.testautomation.commons.config," +
                "org.testautomation.commons.stepdefinitions," +
                "org.testautomation.api.hooks," +
                "org.testautomation.api.stepdefinitions")
public class UniversalTestRunner {
}
