# Login feature — demonstrates successful and failed login UI flows
# @ui tag required so UI hooks fire; @smoke marks regression-critical scenarios

@ui
Feature: Login

  @smoke @login
  Scenario: Successful login with valid credentials redirects to dashboard
    Given I am on the login page
    When I log in with valid credentials
    Then I should be redirected to the dashboard
    And the dashboard should be displayed

  @login @negative
  Scenario: Login with invalid credentials shows an error message
    Given I am on the login page
    When I log in with username "invalid_user" and password "wrong_password"
    Then I should see a login error message
