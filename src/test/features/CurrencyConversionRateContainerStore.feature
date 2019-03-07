Feature: currency conversion store

  # GET CONVERSION RATE
  Scenario: requested rate is succesfully requested
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency rate is requested from "EURO" to "USD" on "3-1-2019"
    Then the price "1.12" is returned

  Scenario: requested rate for date does not exist
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency rate is requested from 'EURO' to 'USD' on '2-2-2019'
    Then an empty optional is returned

  Scenario: requested rate for date does not exist (wrong currency code)
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency rate is requested from 'USD' to 'EURO' on '1-1-2019'
    Then an empty optional is returned

  # ADDING DATA

  Scenario: adding a new entry and retrieving currencies
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency conversion rate is added with the following properties
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-5-2019 | 1.14  |
    And currency conversion rate is added with the following properties
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | CHF              | USD            | 1-5-2019 | 1.14  |
    Then from currencies codes contain
      | EURO | CHF |
    Then to currencies codes contain
      | USD |
