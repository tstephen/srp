package digital.srp.sreport.model;

public enum Q implements QuestionEnum {

    TEST,

    COMMISSIONING_SPEND, TRAVEL_SPEND, PHARMA_SPEND, PAPER_SPEND, OTHER_SPEND, OTHER_MANUFACTURED_SPEND, MED_INSTR_SPEND, CHEM_AND_GAS_SPEND, ICT_SPEND, FREIGHT_SPEND, CATERING_SPEND, CONSTRUCTION_SPEND, BIZ_SVCS_SPEND, LEASED_ASSETS_ENERGY_USE, ELEC_OWNED_RENEWABLE_USED,ELEC_OWNED_RENEWABLE_USED_SDU, WOOD_PELLETS_USED, WOOD_CHIPS_USED, WOOD_LOGS_USED, EXPORTED_THERMAL_ENERGY, CHP_ELECTRICAL_OUTPUT, PORTABLE_NITROUS_OXIDE_MIX, NITROUS_OXIDE, SEVOFLURANE, ISOFLURANE, DESFLURANE, STAFF_COMMUTE_MILES_PP, STAFF_COMMUTE_MILES_TOTAL, VISITOR_MILEAGE, PATIENT_MILEAGE,
    LONG_HAUL_AIR_MILES, SHORT_HAUL_AIR_MILES, DOMESTIC_AIR_MILES, RAIL_MILES,
    FLEET_ROAD_MILES, FLEET_COST,
    WASTE_WATER, RECYCLING_WEIGHT, COMPOSTED_WEIGHT, REUSE_WEIGHT,
    NON_BURN_WEIGHT, WEEE_WEIGHT, LANDFILL_WEIGHT, HIGH_TEMP_DISPOSAL_WEIGHT, HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT, WATER_VOL, ELEC_EXPORTED, ELEC_RENEWABLE_USED, HOT_WATER_USED, STEAM_USED, ELEC_USED_LOCAL, COAL_USED, OIL_USED, GAS_USED, ELEC_USED, CAPITAL_SPEND, NON_PAY_SPEND, OP_EX, WASTE_RECYLING_COST, WATER_COST, WATER_AND_SEWAGE_COST, TOTAL_BIZ_TRAVEL_COST,
    TOTAL_ENERGY_COST, ENERGY_COST_CHANGE_PCT,
    CCG1_SERVED, CCG2_SERVED, CCG3_SERVED, CCG4_SERVED, CCG5_SERVED, CCG6_SERVED,
    PROVIDER1_COMMISSIONED, PROVIDER2_COMMISSIONED, PROVIDER3_COMMISSIONED,
    PROVIDER4_COMMISSIONED, PROVIDER5_COMMISSIONED, PROVIDER6_COMMISSIONED,
    PROVIDER7_COMMISSIONED, PROVIDER8_COMMISSIONED,
    ADAPTATION_PLAN_INC, BOARD_SUSTAINABILITY_AS_RISK, BOARD_LEAD_FOR_SUSTAINABILITY, PATIENT_CONTACT_MEASURE, CARBON_REDUCTION_DEADLINE_YEAR, CARBON_REDUCTION_TARGET_PCT, CARBON_REDUCTION_BASE_YEAR, CARBON_REDUCTION_BASELINE_USED, CARBON_REDUCTION_TARGET, ECLASS_USER, PROMOTE_SUSTAINABILITY_TO_STAFF, LAST_GCC_SCORE, LAST_GCC_DATE, GCC_USER,
    PROC_SUPPLIER_ENGAGEMENT,
    // no longer used
    STRATEGIC_SUSTAINABILITY_PARTNERS, STRATEGIC_SUSTAINABILITY_PARTNERSHIPS,
    PROC_SUPPLIER_SUSTAINABILITY, PROC_SOC_ASSESSMENT, PROC_ENV_ASSESSMENT,
    PROMOTE_HEALTHY_TRAVEL, HEALTHY_TRANSPORT_PLAN,
    SDMP_MISSION_STATEMENT, SDMP_BOARD_REVIEW_WITHIN_12_MONTHS, SDMP_CRMP, SDMP_INC_PROCUREMENT,
    SOCIAL_VALUE, BIODIVERSITY, SDG_STARTING, SDG_CLEAR,
    NO_PATIENT_CONTACTS,
    /* NO_BEDS: use OCCUPIED BEDS */
    NO_STAFF, POPULATION, FLOOR_AREA, ORG_TYPE, ORG_NICKNAME, ORG_CODE, ORG_NAME,

