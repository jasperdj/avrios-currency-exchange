Feature: Local date ring buffer

  # ADDING ITEMS
  Scenario: One item is successfully added on the same day as head
    Given a ring buffer with size 3 is created with Date day: 6  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 6  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0 | 1    | 2    |
      | 1 | null | null |

  Scenario: One item with a larger date than head is not added to the buffer
    Given a ring buffer with size 3 is created with Date day: 1  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 2  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0    | 1    | 2    |
      | null | null | null |

  Scenario:  One item with a date that is smaller than (head - size) is not added
    Given  a ring buffer with size 3 is created with Date day: 5  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 2  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0    | 1    | 2    |
      | null | null | null |

  Scenario:  One item with a date that is exactly (head - size) is added
    Given  a ring buffer with size 3 is created with Date day: 5  month: 4 year: 2019
    When attempt to add item with Integer 1 and Date day: 3  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0    | 1 | 2    |
      | null | 1 | null |


  # GET ITEMS
  Scenario: Items are successfully retrieved by date, dates outside range return null.
    Given a ring buffer with size 5 is created with Date day: 25  month: 4 year: 2019
    And all items in the ring buffer have the same value as their slot index
    And head is 2 with headDate day: 25  month: 4 year: 2019
    When items are retrieved with the following dates
      | 20-4-2019 | 26-4-2019 | 23-4-2019 | 24-4-2019 | 25-4-2019 | 21-4-2019 | 22-4-2019 |
    Then the retrieval results are
      | null | null | 0 | 1 | 2 | 3 | 4 |

  # MOVE HEAD
  Scenario: Head moves up one entire cycle
    Given a ring buffer with size 5 is created with Date day: 20  month: 4 year: 2019
    And all items in the ring buffer have the same value as their slot index
    When Head moves up 6 times
    Then head is 1
    And headDate is day: 26 month: 4 year: 2019
    And ring buffer contains the following items at given indexes
      | 0    | 1    | 2    | 3    | 4    |
      | null | null | null | null | null |

  # RETRIEVE MISSING VALUES
  Scenario: Retrieve missing item dates when a lot of items are missing
    Given a ring buffer with size 5 is created with Date day: 10  month: 4 year: 2019
    And all items in the ring buffer have the same value as their slot index
    When Head moves up 3 times
    And attempt to add item with Integer 10 and Date day: 12  month: 4 year: 2019
    And Head moves up 2 times
    And attempt to add item with Integer 11 and Date day: 14  month: 4 year: 2019
    Then ring buffer contains the following items at given indexes
      | 0    | 1    | 2  | 3    | 4  |
      | null | null | 10 | null | 11 |
    And head is 0
    And headDate is day: 15 month: 4 year: 2019
    And the following item dates are missing
      | 11-04-2019 | 13-04-2019 | 16-04-2019 |