package org.testautomation.ui.stepdefinitions.login;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testautomation.commons.constants.ScenarioContextConstants;
import org.testautomation.core.context.ScenarioContext;
import org.testautomation.ui.config.UiEndpointProperties;
import org.testautomation.ui.pages.DashboardPage;
import org.testautomation.ui.pages.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class LoginSteps {

    private final Page page;
    private final ScenarioContext scenarioContext;
    private final UiEndpointProperties endpoints;

    // Page objects are created per-step-class (not Spring beans) to keep context clean
    private LoginPage loginPage() { return new LoginPage(page); }
    private DashboardPage dashboardPage() { return new DashboardPage(page); }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage().open(endpoints.getApp());
        assertThat(loginPage().isDisplayed())
                .as("Login page should be displayed after navigation")
                .isTrue();
    }

    @When("I log in with valid credentials")
    public void iLogInWithValidCredentials() {
        String username = scenarioContext.getRequired(
                ScenarioContextConstants.CURRENT_USERNAME, String.class);
        // Password resolved from UserPoolManager session — in a real project
        // retrieve it from the session rather than hard-coding
        loginPage().loginAs(username, "changeme");
    }

    @When("I log in with username {string} and password {string}")
    public void iLogInWithUsernameAndPassword(String username, String password) {
        loginPage().loginAs(username, password);
    }

    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToTheDashboard() {
        assertThat(dashboardPage().isDisplayed())
                .as("Dashboard should be visible after successful login")
                .isTrue();
    }

    @Then("I should see a login error message")
    public void iShouldSeeALoginErrorMessage() {
        assertThat(loginPage().hasError())
                .as("An error message should appear after invalid login")
                .isTrue();
    }
}
