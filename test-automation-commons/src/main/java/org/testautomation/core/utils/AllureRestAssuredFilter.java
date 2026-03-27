package org.testautomation.core.utils;

import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Assured filter that logs each API call to Allure as a named step
 * with cURL command and response body attachments.
 */
@Slf4j
@RequiredArgsConstructor
public class AllureRestAssuredFilter implements Filter {

    private final Scenario scenario;
    private final String stepLabel;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        Response response = ctx.next(requestSpec, responseSpec);

        Map<String, String> headers = requestSpec.getHeaders().asList().stream()
                .collect(Collectors.toMap(Header::getName, Header::getValue, (a, b) -> b));

        String curl = ApiLogUtil.buildCurl(
                requestSpec.getMethod(),
                requestSpec.getURI(),
                headers,
                requestSpec.getBody() != null ? requestSpec.getBody().toString() : null);

        String prettyResponse = ApiLogUtil.prettyPrint(response);

        if (scenario != null) {
            ReportManager.getInstance().collectCurl(scenario.getName(), curl);
            ReportManager.getInstance().collectResponse(scenario.getName(), prettyResponse);
        }

        attachToAllure(curl, prettyResponse);

        log.debug("API call — {} {}", requestSpec.getMethod(), requestSpec.getURI());
        return response;
    }

    private void attachToAllure(String curl, String response) {
        try {
            String name = stepLabel != null ? "API: " + stepLabel : "API Call";
            Allure.step(name, () -> {
                Allure.addAttachment("cURL", "text/plain", curl);
                Allure.addAttachment("Response", "application/json", response);
            });
        } catch (Exception e) {
            log.debug("Could not attach API call to Allure: {}", e.getMessage());
        }
    }
}
