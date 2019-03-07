Feature: Ecb xml parser

  Scenario: invalid xml is parsed
    When an invalid xml is given to be parsed
    Then a empty optional is returned

  Scenario: valid xml is parsed
    When an valid xml is given to be parsed
    Then a list is returned