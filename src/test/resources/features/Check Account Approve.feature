#Auto generated Octane revision tag
@BSPID1009REV0.2.0
Feature: Approve


@TSCID1023
	Scenario: ApproveScenario1
		Given numberOdd
		When one
		Then one

@TSCID1024
	Scenario: Approve
		Given numberEven12
		When three
		Then three

@TSCID1025
	Scenario Outline: Online
		Given all
		When  <param2>
		Then <param1>

	Examples:
		| param1 | param2 |
		| 2      | 4      |
		| 4      | 8      |
		| 2      | 2      |
		| 3      | 6      |