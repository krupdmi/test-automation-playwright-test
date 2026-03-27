package org.testautomation.ui.pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page object for the main dashboard shown after successful login.
 * Replace selector strings with the actual values from your application.
 */
@Slf4j
public class DashboardPage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────────
    private static final String DASHBOARD_HEADER  = "[data-testid='dashboard-header']";
    private static final String WELCOME_MESSAGE   = "[data-testid='welcome-message']";
    private static final String USER_MENU         = "[data-testid='user-menu']";
    private static final String LOGOUT_BUTTON     = "[data-testid='logout-button']";

    public DashboardPage(Page page) {
        super(page);
    }

    @Override
    public String getPageUrl() {
        return "/dashboard";
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void logout() {
        log.info("Logging out via dashboard user menu");
        click(USER_MENU);
        click(LOGOUT_BUTTON);
        page.waitForLoadState();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        return isVisible(DASHBOARD_HEADER);
    }

    public String getWelcomeMessage() {
        return getText(WELCOME_MESSAGE);
    }

    public boolean welcomeMessageContains(String text) {
        return getWelcomeMessage().contains(text);
    }
}
