package org.testautomation.commons.utils;

import io.cucumber.java.Scenario;

/**
 * Contract for retrieving authentication tokens during test execution.
 *
 * <p>Implement this interface for your specific auth mechanism and register
 * the implementation as a Spring bean. The API hooks call it in a
 * {@code @Before(order = 0, value = "@userToken")} hook.</p>
 *
 * <p>Example implementation stub:</p>
 * <pre>
 * {@code
 * @Component
 * public class MyTokenProvider implements TokenProvider {
 *     @Override
 *     public String getAuthToken(String username, Scenario scenario) {
 *         // call your auth endpoint, return the access token
 *         return authApiClient.login(username, password).getAccessToken();
 *     }
 * }
 * }
 * </pre>
 */
public interface TokenProvider {

    /**
     * Retrieves an authentication token for the given user.
     *
     * @param username the username to authenticate as
     * @param scenario the current Cucumber scenario (for logging / Allure steps)
     * @return the access token string — never null
     */
    String getAuthToken(String username, Scenario scenario);
}
