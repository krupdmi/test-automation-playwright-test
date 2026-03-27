package org.testautomation.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Abstract base class for all page objects.
 * Provides common wait helpers and a consistent locator API on top of Playwright.
 *
 * <p>Each concrete page receives the current Playwright {@link Page} via constructor injection
 * from the step definition that owns it (pages are NOT Spring beans — they are created per
 * scenario inside step definitions or helpers to keep the Spring context lean).</p>
 */
@Slf4j
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    protected void navigateTo(String url) {
        log.info("Navigate → {}", url);
        page.navigate(url);
        page.waitForLoadState();
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    protected void click(String selector) {
        log.debug("Click '{}'", selector);
        page.locator(selector).click();
    }

    protected void fill(String selector, String value) {
        log.debug("Fill '{}' with '{}'", selector, value);
        page.locator(selector).fill(value);
    }

    protected void selectOption(String selector, String value) {
        page.locator(selector).selectOption(value);
    }

    // ── Waits ─────────────────────────────────────────────────────────────────

    protected Locator waitForVisible(String selector) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        return page.locator(selector);
    }

    protected Locator waitForVisible(String selector, Duration timeout) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(timeout.toMillis()));
        return page.locator(selector);
    }

    protected void waitForHidden(String selector) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected String getText(String selector) {
        return page.locator(selector).innerText();
    }

    protected String getInputValue(String selector) {
        return page.locator(selector).inputValue();
    }

    // ── Subclasses must declare the page URL ──────────────────────────────────

    public abstract String getPageUrl();
}
