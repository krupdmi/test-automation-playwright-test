package org.testautomation.ui.utils;

import com.microsoft.playwright.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic Playwright interaction helpers that complement the BasePage API.
 * Use these for one-off actions outside a page object.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Actions {

    public static void scrollToBottom(Page page) {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
    }

    public static void scrollToTop(Page page) {
        page.evaluate("window.scrollTo(0, 0)");
    }

    public static void scrollIntoView(Page page, String selector) {
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    public static void hover(Page page, String selector) {
        page.locator(selector).hover();
    }

    public static void pressKey(Page page, String key) {
        page.keyboard().press(key);
    }

    public static void clearAndFill(Page page, String selector, String value) {
        page.locator(selector).clear();
        page.locator(selector).fill(value);
    }

    public static void clickWithForce(Page page, String selector) {
        page.locator(selector).click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
    }

    /** Returns the current page URL. */
    public static String getCurrentUrl(Page page) {
        return page.url();
    }

    /** Returns the page <title> text. */
    public static String getPageTitle(Page page) {
        return page.title();
    }
}
