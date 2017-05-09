Feature: Sustainability Returns

#  Scenario: CRUD Question
#  
#  Scenario: CRUD Survey
#  
#  Scenario: Assign questions to survey and categorise
# 
#  Scenario: Import answers for single org, survey type and period
#  
#  Scenario: Import answers for many orgs but single survey type and period
 
  Scenario: List available surveys
    Given the user is logged in
     When a list of surveys is requested
     Then the list of 2 available surveys is returned

#  Scenario: Update current year's SDU survey to latest definition
#    Given the user is logged in
#     When the SDU survey for 2016-17 is uploaded
#     Then the response code is 204

  Scenario: Fetch current year's SDU survey
    Given the user is logged in
     When the survey SDU-2016-17 is requested
     Then the full survey for 2016-17 comprising 116 questions is returned
      And all questions have a label
  
  Scenario: List available returns
    Given the user is logged in
     When a list of returns is requested for organisation RDR
     Then the list of 2 available survey returns is provided including ERIC-2015-16-RDR Published and SDU-2016-17-RDR Draft

  Scenario: CRUD return for my organisation in a given period
    Given the user is logged in
     When the SDU 2016-17 return of RDR is requested
          # Different to no. of questions in survey by the calculated ones
     Then an SDU 2016-17 return containing 171 answers is created if necessary and returned for organisation RDR
     When the updated RDR return is uploaded
     Then the RDR return for 2016-17 is available with status 'Draft'
     #When the SDU 2016-17 return of RDR for period 2015-16 is requested
     #Then the RDR 2015-16 data for 2016-17 questions is returned

  Scenario: View treasury report
    Given the user is logged in
     When the user requests a treasury report for his organisation
     Then the RDR treasury report for 2016-17 is provided

  Scenario: Submit return
    Given the user is logged in
      And the current year's return is complete # TODO What constitutes complete?
     When the user submits the return
     Then it is no longer available for edit
