#Auto generated Octane revision tag
@BSPID2001REV0.2.0

Feature: Validate manual Bike lookup API in Guidewire Customer Engage 10

@TSCID2001
  Scenario Outline: Validation of manual Bike lookup API in Guidewire Customer Engage 10 with In-valid Product code
    Given user constructs request  with "<Request Type>" and with parameters "<Product code>" "<Manufacturer>" and "<Model>"
    When  user retrieves the response  for manual bike lookup
    Then  validate the manual Bike lookup API response error is similar to Guidewire previous version response
    @Regression
    Examples:
      |  Request Type   | Product code   |Manufacturer|  Model   |
      |  ProductCode_1    |MotorCyclu_Ixt_1  |            |          |
    Examples:
      |  Request Type   | Product code   |Manufacturer|  Model   |
      |  ProductCode_2    |MotorCyclu_Ixt_2  |            |          |

@TSCID2002
  Scenario Outline: Validation of manual Bike lookup API in Guidewire Customer Engage 10 with In-valid Product code and Valid manufacturer
    Given user constructs request  with "<Request Type>" and with parameters "<Product code>" "<Manufacturer>" and "<Model>"
    When  user retrieves the response  for manual bike lookup
    Then  validate the manual Bike lookup API response error is similar to Guidewire previous version response
    And nothing happened
    @Regression
    Examples:
      |  Request Type                   | Product code   |Manufacturer   |  Model   |
      |  ProductCodewithManufacturer_1    |MotorCycle_Ixt_1  | APRILIA_1       |          |
    Examples:
      |  Request Type                   | Product code   |Manufacturer   |  Model   |
      |  ProductCodewithManufacturer_2    |MotorCycle_Ixt_2  | APRILIA_2       |          |


