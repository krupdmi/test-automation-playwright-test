package org.testautomation.ui.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.commons.constants.GenericTimeConstants;

/**
 * Explicit wait helpers wrapping Playwright's built-in mechanisms.
 * Prefer these over {@code Thread.sleep} for stable waits.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WaitActions {

    public static void waitForVisible(Page page, String selector) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public static void waitForVisible(Page page, String selector, int timeoutMs) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(timeoutMs));
    }

    public static void waitForHidden(Page page, String selector) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
    }

    public static void waitForNavigation(Page page, Runnable action) {
        page.waitForNavigation(action);
    }

    public static void waitForNetworkIdle(Page page) {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public static void waitForUrl(Page page, String urlPattern) {
        page.waitForURL(urlPattern);
    }

    /** Waits for a fixed number of milliseconds. Use sparingly — prefer event-based waits. */
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
