package org.testautomation.api.stepdefinitions.user;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.api.apiclients.UserApiClient;
import org.testautomation.api.models.be.user.UserRequest;
import org.testautomation.api.models.be.user.UserResponse;
import org.testautomation.commons.builders.UserBuilder;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.core.context.ScenarioContext;
import org.testautomation.core.utils.ScenarioContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class UserApiSteps {

    private final UserApiClient userApiClient;
    private final ScenarioContext scenarioContext;

    @When("I request the list of users")
    public void iRequestTheListOfUsers() {
        Response response = userApiClient.getUsers(ScenarioContextHolder.getScenario());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_STATUS, response.statusCode());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_BODY, response.asString());
        log.info("GET /users → HTTP {}", response.statusCode());
    }

    @Given("I have a valid new user payload")
    public void iHaveAValidNewUserPayload() {
        UserBuilder builder = UserBuilder.random();
        UserRequest request = UserRequest.builder()
                .username(builder.getUsername())
                .email(builder.getEmail())
                .firstName(builder.getFirstName())
                .lastName(builder.getLastName())
                .password(builder.getPassword())
                .build();
        scenarioContext.set(ScenarioContextConstants.REQUEST_BODY, request);
        log.info("Prepared user payload for username '{}'", request.getUsername());
    }

    @When("I submit the create user request")
    public void iSubmitTheCreateUserRequest() {
        UserRequest request = scenarioContext.getRequired(
                ScenarioContextConstants.REQUEST_BODY, UserRequest.class);
        Response response = userApiClient.createUser(request, ScenarioContextHolder.getScenario());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_STATUS, response.statusCode());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_BODY, response.asString());
        log.info("POST /users → HTTP {}", response.statusCode());
    }

    @Then("the created user should have the submitted username")
    public void theCreatedUserShouldHaveTheSubmittedUsername() {
        UserRequest request = scenarioContext.getRequired(
                ScenarioContextConstants.REQUEST_BODY, UserRequest.class);
        String body = scenarioContext.getRequired(
                ScenarioContextConstants.RESPONSE_BODY, String.class);
        // In a real project deserialise properly; here we do a quick string check
        assertThat(body).contains(request.getUsername());
    }
}
