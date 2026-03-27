package org.testautomation.core.api;

import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testautomation.core.utils.AllureRestAssuredFilter;

import static org.testautomation.core.constants.GenericConstants.HTTP_CONNECTION_TIMEOUT;
import static org.testautomation.core.constants.GenericConstants.HTTP_SOCKET_TIMEOUT;
import static org.testautomation.core.constants.HeaderConstants.AUTHORIZATION;
import static org.testautomation.core.constants.HeaderConstants.BEARER;

/**
 * Factory for REST Assured {@link RequestSpecification} instances.
 * Attaches the Allure logging filter and configures connection/socket timeouts.
 *
 * <p>Extend this class to add project-specific default headers.</p>
 */
@Component
public class BaseApiClient {

    @Value("${spring.api.timeout:30000}")
    private int timeout;

    /** Unauthenticated JSON client. */
    public RequestSpecification apiClient(Scenario scenario) {
        return buildSpec(scenario, null, ContentType.JSON, null);
    }

    /** Unauthenticated JSON client with an Allure step label. */
    public RequestSpecification apiClient(Scenario scenario, String stepLabel) {
        return buildSpec(scenario, stepLabel, ContentType.JSON, null);
    }

    /** Unauthenticated XML client. */
    public RequestSpecification xmlApiClient(Scenario scenario) {
        return buildSpec(scenario, null, ContentType.XML, null);
    }

    /** Authenticated JSON client — attaches {@code Authorization: Bearer <token>}. */
    public RequestSpecification authApiClient(String authToken, Scenario scenario) {
        return buildSpec(scenario, null, ContentType.JSON, authToken);
    }

    /** Authenticated JSON client with an Allure step label. */
    public RequestSpecification authApiClient(String authToken, Scenario scenario, String stepLabel) {
        return buildSpec(scenario, stepLabel, ContentType.JSON, authToken);
    }

    // -------------------------------------------------------------------------

    private RequestSpecification buildSpec(Scenario scenario, String stepLabel,
                                           ContentType contentType, String authToken) {
        RestAssured.defaultParser = Parser.fromContentType(contentType.toString());

        RequestSpecification spec = RestAssured.given()
                .filter(new AllureRestAssuredFilter(scenario, stepLabel))
                .config(RestAssured.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam(HTTP_CONNECTION_TIMEOUT, timeout)
                                .setParam(HTTP_SOCKET_TIMEOUT, timeout)))
                .contentType(contentType)
                .accept(contentType)
                .log().all();

        if (authToken != null) {
            spec.header(AUTHORIZATION, BEARER + authToken);
        }
        return spec;
    }
}
