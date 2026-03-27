package org.testautomation.commons.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.core.context.ScenarioContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shared step definitions reusable across all API feature files.
 * Add genuinely cross-cutting steps here; keep domain-specific steps in their own module.
 */
@Slf4j
@RequiredArgsConstructor
public class CommonStepDefinitionsAPI {

    private final ScenarioContext scenarioContext;

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatus) {
        Integer actualStatus = scenarioContext.getRequired(
                ScenarioContextConstants.RESPONSE_STATUS, Integer.class);
        assertThat(actualStatus)
                .as("Expected HTTP status %d but got %d", expectedStatus, actualStatus)
                .isEqualTo(expectedStatus);
    }

    @Then("the response body should not be empty")
    public void theResponseBodyShouldNotBeEmpty() {
        String body = scenarioContext.get(ScenarioContextConstants.RESPONSE_BODY, String.class);
        assertThat(body)
                .as("Response body should not be null or empty")
                .isNotBlank();
    }
}
