Feature: Ecb xml parser

  Scenario: invalid xml is parsed
    When an invalid xml is given to be parsed
    Then no containers are processed

  Scenario: an empty valid xml is parsed
    Given an empty xml
    And the following missing values
      | 2019-03-06 | 2019-03-07 | 2019-03-08 | 2019-03-09 | 2019-03-13 |
    When the xml is parsed
    Then no containers are processed

  Scenario: an inconsistent xml is parsed
    Given a valid xml with the following values
      | 2019-03-13 |     |       |
      |            | USD | 1.134 |
      |            | CHF | 1.145 |
      | 2019-03-08 |     |       |
      | 2019-03-07 |     |       |
      |            | USD | 1.123 |
      |            | CHF | 1.124 |
      |            | NZD | 1.125 |
      | 2019-03-06 |     |       |
      |            | USD | 1.122 |
    And the following missing values
      | 2019-03-06 | 2019-03-07 | 2019-03-08 | 2019-03-09 | 2019-03-13 |
    When the xml is parsed
    Then the following containers are processed
      | 2019-03-06 |     |       |
      |            | USD | 1.122 |
      | 2019-03-07 |     |       |
      |            | USD | 1.123 |
      |            | CHF | 1.124 |
      |            | NZD | 1.125 |
      | 2019-03-08 |     |       |
      | 2019-03-13 |     |       |
      |            | USD | 1.134 |
      |            | CHF | 1.145 |



