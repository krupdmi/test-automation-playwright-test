package org.testautomation.ui.stepdefinitions.dashboard;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.core.context.ScenarioContext;
import org.testautomation.ui.pages.DashboardPage;
import org.testautomation.ui.pages.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class DashboardSteps {

    private final Page page;
    private final ScenarioContext scenarioContext;

    private DashboardPage dashboardPage() { return new DashboardPage(page); }
    private LoginPage loginPage() { return new LoginPage(page); }

    @Then("the dashboard should be displayed")
    public void theDashboardShouldBeDisplayed() {
        assertThat(dashboardPage().isDisplayed())
                .as("Dashboard header should be visible")
                .isTrue();
    }

    @Then("the welcome message should contain my username")
    public void theWelcomeMessageShouldContainMyUsername() {
        String username = scenarioContext.getRequired(
                ScenarioContextConstants.CURRENT_USERNAME, String.class);
        assertThat(dashboardPage().welcomeMessageContains(username))
                .as("Welcome message should contain '%s'", username)
                .isTrue();
    }

    @When("I log out from the dashboard")
    public void iLogOutFromTheDashboard() {
        dashboardPage().logout();
    }

    @Then("I should be returned to the login page")
    public void iShouldBeReturnedToTheLoginPage() {
        assertThat(loginPage().isDisplayed())
                .as("Login page should be shown after logout")
                .isTrue();
    }
}
