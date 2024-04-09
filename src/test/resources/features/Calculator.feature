Feature: Basic Calculator

  Scenario: Simple add operation
    When I "add" 1 and 1
    Then the result is 2

  Scenario: Combine basic operations
    When I "add" 10 and 11
    And I "multiply with" 2
    Then the result is 43

  Scenario: Combine more operations
    When I "add" 10 and 12
    And I "subtract" 1
    And I "multiply with" 2
    Then the result is 42