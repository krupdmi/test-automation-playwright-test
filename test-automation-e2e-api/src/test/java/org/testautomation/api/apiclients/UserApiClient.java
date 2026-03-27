package org.testautomation.api.apiclients;

import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.testautomation.api.config.ApiEndpointProperties;
import org.testautomation.api.models.be.user.UserRequest;
import org.testautomation.core.api.BaseApiClient;

/**
 * REST Assured client for the User service endpoints.
 * Each method maps to one endpoint and returns the raw Response
 * so step definitions can assert status, headers, and body independently.
 */
@Component
@RequiredArgsConstructor
public class UserApiClient {

    private final BaseApiClient baseApiClient;
    private final ApiEndpointProperties endpoints;

    public Response getUsers(Scenario scenario) {
        return baseApiClient.apiClient(scenario, "GET /users")
                .get(endpoints.getUserService());
    }

    public Response getUserById(String userId, Scenario scenario) {
        return baseApiClient.apiClient(scenario, "GET /users/{id}")
                .pathParam("id", userId)
                .get(endpoints.getUserService() + "/{id}");
    }

    public Response createUser(UserRequest request, Scenario scenario) {
        return baseApiClient.apiClient(scenario, "POST /users")
                .body(request)
                .post(endpoints.getUserService());
    }

    public Response createUserAuthenticated(UserRequest request, String token, Scenario scenario) {
        return baseApiClient.authApiClient(token, scenario, "POST /users (auth)")
                .body(request)
                .post(endpoints.getUserService());
    }

    public Response deleteUser(String userId, String token, Scenario scenario) {
        return baseApiClient.authApiClient(token, scenario, "DELETE /users/{id}")
                .pathParam("id", userId)
                .delete(endpoints.getUserService() + "/{id}");
    }
}
