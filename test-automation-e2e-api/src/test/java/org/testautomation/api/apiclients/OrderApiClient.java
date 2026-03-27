package org.testautomation.api.apiclients;

import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.testautomation.api.config.ApiEndpointProperties;
import org.testautomation.api.models.be.order.OrderRequest;
import org.testautomation.core.api.BaseApiClient;

/**
 * REST Assured client for the Order service endpoints.
 */
@Component
@RequiredArgsConstructor
public class OrderApiClient {

    private final BaseApiClient baseApiClient;
    private final ApiEndpointProperties endpoints;

    public Response getOrders(Scenario scenario) {
        return baseApiClient.apiClient(scenario, "GET /orders")
                .get(endpoints.getOrderService());
    }

    public Response getOrderById(String orderId, Scenario scenario) {
        return baseApiClient.apiClient(scenario, "GET /orders/{id}")
                .pathParam("id", orderId)
                .get(endpoints.getOrderService() + "/{id}");
    }

    public Response createOrder(OrderRequest request, String token, Scenario scenario) {
        return baseApiClient.authApiClient(token, scenario, "POST /orders")
                .body(request)
                .post(endpoints.getOrderService());
    }
}
