Feature: Local date ring buffer

  Scenario: One item is succesfully added on the same day as head
    Given a ring buffer with size 3 is created with Date day: 6  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 6  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0 | 1    | 2    |
      | 1 | null | null |