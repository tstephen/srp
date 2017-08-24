Feature: Sustainability Returns

  Scenario: CRUD Question
    Given the user is logged in
     When a list of questions is requested
     Then the list of 308 available questions is returned
     When a new question named "TEST" is added
     Then the new question's identifier is returned
     When the question's identifier is requested
     Then the question with name "TEST" is returned with type = "null", placeholder = "null" and hint = "null"
     When the question's type is set to "number", placeholder to "Enter a number" and hint to "This is a helpful hint"
      And the question's identifier is requested
     Then the question with name "TEST" is returned with type = "number", placeholder = "Enter a number" and hint = "This is a helpful hint"
     When a list of questions is requested
     Then the list of 309 available questions is returned
     When question is deleted
      And a list of questions is requested
     Then the list of 308 available questions is returned

#  Scenario: CRUD Survey
#
#  Scenario: Assign questions to survey and categorise
#
#  Scenario: Import answers for single org, survey type and period

  Scenario: List available surveys
    Given the user is logged in
     When a list of surveys is requested
     Then the list of 4 available surveys is returned

#  Scenario: Update current year's SDU survey to latest definition
#    Given the user is logged in
#     When the SDU survey for 2016-17 is uploaded
#     Then the response code is 204

  Scenario: Fetch current year's SDU survey
    Given the user is logged in
     When the survey SDU-2016-17 is requested
     Then the full survey for 2016-17 comprising 114 questions is returned
      And all questions have a label

  Scenario: List available returns
    Given the user is logged in
     When a list of returns is requested for organisation RDR
     Then the list of available survey returns provided includes ERIC-2015-16-RDR Published and SDU-2016-17-RDR Draft

  Scenario: CRUD return for my organisation in a given period
    Given the user is logged in
     When the SDU 2016-17 return of RDR is requested
          # Different to no. of questions in survey by the calculated and  imported ones
     Then an SDU 2016-17 return for RDR with correct header information is returned
      And the return contains pro-forma answers for 4 years (at least)
     When the RDR return is updated with ELEC_USED, GAS_USED and WATER_VOL and uploaded
     Then the RDR return for 2016-17 is available with status 'Draft' and audit data is set
      And calculations for Energy emissions are present for 4 years (at least)
      And calculations for SDU Carbon profile are present for 4 years (at least)
     When historic ERIC data is imported for RDR 2016-17
     Then 4 years answers exist for ELEC_USED, GAS_USED and WATER_VOL

#  Scenario: Submit return
#    Given the user is logged in
#      And the SDU 2016-17 return of RDR is complete
#     When the user submits the return
#     Then the RDR return for 2016-17 is available with status 'Submitted' and audit data is set
#      And it is no longer available for edit
#     When the user restates the return
#     Then the RDR return for 2016-17 is available with status 'Draft' and audit data is set
