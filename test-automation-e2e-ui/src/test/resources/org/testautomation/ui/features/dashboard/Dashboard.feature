# Dashboard feature — post-login state assertions and logout flow

@ui
Feature: Dashboard

  @smoke @dashboard
  Scenario: Dashboard displays welcome message with logged-in username
    Given I am on the login page
    When I log in with valid credentials
    Then the dashboard should be displayed
    And the welcome message should contain my username

  @smoke @dashboard @logout
  Scenario: Logging out from the dashboard returns to the login page
    Given I am on the login page
    When I log in with valid credentials
    And I log out from the dashboard
    Then I should be returned to the login page
