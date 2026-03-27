package org.testautomation.core.utils;

import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Utility for formatting REST Assured requests and responses into human-readable strings.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiLogUtil {

    /**
     * Builds a cURL command string from request components.
     */
    public static String buildCurl(String method, String uri,
                                   Map<String, String> headers, String body) {
        StringBuilder sb = new StringBuilder("curl -X ").append(method).append(" '").append(uri).append("'");

        if (headers != null) {
            headers.forEach((k, v) -> sb.append(" \\\n  -H '").append(k).append(": ").append(v).append("'"));
        }

        if (body != null && !body.isBlank()) {
            sb.append(" \\\n  -d '").append(body).append("'");
        }

        return sb.toString();
    }

    /**
     * Returns the response body as a formatted string.
     * Falls back to the raw string if pretty-printing fails.
     */
    public static String prettyPrint(Response response) {
        if (response == null) {
            return "<null response>";
        }
        try {
            String body = response.getBody().asPrettyString();
            return String.format("HTTP %d%n%s", response.getStatusCode(), body);
        } catch (Exception e) {
            log.debug("Could not pretty-print response: {}", e.getMessage());
            return response.asString();
        }
    }
}
