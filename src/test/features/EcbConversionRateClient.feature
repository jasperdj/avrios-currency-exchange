Feature: Ecb client service

  Scenario: Successfully retrieve xml String for 1 day window
    When xml string is requested for a 1 day window
    Then xml string for 1 day window is returned

  Scenario: Successfully retrieve xml String for 2 day window
    When xml string is requested for a 2 day window
    Then xml string for 88 day window is returned

  Scenario: Successfully retrieve xml String for 88 day window
    When xml string is requested for a 88 day window
    Then xml string for 88 day window is returned

  Scenario: Successfully retrieve xml String for 89 day window
    When xml string is requested for a 89 day window
    Then xml string for 88 day window is returned