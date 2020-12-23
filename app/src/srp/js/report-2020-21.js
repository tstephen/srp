/*******************************************************************************
 * Copyright 2015-2020 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    labels: {
      '2017-18': '2019-20 (%)',
      '2016-17': '2018-19 (%)',
      '2017-18': '2017-18 (%)',
      '2016-17': '2016-17 (%)',
      '3RD_PARTY_ADDITIONAL_PCT': 'Third party additional %',
      'ADAPTATION_PERF': 'Commentary on adaptation performance',
      'ADAPTATION_PLAN_INC': 'Board approved Adaptation Plan ()',
      'ADAPTATION_PLAN_INC': 'Do your board approved plans address the potential need to adapt the delivery of your organisation&apos;s activities and organisation&apos;s infrastructure as a result of climate change and adverse weather events?',
      'AMI': 'Aqueous mist inhalers',
      'ANAESTHETIC_GASES_CO2E': 'Anaesthetic Gases',
      'BACKLOG_MAINTENANCE_VAL': 'Investment to reduce backlog maintenance (£)',
      'BEDDING_LINEN_AND_TEXTILES': 'Bedding Linen & Textiles',
      'BEDDING_LINEN_AND_TEXTILES_CO2E': 'Bedding Linen & Textiles',
      'BENCHMARK_PERF': 'Commentary on benchmark performance',
      'BIZ_MILEAGE_AIR_CO2E': 'Business Mileage - Air',
      'BIZ_MILEAGE': 'Business travel and fleet',
      'BIZ_MILEAGE_CO2E': 'Business travel and fleet',
      'BIZ_MILEAGE_RAIL_CO2E': 'Business Mileage - Rail',
      'BIZ_MILEAGE_ROAD': 'Business Mileage - Road',
      'BIZ_MILEAGE_ROAD_CO2E': 'Business Mileage - Road',
      'BIZ_SVCS_CO2E': 'Business services',
      'BIZ_SVCS_SPEND': 'Business services',
      'BLDG_AND_ENG_PROD_AND_SVCS_CO2E': 'Building & Engineering Products & Services',
      'BOARD_LEAD_FOR_SUSTAINABILITY': 'Is there a Board Level lead for Sustainability on your Board?',
      'BOARD_SUSTAINABILITY_AS_RISK': 'Does your board consider sustainability issues as part of its risk management process?',
      'BUILDING_AND_ENGINEERING_PRODUCTS_AND_SERVICES': 'Building & Engineering Products & Services',
      'CAPITAL_CO2E': 'Capital spending',
      'CAPITAL_EQUIPMENT': 'Capital investment for equipment (£)',
      'CAPITAL_IMPROVING_EXISTING': 'Capital investment for improving existing buildings (£)',
      'CAPITAL_NEW_BUILD': 'Capital investment for new build (£)',
      'CAPITAL_SPEND': 'Capital',
      'CAPITAL_SPEND': 'Capital spend',
      'CAR_DIESEL_USED': 'Diesel used by fleet (litres)',
      'CAR_HYBRID_FUEL_USED': 'Hybrid elecricity used by fleet (kWh)',
      'CAR_PETROL_USED': 'Petrol used by fleet (litres)',
      'CAR_MILES_DIESEL':'Fleet mileage in diesel car',
      'OWNED_LEASED_CARBON_MILES':'Fleet mileage included in leased car',
      'CAR_MILES_PETROL':'Fleet mileage in petrol car',
      'CAR_MILES_UNKNOWN_FUEL':'Fleet mileage in car of unknown fuel',
      'CAR_MILES_DIESEL_CO2E':'Emissions resulting from diesel car mileage',
      'CAR_MILES_PETROL_CO2E':'Emissions resulting from petrol car mileage',
      'CARBON_REDUCTION_BASELINE_USED': 'Does your organisation use a baseline year?',
      'CARBON_REDUCTION_BASE_YEAR': 'Which year is it?',
      'CARBON_REDUCTION_DEADLINE_YEAR': 'What is the deadline year for this target?',
      'CARBON_REDUCTION_TARGET': 'Does your organisation have its own carbon reduction target?',
      'CARBON_REDUCTION_TARGET_PCT': 'What is the percentage reduction target?',
      'CATERING_CO2E': 'Food and catering',
      'CATERING_SPEND': 'Food and catering',
      'CCGS_SERVED': 'We provide services to the following CCGs',
      'CHEM_AND_GAS_CO2E': 'Manufactured fuels, chemicals and gases',
      'CHEM_AND_GAS_SPEND': 'Manufactured fuels, chemicals and gases',
      'CHEMICALS_AND_REAGENTS': 'Chemicals & Reagents',
      'CHEMICALS_AND_REAGENTS_CO2E': 'Chemicals & Reagents',
      'CHEMS_AND_GASES_CO2E': 'Manufactured fuels chemicals and gases',
      'CHP_ELECTRICAL_OUTPUT': 'Total electrical energy output of the CHP system/s',
      'CITIZEN_CO2E': 'Citizen',
      'COAL_CO2E': 'Coal',
      'COAL_USED': 'Coal Consumed',
      'COMMISSIONING_CO2E': 'Commissioning',
      'COMMISSIONING_CO2E_PCT': 'Commissioning',
      'COMMISSIONING_CO2E_PER_POUND': 'Commissioning (tCO\u2082e)',
      'COMMISSIONING_REGION': 'Commissioning Region',
      'COMMISSIONING_SPEND': 'Commissioning',
      'COMPOSTED_WEIGHT': 'Composted',
      'COMMUNITY_CO2E_PCT': 'Community',
      'CONSTRUCTION_CO2E': 'Construction',
      'CONSTRUCTION_SPEND': 'Construction',
      'CONSULTING_SVCS_AND_EXPENSES_CO2E': 'Staff & Patient Consulting Services & Expenses',
      'CONTRACTING_OUT_PCT': 'Percentage of hard FM (estates) and soft FM (hotel services) contracted out (%)',
      'CONTRACTING_OUT_VAL': 'Value of contracted out services (£)',
      'CORE_CO2E': 'Core emissions',
      'CORE_CO2E_PCT': 'Core emissions',
      'DENTAL_AND_OPTICAL_EQUIPMENT': 'Dental & Optical Equipment',
      'DENTAL_AND_OPTICAL_EQUIPT_CO2E': 'Dental & Optical Equipment',
      'DESFLURANE_CO2E': 'Desflurane - liquid',
      'DESFLURANE': 'Desflurane - liquid',
      'DOMESTIC_AIR_MILES': 'Air - Domestic',
      'DPI': 'Dry powder inhalers',
      'DRESSINGS_CO2E': 'Dressings',
      'DRESSINGS': 'Dressings',
      'ECLASS_USER': 'Do you use eClass for procurement?',
      'ELEC_CO2E': 'Electricity',
      'ELEC_EXPORTED': 'Total exported electricity',
      'ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E': 'Green tariff',
      'ELEC_NON_RENEWABLE_3RD_PARTY_CO2E': 'Third party',
      'ELEC_OWNED_RENEWABLE_USED': 'Green electricity consumed',
      'ELEC_OWNED_RENEWABLE_USED_SDU': 'Green electricity consumed',
      'ELEC_RENEWABLE_CO2E': 'Green electricity',
      'ELEC_RENEWABLE': 'Green electricity',
      'ELEC_TOTAL_RENEWABLE_USED': 'Green electricity',
      'ELEC_USED_3RD_PTY_RENEWABLE': 'Electricity Consumed - third party owned renewable',
      'ELEC_USED': 'Electricity Consumed',
      'ELEC_USED_GREEN_TARIFF': 'Green Tariff Electricity Used',
      'ELEC_USED_LOCAL': 'Electricity Consumed - local',
      'ELEC_USED_RENEWABLE': 'Electricity Consumed - renewable',
      'ENERGY': 'This is based on your response to the questions in the Electricity and Thermal sections',
      'ENERGY_CO2E_PCT': 'Energy',
      'ENERGY_CO2E_PER_POUND': 'Energy (tCO\u2082e)',
      'ENERGY_CTXT': 'Energy Context',
      'ENERGY_PERF': 'Energy Performance',
      'ESTATES_DEV_STRATEGY': 'Estates Development Strategy ()',
      'EXPORTED_THERMAL_ENERGY_CO2E': 'Exported thermal',
      'EXPORTED_THERMAL_ENERGY': 'Exported thermal energy',
      'FLOOR_AREA': 'Total gross internal floor space',
      'FREIGHT_CO2E': 'Freight transport',
      'FREIGHT_SPEND': 'Freight transport',
      'FUEL_LIGHT_POWER_WATER_CO2E': 'Fuel Light Power Water',
      'FUEL_WTT': 'Fuel Well to Tank',
      'FURNITURE_FITTINGS_CO2E': 'Furniture Fittings',
      'FURNITURE_FITTINGS': 'Furniture Fittings',
      'GARDENING_AND_FARMING_CO2E': 'Gardening & Farming',
      'GARDENING_AND_FARMING': 'Gardening & Farming',
      'GAS_CO2E': 'Gas',
      'GAS_USED': 'Gas Consumed',
      'GCC_USER': 'Does your organisation use the Sustainable Development Assessment Tool (SDAT) tool?',
      'GREEN_TARIFF_ADDITIONAL_PCT': 'Percentage of green tariff supply proven as additional',
      'HARDWARE_CROCKERY_CO2E': 'Hardware & Crockery',
      'HARDWARE_CROCKERY': 'Hardware Crockery',
      'HEALTH_IMPACT_OF_TRAVEL': 'Health impacts of travel and transport associated with provider activities',
      'HEALTHY_TRANSPORT_PLAN': 'Does your organisation have a healthy or green transport plan?',
      'HIGH_TEMP_DISPOSAL_WEIGHT': 'High Temperature Disposal Waste',
      'HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT': 'High Temperature Disposal Waste with Energy Recovery',
      'HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E': 'Hotel Services Equipment Materials & Services',
      'HOTEL_SERVICES_EQUIPMENT_MATERIALS_AND_SERVICES': 'Hotel Services Equipment Materials & Services',
      'HOT_WATER_CO2E': 'Hot water',
      'HOT_WATER_USED': 'Hot Water Consumed',
      'ICT_CO2E': 'Information and communication technologies',
      'ICT_SPEND': 'Information and communication technologies',
      'IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E': 'Diagnostic Imaging & Radiotherapy Equipment & Services',
      'INCINERATION_CO2E': 'Incineration',
      'INCINERATION_WEIGHT': 'Incineration disposal weight',
      'INCOME_CATERING': 'Income from services provided to other organisations - catering (£)',
      'INCOME_LAUNDRY': 'Income from services provided to other organisations - laundry and linen (£)',
      'INCOME_OTHER': 'Income from services provided to other organisations - other (£)',
      'ISOFLURANE': 'Isoflurane - liquid',
      'ISOFLURANE_CO2E': 'Isoflurane - liquid',
      'LAB_EQUIPT_AND_SVCS_CO2E': 'Laboratory Equipment & Services',
      'LABORATORY_EQUIPMENT_AND_SERVICES': 'Laboratory Equipment & Services',
      'LANDFILL_CO2E': 'Landfill',
      'LANDFILL_WEIGHT': 'Landfill disposal weight',
      'LAST_GCC_DATE': 'If your organisation uses the <a href="http://www.sduhealth.org.uk/sdat/">Sustainable Development Assessment Tool (SDAT)</a> tool when was your last self assessment?',
      'LAST_GCC_SCORE': 'What was your score?',
      'LEASED_ASSETS_ENERGY_USE': 'Leased Assets Energy Use (Upstream - Gas, Coal & Electricity)',
      'LEASED_FLEET_TRAVEL': 'Non-organisation Owned Fleet/Pool Road Travel (Leased, hired etc.)',
      'LONG_HAUL_AIR_MILES': 'Air - Long Haul International Flights',
      'MDI': 'Metered dose inhalers',
      'MEDICAL_AND_SURGICAL_EQUIPMENT': 'Medical & Surgical Equipment',
      'MEDICAL_AND_SURGICAL_EQUIPT_CO2E': 'Medical & Surgical Equipment',
      'MED_INSTR_CO2E': 'Medical instruments / equipment',
      'MED_INSTR_SPEND': 'Medical instruments',
      'MOD_SLAVERY': 'If your organisation has a Modern Slavery statement, what is it?',
      'NET_ELEC_CO2E': 'Electricity (net of any exports)',
      'NET_THERMAL_ENERGY_CO2E': 'Thermal energy (net of any exports)',
      'NITROUS_OXIDE': 'Nitrous oxide - gas',
      'NITROUS_OXIDE_CO2E': 'Nitrous oxide',
      'NO_ACUTE_SITES': 'Number of sites - General acute hospital (No.)',
      'OCCUPIED_BEDS': 'Total no. occupied beds',
      'NO_COMMUNITY_HOSPITAL_SITES': 'Number of sites - Community hospital (with inpatient beds) (No.)',
      'NO_EVAC_INJURIES': 'Number of patients sustaining injuries during evacuation (No.)',
      'NO_FALSE_ALARMS': 'False alarms (No.)',
      'NO_FIRE_DEATHS': 'Number of deaths resulting from fire(s) (No.)',
      'NO_FIRE_INJURIES': 'Number of people injured resulting from fire(s) (No.)',
      'NO_FIRES': 'Fires recorded (No.)',
      'NO_FM_CLINICAL_INCIDENTS': 'Clinical service incidents caused by estates and infrastructure failure (No.)',
      'NO_FM_INCIDENTS': 'Estates and facilities related incidents (No.)',
      'NO_LD_SITES': 'Number of sites - Learning Disabilities (No.)',
      'NO_MENTAL_HEALTH_LD_SITES': 'Number of sites - Mental Health and Learning Disabilities (No.)',
      'NO_MENTAL_HEALTH_SITES': 'Number of sites - Mental Health (including Specialist services) (No.)',
      'NO_MIXED_SITES': 'Number of sites - Mixed service hospital (No.)',
      'NON_BURN_WEIGHT': 'Non Burn Treatment Disposal Waste',
      'NON_EMERGENCY_TRANSPORT_VAL': 'Non-emergency patient transport (£)',
      'NON_PAY_SPEND': 'Non-pay spend',
      'NO_OTHER_INPATIENT_SITES': 'Number of sites - Other in-patient (No.)',
      'NO_OTHER_SITES': 'Number of sites - Unreported sites (No.)',
      'NO_OUTPATIENT_SITES': 'Number of sites - Non inpatient (No.)',
      'NO_PATIENT_CONTACTS': 'Total patient contacts',
      'NO_PLASTICS_CUPS': 'Number of plastic cups purchased',
      'NO_PLASTIC_LINED_COFFEE_CUPS':'Number of plastic-lined cups purchased',
      'NO_PLASTIC_PLATES':'Number of plastic plates purchased',
      'NO_PLASTIC_CUTLERY':'Number of items of plastic cutlery purchased',
      'NO_PLASTIC_STRAWS':'Number of plastic straws purchased',
      'NO_PLASTIC_STIRRERS':'Number of plastic stirrers purchased',
      'NO_PLASTIC_FOOD_CONTAINERS':'Number of plastic food containers purchased',
      'NO_RIDDOR_INCIDENTS': 'RIDDOR incidents (No.)',
      'NO_SPECIALIST_SITES': 'Number of sites - Specialist hospital (acute only) (No.)',
      'NO_STAFF': 'Total no. of staff employed',
      'NO_SUPPORT_SITES': 'Number of sites - Support facilities (No.)',
      'NON_BURN_WEIGHT': 'Non Burn Treatment Disposal Waste',
      'NON_EMERGENCY_TRANSPORT_VAL': 'Non-emergency patient transport (£)',
      'NON_PAY_SPEND': 'Non-pay spend',
      'NOX_PPM':'NOx pollution produced by your organisation&apos;s fleet (tonnes)',
      'OFFICE_EQUIPMENT_TELECOMMS_COMPUTERS_AND_STATIONERY': 'Office Equipment Telecomms Computers & Stationery',
      'OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E': 'Office Equipment Telecomms Computers & Stationery',
      'OIL_CO2E': 'Oil',
      'OIL_USED': 'Oil Consumed',
      'OP_EX': 'Operating Expenditure',
      'ORG_CODE': 'Organisation code e.g. RAA',
      'ORG_NAME': 'Name of organisation',
      'ORG_NICKNAME': 'Abbreviation or nick name of organisation used',
      'ORG_TYPE': 'Organisation type',
      'OTHER_AOI_CORE': 'Other AoI Core',
      'OTHER_AOI_COMMISSIONING': 'Other AoI Commissioning',
      'OTHER_AOI_PROCUREMENT': 'Other AoI Procurement',
      'OTHER_AOI_COMMUNITY': 'Other AoI Community',
      'OTHER_MANUFACTURED_CO2E': 'Other manufactured goods',
      'OTHER_MANUFACTURED_SPEND': 'Other manufactured goods',
      'OTHER_PROCUREMENT_CO2E': 'Other procurement',
      'OTHER_RECOVERY_CO2E': 'Recovery',
      'OTHER_RECOVERY_WEIGHT': 'Other recovery weight',
      'OTHER_SPEND': 'Other procurement',
      'OWNED_BUILDINGS_COAL': 'Coal',
      'OWNED_BUILDINGS_GAS': 'Gas',
      'OWNED_BUILDINGS_OIL': 'Oil',
      'OWNED_BUILDINGS': 'Owned Buildings',
      'OWNED_FLEET_TRAVEL_CO2E': 'Business and fleet estimate',
      'OWNED_FLEET_TRAVEL': 'Organisation Owned Fleet/Pool Road Travel',
      'OWNED_VEHICLES': 'Owned vehicles',
      'PAPER': 'This is based on your response to Spend question 5 and Waste question 3',
      'PAPER_CO2E': 'Paper emissions (tCO\u2082e)',
      'PAPER_AND_CARD_CO2E': 'Paper products',
      'PAPER_AND_CARD_SPEND': 'Paper products spend',
      'PAPER_SPEND': 'Paper spend (&pound;)',
      'PAPER_USED': 'Paper products used (tonnes)',
      'PARTICULATES_PPM': 'Fine particulate matter (PM2.5) produced by your fleet (tonnes)',
      'PATIENT_AND_VISITOR_MILEAGE_CO2E': 'Patient and visitor travel',
      'PATIENT_AND_VISITOR_MILEAGE': 'Patient and visitor travel',
      'PATIENT_CONTACT_MEASURE': 'Define the measure you have used for patient contacts',
      'PATIENT_MILEAGE': 'Patient Transport Mileage',
      'PATIENTS_APPLIANCES_CO2E': 'Patients Appliances',
      'PATIENTS_APPLIANCES': 'Patients Appliances',
      'PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E': 'Patients Clothing & Footwear',
      'PATIENTS_CLOTHING_AND_FOOTWEAR': 'Patients Clothing & Footwear',
      'PFA_ACTION_PLAN': 'NHS Premises and Facilities Assurance - action plan (Select)',
      'PFA_ACTION_PLAN_VAL': 'Cost to meet NHS Premises and Facilities Assurance action plan (£)',
      'PFA_ASSESSMENT': 'NHS Premises and Facilities Assurance - Assessment/Approval (Select)',
      'PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E': 'Pharmaceuticals Blood Products & Medical Gases',
      'PHARMACEUTICALS_BLOOD_PRODUCTS_AND_MEDICAL_GASES': 'Pharmaceuticals Blood Products & Medical Gases',
      'PHARMA_CO2E': 'Pharmaceuticals',
      'PHARMA_CO2E': 'Pharmaceuticals',
      'PHARMA_SPEND': 'Pharmaceuticals',
      'PHARMA_SPEND': 'Pharmaceuticals',
      'POPULATION': 'Registered population or population served',
      'PORTABLE_NITROUS_OXIDE_MIX_CO2E': 'Portable Nitrous Oxide (tCO\u2082e)',
      'PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E': 'Portable Nitrous Oxide (maternity) (tCO\u2082e)',
      'PORTABLE_NITROUS_OXIDE_MIX_MATERNITY': 'Maternity Manifold nitrous oxide and oxygen 50/50 split - gas',
      'PORTABLE_NITROUS_OXIDE_MIX': 'Portable nitrous oxide and oxygen 50/50 split - gas',
      'PRIVATE_SECTOR': 'Private Sector investment (£)',
      'PROC_ENV_ASSESSMENT': 'Do your commissioning, tendering and procurement processes include an assessment of the environmental impacts?',
      'PROC_SOC_ASSESSMENT': 'Do your commissioning, tendering and procurement processes include an assessment of the social impacts?',
      'PROC_SUPPLIER_SUSTAINABILITY': 'Do your commissioning, tendering and procurement processes include consideration of the suppliers&apos; sustainability policies?',
      'PROCUREMENT_2017_CO2E_PCT': 'Procurement',
      'PROCUREMENT_CO2E_PCT': 'Procurement',
      'PROCUREMENT_CO2E_PER_POUND': 'Procurement (tCO\u2082e)',
      'PROCUREMENT_CO2E': 'Procurement',
      'PROMOTE_HEALTHY_TRAVEL': 'Promote healthy travel',
      'PROMOTE_SUSTAINABILITY_TO_STAFF': 'Does your organisation promote sustainability to its employees?',
      'PROVISIONS_CO2E': 'Provisions - Food & Catering',
      'PROVISIONS': 'Provisions',
      'PURCHASED_HEALTHCARE_CO2E': 'Purchased Healthcare',
      'PURCHASED_HEALTHCARE': 'Purchased Healthcare',
      'QUANT_ES_IMPACTS': 'What are the qualitative or quantified social and environmental impacts?',
      'QUANT_ESIMPACTS': 'What are the qualitative or quantified social and environmental impacts?',
      'QUANT_TRAVEL_IMPACTS': 'Does your organisation quantify the health impacts of travel and transport?',
      'RAIL_MILES': 'Rail',
      'REC_EQUIPT_AND_SOUVENIRS_CO2E': 'Recreational Equipment & Souvenirs',
      'RECREATIONAL_EQUIPMENT_AND_SOUVENIRS': 'Recreational Equipment & Souvenirs',
      'RECYCLING_CO2E': 'Recycling',
      'RECYCLING_WEIGHT': 'Waste recycling weight',
      'RENEWABLE_USED': 'Non-fossil fuel Consumed - renewable',
      'REUSE_WEIGHT': 'Preparing for re-use',
      'SCOPE_1': 'Scope 1',
      'SCOPE_2': 'Scope 2',
      'SCOPE_3_BIOMASS': 'Biomass',
      'SCOPE_3_ENERGY_WTT': 'Energy Transmission and Well to Tank',
      'SCOPE_3': 'Scope 3',
      'SCOPE_3_TRAVEL': 'Travel',
      'SCOPE_3_WASTE': 'Waste',
      'SCOPE_3_WATER': 'Water',
      'SCOPE_ALL': 'Overall Progress',
      'SDMP_BOARD_REVIEW_WITHIN_12_MONTHS': 'Was the SDMP reviewed or approved by the board in the last 12 months?',
      'SDMP_CRMP': 'Does your organisation have a current* Board-approved Sustainable Development Management Plan (SDMP) or Carbon Reduction Management Plan (CRMP)?',
      'SDMP_MISSION_STATEMENT': 'If your SDMP has a sustainability mission statement what is it?',
      'SEVOFLURANE_CO2E': 'Sevoflurane (tCO\u2082e)',
      'SEVOFLURANE': 'Sevoflurane - liquid',
      'SHORT_HAUL_AIR_MILES': 'Air - Short Haul International Flights',
      'SI_TOTAL': 'Total Investment Recorded',
      'SIA': 'Does your business case process include a Sustainability Impact Assessment?',
      'STAFF_AND_PATIENT_CONSULTING_SERVICES_AND_EXPENSES': 'Staff & Patient Consulting Services & Expenses',
      'STAFF_CLOTHING_CO2E': 'Staff Clothing',
      'STAFF_CLOTHING': 'Staff Clothing',
      'STAFF_COMMUTE_MILES_CO2E': 'Staff commute',
      'STAFF_COMMUTE_MILES_PP': 'Staff commute - Average annual distance travelled by road to work',
      'STAFF_COMMUTE_MILES_TOTAL': 'Staff commute',
      'STEAM_CO2E': 'Steam',
      'STEAM_USED': 'Steam Consumed',
      'STRATEGIC_SUSTAINABILITY_PARTNERSHIPS': 'Are you in a strategic partnership with other organisations on sustainability?',
      'STRATEGIC_SUSTAINABILITY_PARTNERS': 'Who are they?',
      'SV_SOCIAL':'Social Value due to social activities',
      'SI_SOCIAL':'Reported investment to provided additional social activities',
      'SV_JOBS':'Social Value arising from additional jobs',
      'SI_JOBS':'Reported investment to create additional jobs',
      'SV_GROWTH':'Social Value arising from additional growth',
      'SI_GROWTH':'Reported investment to create additional growth',
      'SV_ENVIRONMENT':'Social Value due to environmental measures',
      'SI_ENVIRONMENT':'Reported investment on environmental measures',
      'SV_INNOVATION':'Social Value arising from innovation',
      'SI_INNOVATION':'Reported investment to stimulate innovation',
      'SV_TOTAL':'Total Social Value',
      'SI_TOTAL':'Total reported investment',
      'SV_CTXT':'Explanation and context for Social Value responses',
      'THIRD_PARTY_ADDITIONAL_PCT': 'Percentage of third party owned supply proven as additional',
      'TOTAL_CO2E_BY_BEDS': 'Total',
      'TOTAL_CO2E_BY_FLOOR': 'Total',
      'TOTAL_CO2E_BY_OPEX': 'Total',
      'TOTAL_CO2E_BY_PATIENT_CONTACT': 'Total',
      'TOTAL_CO2E_BY_POP': 'Total',
      'TOTAL_CO2E_BY_WTE': 'Total',
      'TOTAL_CO2E': 'Total',
      'TOTAL_CORE_CO2E': 'Core emissions',
      'TOTAL_CORE_CO2E': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_BEDS': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_FLOOR': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_OPEX': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_PATIENT_CONTACT': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_POP': 'Core emissions',
      'TOTAL_CORE_CO2E_BY_WTE': 'Core emissions',
      'TOTAL_COMMISSIONING_CO2E': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_BEDS': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_FLOOR': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_OPEX': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_PATIENT_CONTACT': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_POP': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_WTE': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_BEDS': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_FLOOR': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_OPEX': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_PATIENT_CONTACT': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_POP': 'Commissioning',
      'TOTAL_COMMISSIONING_CO2E_BY_WTE': 'Commissioning',
      'TOTAL_COMMUNITY_CO2E': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_BEDS': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_FLOOR': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_OPEX': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_PATIENT_CONTACT': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_POP': 'Community',
      'TOTAL_COMMUNITY_CO2E_BY_WTE': 'Community',
      'TOTAL_EMPLOYEES': 'Total Employees in Organisation',
      'TOTAL_ENERGY_BY_WTE': 'Total Energy Used By WTE',
      'TOTAL_ENERGY_CO2E_BY_BEDS': 'Energy',
      'TOTAL_ENERGY_CO2E_BY_FLOOR': 'Energy',
      'TOTAL_ENERGY_CO2E_BY_OPEX': 'Energy',
      'TOTAL_ENERGY_CO2E_BY_PATIENT_CONTACT': 'Energy',
      'TOTAL_ENERGY_CO2E_BY_POP': 'Energy',
      'TOTAL_ENERGY_CO2E_BY_WTE': 'Energy',
      'TOTAL_ENERGY_CO2E': 'Energy',
      'TOTAL_ENERGY_COST': 'Total Energy Cost (all energy supplies)',
      'TOTAL_ENERGY': 'Total Energy Used',
      'TOTAL_PROCUREMENT_CO2E_BY_BEDS': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E_BY_FLOOR': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E_BY_OPEX': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E_BY_PATIENT_CONTACT': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E_BY_POP': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E_BY_WTE': 'Procurement',
      'TOTAL_PROCUREMENT_CO2E': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_BEDS': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_FLOOR': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_OPEX': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_PATIENT_CONTACT': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_POP': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E_BY_WTE': 'Procurement',
      'TOTAL_PROCUREMENT_2017_CO2E': 'Procurement',
      'TOTAL': 'Total',
      'TOTAL_TRAVEL_CO2E_BY_BEDS': 'Travel',
      'TOTAL_TRAVEL_CO2E_BY_FLOOR': 'Travel',
      'TOTAL_TRAVEL_CO2E_BY_OPEX': 'Travel',
      'TOTAL_TRAVEL_CO2E_BY_PATIENT_CONTACT': 'Travel',
      'TOTAL_TRAVEL_CO2E_BY_POP': 'Travel',
      'TOTAL_TRAVEL_CO2E_BY_WTE': 'Travel',
      'TRAJECTORY_PERF': 'Trajectory performance',
      'TRANSPORTATION_CO2E': 'Transportation',
      'TRAVEL': 'This is based on your response to the questions in the Business and Other Travel sections',
      'TRAVEL_CO2E_PCT': 'Travel',
      'TRAVEL_CO2E_PER_POUND': 'Travel (tCO\u2082e)',
      'TRAVEL_CO2E': 'Travel',
      'TRAVEL_CTXT': 'Travel context',
      'TRAVEL_PERF': 'Travel performance',
      'TRAVEL_SPEND': 'Business travel and fleet',
      'VEHICLES_NO': 'Number of vehicles',
      'VEHICLES_LEV_PCT':'Percentage of low emission vehicles',
      'VEHICLES_ULEV_PCT':'Percentage of ultra-low emission vehicles',
      'VISITOR_MILEAGE': 'Patient and Visitor Travel',
      'WASTE': 'This is based on your response to the questions in the Waste section',
      'WASTE_AND_WATER_CO2E': 'Waste and Water',
      'WASTE_AND_WATER': 'Waste and Water',
      'WASTE_CO2E': 'Waste products and recycling',
      'WASTE_CTXT': 'Waste context',
      'WASTE_PERF': 'Waste performance',
      'WASTE_RECYLING_COST': 'Waste recycling, recovery and preparing for re-use cost',
      'WASTE_WATER': 'Waste water volume (m\u00B3)',
      'WATER': 'This is based on your response to the questions in the Water section',
      'WATER_AND_SEWAGE_COST': 'Water and sewage cost (£)',
      'WATER_CO2E': 'Water related emissions',
      'WATER_COST': 'Water cost (£)',
      'WATER_CTXT': 'Water context',
      'WATER_PERF': 'Water performance',
      'WATER_TREATMENT_CO2E': 'Water treatment related emissions',
      'WATER_VOL': 'Water volume (m\u00B3)',
      'WATER_VOL_BY_WTE': 'Water volume (m\u00B3)',
      'WEEE_WEIGHT': 'Waste Electrical and Electronic Equipment (WEEE)',
      'WOOD_CHIPS_CO2E': 'Wood chips',
      'WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION': 'Non-fossil fuel Consumed - renewable (wood chips)',
      'WOOD_CHIPS_WTT_CO2E': 'Wood chips WTT',
      'WOOD_LOGS_CO2E': 'Wood logs',
      'WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION': 'Non-fossil fuel Consumed - renewable (wood logs)',
      'WOOD_LOGS_WTT_CO2E': 'Wood logs WTT',
      'WOOD_PELLETS_CO2E': 'Wood pellets',
      'WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION': 'Non-fossil fuel Consumed - renewable (wood pellets)',
      'WOOD_PELLETS_WTT_CO2E': 'Wood pellets WTT',
      'FLEET_ROAD_MILES': 'Business miles road (owned fleet)',
      'BIZ_MILEAGE_ROAD_COST': 'Cost business miles road (non-owned fleet)',
      'OWNED_LEASED_LOW_CARBON_MILES': '',
      'BUS_MILES': 'Business miles by bus',
      'TAXI_MILES': 'Business miles by taxi',
      'GROUNDS_COSTS': 'Grounds and gardens maintenance (£)',
      'LANDFILL_COST': 'Landfill disposal cost (£)',
      'ELECTRO_BIO_MEDICAL_EQUIPT': 'Electro Bio Medical Equipment maintenance cost (£)',
      'SITE_UNUSED_FOOTPRINT': 'Land area not delivering services (Hectares)',
      'ISOLATION_ROOMS': 'Isolation rooms (No.)',
      'WASTE_RECYLING_WEIGHT': 'Waste reycling cost (£)',
      'WASTE_INCINERATION_COST': 'Incineration disposal cost (£)',
      'WASTE_INCINERATION_WEIGHT': 'Incineration disposal volume (Tonnes)',
      'OTHER_RECOVERY_COST': 'Other recovery cost (£)',
      'COST_SAFETY_RISK': 'Cost to eradicate Safety related Critical Infrastructure Risk (£)',
      'COST_COMPLIANCE_RISK': 'Cost to eradicate non-compliance related Critical Infrastructure Risk (£)',
      'COST_CONTINUITY_RISK': 'Cost to eradicate continuity related Critical Infrastructure Risk (£)',
      'CHP_SIZE': 'CHP unit/s size (Watts)',
      'CHP_EFFICIENCY': 'CHP unit/s efficiency (%)',
      'ELEC_PEAK_LOAD': 'Peak electrical load (MW)',
      'ELEC_MAX_LOAD': 'Maximum electrical load (MW)'
    },
    narrativeCtxtPrompt: 'Please insert a commentary on contextual information e.g. projects, initiatives etc. related to this area.',
    narrativePerfPrompt: 'Please click to insert a commentary on your performance in this area.',
    orgAnswerNames: ['PROVIDER1_COMMISSIONED','PROVIDER2_COMMISSIONED','PROVIDER3_COMMISSIONED',
        'PROVIDER4_COMMISSIONED','PROVIDER5_COMMISSIONED','PROVIDER6_COMMISSIONED',
        'PROVIDER7_COMMISSIONED','PROVIDER8_COMMISSIONED','CCG1_SERVED',
        'CCG2_SERVED','CCG3_SERVED','CCG4_SERVED','CCG5_SERVED','CCG6_SERVED','CCG7_SERVED','CCG8_SERVED'],
    requiredAnswers: ['ORG_CODE', 'ORG_NAME', 'ORG_TYPE', 'SDMP_CRMP', 'HEALTHY_TRANSPORT_PLAN', 'PROMOTE_HEALTHY_TRAVEL'],
    period: '2020-21',
    server: $env.server,
    survey: 'SDU-2020-21',
    tenant: { id: 'sdu' },
    username: localStorage['username'],
    formatAbsAnswer: function(qName, period) {
      try {
        var answer = ractive.getAnswer(qName, period);
        return answer == undefined ? '' : Math.abs(answer);
      } catch (e) {
        return '';
      }
    },
    formatAnswer: function(qName, period) {
      if (qName==undefined || ractive.get('surveyReturn')==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, period);
          return answer == undefined ? '' : answer;
        } catch (e) {
          return '';
        }
      }
    },
    formatDecimalAnswer: function(qName, period, decimalPlaces) {
      if (qName==undefined || ractive.get('surveyReturn')==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, period);
          return answer == undefined ? '' : parseFloat(answer).formatDecimal(decimalPlaces);
        } catch (e) {
          return '';
        }
      }
    },
    formatCommissionerAnswer: function(idx,qName) {
      if (qName==undefined || ractive.get('surveyReturn.commissioners.'+idx)==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, ractive.get('period'), ractive.get('surveyReturn.commissioners.'+idx+'.answers'));
          return answer;
        } catch (e) {
          return '';
        }
      }
    },
    formatDateTime: function(timeString) {
      // console.log('formatDate: '+timeString);
      if (timeString==undefined) return 'n/a';
      return new Date(timeString).toLocaleString(navigator.languages);
    },
    formatHint: function(qName) {
      for (i in ractive.get('q.categories')) {
        for (j in ractive.get('q.categories.'+i+'.questions')) {
          if (ractive.get('q.categories.'+i+'.questions.'+j+'.name')==qName) return 'This is based on your response to '+ractive.get('q.categories.'+i+'.name')+' question '+(parseInt(j)+1);
        }
      }
      if (ractive.get('labels.'+qName)!=undefined) return ractive.get('labels.'+qName);
      else return qName;
    },
    formatProviderAnswer: function(idx,qName) {
      if (qName==undefined || ractive.get('surveyReturn.providers.'+idx)==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, ractive.get('period'), ractive.get('surveyReturn.providers.'+idx+'.answers'));
          return answer;
        } catch (e) {
          return '';
        }
      }
    },
    haveAnswer: function(qName) {
      try {
        var a = ractive.getAnswer(qName);
        return (a == undefined || a == '' || a == ractive.get('narrativeCtxtPrompt') || a == ractive.get('narrativePerfPrompt'))
            ? false
            : true;
      } catch (e) {
        return false;
      }
    },
    isCcg: function() {
      return ractive.isCcg();
    },
    isEClassUser: function() {
      return ractive.isEClassUser();
    },
    matchRole: function(role) {
      console.info('matchRole: '+role)
      if (role==undefined || ractive.hasRole(role)) {
        $('.'+role).show();
        return true;
      } else {
        return false;
      }
    },
    matchSearch: function(obj) {
      var searchTerm = ractive.get('searchTerm');
      //console.info('matchSearch: '+searchTerm);
      if (searchTerm==undefined || searchTerm.length==0) {
        return true;
      } else {
        return obj.question.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.applicablePeriod.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.status.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || (searchTerm.startsWith('revision:') && obj.revision==searchTerm.substring(9))
          || (searchTerm.startsWith('updated>') && new Date(obj.lastUpdated)>new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('created>') && new Date(obj.created)>new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('updated<') && new Date(obj.lastUpdated)<new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('created<') && new Date(obj.created)<new Date(searchTerm.substring(8)));
      }
    },
    sort: function (array, column, asc) {
      console.info('sort '+(asc ? 'ascending' : 'descending')+' on: '+column);
      array = array.slice(); // clone, so we don't modify the underlying data

      return array.sort( function ( a, b ) {
        console.log('sort '+a+','+b);
        var aVal = eval('a.'+column);
        var bVal = eval('b.'+column);
        if (bVal==undefined || bVal==null || bVal=='') {
          return (aVal==undefined || aVal==null || aVal=='') ? 0 : -1;
        } else if (asc) {
          return aVal < bVal ? -1 : 1;
        } else {
          return aVal > bVal ? -1 : 1;
        }
      });
    },
    sortAsc: true,
    sortColumn: 'id',
    sorted: function(column) {
//      console.info('sorted:'+column);
      return 'hidden'; // hide as sorting is disabled due to perf
      if (ractive.get('sortColumn') == column && ractive.get('sortAsc')) return 'sort-asc';
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      
      { "name": "sidebar", "url": $env.server+"/partials/sidebar.html"},
      { "name": "toolbar", "url": $env.server+"/partials/toolbar.html"},
      { "name": "statusSect", "url": "/srp/vsn/partials/status-sect.html"}
    ],
  },
  partials: {
    'loginSect': '',
    'shareCtrl': '<div class="controls pull-right" style="display:none">'
                +'  <span class="glyphicon icon-btn kp-icon-share"></span>'
                +'  <!--span class="glyphicon icon-btn kp-icon-link"></span-->'
                +'  <!--span class="glyphicon icon-btn kp-icon-copy"></span-->'
                +'</div>'
  },
  calculate: function () {
    console.info('calculate...');
    $('.btn-calc,.btn-refresh,.btn-submit').attr('disabled','disabled');
    $('#ajax-loader').show();
    $.ajax({
      dataType: 'json',
      type: 'POST',
      url: ractive.getServer()+'/calculations/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  copyLink: function(ev) {
    if (navigator['clipboard'] == undefined) {
      alert('Please upgrade your browser to be able to copy links');
    } else {
      navigator.clipboard.writeText(ev.target.getAttribute('data-share'));
      console.info('copied link');
      var toast = new iqwerty.toast.Toast('Copied!', { style: { main: {
	  background: '#0078c1',
	  color: 'white',
	  'box-shadow': '0 0 50px rgba(0, 120, 193, .7)'
	}}});
    }
    return false;
  },
  enter: function () {
    console.info('enter...');
  },
  fetch: function() {
    console.info('fetch...');
    if (getSearchParameters()['id']==undefined || !ractive.hasRole('analyst')) ractive.fetchImplicit();
    else ractive.fetchExplicit();
  },
  fetchExplicit: function() {
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/returns/'+getSearchParameters()['id'],
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  fetchImplicit: function() {
    if (ractive.getProfile() == undefined) return; // still loading
    ractive.set('org', ractive.getAttribute('org'));
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/returns/findCurrentBySurveyNameAndOrg/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  fetchCsv: function(ctrl, callback) {
    var url = $(ctrl)
        .data('src').indexOf('//')==-1
            ? ractive.getServer()+$(ctrl).data('src')
            : $(ctrl).data('src').replace(/host:port/,window.location.host)
        .attr('width', '1024')
        .attr('width', '400')
    $.ajax({
      dataType: "text",
      url: url,
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('saveObserver', false);
        //ractive.set(keypath,data);
        var options = {};
        if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
        if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
        if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
        if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
        if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

        $(ctrl)
          .on('mouseover', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').show();
          })
          .on('mouseout', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').hide();
          });
        $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
        callback('#'+ctrl.id, data, options);
        ractive.set('saveObserver', true);
      }
    });
  },
  fetchOrgData: function() {
    console.info('fetchOrgData');
    $( "#ajax-loader" ).show();
    ractive.set('saveObserver', false);
    ractive.set('surveyReturn.orgs', []);
    var orgs = ractive.get('orgAnswerNames').filter(function(el, idx) {
      return (ractive.isCcg() && el.startsWith('PROVIDER')) || (!ractive.isCcg() && el.startsWith('CCG'));
    });
    for (var idx = 0 ; idx < orgs.length ; idx++) {
      try {
        var org = ractive.getAnswer(orgs[idx]);
        if (org == undefined || org.trim().length==0) continue;
        $.ajax({
          dataType: "json",
          url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/accounts/findByName/'+encodeURIComponent(org),
          crossDomain: true,
          headers: {
            "X-Requested-With": "XMLHttpRequest",
            "X-Authorization": "Bearer "+localStorage['token'],
            "Cache-Control": "no-cache"
          },
          success: function( data ) {
            ractive.set('saveObserver', false);
            for (j = 0 ; j < ractive.get('surveyReturn.orgs').length ; j++) {
              if (ractive.get('surveyReturn.orgs.'+j+'.name') == data.name) {
                ractive.splice('surveyReturn.orgs',j,1);
              }
            }
            ractive.push('surveyReturn.orgs', data);
            $( "#ajax-loader" ).hide();
            ractive.set('saveObserver', true);
          },
          error: function(jqXHR, textStatus, errorThrown ) {
            var org = this.url.substring(this.url.lastIndexOf('/')+1);
            console.warn('Unable to fetch data for '+org+'.'+jqXHR.status+':'+textStatus+','+errorThrown);
            $( "#ajax-loader" ).hide();
            ractive.set('saveObserver', true);
          }
        });
      } catch (e) {
        console.info('Assume no org at idx: '+idx);
      }
    }
  },
  fetchSuccessHandler: function( data ) {
    ractive.set('saveObserver', false);
    if (Array.isArray(data)) ractive.set('surveyReturn', data[0]);
    else ractive.set('surveyReturn', data);
    if (ractive.isCcg()) ractive.fetchOrgData();
    else ractive.fetchOrgData();
    //if (ractive.hasRole('admin')) $('.admin').show();
    //if (ractive.hasRole('power-user')) $('.power-user').show();
    if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
    //ractive.set('searchMatched',$('#contactsTable tbody tr:visible').length);
    $('.rpt.pie').each(function(i,d) {
      ractive.fetchCsv(d, renderPie);
    });
    $('.rpt.pie2').each(function(i,d) {
      ractive.renderCsvForPie(d, renderPie);
    });
    $('.rpt.stacked,.rpt.stacked2').each(function(i,d) {
      switch (true) {
      case (window.innerWidth < 480):
        $(d).attr('width',440).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 768):
        $(d).attr('width',720).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 980):
        $(d).attr('width',720).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 1200):
        $(d).attr('width',window.innerWidth* .8).attr('height', window.innerHeight* .4);
        break;
      default:
        $(d).attr('width',1140).attr('height', window.innerHeight* .4);
      }
    });
    $('.rpt.stacked').each(function(i,d) {
      ractive.fetchCsv(d, renderStacked);
    });
    $('.rpt.stacked2').each(function(i,d) {
      ractive.renderCsv(d, renderStacked);
    });
    $('.rpt.table').each(function(i,d) {
      ractive.fetchTable(d);
    });
    $('.rpt.table2').each(function(i,d) {
      ractive.renderTable(d);
    });
    if (data.status == 'Draft') {
      $('.btn-calc').removeAttr('disabled');
      $('.btn-refresh').removeAttr('disabled');
      $('.btn-submit').removeAttr('disabled');
      $('.btn-restate').attr('disabled','disabled');
    }
    if (data.status == 'Submitted') {
      $('.btn-calc').attr('disabled','disabled');
      $('.btn-refresh').attr('disabled','disabled');
      $('.btn-submit').attr('disabled','disabled');
      $('.btn-restate').removeAttr('disabled');
    }
    var qs=0;
    var coreQs=0;
    var answeredCoreQs=0;
    var answeredQs=0;
    var periods = [];
    for (idx = 0 ; idx < ractive.get('surveyReturn.answers').length ; idx++) {
      qs++;
      if (ractive.get('surveyReturn.answers.'+idx+'.question.required')==true) coreQs++;
      if (ractive.get('surveyReturn.answers.'+idx+'.response')!=undefined
          && ractive.get('surveyReturn.answers.'+idx+'.response')!=''
          && ractive.get('surveyReturn.answers.'+idx+'.derived')==false
          && ractive.get('surveyReturn.answers.'+idx+'.status')!='Superseded') {
        answeredQs++;
        if (ractive.get('surveyReturn.answers.'+idx+'.question.required')==true) answeredCoreQs++;
        periods.push(ractive.get('surveyReturn.answers.'+idx+'.applicablePeriod'));
      }
    }
    ractive.set('surveyReturn.completeness.qs',qs);
    ractive.set('surveyReturn.completeness.coreQs',coreQs);
    ractive.set('surveyReturn.completeness.answeredCoreQs',answeredCoreQs);
    ractive.set('surveyReturn.completeness.answeredQs',answeredQs);
    ractive.set('surveyReturn.completeness.periods',periods.uniq());
    var requiredMissing = [];
    for (idx = 0 ; idx < ractive.get('requiredAnswers').length ; idx++) {
      if (ractive.getAnswer(ractive.get('requiredAnswers.'+idx)) != undefined) {
        requiredMissing.push(ractive.get('requiredAnswers.'+idx));
      }
    }
    ractive.set('surveyReturn.completeness.missingrequired',requiredMissing);
    ractive.initNarrative();
    $('title').empty().append('Sustainability Report &ndash; '+ ractive.get('surveyReturn.applicablePeriod') +' &ndash; '+ ractive.get('surveyReturn.org'));
    ractive.fetchSurvey();
    ractive.set('saveObserver', true);
  },
  fetchSurvey: function() {
    console.info('fetchSurvey');
    $.ajax({
       type: 'GET',
       url: ractive.getServer()+'/surveys/findByName/'+ractive.get('survey'),
       dataType: 'json',
       success: function(data, textStatus, jqxhr) {
         console.log('success:'+data);
         ractive.set('q', data);
       }
    });
  },
  fetchTable: function(ctrl) {
    $.ajax({
      dataType: "html",
      url: ractive.getServer()+$(ctrl).data('src'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('saveObserver', false);
        $(ctrl).empty().append(data)
          .on('mouseover', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').show();
          })
          .on('mouseout', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').hide();
          });
        $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
        ractive.set('saveObserver', true);
      }
    });
  },
  formatNumber: function() {
    $('.number').each(function(i,d) {
      var a = d.innerText.substring(0,d.innerText.indexOf('.'));
      d.innerText = a.replace(new RegExp("^(\\d{" + (a.length%3?a.length%3:0) + "})(\\d{3})", "g"), "$1,$2").replace(/(\d{3})+?/gi, "$1,").replace(/^,/,'').slice(0,-1);
    });
  },
  getAnswer: function(qName, period) {
    if (period == undefined) period = ractive.get('surveyReturn.applicablePeriod');
    var answers = ractive.get('surveyReturn.answers');
    return ractive.getAnswerFromArray(qName, period, answers);
  },
  getAnswerFromArray: function(qName, period, answers, unformatted) {
    for (var idx = 0 ; idx < answers.length ; idx++) {
      var a = answers[idx];
      if (a.question.name == qName && a.applicablePeriod == period && a.response=='true') {
        return true;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.response=='false') {
        return false;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number' && unformatted == true) {
        var x = a.response == undefined ? 0 : parseFloat(a.response);
        if (isNaN(x)) return 0;
        else return x;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number' && parseFloat(a.response)<100) {
        return parseFloat(a.response).sigFigs(3);
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number') {
        return parseFloat(a.response).formatDecimal(0);
      } else if (a.question.name == qName && a.applicablePeriod == period) {
        return a.response;
      }
    }
    return a.question.type == 'number' ? 0 : undefined;
  },
  getAttribute: function(attr) {
    return ractive.getProfile().attributes[attr];
  },
  getPeriod: function(offset) {
    var currentYear = parseInt(ractive.get('period').substring(0,4));
    var yearEnd = (currentYear-1999+offset);
    var period = (currentYear+offset)+'-'+(yearEnd < 10 ? '0'+yearEnd : yearEnd);
    return period;
  },
  getProfile: function() {
    var profile = localStorage['profile'];
    if (profile == undefined) {
      alert('Unable to authenticate you at the moment, please try later');
      return;
    } else {
      return JSON.parse(profile);
    }
  },
  initNarrative: function() {
    if (ractive.get('surveyReturn.status')!='Draft') {
      console.info('Skipping initNarrative because status is '+ractive.get('surveyReturn.status'));
      $('.narrative').removeAttr('contenteditable');
      return;
    }
    $('.narrative')
        .attr('contenteditable', 'true')
        .click(function(ev) {
          if ($(ev.target).hasClass('narrative')) {
            console.info('Edit... '+$(ev.target).data('q'));
          } else {
            console.info('Edit: '+ev.target.tagName+$(ev.target).closest('contenteditable').data('q'));
          }
        })
        .blur(function(ev) {
          if ($(ev.target).hasClass('narrative')) {
            ractive.saveNarrative($(ev.target).data('q'), ev.target.innerText);
          } else {
            ractive.saveNarrative($(ev.target).closest('contenteditable').data('q'),
                ev.target.innerText);
          }
        });
    $('.narrative.context:empty')
        .html(ractive.get('narrativeCtxtPrompt'));
    $('.narrative.performance:empty')
        .html(ractive.get('narrativePerfPrompt'));
  },
  isCcg: function() {
    if (ractive.get('surveyReturn')==undefined) return false;
    if (ractive.getAnswer('ORG_TYPE')=='Clinical Commissioning Group') return true;
    else return false;
  },
  isEClassUser: function() {
    // unclear what has changed but this used to report 0-E-CLASS but now reports true
    // cater for both
    var eClass = ractive.getAnswer('ECLASS_USER');
    if (eClass || (eClass!=undefined && eClass.charAt(0)==0)) return true;
    else return false;
  },
  oninit: function() {
  },
  renderLabel: function(qName) {
    if (ractive.get('labels.'+qName)!=undefined) return ractive.get('labels.'+qName);
    else return qName.toLabel();
  },
  renderCsv: function(ctrl, callback) {
    console.log('renderCsv: '+ctrl.id);
    var periods = parseInt(ctrl.getAttribute('data-periods'));
    var qs = ctrl.getAttribute('data-qs').split(',');
    var csv = 'Period,';
    for (var i = 0 ; i < qs.length ; i++) {
      csv += ractive.renderLabel(qs[i]);
      if (i+1 < qs.length) csv += ',';
      else csv += '\n';
    }
    var answers = ractive.get('surveyReturn.answers');
    for (var idx = 0 ; idx < periods ; idx++) {
      var period = ractive.getPeriod(idx-periods+1);
      csv += period;
      csv += ',';
      for (var i = 0 ; i < qs.length ; i++) {
        csv += ractive.getAnswerFromArray(qs[i], period, answers, true);
        if (i+1 < qs.length) csv += ',';
        else csv += '\n';
      }
    }

    var options = {};
    if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
    if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
    if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
    if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
    if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

    $(ctrl)
      .on('mouseover', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').show();
      })
      .on('mouseout', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').hide();
      });
    $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
    callback('#'+ctrl.id, csv, options);
  },
  renderCsvForPie: function(ctrl, callback) {
    console.log('renderCsvForPie: '+ctrl.id);
    var period = ractive.getPeriod(parseInt(1-ctrl.getAttribute('data-periods')));
    var qs = ctrl.getAttribute('data-qs').split(',');
    var answers = ractive.get('surveyReturn.answers');

    var csv = 'classification,percentage\n';
    for (var i = 0 ; i < qs.length ; i++) {
      csv += ractive.renderLabel(qs[i]);
      csv += ',';
      csv += ractive.getAnswerFromArray(qs[i], period, answers, true);
      csv += '\n';
    }

    var options = {};
    if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
    if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
    if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
    if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
    if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

    $(ctrl)
      .on('mouseover', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').show();
      })
      .on('mouseout', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').hide();
      });
    $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
    callback('#'+ctrl.id, csv, options);
  },
  renderTable: function(d) {
    console.log('renderTable: '+d.id);
    var periods = parseInt(d.getAttribute('data-periods'));
    var qs = d.getAttribute('data-qs').split(',');
    var table = '<table class="table table-striped"><thead><tr><th>&nbsp;</th><th>&nbsp;</th>';
    for (var i = 1 ; i <= periods ; i++) {
      table += '<th class="number">'+ractive.getPeriod(i-periods)+'</th>';
    }
    table += '</tr><tbody>';
    for (var idx = 0 ; idx < qs.length ; idx++) {
      table += '<th>'+ractive.renderLabel(qs[idx])+'</th>';
      table += '<th class="legend '+qs[idx].toLowerCase()+'">&nbsp;</th>';
      for (var i = 1 ; i <= periods ; i++) {
        var ans = ractive.getAnswer(qs[idx], ractive.getPeriod(i-periods));
        table += '<td class="number">';
        table += ans == undefined ? 'n/a' : isNaN(ans) ? ans : parseFloat(ans).sigFigs(3);
        table += '</td>';
      }
      table += '</tr>';
    }

    table += '</tbody><tfoot>';
    if (d.getAttribute('data-total')=='true') {
      table += '<th>Total</th><th>&nbsp;</th>';
      for (var i = 1 ; i <= periods ; i++) {
        table += '<td class="number">'+ractive.total(qs, ractive.getPeriod(i-periods))+'</td>';
      }
    }
    if (d.hasAttribute('data-share')) {
      table += '<tr><td colspan="'+(periods+2)+'">'
            +'<a href="#" title="Copy link to this table" data-share="'+d.getAttribute('data-share')
            +'" onclick="return ractive.copyLink(event);" '
            +'class="pull-right no-print"><span class="glyphicon glyphicon-copy"></span>Copy</a>'
            +'<a href="'+d.getAttribute('data-share')+'" target="_blank" title="Open the table in a new window" '
            +'class="pull-right no-print"><span class="glyphicon glyphicon-share-alt"></span>Share</a>'
            +'</td></tr>';
    }
    table += '</tfoot></table>';
    $(d).empty().append(table);
  },
  reset: function() {
    console.info('reset');
    if (document.getElementById('resetForm').checkValidity()) {
      $('#resetSect').slideUp();
      $('#loginSect').slideDown();
      var addr = $('#email').val();
      $.ajax({
        url: ractive.getServer()+'/msg/srp/omny.passwordResetRequest.json',
        type: 'POST',
        data: { json: JSON.stringify({ email: addr, tenantId: 'srp' }) },
        dataType: 'text',
        success: function(data) {
          ractive.showMessage('A reset link has been sent to '+addr);
        },
      });
    } else {
      ractive.showError('Please enter the email address you registered with');
    }
  },
  restate: function () {
    console.info('restate...');
    $('#ajax-loader').show();
    var instanceToStart = {
      businessKey: 'Sustainability report '+ractive.get('surveyReturn.revision')+' for '+ractive.get('surveyReturn.org')+' '+ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'RestateSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: ractive.getProfile('firstName'),
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Restating your return, this may take a little while...');
    $.ajax({
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(instanceToStart),
      success: function(data, textStatus, jqXHR) {
        console.log('response: '+ jqXHR.status+", Location: "+jqXHR.getResponseHeader('Location'));
        ractive.showMessage('Created a new, draft revision of your return');
        $('#ajax-loader').hide();
        ractive.fetch();
      }
    });
  },
  saveNarrative: function(q, val) {
    console.info('saveNarrative of '+q+' as '+val);
    if (q!=undefined && val!=undefined && val.trim()!=ractive.get('narrativePrompt')) {
      $('.save-indicator').show();
      $.ajax({
        url: ractive.getServer()+'/returns/'+ractive.get('surveyReturn.id')+'/answers/'+q,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(val),
        success: function(data) {
          console.info('...saved');
          $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
          setTimeout(function() {
            $('.save-indicator').fadeOut(function() {
              $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
            });
          }, 3000);
        },
      });
    }
  },
  showReset: function() {
    $('#loginSect').slideUp();
    $('#resetSect').slideDown();
  },
  submit: function () {
    console.info('submit...');
    $('#ajax-loader').show();
    var instanceToStart = {
      businessKey: 'Sustainability report '+ractive.get('surveyReturn.revision')+' for '+ractive.get('surveyReturn.org')+' '+ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'SubmitSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: ractive.getProfile().username,
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Submitting your return...');
    $.ajax({
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(instanceToStart),
      success: function(data, textStatus, jqXHR) {
        console.log('response: '+ jqXHR.status+", Location: "+jqXHR.getResponseHeader('Location'));
        ractive.showMessage('Your return has been received');
        $('#ajax-loader').hide();
        ractive.fetch();
      }
    });
  },
  toggleFieldHint: function(id) {
    console.log('toggleFieldHint');
    if ($('#'+id+'Hint:visible').length == 0) {
      $('#'+id+'Hint').slideDown(ractive.get('easingDuration')).removeClass('hidden');
    } else {
      $('#'+id+'Hint').slideUp(ractive.get('easingDuration'));
    }
  },
  total: function(qs, period) {
    var total = 0;
    var answers = ractive.get('surveyReturn.answers');
    for (i = 0 ; i < qs.length ; i++) {
      var val = ractive.getAnswerFromArray(qs[i], period, answers, true);
      if (!isNaN(val)) total += parseFloat(val);
    }
    if (total < 100) return total.sigFigs(3);
    else return total.formatDecimal(0);
  }
});

$(document).ready(function() {
  $('head').append('<link href="'+ractive.getServer()+'/css/sdu-1.0.0.css" rel="stylesheet">');
  $('.menu-burger, .toolbar').addClass('no-print');
})

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  //ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#answersTable tbody tr').length);
  }, 500);
});
ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});