    THIRD_PARTY_ADDITIONAL_PCT, GREEN_TARIFF_ADDITIONAL_PCT, PORTABLE_NITROUS_OXIDE_MIX_MATERNITY, PORTABLE_NITROUS_OXIDE_MIX_TOTAL, PORTABLE_NITROUS_OXIDE_TOTAL, INCINERATION_WEIGHT, OTHER_RECOVERY_WEIGHT, ADAPTATION_PERF, BENCHMARK_PERF, TRAJECTORY_PERF, WATER_CTXT, WATER_PERF, WASTE_CTXT, WASTE_PERF, TRAVEL_CNTXT, TRAVEL_PERF, ENERGY_CTXT, ENERGY_PERF, SIA, MOD_SLAVERY, QUANT_TRAVEL_IMPACTS, QUANT_ES_IMPACTS,

    /* CALCULATED */

    ELEC_RENEWABLE, ELEC_RENEWABLE_CO2E, HOT_WATER_CO2E,

    STEAM_CO2E, EXPORTED_THERMAL_ENERGY_CO2E,

    ELEC_CO2E,

    ELEC_USED_GREEN_TARIFF,

    // sum PATIENT_MILEAGE, VISITOR_MILEAGE
    PATIENT_AND_VISITOR_MILEAGE,
    // sum LONG_HAUL_AIR_MILES, SHORT_HAUL_AIR_MILES,
    // DOMESTIC_AIR_MILES, RAIL_MILES,
    LEASED_FLEET_TRAVEL, LEASED_FLEET_TRAVEL_CO2E, OWNED_FLEET_TRAVEL, OWNED_FLEET_TRAVEL_CO2E, BIZ_MILEAGE, BIZ_MILEAGE_ROAD, BIZ_MILEAGE_ROAD_COST,
    PATIENT_AND_VISITOR_MILEAGE_CO2E, BIZ_MILEAGE_ROAD_CO2E,
    BIZ_MILEAGE_RAIL_CO2E, BIZ_MILEAGE_AIR_CO2E,
    BIZ_MILEAGE_BUS_CO2E, BIZ_MILEAGE_TAXI_CO2E, BIZ_MILEAGE_CO2E,
    FUEL_WTT, STAFF_COMMUTE_MILES, STAFF_COMMUTE_MILES_CO2E,

    RECYCLING_CO2E, OTHER_RECOVERY_CO2E, INCINERATION_CO2E, LANDFILL_CO2E,

    COMMISSIONING_CO2E, ANAESTHETIC_GASES_CO2E, PHARMA_CO2E, PAPER_CO2E, OTHER_PROCUREMENT_CO2E, OTHER_MANUFACTURED_CO2E, MED_INSTR_CO2E, CHEM_AND_GAS_CO2E, ICT_CO2E, FREIGHT_CO2E, CATERING_CO2E, CONSTRUCTION_CO2E, BIZ_SVCS_CO2E, CAPITAL_CO2E, WATER_CO2E, WATER_TREATMENT_CO2E, WASTE_CO2E, TRAVEL_CO2E,

    ENERGY_CO2E_PCT, COMMISSIONING_CO2E_PCT, PROCUREMENT_CO2E_PCT, TRAVEL_CO2E_PCT,

    ENERGY_CO2E_PER_POUND, COMMISSIONING_CO2E_PER_POUND, PROCUREMENT_CO2E_PER_POUND, TRAVEL_CO2E_PER_POUND,

