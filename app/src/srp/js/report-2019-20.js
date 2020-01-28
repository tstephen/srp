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
      '2019-20': '2019-20 (%)',
      '2018-19': '2018-19 (%)',
      '2017-18': '2017-18 (%)',
      '2016-17': '2016-17 (%)',
      '3RD_PARTY_ADDITIONAL_PCT': 'Third party additional %',
      ADAPTATION_PERF: 'Commentary on adaptation performance',
      ADAPTATION_PLAN_INC: 'Do your board approved plans address the potential need to adapt the delivery of your organisation&apos;s activities and organisation&apos;s infrastructure as a result of climate change and adverse weather events?',
      ANAESTHETIC_GASES_CO2E: 'Anaesthetic Gases',
      BACKLOG_MAINTENANCE_VAL: 'Investment to reduce backlog maintenance (£)',
      BEDDING_LINEN_AND_TEXTILES: 'Bedding Linen & Textiles',
      BEDDING_LINEN_AND_TEXTILES_CO2E: 'Bedding Linen & Textiles',
      BENCHMARK_PERF: 'Commentary on benchmark performance',
      BIZ_MILEAGE_AIR_CO2E: 'Business Mileage - Air',
      BIZ_MILEAGE: 'Business travel and fleet',
      BIZ_MILEAGE_CO2E: 'Business travel and fleet',
      BIZ_MILEAGE_RAIL_CO2E: 'Business Mileage - Rail',
      BIZ_MILEAGE_ROAD: 'Business Mileage - Road',
      BIZ_MILEAGE_ROAD_CO2E: 'Business Mileage - Road',
      BIZ_SVCS_CO2E: 'Business services',
      BIZ_SVCS_SPEND: 'Business services',
      BLDG_AND_ENG_PROD_AND_SVCS_CO2E: 'Building & Engineering Products & Services',
      BOARD_LEAD_FOR_SUSTAINABILITY: 'Is there a Board Level lead for Sustainability on your Board?',
      BOARD_SUSTAINABILITY_AS_RISK: 'Does your board consider sustainability issues as part of its risk management process?',
      BUILDING_AND_ENGINEERING_PRODUCTS_AND_SERVICES: 'Building & Engineering Products & Services',
      CAPITAL_CO2E: 'Capital spending',
      CAPITAL_EQUIPMENT: 'Capital investment for equipment (£)',
      CAPITAL_IMPROVING_EXISTING: 'Capital investment for improving existing buildings (£)',
      CAPITAL_NEW_BUILD: 'Capital investment for new build (£)',
      CAPITAL_SPEND: 'Capital spend',
      CARBON_REDUCTION_BASELINE_USED: 'Does your organisation use a baseline year?',
      CARBON_REDUCTION_BASE_YEAR: 'Which year is it?',
      CARBON_REDUCTION_DEADLINE_YEAR: 'What is the deadline year for this target?',
      CARBON_REDUCTION_TARGET: 'Does your organisation have its own carbon reduction target?',
      CARBON_REDUCTION_TARGET_PCT: 'What is the percentage reduction target?',
      CATERING_CO2E: 'Food and catering',
      CATERING_SPEND: 'Food and catering',
      CCGS_SERVED: 'We provide services to the following CCGs',
      CHEM_AND_GAS_CO2E: 'Manufactured fuels, chemicals and gases',
      CHEM_AND_GAS_SPEND: 'Manufactured fuels, chemicals and gases',
      CHEMICALS_AND_REAGENTS: 'Chemicals & Reagents',
      CHEMICALS_AND_REAGENTS_CO2E: 'Chemicals & Reagents',
      CHEMS_AND_GASES_CO2E: 'Manufactured fuels chemicals and gases',
      CHP_ELECTRICAL_OUTPUT: 'Total electrical energy output of the CHP system/s',
      CITIZEN_CO2E: 'Citizen',
      COAL_CO2E: 'Coal',
      COAL_USED: 'Coal Consumed',
      COMMISSIONING_CO2E: 'Commissioning',
      COMMISSIONING_CO2E_PCT: 'Commissioning',
      COMMISSIONING_CO2E_PER_POUND: 'Commissioning (tCO₂e)',
      COMMISSIONING_REGION: 'Commissioning Region',
      COMMISSIONING_SPEND: 'Commissioning',
      COMPOSTED_WEIGHT: 'Composted',
      COMMUNITY_CO2E_PCT: 'Community',
      CONSTRUCTION_CO2E: 'Construction',
      CONSTRUCTION_SPEND: 'Construction',
      CONSULTING_SVCS_AND_EXPENSES_CO2E: 'Staff & Patient Consulting Services & Expenses',
      CONTRACTING_OUT_PCT: 'Percentage of hard FM (estates) and soft FM (hotel services) contracted out (%)',
      CONTRACTING_OUT_VAL: 'Value of contracted out services (£)',
      CORE_CO2E: 'Core emissions',
      CORE_CO2E_PCT: 'Core emissions',
      DENTAL_AND_OPTICAL_EQUIPMENT: 'Dental & Optical Equipment',
      DENTAL_AND_OPTICAL_EQUIPT_CO2E: 'Dental & Optical Equipment',
      DESFLURANE_CO2E: 'Desflurane - liquid',
      DESFLURANE: 'Desflurane - liquid',
      DOMESTIC_AIR_MILES: 'Air - Domestic',
      DRESSINGS_CO2E: 'Dressings',
      DRESSINGS: 'Dressings',
      ECLASS_USER: 'Do you use eClass for procurement?',
      ELEC_CO2E: 'Electricity',
      ELEC_EXPORTED: 'Total exported electricity',
      ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E: 'Green tariff',
      ELEC_NON_RENEWABLE_3RD_PARTY_CO2E: 'Third party',
      ELEC_OWNED_RENEWABLE_USED: 'Green electricity consumed',
      ELEC_OWNED_RENEWABLE_USED_SDU: 'Green electricity consumed',
      ELEC_RENEWABLE_CO2E: 'Green electricity',
      ELEC_RENEWABLE: 'Green electricity',
      ELEC_TOTAL_RENEWABLE_USED: 'Green electricity',
      ELEC_USED_3RD_PTY_RENEWABLE: 'Electricity Consumed - third party owned renewable',
      ELEC_USED: 'Electricity Consumed',
      ELEC_USED_GREEN_TARIFF: 'Green Tariff Electricity Used',
      ELEC_USED_LOCAL: 'Electricity Consumed - local',
      ELEC_USED_RENEWABLE: 'Electricity Consumed - renewable',
      ENERGY: 'This is based on your response to the questions in the Electricity and Thermal sections',
      ENERGY_CO2E_PCT: 'Energy',
      ENERGY_CO2E_PER_POUND: 'Energy (tCO₂e)',
      ENERGY_CTXT: 'Energy Context',
      ENERGY_PERF: 'Energy Performance',
      ESTATES_DEV_STRATEGY: 'Estates Development Strategy ()',
      EXPORTED_THERMAL_ENERGY_CO2E: 'Exported thermal',
      EXPORTED_THERMAL_ENERGY: 'Exported thermal energy',
      FLOOR_AREA: 'Total gross internal floor space',
      FREIGHT_CO2E: 'Freight transport',
      FREIGHT_SPEND: 'Freight transport',
      FUEL_LIGHT_POWER_WATER_CO2E: 'Fuel Light Power Water',
      FUEL_WTT: 'Fuel Well to Tank',
      FURNITURE_FITTINGS_CO2E: 'Furniture Fittings',
      FURNITURE_FITTINGS: 'Furniture Fittings',
      GARDENING_AND_FARMING_CO2E: 'Gardening & Farming',
      GARDENING_AND_FARMING: 'Gardening & Farming',
      GAS_CO2E: 'Gas',
      GAS_USED: 'Gas Consumed',
      GCC_USER: 'Does your organisation use the Sustainable Development Assessment Tool (SDAT) tool?',
      GREEN_TARIFF_ADDITIONAL_PCT: 'Percentage of green tariff supply proven as additional',
      HARDWARE_CROCKERY_CO2E: 'Hardware & Crockery',
      HARDWARE_CROCKERY: 'Hardware Crockery',
      HEALTH_IMPACT_OF_TRAVEL: 'Health impacts of travel and transport associated with provider activities',
      HEALTHY_TRANSPORT_PLAN: 'Does your organisation have a healthy or green transport plan?',
      HIGH_TEMP_DISPOSAL_WEIGHT: 'High Temperature Disposal Waste',
      HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT: 'High Temperature Disposal Waste with Energy Recovery',
      HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E: 'Hotel Services Equipment Materials & Services',
      HOTEL_SERVICES_EQUIPMENT_MATERIALS_AND_SERVICES: 'Hotel Services Equipment Materials & Services',
      HOT_WATER_CO2E: 'Hot water',
      HOT_WATER_USED: 'Hot Water Consumed',
      ICT_CO2E: 'Information and communication technologies',
      ICT_SPEND: 'Information and communication technologies',
      IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E: 'Diagnostic Imaging & Radiotherapy Equipment & Services',
      INCINERATION_CO2E: 'Incineration',
      INCINERATION_WEIGHT: 'Incineration disposal weight',
      INCOME_CATERING: 'Income from services provided to other organisations - catering (£)',
      INCOME_LAUNDRY: 'Income from services provided to other organisations - laundry and linen (£)',
      INCOME_OTHER: 'Income from services provided to other organisations - other (£)',
      ISOFLURANE: 'Isoflurane - liquid',
      ISOFLURANE_CO2E: 'Isoflurane - liquid',
      LAB_EQUIPT_AND_SVCS_CO2E: 'Laboratory Equipment & Services',
      LABORATORY_EQUIPMENT_AND_SERVICES: 'Laboratory Equipment & Services',
      LANDFILL_CO2E: 'Landfill',
      LANDFILL_WEIGHT: 'Landfill disposal weight',
      LAST_GCC_DATE: 'If your organisation uses the <a href="http://www.sduhealth.org.uk/sdat/">Sustainable Development Assessment Tool (SDAT)</a> tool when was your last self assessment?',
      LAST_GCC_SCORE: 'What was your score?',
      LEASED_ASSETS_ENERGY_USE: 'Leased Assets Energy Use (Upstream - Gas, Coal & Electricity)',
      LEASED_FLEET_TRAVEL: 'Non-organisation Owned Fleet/Pool Road Travel (Leased, hired etc.)',
      LONG_HAUL_AIR_MILES: 'Air - Long Haul International Flights',
      MEDICAL_AND_SURGICAL_EQUIPMENT: 'Medical & Surgical Equipment',
      MEDICAL_AND_SURGICAL_EQUIPT_CO2E: 'Medical & Surgical Equipment',
      MED_INSTR_CO2E: 'Medical instruments / equipment',
      MED_INSTR_SPEND: 'Medical instruments',
      MOD_SLAVERY: 'If your organisation has a Modern Slavery statement, what is it?',
      NET_ELEC_CO2E: 'Electricity (net of any exports)',
      NET_THERMAL_ENERGY_CO2E: 'Thermal energy (net of any exports)',
      NITROUS_OXIDE: 'Nitrous oxide - gas',
      NITROUS_OXIDE_CO2E: 'Nitrous oxide',
      NO_ACUTE_SITES: 'Number of sites - General acute hospital (No.)',
      OCCUPIED_BEDS: 'Total no. occupied beds',
      NO_COMMUNITY_HOSPITAL_SITES: 'Number of sites - Community hospital (with inpatient beds) (No.)',
      NO_EVAC_INJURIES: 'Number of patients sustaining injuries during evacuation (No.)',
      NO_FALSE_ALARMS: 'False alarms (No.)',
      NO_FIRE_DEATHS: 'Number of deaths resulting from fire(s) (No.)',
      NO_FIRE_INJURIES: 'Number of people injured resulting from fire(s) (No.)',
      NO_FIRES: 'Fires recorded (No.)',
      NO_FM_CLINICAL_INCIDENTS: 'Clinical service incidents caused by estates and infrastructure failure (No.)',
      NO_FM_INCIDENTS: 'Estates and facilities related incidents (No.)',
      NO_LD_SITES: 'Number of sites - Learning Disabilities (No.)',
      NO_MENTAL_HEALTH_LD_SITES: 'Number of sites - Mental Health and Learning Disabilities (No.)',
      NO_MENTAL_HEALTH_SITES: 'Number of sites - Mental Health (including Specialist services) (No.)',
      NO_MIXED_SITES: 'Number of sites - Mixed service hospital (No.)',
      NON_BURN_WEIGHT: 'Non Burn Treatment Disposal Waste',
      NON_EMERGENCY_TRANSPORT_VAL: 'Non-emergency patient transport (£)',
      NON_PAY_SPEND: 'Non-pay spend',
      NO_OTHER_INPATIENT_SITES: 'Number of sites - Other in-patient (No.)',
      NO_OTHER_SITES: 'Number of sites - Unreported sites (No.)',
      NO_OUTPATIENT_SITES: 'Number of sites - Non inpatient (No.)',
      NO_PATIENT_CONTACTS: 'Total Patient Contacts',
      NO_RIDDOR_INCIDENTS: 'RIDDOR incidents (No.)',
      NO_SPECIALIST_SITES: 'Number of sites - Specialist hospital (acute only) (No.)',
      NO_STAFF: 'Total no. of staff employed',
      NO_SUPPORT_SITES: 'Number of sites - Support facilities (No.)',
      OFFICE_EQUIPMENT_TELECOMMS_COMPUTERS_AND_STATIONERY: 'Office Equipment Telecomms Computers & Stationery',
      OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E: 'Office Equipment Telecomms Computers & Stationery',
      OIL_CO2E: 'Oil',
      OIL_USED: 'Oil Consumed',
      OP_EX: 'Operating Expenditure',
      ORG_CODE: 'Organisation code e.g. RAA',
      ORG_NAME: 'Name of organisation',
      ORG_NICKNAME: 'Abbreviation or nick name of organisation used',
      ORG_TYPE: 'Organisation type',
      OTHER_AOI_CORE: 'Other AoI Core',
      OTHER_AOI_COMMISSIONING: 'Other AoI Commissioning',
      OTHER_AOI_PROCUREMENT: 'Other AoI Procurement',
      OTHER_AOI_COMMUNITY: 'Other AoI Community',
      OTHER_MANUFACTURED_CO2E: 'Other manufactured goods',
      OTHER_MANUFACTURED_SPEND: 'Other manufactured goods',
      OTHER_PROCUREMENT_CO2E: 'Other procurement',
      OTHER_RECOVERY_CO2E: 'Recovery',
      OTHER_RECOVERY_WEIGHT: 'Other recovery weight',
      OTHER_SPEND: 'Other procurement',
      OWNED_BUILDINGS_COAL: 'Coal',
      OWNED_BUILDINGS_GAS: 'Gas',
      OWNED_BUILDINGS_OIL: 'Oil',
      OWNED_BUILDINGS: 'Owned Buildings',
      OWNED_FLEET_TRAVEL_CO2E: 'Business and fleet estimate',
      OWNED_FLEET_TRAVEL: 'Organisation Owned Fleet/Pool Road Travel',
      OWNED_VEHICLES: 'Owned vehicles',
      PAPER: 'This is based on your response to Spend question 5 and Waste question 3',
      PAPER_CO2E: 'Paper emissions (tCO₂e)',
      PAPER_AND_CARD_CO2E: 'Paper products',
      PAPER_AND_CARD_SPEND: 'Paper products spend',
      PAPER_SPEND: 'Paper spend (&pound;)',
      PAPER_USED: 'Paper products used (tonnes)',
      PATIENT_AND_VISITOR_MILEAGE_CO2E: 'Patient and visitor travel',
      PATIENT_AND_VISITOR_MILEAGE: 'Patient and visitor travel',
      PATIENT_CONTACT_MEASURE: 'Define the measure you have used for patient contacts',
      PATIENT_MILEAGE: 'Patient Transport Mileage',
      PATIENTS_APPLIANCES_CO2E: 'Patients Appliances',
      PATIENTS_APPLIANCES: 'Patients Appliances',
      PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E: 'Patients Clothing & Footwear',
      PATIENTS_CLOTHING_AND_FOOTWEAR: 'Patients Clothing & Footwear',
      PFA_ACTION_PLAN: 'NHS Premises and Facilities Assurance - action plan (Select)',
      PFA_ACTION_PLAN_VAL: 'Cost to meet NHS Premises and Facilities Assurance action plan (£)',
      PFA_ASSESSMENT: 'NHS Premises and Facilities Assurance - Assessment/Approval (Select)',
      PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E: 'Pharmaceuticals Blood Products & Medical Gases',
      PHARMACEUTICALS_BLOOD_PRODUCTS_AND_MEDICAL_GASES: 'Pharmaceuticals Blood Products & Medical Gases',
      PHARMA_CO2E: 'Pharmaceuticals',
      PHARMA_SPEND: 'Pharmaceuticals',
      POPULATION: 'Registered population or population served',
      PORTABLE_NITROUS_OXIDE_MIX_CO2E: 'Portable Nitrous Oxide (tCO₂e)',
      PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E: 'Portable Nitrous Oxide (maternity) (tCO₂e)',
      PORTABLE_NITROUS_OXIDE_MIX_MATERNITY: 'Maternity Manifold nitrous oxide and oxygen 50/50 split - gas',
      PORTABLE_NITROUS_OXIDE_MIX: 'Portable nitrous oxide and oxygen 50/50 split - gas',
      PRIVATE_SECTOR: 'Private Sector investment (£)',
      PROC_ENV_ASSESSMENT: 'Do your commissioning, tendering and procurement processes include an assessment of the environmental impacts?',
      PROC_SOC_ASSESSMENT: 'Do your commissioning, tendering and procurement processes include an assessment of the social impacts?',
      PROC_SUPPLIER_SUSTAINABILITY: 'Do your commissioning, tendering and procurement processes include consideration of the suppliers&apos; sustainability policies?',
      PROCUREMENT_2017_CO2E_PCT: 'Procurement',
      PROCUREMENT_CO2E_PCT: 'Procurement',
      PROCUREMENT_CO2E_PER_POUND: 'Procurement (tCO₂e)',
      PROCUREMENT_CO2E: 'Procurement',
      PROMOTE_HEALTHY_TRAVEL: 'Promote healthy travel',
      PROMOTE_SUSTAINABILITY_TO_STAFF: 'Does your organisation promote sustainability to its employees?',
      PROVISIONS_CO2E: 'Provisions - Food & Catering',
      PROVISIONS: 'Provisions',
      PURCHASED_HEALTHCARE_CO2E: 'Purchased Healthcare',
      PURCHASED_HEALTHCARE: 'Purchased Healthcare',
      QUANT_ES_IMPACTS: 'What are the qualitative or quantified social and environmental impacts?',
      QUANT_ESIMPACTS: 'What are the qualitative or quantified social and environmental impacts?',
      QUANT_TRAVEL_IMPACTS: 'Does your organisation quantify the health impacts of travel and transport?',
      RAIL_MILES: 'Rail',
      REC_EQUIPT_AND_SOUVENIRS_CO2E: 'Recreational Equipment & Souvenirs',
      RECREATIONAL_EQUIPMENT_AND_SOUVENIRS: 'Recreational Equipment & Souvenirs',
      RECYCLING_CO2E: 'Recycling',
      RECYCLING_WEIGHT: 'Waste recycling weight',
      RENEWABLE_USED: 'Non-fossil fuel Consumed - renewable',
      REUSE_WEIGHT: 'Preparing for re-use',
      SCOPE_1: 'Scope 1',
      SCOPE_2: 'Scope 2',
      SCOPE_3_BIOMASS: 'Biomass',
      SCOPE_3_ENERGY_WTT: 'Energy Transmission and Well to Tank',
      SCOPE_3: 'Scope 3',
      SCOPE_3_TRAVEL: 'Travel',
      SCOPE_3_WASTE: 'Waste',
      SCOPE_3_WATER: 'Water',
      SCOPE_ALL: 'Overall Progress',
      SDMP_BOARD_REVIEW_WITHIN_12_MONTHS: 'Was the SDMP reviewed or approved by the board in the last 12 months?',
      SDMP_CRMP: 'Does your organisation have a current* Board-approved Sustainable Development Management Plan (SDMP) or Carbon Reduction Management Plan (CRMP)?',
      SDMP_MISSION_STATEMENT: 'If your SDMP has a sustainability mission statement what is it?',
      SEVOFLURANE_CO2E: 'Sevoflurane (tCO₂e)',
      SEVOFLURANE: 'Sevoflurane - liquid',
      SHORT_HAUL_AIR_MILES: 'Air - Short Haul International Flights',
      SI_TOTAL: 'Total reported investment',
      SIA: 'Does your business case process include a Sustainability Impact Assessment?',
      STAFF_AND_PATIENT_CONSULTING_SERVICES_AND_EXPENSES: 'Staff & Patient Consulting Services & Expenses',
      STAFF_CLOTHING_CO2E: 'Staff Clothing',
      STAFF_CLOTHING: 'Staff Clothing',
      STAFF_COMMUTE_MILES_CO2E: 'Staff commute',
      STAFF_COMMUTE_MILES_PP: 'Staff commute - Average annual distance travelled by road to work',
      STAFF_COMMUTE_MILES_TOTAL: 'Staff commute',
      STEAM_CO2E: 'Steam',
      STEAM_USED: 'Steam Consumed',
      STRATEGIC_SUSTAINABILITY_PARTNERSHIPS: 'Are you in a strategic partnership with other organisations on sustainability?',
      STRATEGIC_SUSTAINABILITY_PARTNERS: 'Who are they?',
      SV_SOCIAL: 'Social Value due to social activities',
      SI_SOCIAL: 'Reported investment to provided additional social activities',
      SV_JOBS: 'Social Value arising from additional jobs',
      SI_JOBS: 'Reported investment to create additional jobs',
      SV_GROWTH: 'Social Value arising from additional growth',
      SI_GROWTH: 'Reported investment to create additional growth',
      SV_ENVIRONMENT: 'Social Value due to environmental measures',
      SI_ENVIRONMENT: 'Reported investment on environmental measures',
      SV_INNOVATION: 'Social Value arising from innovation',
      SI_INNOVATION: 'Reported investment to stimulate innovation',
      SV_TOTAL: 'Total Social Value',
      SV_CTXT: 'Explanation and context for Social Value responses',
      THIRD_PARTY_ADDITIONAL_PCT: 'Percentage of third party owned supply proven as additional',
      TOTAL_CO2E_BY_BEDS: 'Total',
      TOTAL_CO2E_BY_FLOOR: 'Total',
      TOTAL_CO2E_BY_OPEX: 'Total',
      TOTAL_CO2E_BY_PATIENT_CONTACT: 'Total',
      TOTAL_CO2E_BY_POP: 'Total',
      TOTAL_CO2E_BY_WTE: 'Total',
      TOTAL_CO2E: 'Total',
      TOTAL_CORE_CO2E: 'Core emissions',
      TOTAL_CORE_CO2E_BY_BEDS: 'Core emissions',
      TOTAL_CORE_CO2E_BY_FLOOR: 'Core emissions',
      TOTAL_CORE_CO2E_BY_OPEX: 'Core emissions',
      TOTAL_CORE_CO2E_BY_PATIENT_CONTACT: 'Core emissions',
      TOTAL_CORE_CO2E_BY_POP: 'Core emissions',
      TOTAL_CORE_CO2E_BY_WTE: 'Core emissions',
      TOTAL_COMMISSIONING_CO2E: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_BEDS: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_FLOOR: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_OPEX: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_PATIENT_CONTACT: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_POP: 'Commissioning',
      TOTAL_COMMISSIONING_CO2E_BY_WTE: 'Commissioning',
      TOTAL_COMMUNITY_CO2E: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_BEDS: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_FLOOR: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_OPEX: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_PATIENT_CONTACT: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_POP: 'Community',
      TOTAL_COMMUNITY_CO2E_BY_WTE: 'Community',
      TOTAL_EMPLOYEES: 'Total Employees in Organisation',
      TOTAL_ENERGY_BY_WTE: 'Total Energy Used By WTE',
      TOTAL_ENERGY_CO2E_BY_BEDS: 'Energy',
      TOTAL_ENERGY_CO2E_BY_FLOOR: 'Energy',
      TOTAL_ENERGY_CO2E_BY_OPEX: 'Energy',
      TOTAL_ENERGY_CO2E_BY_PATIENT_CONTACT: 'Energy',
      TOTAL_ENERGY_CO2E_BY_POP: 'Energy',
      TOTAL_ENERGY_CO2E_BY_WTE: 'Energy',
      TOTAL_ENERGY_CO2E: 'Energy',
      TOTAL_ENERGY_COST: 'Total Energy Cost (all energy supplies)',
      TOTAL_ENERGY: 'Total Energy Used',
      TOTAL_PROCUREMENT_CO2E_BY_BEDS: 'Procurement',
      TOTAL_PROCUREMENT_CO2E_BY_FLOOR: 'Procurement',
      TOTAL_PROCUREMENT_CO2E_BY_OPEX: 'Procurement',
      TOTAL_PROCUREMENT_CO2E_BY_PATIENT_CONTACT: 'Procurement',
      TOTAL_PROCUREMENT_CO2E_BY_POP: 'Procurement',
      TOTAL_PROCUREMENT_CO2E_BY_WTE: 'Procurement',
      TOTAL_PROCUREMENT_CO2E: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_BEDS: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_FLOOR: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_OPEX: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_PATIENT_CONTACT: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_POP: 'Procurement',
      TOTAL_PROCUREMENT_2017_CO2E_BY_WTE: 'Procurement',
      TOTAL: 'Total',
      TOTAL_TRAVEL_CO2E_BY_BEDS: 'Travel',
      TOTAL_TRAVEL_CO2E_BY_FLOOR: 'Travel',
      TOTAL_TRAVEL_CO2E_BY_OPEX: 'Travel',
      TOTAL_TRAVEL_CO2E_BY_PATIENT_CONTACT: 'Travel',
      TOTAL_TRAVEL_CO2E_BY_POP: 'Travel',
      TOTAL_TRAVEL_CO2E_BY_WTE: 'Travel',
      TRAJECTORY_PERF: 'Trajectory performance',
      TRANSPORTATION_CO2E: 'Transportation',
      TRAVEL: 'This is based on your response to the questions in the Business and Other Travel sections',
      TRAVEL_CO2E_PCT: 'Travel',
      TRAVEL_CO2E_PER_POUND: 'Travel (tCO₂e)',
      TRAVEL_CO2E: 'Travel',
      TRAVEL_CTXT: 'Travel context',
      TRAVEL_PERF: 'Travel performance',
      TRAVEL_SPEND: 'Business travel and fleet',
      VISITOR_MILEAGE: 'Patient and Visitor Travel',
      WASTE: 'This is based on your response to the questions in the Waste section',
      WASTE_AND_WATER_CO2E: 'Waste and Water',
      WASTE_AND_WATER: 'Waste and Water',
      WASTE_CO2E: 'Waste products and recycling',
      WASTE_CTXT: 'Waste context',
      WASTE_PERF: 'Waste performance',
      WASTE_RECYLING_COST: 'Waste recycling, recovery and preparing for re-use cost',
      WASTE_WATER: 'Waste water volume (m³)',
      WATER: 'This is based on your response to the questions in the Water section',
      WATER_AND_SEWAGE_COST: 'Water and sewage cost (£)',
      WATER_CO2E: 'Water related emissions',
      WATER_COST: 'Water cost (£)',
      WATER_CTXT: 'Water context',
      WATER_PERF: 'Water performance',
      WATER_TREATMENT_CO2E: 'Water treatment related emissions',
      WATER_VOL: 'Water volume (m³)',
      WATER_VOL_BY_WTE: 'Water volume (m³)',
      WEEE_WEIGHT: 'Waste Electrical and Electronic Equipment (WEEE)',
      WOOD_CHIPS_CO2E: 'Wood chips',
      WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION: 'Non-fossil fuel Consumed - renewable (wood chips)',
      WOOD_CHIPS_WTT_CO2E: 'Wood chips WTT',
      WOOD_LOGS_CO2E: 'Wood logs',
      WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION: 'Non-fossil fuel Consumed - renewable (wood logs)',
      WOOD_LOGS_WTT_CO2E: 'Wood logs WTT',
      WOOD_PELLETS_CO2E: 'Wood pellets',
      WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION: 'Non-fossil fuel Consumed - renewable (wood pellets)',
      WOOD_PELLETS_WTT_CO2E: 'Wood pellets WTT',
      FLEET_ROAD_MILES: 'Business miles road (owned fleet)',
      BIZ_MILEAGE_ROAD_COST: 'Cost business miles road (non-owned fleet)',
      OWNED_LEASED_LOW_CARBON_MILES: '',
      BUS_MILES: 'Business miles by bus',
      TAXI_MILES: 'Business miles by taxi',
      GROUNDS_COSTS: 'Grounds and gardens maintenance (£)',
      LANDFILL_COST: 'Landfill disposal cost (£)',
      ELECTRO_BIO_MEDICAL_EQUIPT: 'Electro Bio Medical Equipment maintenance cost (£)',
      SITE_UNUSED_FOOTPRINT: 'Land area not delivering services (Hectares)',
      ISOLATION_ROOMS: 'Isolation rooms (No.)',
      WASTE_RECYLING_WEIGHT: 'Waste reycling cost (£)',
      WASTE_INCINERATION_COST: 'Incineration disposal cost (£)',
      WASTE_INCINERATION_WEIGHT: 'Incineration disposal volume (Tonnes)',
      OTHER_RECOVERY_COST: 'Other recovery cost (£)',
      COST_SAFETY_RISK: 'Cost to eradicate Safety related Critical Infrastructure Risk (£)',
      COST_COMPLIANCE_RISK: 'Cost to eradicate non-compliance related Critical Infrastructure Risk (£)',
      COST_CONTINUITY_RISK: 'Cost to eradicate continuity related Critical Infrastructure Risk (£)',
      CHP_SIZE: 'CHP unit/s size (Watts)',
      CHP_EFFICIENCY: 'CHP unit/s efficiency (%)',
      ELEC_PEAK_LOAD: 'Peak electrical load (MW)',
      ELEC_MAX_LOAD: 'Maximum electrical load (MW)',
      PLASTICS_PLEDGE: 'Pledge on reduction of single-use plastics',
      NO_PLASTICS_CUPS: 'Number of plastic cups',
      NO_PLASTIC_LINED_COFFEE_CUPS: 'Number of plastic lined coffee cups',
      NO_PLASTIC_PLATES: 'Number of plastic plates',
      NO_PLASTIC_CUTLERY: 'Number of plastic cutlery',
      NO_PLASTIC_STRAWS: 'Number of plastic straws',
      NO_PLASTIC_STIRRERS: 'Number of plastic stirrers',
      NO_PLASTIC_FOOD_CONTAINERS: 'Number of plastic food containers',
      PLASTICS_CTXT: 'Statement about implementation of single-use plastic reduction',
      ULEV_POLICY: 'Low and Ultra-Low emission vehicle policy statement',
      NOX_PPM: 'NOx emissions',
      PARTICULATES_PPM: 'Particulates (PM<sub>2.5</sub>) emissions',
      VEHICLES_NO: 'No. of fleet vehicles',
      VEHICLES_LEV_PCT: 'Percentage of fleet vehicles that are LEV',
      VEHICLES_ULEV_PCT: 'Percentage of fleet vehicles that are ULEV',
      NO_EV_CHARGE_POINTS: 'No. of EV charge points',
      AIR_POLLUTION_CTXT: 'Statement about air pollution strategy implementation'
    },
    narrativeCtxtPrompt: 'Please insert a commentary on contextual information e.g. projects, initiatives etc. related to this area.',
    narrativePerfPrompt: 'Please click to insert a commentary on your performance in this area.',
    orgAnswerNames: [
      'PROVIDER1_COMMISSIONED',
      'PROVIDER2_COMMISSIONED',
      'PROVIDER3_COMMISSIONED',
      'PROVIDER4_COMMISSIONED',
      'PROVIDER5_COMMISSIONED',
      'PROVIDER6_COMMISSIONED',
      'PROVIDER7_COMMISSIONED',
      'PROVIDER8_COMMISSIONED',
      'CCG1_SERVED',
      'CCG2_SERVED',
      'CCG3_SERVED',
      'CCG4_SERVED',
      'CCG5_SERVED',
      'CCG6_SERVED',
      'CCG7_SERVED',
      'CCG8_SERVED'
    ],
    requiredAnswers: [
      'ORG_CODE',
      'ORG_NAME',
      'ORG_TYPE',
      'SDMP_CRMP',
      'HEALTHY_TRANSPORT_PLAN',
      'PROMOTE_HEALTHY_TRAVEL'
    ],
    period: '2019-20',
    server: $env.server,
    survey: 'SDU-2019-20',
    tenant: {
      id: 'sdu'
    },
    username: localStorage.username,
    formatAbsAnswer: function (e, t) {
      try {
        var a = ractive.getAnswer(e, t);
        return null == a ? '' : Math.abs(a)
      } catch (e) {
        return ''
      }
    },
    formatAnswer: function (e, t) {
      if (null == e || null == ractive.get('surveyReturn')) return '';
      try {
        var a = ractive.getAnswer(e, t);
        return null == a ? '' : a
      } catch (e) {
        return ''
      }
    },
    formatCommissionerAnswer: function (e, t) {
      if (null == t || null == ractive.get('surveyReturn.commissioners.' + e)) return '';
      try {
        return ractive.getAnswer(t, ractive.get('period'), ractive.get('surveyReturn.commissioners.' + e + '.answers'))
      } catch (e) {
        return ''
      }
    },
    formatDateTime: function (e) {
      return null == e ? 'n/a' : new Date(e).toLocaleString(navigator.languages)
    },
    formatHint: function (e) {
      for (i in ractive.get('q.categories')) for (j in ractive.get('q.categories.' + i + '.questions')) if (ractive.get('q.categories.' + i + '.questions.' + j + '.name') == e) return 'This is based on your response to ' + ractive.get('q.categories.' + i + '.name') + ' question ' + (parseInt(j) + 1);
      return null != ractive.get('labels.' + e) ? ractive.get('labels.' + e)  : e
    },
    formatProviderAnswer: function (e, t) {
      if (null == t || null == ractive.get('surveyReturn.providers.' + e)) return '';
      try {
        return ractive.getAnswer(t, ractive.get('period'), ractive.get('surveyReturn.providers.' + e + '.answers'))
      } catch (e) {
        return ''
      }
    },
    haveAnswer: function (e) {
      try {
        var t = ractive.getAnswer(e);
        return null != t && '' != t && t != ractive.get('narrativeCtxtPrompt') && t != ractive.get('narrativePerfPrompt')
      } catch (e) {
        return !1
      }
    },
    isCcg: function () {
      return ractive.isCcg()
    },
    isEClassUser: function () {
      return ractive.isEClassUser()
    },
    matchRole: function (e) {
      return console.info('matchRole: ' + e),
      !(null != e && !ractive.hasRole(e)) && ($('.' + e).show(), !0)
    },
    matchSearch: function (e) {
      var t = ractive.get('searchTerm');
      return null == t || 0 == t.length || (0 <= e.question.name.toLowerCase().indexOf(t.toLowerCase()) || 0 <= e.applicablePeriod.toLowerCase().indexOf(t.toLowerCase()) || 0 <= e.status.toLowerCase().indexOf(t.toLowerCase()) || t.startsWith('revision:') && e.revision == t.substring(9) || t.startsWith('updated>') && new Date(e.lastUpdated) > new Date(t.substring(8)) || t.startsWith('created>') && new Date(e.created) > new Date(t.substring(8)) || t.startsWith('updated<') && new Date(e.lastUpdated) < new Date(t.substring(8)) || t.startsWith('created<') && new Date(e.created) < new Date(t.substring(8)))
    },
    sort: function (array, column, asc) {
      return console.info('sort ' + (asc ? 'ascending' : 'descending') + ' on: ' + column),
      array = array.slice(),
      array.sort(function (a, b) {
        console.log('sort ' + a + ',' + b);
        var aVal = eval('a.' + column),
        bVal = eval('b.' + column);
        return null == bVal || null == bVal || '' == bVal ? null == aVal || null == aVal || '' == aVal ? 0 : - 1 : asc ? aVal < bVal ? - 1 : 1 : bVal < aVal ? - 1 : 1
      })
    },
    sortAsc: !0,
    sortColumn: 'id',
    sorted: function (e) {
      return 'hidden'
    },
    stdPartials: [
      {
        name: 'loginSect',
        url: $env.server + '/webjars/auth/1.1.0/partials/login-sect.html'
      },
      {
        name: 'sidebar',
        url: $env.server + '/partials/sidebar.html'
      },
      {
        name: 'toolbar',
        url: $env.server + '/partials/toolbar.html'
      },
      {
        name: 'statusSect',
        url: '/srp/2.3.0/partials/status-sect.html'
      }
    ]
  },
  partials: {
    loginSect: '',
    shareCtrl: '<div class="controls pull-right" style="display:none">  <span class="glyphicon icon-btn kp-icon-share"></span>  <!--span class="glyphicon icon-btn kp-icon-link"></span-->  <!--span class="glyphicon icon-btn kp-icon-copy"></span--></div>'
  },
  calculate: function () {
    console.info('calculate...'),
    $('.btn-calc,.btn-refresh,.btn-submit').attr('disabled', 'disabled'),
    $('#ajax-loader').show(),
    $.ajax({
      dataType: 'json',
      type: 'POST',
      url: ractive.getServer() + '/calculations/' + ractive.get('survey') + '/' + ractive.get('org'),
      crossDomain: !0,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Authorization': 'Bearer ' + localStorage.token,
        'Cache-Control': 'no-cache'
      },
      success: ractive.fetchSuccessHandler
    })
  },
  copyLink: function (e) {
    if (null == navigator.clipboard) alert('Please upgrade your browser to be able to copy links');
     else {
      navigator.clipboard.writeText(e.target.getAttribute('data-share')),
      console.info('copied link');
      new iqwerty.toast.Toast('Copied!', {
        style: {
          main: {
            background: '#0078c1',
            color: 'white',
            'box-shadow': '0 0 50px rgba(0, 120, 193, .7)'
          }
        }
      })
    }
    return !1
  },
  enter: function () {
    console.info('enter...')
  },
  fetch: function () {
    console.info('fetch...'),
    null != getSearchParameters().id && ractive.hasRole('analyst') ? ractive.fetchExplicit()  : ractive.fetchImplicit()
  },
  fetchExplicit: function () {
    $.ajax({
      dataType: 'json',
      url: ractive.getServer() + '/returns/' + getSearchParameters().id,
      crossDomain: !0,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Authorization': 'Bearer ' + localStorage.token,
        'Cache-Control': 'no-cache'
      },
      success: ractive.fetchSuccessHandler
    })
  },
  fetchImplicit: function () {
    null != $auth.getClaim('org') && (ractive.set('org', $auth.getClaim('org')), $.ajax({
      dataType: 'json',
      url: ractive.getServer() + '/returns/findCurrentBySurveyNameAndOrg/' + ractive.get('survey') + '/' + ractive.get('org'),
      crossDomain: !0,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Authorization': 'Bearer ' + localStorage.token,
        'Cache-Control': 'no-cache'
      },
      success: ractive.fetchSuccessHandler
    }))
  },
  fetchCsv: function (a, r) {
    var e = - 1 == $(a).data('src').indexOf('//') ? ractive.getServer() + $(a).data('src')  : $(a).data('src').replace(/host:port/, window.location.host).attr('width', '1024').attr('width', '400');
    $.ajax({
      dataType: 'text',
      url: e,
      crossDomain: !0,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Authorization': 'Bearer ' + localStorage.token,
        'Cache-Control': 'no-cache'
      },
      success: function (e) {
        ractive.set('saveObserver', !1);
        var t = {
        };
        null != $(a).data('colors') && (t.colors = $(a).data('colors').split(',')),
        null != $(a).data('labels') && (t.labels = $(a).data('labels')),
        null != $(a).data('other') && (t.other = parseFloat($(a).data('other'))),
        null != $(a).data('x-axis-label') && (t.xAxisLabel = $(a).data('x-axis-label')),
        null != $(a).data('y-axis-label') && (t.yAxisLabel = $(a).data('y-axis-label')),
        $(a).on('mouseover', function (e) {
          $('#' + e.currentTarget.id + ' .controls').show()
        }).on('mouseout', function (e) {
          $('#' + e.currentTarget.id + ' .controls').hide()
        }),
        $('#' + a.id + ' .controls .kp-icon-new-tab').wrap('<a href="' + ractive.getServer() + $(a).data('src') + '" target="_blank"></a>'),
        r('#' + a.id, e, t),
        ractive.set('saveObserver', !0)
      }
    })
  },
  fetchOrgData: function () {
    console.info('fetchOrgData'),
    $('#ajax-loader').show(),
    ractive.set('saveObserver', !1),
    ractive.set('surveyReturn.orgs', [
    ]);
    for (var e = ractive.get('orgAnswerNames').filter(function (e, t) {
      return ractive.isCcg() && e.startsWith('PROVIDER') || !ractive.isCcg() && e.startsWith('CCG')
    }), t = 0; t < e.length; t++) try {
      var a = ractive.getAnswer(e[t]);
      if (null == a || 0 == a.trim().length) continue;
      $.ajax({
        dataType: 'json',
        url: ractive.getServer() + '/' + ractive.get('tenant.id') + '/accounts/findByName/' + encodeURIComponent(a),
        crossDomain: !0,
        headers: {
          'X-Requested-With': 'XMLHttpRequest',
          'X-Authorization': 'Bearer ' + localStorage.token,
          'Cache-Control': 'no-cache'
        },
        success: function (e) {
          for (ractive.set('saveObserver', !1), j = 0; j < ractive.get('surveyReturn.orgs').length; j++) ractive.get('surveyReturn.orgs.' + j + '.name') == e.name && ractive.splice('surveyReturn.orgs', j, 1);
          ractive.push('surveyReturn.orgs', e),
          $('#ajax-loader').hide(),
          ractive.set('saveObserver', !0)
        },
        error: function (e, t, a) {
          var r = this.url.substring(this.url.lastIndexOf('/') + 1);
          console.warn('Unable to fetch data for ' + r + '.' + e.status + ':' + t + ',' + a),
          $('#ajax-loader').hide(),
          ractive.set('saveObserver', !0)
        }
      })
    } catch (e) {
      console.info('Assume no org at idx: ' + t)
    }
  },
  fetchSuccessHandler: function (e) {
    ractive.set('saveObserver', !1),
    Array.isArray(e) ? ractive.set('surveyReturn', e[0])  : ractive.set('surveyReturn', e),
    ractive.isCcg(),
    ractive.fetchOrgData(),
    null != ractive.fetchCallbacks && ractive.fetchCallbacks.fire(),
    $('.rpt.pie').each(function (e, t) {
      ractive.fetchCsv(t, renderPie)
    }),
    $('.rpt.pie2').each(function (e, t) {
      ractive.renderCsvForPie(t, renderPie)
    }),
    $('.rpt.stacked,.rpt.stacked2').each(function (e, t) {
      switch (!0) {
        case window.innerWidth < 480:
          $(t).attr('width', 440).attr('height', 0.4 * window.innerHeight);
          break;
        case window.innerWidth < 768:
        case window.innerWidth < 980:
          $(t).attr('width', 720).attr('height', 0.4 * window.innerHeight);
          break;
        case window.innerWidth < 1200:
          $(t).attr('width', 0.8 * window.innerWidth).attr('height', 0.4 * window.innerHeight);
          break;
        default:
          $(t).attr('width', 1140).attr('height', 0.4 * window.innerHeight)
      }
    }), $('.rpt.stacked').each(function (e, t) {
      ractive.fetchCsv(t, renderStacked)
    }), $('.rpt.stacked2').each(function (e, t) {
      ractive.renderCsv(t, renderStacked)
    }), $('.rpt.table').each(function (e, t) {
      ractive.fetchTable(t)
    }), $('.rpt.table2').each(function (e, t) {
      ractive.renderTable(t)
    }), 'Draft' == e.status && ($('.btn-calc').removeAttr('disabled'), $('.btn-refresh').removeAttr('disabled'), $('.btn-submit').removeAttr('disabled'), $('.btn-restate').attr('disabled', 'disabled')), 'Submitted' == e.status && ($('.btn-calc').attr('disabled', 'disabled'), $('.btn-refresh').attr('disabled', 'disabled'), $('.btn-submit').attr('disabled', 'disabled'), $('.btn-restate').removeAttr('disabled')); var t = 0, a = 0, r = 0, i = 0, n = [
    ]; for (idx = 0; idx < ractive.get('surveyReturn.answers').length; idx++) t++, 1 == ractive.get('surveyReturn.answers.' + idx + '.question.required') && a++, null != ractive.get('surveyReturn.answers.' + idx + '.response') && '' != ractive.get('surveyReturn.answers.' + idx + '.response') && 0 == ractive.get('surveyReturn.answers.' + idx + '.derived') && 'Superseded' != ractive.get('surveyReturn.answers.' + idx + '.status') && (i++, 1 == ractive.get('surveyReturn.answers.' + idx + '.question.required') && r++, n.push(ractive.get('surveyReturn.answers.' + idx + '.applicablePeriod'))); ractive.set('surveyReturn.completeness.qs', t), ractive.set('surveyReturn.completeness.coreQs', a), ractive.set('surveyReturn.completeness.answeredCoreQs', r), ractive.set('surveyReturn.completeness.answeredQs', i), ractive.set('surveyReturn.completeness.periods', n.uniq()); var s = [
    ]; for (idx = 0; idx < ractive.get('requiredAnswers').length; idx++) null != ractive.getAnswer(ractive.get('requiredAnswers.' + idx)) && s.push(ractive.get('requiredAnswers.' + idx)); ractive.set('surveyReturn.completeness.missingrequired', s), ractive.initNarrative(), $('title').empty().append('Sustainability Report &ndash; ' + ractive.get('surveyReturn.applicablePeriod') + ' &ndash; ' + ractive.get('surveyReturn.org')), ractive.fetchSurvey(), ractive.set('saveObserver', !0)
  },
  fetchSurvey: function () {
    console.info('fetchSurvey'),
    $.ajax({
      type: 'GET',
      url: ractive.getServer() + '/surveys/findByName/' + ractive.get('survey'),
      dataType: 'json',
      success: function (e, t, a) {
        console.log('success:' + e),
        ractive.set('q', e)
      }
    })
  },
  fetchTable: function (t) {
    $.ajax({
      dataType: 'html',
      url: ractive.getServer() + $(t).data('src'),
      crossDomain: !0,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'X-Authorization': 'Bearer ' + localStorage.token,
        'Cache-Control': 'no-cache'
      },
      success: function (e) {
        ractive.set('saveObserver', !1),
        $(t).empty().append(e).on('mouseover', function (e) {
          $('#' + e.currentTarget.id + ' .controls').show()
        }).on('mouseout', function (e) {
          $('#' + e.currentTarget.id + ' .controls').hide()
        }),
        $('#' + t.id + ' .controls .kp-icon-new-tab').wrap('<a href="' + ractive.getServer() + $(t).data('src') + '" target="_blank"></a>'),
        ractive.set('saveObserver', !0)
      }
    })
  },
  formatNumber: function () {
    $('.number').each(function (e, t) {
      var a = t.innerText.substring(0, t.innerText.indexOf('.'));
      t.innerText = a.replace(new RegExp('^(\\d{' + (a.length % 3 ? a.length % 3 : 0) + '})(\\d{3})', 'g'), '$1,$2').replace(/(\d{3})+?/gi, '$1,').replace(/^,/, '').slice(0, - 1)
    })
  },
  getAnswer: function (e, t) {
    null == t && (t = ractive.get('surveyReturn.applicablePeriod'));
    var a = ractive.get('surveyReturn.answers');
    return ractive.getAnswerFromArray(e, t, a)
  },
  getAnswerFromArray: function (e, t, a, r) {
    for (var i = 0; i < a.length; i++) {
      var n = a[i];
      if (n.question.name == e && n.applicablePeriod == t && 'true' == n.response) return !0;
      if (n.question.name == e && n.applicablePeriod == t && 'false' == n.response) return !1;
      if (n.question.name == e && n.applicablePeriod == t && 'number' == n.question.type && 1 == r) {
        var s = null == n.response ? 0 : parseFloat(n.response);
        return isNaN(s) ? 0 : s
      }
      if (n.question.name == e && n.applicablePeriod == t && 'number' == n.question.type && parseFloat(n.response) < 100) return parseFloat(n.response).sigFigs(3);
      if (n.question.name == e && n.applicablePeriod == t && 'number' == n.question.type) return parseFloat(n.response).formatDecimal(0);
      if (n.question.name == e && n.applicablePeriod == t) return n.response
    }
    return 'number' == n.question.type ? 0 : void 0
  },
  getPeriod: function (e) {
    var t = parseInt(ractive.get('period').substring(0, 4)),
    a = t - 1999 + e;
    return t + e + '-' + (a < 10 ? '0' + a : a)
  },
  initNarrative: function () {
    if ('Draft' != ractive.get('surveyReturn.status')) return console.info('Skipping initNarrative because status is ' + ractive.get('surveyReturn.status')),
    void $('.narrative').removeAttr('contenteditable');
    $('.narrative').attr('contenteditable', 'true').click(function (e) {
      $(e.target).hasClass('narrative') ? console.info('Edit... ' + $(e.target).data('q'))  : console.info('Edit: ' + e.target.tagName + $(e.target).closest('contenteditable').data('q'))
    }).blur(function (e) {
      $(e.target).hasClass('narrative') ? ractive.saveNarrative($(e.target).data('q'), e.target.innerText)  : ractive.saveNarrative($(e.target).closest('contenteditable').data('q'), e.target.innerText)
    }),
    $('.narrative.context:empty').html(ractive.get('narrativeCtxtPrompt')),
    $('.narrative.performance:empty').html(ractive.get('narrativePerfPrompt'))
  },
  isCcg: function () {
    return null != ractive.get('surveyReturn') && 'Clinical Commissioning Group' == ractive.getAnswer('ORG_TYPE')
  },
  isEClassUser: function () {
    return null != ractive.getAnswer('ECLASS_USER') && 0 == ractive.getAnswer('ECLASS_USER').charAt(0)
  },
  oninit: function () {
  },
  renderLabel: function (e) {
    return null != ractive.get('labels.' + e) ? ractive.get('labels.' + e)  : e.toLabel()
  },
  renderCsv: function (e, t) {
    console.log('renderCsv: ' + e.id);
    for (var a = parseInt(e.getAttribute('data-periods')), r = e.getAttribute('data-qs').split(','), i = 'Period,', n = 0; n < r.length; n++) i += ractive.renderLabel(r[n]),
    n + 1 < r.length ? i += ',' : i += '\n';
    for (var s = ractive.get('surveyReturn.answers'), o = 0; o < a; o++) {
      var E = ractive.getPeriod(o - a + 1);
      i += E,
      i += ',';
      for (n = 0; n < r.length; n++) i += ractive.getAnswerFromArray(r[n], E, s, !0),
      n + 1 < r.length ? i += ',' : i += '\n'
    }
    var c = {
    };
    null != $(e).data('colors') && (c.colors = $(e).data('colors').split(',')),
    null != $(e).data('labels') && (c.labels = $(e).data('labels')),
    null != $(e).data('other') && (c.other = parseFloat($(e).data('other'))),
    null != $(e).data('x-axis-label') && (c.xAxisLabel = $(e).data('x-axis-label')),
    null != $(e).data('y-axis-label') && (c.yAxisLabel = $(e).data('y-axis-label')),
    $(e).on('mouseover', function (e) {
      $('#' + e.currentTarget.id + ' .controls').show()
    }).on('mouseout', function (e) {
      $('#' + e.currentTarget.id + ' .controls').hide()
    }),
    $('#' + e.id + ' .controls .kp-icon-new-tab').wrap('<a href="' + ractive.getServer() + $(e).data('src') + '" target="_blank"></a>'),
    t('#' + e.id, i, c)
  },
  renderCsvForPie: function (e, t) {
    console.log('renderCsvForPie: ' + e.id);
    for (var a = ractive.getPeriod(parseInt(1 - e.getAttribute('data-periods'))), r = e.getAttribute('data-qs').split(','), i = ractive.get('surveyReturn.answers'), n = 'classification,percentage\n', s = 0; s < r.length; s++) n += ractive.renderLabel(r[s]),
    n += ',',
    n += ractive.getAnswerFromArray(r[s], a, i, !0),
    n += '\n';
    var o = {
    };
    null != $(e).data('colors') && (o.colors = $(e).data('colors').split(',')),
    null != $(e).data('labels') && (o.labels = $(e).data('labels')),
    null != $(e).data('other') && (o.other = parseFloat($(e).data('other'))),
    null != $(e).data('x-axis-label') && (o.xAxisLabel = $(e).data('x-axis-label')),
    null != $(e).data('y-axis-label') && (o.yAxisLabel = $(e).data('y-axis-label')),
    $(e).on('mouseover', function (e) {
      $('#' + e.currentTarget.id + ' .controls').show()
    }).on('mouseout', function (e) {
      $('#' + e.currentTarget.id + ' .controls').hide()
    }),
    $('#' + e.id + ' .controls .kp-icon-new-tab').wrap('<a href="' + ractive.getServer() + $(e).data('src') + '" target="_blank"></a>'),
    t('#' + e.id, n, o)
  },
  renderTable: function (e) {
    console.log('renderTable: ' + e.id);
    for (var t = parseInt(e.getAttribute('data-periods')), a = e.getAttribute('data-qs').split(','), r = '<table class="table table-striped"><thead><tr><th>&nbsp;</th><th>&nbsp;</th>', i = 1; i <= t; i++) r += '<th class="number">' + ractive.getPeriod(i - t) + '</th>';
    r += '</tr><tbody>';
    for (var n = 0; n < a.length; n++) {
      r += '<th>' + ractive.renderLabel(a[n]) + '</th>',
      r += '<th class="legend ' + a[n].toLowerCase() + '">&nbsp;</th>';
      for (i = 1; i <= t; i++) r += '<td class="number">' + ractive.getAnswer(a[n], ractive.getPeriod(i - t)) + '</td>';
      r += '</tr>'
    }
    if (r += '</tbody><tfoot>', 'true' == e.getAttribute('data-total')) {
      r += '<th>Total</th><th>&nbsp;</th>';
      for (i = 1; i <= t; i++) r += '<td class="number">' + ractive.total(a, ractive.getPeriod(i - t)) + '</td>'
    }
    e.hasAttribute('data-share') && (r += '<tr><td colspan="' + (t + 2) + '"><a href="#" title="Copy link to this table" data-share="' + e.getAttribute('data-share') + '" onclick="return ractive.copyLink(event);" class="pull-right no-print"><span class="glyphicon glyphicon-copy"></span>Copy</a><a href="' + e.getAttribute('data-share') + '" target="_blank" title="Open the table in a new window" class="pull-right no-print"><span class="glyphicon glyphicon-share-alt"></span>Share</a></td></tr>'),
    r += '</tfoot></table>',
    $(e).empty().append(r)
  },
  reset: function () {
    if (console.info('reset'), document.getElementById('resetForm').checkValidity()) {
      $('#resetSect').slideUp(),
      $('#loginSect').slideDown();
      var t = $('#email').val();
      $.ajax({
        url: ractive.getServer() + '/msg/srp/omny.passwordResetRequest.json',
        type: 'POST',
        data: {
          json: JSON.stringify({
            email: t,
            tenantId: 'srp'
          })
        },
        dataType: 'text',
        success: function (e) {
          ractive.showMessage('A reset link has been sent to ' + t)
        }
      })
    } else ractive.showError('Please enter the email address you registered with')
  },
  restate: function () {
    console.info('restate...'),
    $('#ajax-loader').show();
    var e = {
      businessKey: 'Sustainability report ' + ractive.get('surveyReturn.revision') + ' for ' + ractive.get('surveyReturn.org') + ' ' + ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'RestateSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: $auth.getClaim('sub'),
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Restating your return, this may take a little while...'),
    $.ajax({
      url: ractive.getServer() + '/' + ractive.get('tenant.id') + '/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(e),
      success: function (e, t, a) {
        console.log('response: ' + a.status + ', Location: ' + a.getResponseHeader('Location')),
        ractive.showMessage('Created a new, draft revision of your return'),
        $('#ajax-loader').hide(),
        ractive.fetch()
      }
    })
  },
  saveNarrative: function (e, t) {
    console.info('saveNarrative of ' + e + ' as ' + t),
    null != e && null != t && t.trim() != ractive.get('narrativePrompt') && ($('.save-indicator').show(), $.ajax({
      url: ractive.getServer() + '/returns/' + ractive.get('surveyReturn.id') + '/answers/' + e,
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(t),
      success: function (e) {
        console.info('...saved'),
        $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved'),
        setTimeout(function () {
          $('.save-indicator').fadeOut(function () {
            $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved')
          })
        }, 3000)
      }
    }))
  },
  showReset: function () {
    $('#loginSect').slideUp(),
    $('#resetSect').slideDown()
  },
  submit: function () {
    console.info('submit...'),
    $('#ajax-loader').show();
    var e = {
      businessKey: 'Sustainability report ' + ractive.get('surveyReturn.revision') + ' for ' + ractive.get('surveyReturn.org') + ' ' + ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'SubmitSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: $auth.getClaim('sub'),
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Submitting your return...'),
    $.ajax({
      url: ractive.getServer() + '/' + ractive.get('tenant.id') + '/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(e),
      success: function (e, t, a) {
        console.log('response: ' + a.status + ', Location: ' + a.getResponseHeader('Location')),
        ractive.showMessage('Your return has been received'),
        $('#ajax-loader').hide(),
        ractive.fetch()
      }
    })
  },
  toggleFieldHint: function (e) {
    console.log('toggleFieldHint'),
    0 == $('#' + e + 'Hint:visible').length ? $('#' + e + 'Hint').slideDown(ractive.get('easingDuration')).removeClass('hidden')  : $('#' + e + 'Hint').slideUp(ractive.get('easingDuration'))
  },
  total: function (e, t) {
    var a = 0,
    r = ractive.get('surveyReturn.answers');
    for (i = 0; i < e.length; i++) {
      var n = ractive.getAnswerFromArray(e[i], t, r, !0);
      isNaN(n) || (a += parseFloat(n))
    }
    return a < 100 ? a.sigFigs(3)  : a.formatDecimal(0)
  }
}); $(document).ready(function () {
  $('head').append('<link href="' + ractive.getServer() + '/css/sdu-1.0.0.css" rel="stylesheet">'),
  $('.menu-burger, .toolbar').addClass('no-print'),
  - 1 != Object.keys(getSearchParameters()).indexOf('error') ? ractive.showError('The username and password provided do not match a valid account')  : - 1 != Object.keys(getSearchParameters()).indexOf('logout') && ractive.showMessage('You have been successfully logged out'),
  $auth.addLoginCallback(ractive.fetch),
  $auth.addLoginCallback(ractive.getProfile)
}), ractive.observe('searchTerm', function (e, t, a) {
  console.log('searchTerm changed'),
  setTimeout(function () {
    ractive.set('searchMatched', $('#answersTable tbody tr').length)
  }, 500)
}), ractive.on('sort', function (e, t) {
  console.info('sort on ' + t),
  this.get('sortColumn') == t && this.set('sortAsc', !this.get('sortAsc')),
  this.set('sortColumn', t)
});

