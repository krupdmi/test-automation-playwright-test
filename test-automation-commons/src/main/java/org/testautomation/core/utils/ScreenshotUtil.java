package org.testautomation.core.utils;

import com.microsoft.playwright.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Captures Playwright screenshots to {@code test-output/screenshots/}.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "test-output/screenshots";

    /**
     * Takes a full-page screenshot and returns the file path, or {@code null} on failure.
     */
    public static String capture(Page page, String scenarioName) {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
            String fileName = sanitize(scenarioName) + "_" + UUID.randomUUID() + ".png";
            Path path = Paths.get(SCREENSHOT_DIR, fileName);
            page.screenshot(new Page.ScreenshotOptions().setPath(path).setFullPage(true));
            log.info("Screenshot saved: {}", path);
            return path.toString();
        } catch (Exception e) {
            log.error("Failed to capture screenshot for '{}': {}", scenarioName, e.getMessage());
            return null;
        }
    }

    private static String sanitize(String name) {
        return name == null ? "unknown" : name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