    OWNED_BUILDINGS, OWNED_BUILDINGS_GAS, OWNED_BUILDINGS_OIL, OWNED_BUILDINGS_COAL,

    OWNED_VEHICLES,

    DESFLURANE_CO2E, ISOFLURANE_CO2E, SEVOFLURANE_CO2E, NITROUS_OXIDE_CO2E, PORTABLE_NITROUS_OXIDE_MIX_CO2E, PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E,

    SCOPE_ALL, SCOPE_1, SCOPE_2, SCOPE_3, SCOPE_3_TRAVEL, SCOPE_3_WATER, SCOPE_3_WASTE, SCOPE_3_ENERGY_WTT, SCOPE_3_BIOMASS_WTT, SCOPE_3_BIOMASS, NET_THERMAL_ENERGY_CO2E, ELEC_3RD_PTY_RENEWABLE_USED, ELEC_USED_CO2E, ELEC_EXPORTED_CO2E, NET_ELEC_CO2E, WOOD_LOGS_WTT_CO2E, WOOD_CHIPS_WTT_CO2E, WOOD_PELLETS_WTT_CO2E, WOOD_LOGS_CO2E, WOOD_CHIPS_CO2E, WOOD_PELLETS_CO2E, HEALTH_IMPACT_OF_TRAVEL,

    WASTE_AND_WATER,    WASTE_AND_WATER_CO2E,

    /* ERIC QUESTIONS */
    NO_EVAC_INJURIES, NO_FIRE_INJURIES, NO_FIRE_DEATHS, NO_FALSE_ALARMS, NO_FIRES, NO_FM_CLINICAL_INCIDENTS, NO_FM_INCIDENTS, NO_RIDDOR_INCIDENTS, INCOME_OTHER, INCOME_LAUNDRY, INCOME_CATERING, NON_EMERGENCY_TRANSPORT_VAL, PFA_ACTION_PLAN_VAL, BACKLOG_MAINTENANCE_VAL, PRIVATE_SECTOR, CAPITAL_EQUIPMENT, CAPITAL_IMPROVING_EXISTING, CAPITAL_NEW_BUILD, CONTRACTING_OUT_PCT, CONTRACTING_OUT_VAL, PFA_ACTION_PLAN, PFA_ASSESSMENT, BOARD_ADAPTATION_PLAN, ESTATES_DEV_STRATEGY, NO_OTHER_SITES, NO_SUPPORT_SITES, NO_OUTPATIENT_SITES, NO_OTHER_INPATIENT_SITES, NO_COMMUNITY_HOSPITAL_SITES, NO_MENTAL_HEALTH_LD_SITES, NO_LD_SITES, NO_MENTAL_HEALTH_SITES, NO_MIXED_SITES, NO_SPECIALIST_SITES, NO_ACUTE_SITES, COMMISSIONING_REGION, RENEWABLE_USED, DURABLES_INTERNAL_REUSE, DURABLES_EXTERNAL_REUSE, PAPER_USED,
    OWNED_LEASED_LOW_CARBON_MILES, OWNED_LEASED_LOW_CARBON_COST, OWNED_LEASED_LOW_CARBON_CO2E,
    RAIL_COST, BUS_COST, BUS_MILES, TAXI_COST, TAXI_MILES, CYCLE_MILES,

