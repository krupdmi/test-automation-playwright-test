package org.testautomation.core.ui;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Intercepts outgoing network requests in a Playwright {@link BrowserContext}
 * and captures them as cURL commands for Allure report attachments.
 */
@Component
@Slf4j
public class PlaywrightCurlCaptureService {

    private final Map<Long, StringBuilder> capturedCurls = new ConcurrentHashMap<>();

    /**
     * Attaches a request listener to the given context.
     * Call once per {@link BrowserContext} immediately after creation.
     */
    public void setupRequestInterception(BrowserContext context) {
        context.onRequest(this::captureRequest);
    }

    /** Returns all captured cURL commands for the current thread, then clears them. */
    public String flushCapturedCurls() {
        StringBuilder sb = capturedCurls.remove(Thread.currentThread().getId());
        return sb != null ? sb.toString() : "";
    }

    private void captureRequest(Request request) {
        try {
            String curl = buildCurl(request);
            capturedCurls
                    .computeIfAbsent(Thread.currentThread().getId(), id -> new StringBuilder())
                    .append(curl).append("\n\n");
        } catch (Exception e) {
            log.debug("Could not capture request as cURL: {}", e.getMessage());
        }
    }

    private String buildCurl(Request request) {
        StringBuilder sb = new StringBuilder("curl -X ").append(request.method())
                .append(" '").append(request.url()).append("'");

        Map<String, String> headers = request.headers();
        if (headers != null) {
            headers.forEach((k, v) -> sb.append(" \\\n  -H '").append(k).append(": ").append(v).append("'"));
        }

        String postData = request.postData();
        if (postData != null && !postData.isBlank()) {
            sb.append(" \\\n  -d '").append(postData).append("'");
        }

        return sb.toString();
    }
}
