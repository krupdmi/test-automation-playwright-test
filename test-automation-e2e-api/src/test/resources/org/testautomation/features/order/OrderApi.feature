# Order API feature — demonstrates GET and authenticated POST scenarios
# @userToken tag triggers the token retrieval Before hook (order=0)

@api
Feature: Order API

  @smoke @orderList
  Scenario: Get list of orders returns 200
    When I request the list of orders
    Then the response status code should be 200
    And the response body should not be empty

  @smoke @orderCreate @userToken
  Scenario: Create a new order returns 201 with PENDING status
    Given I have a valid new order payload
    When I submit the create order request
    Then the response status code should be 201
    And the created order should have status "PENDING"