    HARD_FM_COSTS, CLEANING_COST, CLINICAL_FLOOR_AREA, BLDG_FOOTPRINT, CHP_THERMAL_OUTPUT, CLINICAL_WASTE_COST, DISABLED_PARKING_SPACES, CLINICAL_WASTE_WEIGHT, DOMESTIC_WASTE_COST, EMPTY_FLOOR_AREA, ERADICATE_BACKLOG_COST, ERADICATE_HIGH_RISK_BACKLOG_COST, ERADICATE_LOW_RISK_BACKLOG_COST, ERADICATE_MOD_RISK_BACKLOG_COST, ERADICATE_SIG_RISK_BACKLOG_COST, FM_FINANCE_COSTS, FOSSIL_USED_IN_CHP, HEATED_VOL, HS_RISK_CLEANING_AREA, HS_RISK_CLEANING_AREA_ACTUAL_PCT, HS_RISK_CLEANING_AREA_REQD_PCT, AGE_PROFILE_1948_1954, AGE_PROFILE_1955_1964, AGE_PROFILE_1965_1974, AGE_PROFILE_1975_1984, AGE_PROFILE_1985_1994, AGE_PROFILE_1995_2004, AGE_PROFILE_2005_2014, AGE_PROFILE_2015_2024, AGE_PROFILE_PRE_1948, AGE_PROFILE_TOTAL, AREA_RETAIL, AVG_PARKING_FEE, AVG_STAFF_PARKING_FEE, HS_RISK_CLEANING_COST, INCOME_RETAIL, INFREQ_CLEANING_AREA, INFREQ_CLEANING_COST, L_RISK_CLEANING_AREA, L_RISK_CLEANING_AREA_ACTUAL_PCT, L_RISK_CLEANING_AREA_REQD_PCT, L_RISK_CLEANING_COST, LAUNDRY_COST, MAINT_COSTS, NHS_OCCUPIED_FLOOR_AREA, NO_CLEANING_STAFF, NO_LAUNDRY_ITEMS, NO_MEALS, SOFT_FM_COSTS, SPECIAL_WASTE_WEIGHT, UNDER_USED_FLOOR_AREA, UNSUITABLE_OCCUPIED_FLOOR_AREA, UNSUITABLE_PATIENT_OCCUPIED_FLOOR_AREA, VH_RISK_CLEANING_AREA, VH_RISK_CLEANING_AREA_ACTUAL_PCT, VH_RISK_CLEANING_AREA_REQD_PCT, NO_PORTERING_STAFF, NON_CLINICAL_FLOOR_AREA, OCCUPIED_FLOOR_AREA, PARKING_SPACES, PER_PATIENT_DAY_MEAL_COST, PORTERING_COST, RISK_ADJUSTED_BACKLOG_COST, SEWAGE_COST, SINGLE_BED_WITH_EN_SUITE, SINGLE_BED_WITHOUT_EN_SUITE, SITE_FOOTPRINT, SPECIAL_WASTE_COST, VH_RISK_CLEANING_COST,

    // ERIC 2013-14 additional
    PATIENT_OCCUPIED_FLOOR_AREA, NON_PATIENT_OCCUPIED_FLOOR_AREA, AVAIL_BEDS, OCCUPIED_BEDS, NO_CHP_UNITS, ELEC_GENERATION, TOTAL_WASTE_COST, ALT_WASTE_DISPOSAL_WEIGHT, PATIENT_AND_VISITOR_CONCESSION_SCHEME, DISABLED_STAFF_PARKING_SPACES, WARD_FOOD_WASTE,

    // ERIC 14-15 additional
    INCOME_ALL, PFI_CHARGES,

    // EClasses
    PROVISIONS,
    STAFF_CLOTHING,
    PATIENTS_CLOTHING_AND_FOOTWEAR,
    PHARMA_BLOOD_PROD_AND_MED_GASES,
    DRESSINGS,
    MEDICAL_AND_SURGICAL_EQUIPT,
    PATIENTS_APPLIANCES,
    CHEMICALS_AND_REAGENTS,
    DENTAL_AND_OPTICAL_EQUIPT,
    IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS,
    LAB_EQUIPT_AND_SVCS,
    FUEL_LIGHT_POWER_WATER,
    HOTEL_EQUIPT_MATERIALS_AND_SVCS,
    BLDG_AND_ENG_PROD_AND_SVCS,
    PURCHASED_HEALTHCARE,
    GARDENING_AND_FARMING,
    FURNITURE_FITTINGS,
    HARDWARE_CROCKERY,
    BEDDING_LINEN_AND_TEXTILES,
    OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY,
    TRANSPORTATION,
    REC_EQUIPT_AND_SOUVENIRS,
    CONSULTING_SVCS_AND_EXPENSES,

