Feature: sample controller

  Scenario: Retrieve hello world
    When hello world is requested
    Then the text 'Hello World!' is returned.