# User API feature — demonstrates GET and POST scenarios
# Tags: @api required on all scenarios so API hooks fire
# Replace the placeholder base URL in application-endpoints.properties

@api
Feature: User API

  @smoke @userList
  Scenario: Get list of users returns 200
    When I request the list of users
    Then the response status code should be 200
    And the response body should not be empty

  @smoke @userCreate
  Scenario: Create a new user returns 201
    Given I have a valid new user payload
    When I submit the create user request
    Then the response status code should be 201
    And the created user should have the submitted username
