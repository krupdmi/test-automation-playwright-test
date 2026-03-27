package org.testautomation.api.stepdefinitions.order;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.api.apiclients.OrderApiClient;
import org.testautomation.api.models.be.order.OrderRequest;
import org.testautomation.commons.builders.OrderBuilder;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.commons.utils.TokenHolder;
import org.testautomation.core.context.ScenarioContext;
import org.testautomation.core.utils.ScenarioContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class OrderApiSteps {

    private final OrderApiClient orderApiClient;
    private final ScenarioContext scenarioContext;

    @When("I request the list of orders")
    public void iRequestTheListOfOrders() {
        Response response = orderApiClient.getOrders(ScenarioContextHolder.getScenario());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_STATUS, response.statusCode());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_BODY, response.asString());
        log.info("GET /orders → HTTP {}", response.statusCode());
    }

    @Given("I have a valid new order payload")
    public void iHaveAValidNewOrderPayload() {
        OrderBuilder builder = OrderBuilder.random();
        OrderRequest request = OrderRequest.builder()
                .userId(builder.getUserId())
                .amount(builder.getAmount())
                .currency(builder.getCurrency())
                .description(builder.getDescription())
                .build();
        scenarioContext.set(ScenarioContextConstants.REQUEST_BODY, request);
        log.info("Prepared order payload — amount {} {}", request.getAmount(), request.getCurrency());
    }

    @When("I submit the create order request")
    public void iSubmitTheCreateOrderRequest() {
        OrderRequest request = scenarioContext.getRequired(
                ScenarioContextConstants.REQUEST_BODY, OrderRequest.class);
        Response response = orderApiClient.createOrder(
                request, TokenHolder.getUserToken(), ScenarioContextHolder.getScenario());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_STATUS, response.statusCode());
        scenarioContext.set(ScenarioContextConstants.RESPONSE_BODY, response.asString());
        log.info("POST /orders → HTTP {}", response.statusCode());
    }

    @Then("the created order should have status {string}")
    public void theCreatedOrderShouldHaveStatus(String expectedStatus) {
        String body = scenarioContext.getRequired(
                ScenarioContextConstants.RESPONSE_BODY, String.class);
        assertThat(body).contains(expectedStatus);
    }
}
