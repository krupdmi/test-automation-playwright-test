package org.testautomation.core.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepts Playwright network requests and serves configurable mock responses.
 * Useful for isolating UI tests from backend dependencies.
 *
 * <p>Usage: inject this bean, call {@link #addMock}, then {@link #setupInterception} on your page.</p>
 */
@Component
@Slf4j
public class PlaywrightNetworkInterceptor {

    private final Map<String, String> mocks = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    /** Register a mock response for a URL pattern (supports {@code **} wildcards). */
    public void addMock(String urlPattern, String responseBody) {
        mocks.put(urlPattern, responseBody);
    }

    /** Serialize {@code responseObject} to JSON and register as a mock. */
    public void addMock(String urlPattern, Object responseObject) {
        try {
            mocks.put(urlPattern, mapper.writeValueAsString(responseObject));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize mock response to JSON", e);
        }
    }

    public void removeMock(String urlPattern) {
        mocks.remove(urlPattern);
    }

    public void clearMocks() {
        mocks.clear();
    }

    /** Attach interception to a page. Matched requests are fulfilled; others pass through. */
    public void setupInterception(Page page) {
        page.route("**/*", this::handleRoute);
    }

    private void handleRoute(Route route) {
        String url = route.request().url();
        String match = mocks.keySet().stream().filter(p -> matches(url, p)).findFirst().orElse(null);
        if (match != null) {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody(mocks.get(match)));
        } else {
            route.resume();
        }
    }

    private boolean matches(String url, String pattern) {
        if (pattern.startsWith("**") && pattern.endsWith("**")) {
            return url.contains(pattern.substring(2, pattern.length() - 2));
        } else if (pattern.startsWith("**")) {
            return url.endsWith(pattern.substring(2));
        } else if (pattern.endsWith("**")) {
            return url.startsWith(pattern.substring(0, pattern.length() - 2));
        }
        return url.equals(pattern);
    }
}
