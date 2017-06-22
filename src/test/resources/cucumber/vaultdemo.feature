@DemoOnly

Feature: Demo Logic

	Scenario: (Business Logic) Checking that Customers with opt out clauses does not exist in target views

		Given User is successfully connected to the database
		When I get "email" from the "customer" source table for records with "opt_out_email" = 1
		And I get "email" from the "email_campaign_t" consumption view
		Then records in the first set should not be present in the second set

	Scenario: (Quality Assurance) Checking for duplicate business key hash in the Hub Tables

	    Given User is successfully connected to the database
	    When I count the number of duplicate records in the "hash_customer_id" column of the "H_Customer" table
	    Then I should get exactly 0 results


	Scenario: (Quality Assurance) Checking that appropriate metadata exists on the Hub Tables

	    Given User is successfully connected to the database
	    When I list the columns in the "H_Customer" raw vault table
	    Then I should find "hash_customer_id", "load_dts" and "record_source" columns

	Scenario: (Reconciliation Logic) Checking that number of source customers is identical to the number of vault Customers

	    Given User is successfully connected to the database
	    When I get "customer_id" from the "customer" source table
	    And I get "hash_customer_id" from the "H_Customer" raw vault table
	    Then number of records returned should be identical