    PROVISIONS_CO2E,
    STAFF_CLOTHING_CO2E,
    PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E,
    PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E,
    DRESSINGS_CO2E,
    MEDICAL_AND_SURGICAL_EQUIPT_CO2E,
    PATIENTS_APPLIANCES_CO2E,
    CHEMICALS_AND_REAGENTS_CO2E,
    DENTAL_AND_OPTICAL_EQUIPT_CO2E,
    IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E,
    LAB_EQUIPT_AND_SVCS_CO2E,
    FUEL_LIGHT_POWER_WATER_CO2E,
    HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E,
    BLDG_AND_ENG_PROD_AND_SVCS_CO2E,
    PURCHASED_HEALTHCARE_CO2E,
    GARDENING_AND_FARMING_CO2E,
    FURNITURE_FITTINGS_CO2E,
    HARDWARE_CROCKERY_CO2E,
    BEDDING_LINEN_AND_TEXTILES_CO2E,
    OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E,
    TRANSPORTATION_CO2E,
    REC_EQUIPT_AND_SOUVENIRS_CO2E,
    CONSULTING_SVCS_AND_EXPENSES_CO2E,

    // Carbon emissions profile over time
    CORE_CO2E, PROCUREMENT_CO2E, CITIZEN_CO2E,

    TOTAL_ENERGY_CO2E, TOTAL_PROCUREMENT_CO2E, TOTAL_CO2E,
    TOTAL_CO2E_BY_POP, TOTAL_CO2E_BY_FLOOR, TOTAL_CO2E_BY_WTE,
    TOTAL_CO2E_BY_PATIENT_CONTACT, TOTAL_ENERGY_CO2E_BY_POP,
    TOTAL_TRAVEL_CO2E_BY_POP, TOTAL_PROCUREMENT_CO2E_BY_POP,
    TOTAL_COMMISSIONING_CO2E_BY_POP, TOTAL_ENERGY_CO2E_BY_FLOOR,
    TOTAL_TRAVEL_CO2E_BY_FLOOR, TOTAL_PROCUREMENT_CO2E_BY_FLOOR,
    TOTAL_COMMISSIONING_CO2E_BY_FLOOR, TOTAL_ENERGY_CO2E_BY_WTE,
    TOTAL_TRAVEL_CO2E_BY_WTE, TOTAL_PROCUREMENT_CO2E_BY_WTE,
    TOTAL_COMMISSIONING_CO2E_BY_WTE, TOTAL_ENERGY_CO2E_BY_PATIENT_CONTACT,
    TOTAL_TRAVEL_CO2E_BY_PATIENT_CONTACT,
    TOTAL_PROCUREMENT_CO2E_BY_PATIENT_CONTACT,
    TOTAL_COMMISSIONING_CO2E_BY_PATIENT_CONTACT,
    TOTAL_ENERGY_CO2E_BY_BEDS, TOTAL_CO2E_BY_BEDS, TOTAL_TRAVEL_CO2E_BY_BEDS,
    TOTAL_PROCUREMENT_CO2E_BY_BEDS, TOTAL_COMMISSIONING_CO2E_BY_BEDS,
    TOTAL_CO2E_BY_OPEX, TOTAL_ENERGY_CO2E_BY_OPEX, TOTAL_TRAVEL_CO2E_BY_OPEX,
    TOTAL_PROCUREMENT_CO2E_BY_OPEX, TOTAL_COMMISSIONING_CO2E_BY_OPEX,

    TOTAL_ENERGY, TOTAL_ENERGY_BY_WTE, WATER_VOL_BY_WTE,

    NO_NHS_PROPERTY_SVCS_SITES, CIP_ACTUAL_VAL, CIP_PLANNED_VAL,OVERHEATING_EVENTS, PCT_TEMP_MONITORED;

    private String code;

    Q() {
        this.code = name();
    }

    Q(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

}