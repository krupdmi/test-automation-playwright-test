package org.testautomation.ui.pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page object for the login screen.
 *
 * <p>Locators use {@code data-testid} attributes — set
 * {@code playwright.selectors().setTestIdAttribute("data-testid")} in
 * {@link org.testautomation.core.ui.PlaywrightInstanceProvider}.</p>
 *
 * <p>Replace selector strings with the actual values from your application.</p>
 */
@Slf4j
public class LoginPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────
    private static final String USERNAME_INPUT  = "[data-testid='username-input']";
    private static final String PASSWORD_INPUT  = "[data-testid='password-input']";
    private static final String SUBMIT_BUTTON   = "[data-testid='login-submit']";
    private static final String ERROR_MESSAGE   = "[data-testid='login-error']";
    private static final String PAGE_TITLE      = "[data-testid='login-title']";

    public LoginPage(Page page) {
        super(page);
    }

    @Override
    public String getPageUrl() {
        return "/login";
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void open(String baseUrl) {
        navigateTo(baseUrl + getPageUrl());
    }

    public void enterUsername(String username) {
        fill(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        fill(PASSWORD_INPUT, password);
    }

    public void submit() {
        click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }

    public void loginAs(String username, String password) {
        log.info("Logging in as '{}'", username);
        enterUsername(username);
        enterPassword(password);
        submit();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        return isVisible(PAGE_TITLE);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean hasError() {
        return isVisible(ERROR_MESSAGE);
    }
}
