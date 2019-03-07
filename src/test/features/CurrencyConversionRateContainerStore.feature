Feature: currency conversion store

  # GET CONVERSION RATE
  Scenario: requested rate is succesfully requested
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency rate is requested from "EURO" to "USD" on "1-1-2019"
    Then the price "1.11" is returned

  Scenario: requested rate for date does not exist
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency rate is requested from 'EURO' to 'USD' on '2-1-2019'
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
  Scenario: updating an entry
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency conversion rate is added with the following properties
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.13  |
    And currency rate is requested from 'EURO' to 'USD' on '1-1-2019'
    Then the price "1.13" is returned
    And the amount of entries in the data store is 3
    And a warn log with text "A currency conversion rate has been changed" has been pushed to the console

  Scenario: adding a new entry
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency conversion rate is added with the following properties
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-5-2019 | 1.14  |
    And currency rate is requested from 'EURO' to 'USD' on '1-5-2019'
    Then the price "1.14" is returned

  Scenario: adding multiple entries
    Given currency conversion store contains the following entries
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-1-2019 | 1.11  |
      | EURO             | USD            | 3-1-2019 | 1.12  |
      | EURO             | USD            | 4-1-2019 | 1.13  |
    When currency conversion rates are added with the following properties
      | fromCurrencyCode | toCurrencyCode | date     | price |
      | EURO             | USD            | 1-5-2019 | 1.14  |
      | EURO             | USD            | 1-6-2019 | 1.15  |
    Then the amount of entries in the data store is 5