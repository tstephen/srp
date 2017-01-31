1. There is an intersect of ERIC data with the SDU data. Both conform to the Q&A 
model but are different.

2. Need to be able to tell if SDU data was imported from ERIC or not. Explore 
`Auditable` for this?

If not then need to have separation. Conclusions: store ERIC in distinct categories

  ERIC categories
    - Unnamed -> Organisation
    - Trust Profile 
    - Strategies and Policies
    - Contracted Out Services
    - Finance
    - Safety
    - Fire Safety
    
  SDU categories 
    - Organisation Information
    - Policy Information
    - Performance Information
    - Financial Data (Spend):
    - Resource Use: (Energy)
    - Waste:
    - Water:
    - Business Travel:
    - Other Travel:
    - Anaesthetic Gases:
    - Additional Breakdown Information:
    - Goods and Services Spend Profile
    - Goods and Services Carbon Profile by SDU
    - Goods and Services Spend Profile by eClass
    
3. ERIC survey definition
    
3. ERIC importer #57 

4. API / service layer 

Stories
    - (System) Create initial survey
        - Needs to be smart: create automatically if not present, but not if unchanged
    - (Analyst) List all surveys
    - (Analyst) View single survey (audit? status?)
    - ? (Analyst) Create new survey by modification of previous 

    - ??? (Analyst) List organisations (inc. most recent report year?)
    - (Reporter) List returns for organisation 
       - full data for all available or summary list then detail?
    - (Reporter) create return for period and organisation, 
      - NOTE ERIC data won't be available yet
    - (Reporter) Update single period return 
    - (Approver) Submit Report
    - (Approver) Submit Revised Report
    
    - (Analyst) Maintain Reference Data



4. Cucumber for API testing

5. Questionnaire UI mapped onto service layer  #46 
 Use ractive?
 
 
 