Feature: Local date ring buffer

  Scenario: One item is succesfully added on the same day as head
    Given a ring buffer with size 3 is created with Date day: 6  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 6  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0 | 1    | 2    |
      | 1 | null | null |


  # 0 1 2 3 4 -> | 4-6 |
  Scenario: Items are successfully retrieved by date
    Given a ring buffer with size 5 is created with Date day: 25  month: 4 year: 2019
    And all items in the ring buffer have the same value as their slot index
    And head is 2 with headDate day: 25  month: 4 year: 2019
    When items are retrieved with the following dates
      | 23-4-2019 | 24-4-2019 | 25-4-2019 | 21-4-2019 | 22-4-2019 |
    Then the retrieval results are
      | 0 | 1 | 2 | 3 | 4 |
