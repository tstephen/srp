package digital.srp.sreport.model.surveys;

import digital.srp.sreport.model.Q;

public interface SduQuestions {

    Q[] SPEND_HDRS = new Q[] {
            Q.ENERGY_CO2E_PER_POUND,
            Q.COMMISSIONING_CO2E_PER_POUND,
            Q.PROCUREMENT_CO2E_PER_POUND,
            Q.TRAVEL_CO2E_PER_POUND
    };

    Q[] FOOTPRINT_PCT_HDRS = new Q[] { Q.ENERGY_CO2E_PCT,
            Q.COMMISSIONING_CO2E_PCT,
            Q.PROCUREMENT_CO2E_PCT,
            Q.TRAVEL_CO2E_PCT
    };

    Q[] WATER_HDRS = new Q[] { Q.WATER_VOL, Q.WATER_CO2E, Q.WATER_AND_SEWAGE_COST };

    Q[] WATER_CO2E_HDRS = new Q[] { Q.WATER_CO2E, Q.WATER_TREATMENT_CO2E };

    Q[] WASTE_HDRS = new Q[] {Q. RECYCLING_WEIGHT,
            Q.OTHER_RECOVERY_WEIGHT, Q.INCINERATION_WEIGHT,Q. LANDFILL_WEIGHT
    };

    Q[] WASTE_CO2E_HDRS = new Q[] {Q. RECYCLING_CO2E,
            Q.OTHER_RECOVERY_CO2E, Q.INCINERATION_CO2E,Q. LANDFILL_CO2E
    };

    Q[] BIZ_MILEAGE_HDRS = new Q[] { Q.BIZ_MILEAGE_ROAD, Q.FLEET_ROAD_MILES,
            Q.BIZ_MILEAGE_ROAD_COST, Q.OWNED_LEASED_LOW_CARBON_MILES,
            Q.RAIL_MILES, Q.BUS_MILES, Q.TAXI_MILES,
            Q.DOMESTIC_AIR_MILES, Q.SHORT_HAUL_AIR_MILES, Q.LONG_HAUL_AIR_MILES
    };

    Q[] TRAVEL_HDRS = new Q[] { Q.PATIENT_AND_VISITOR_MILEAGE,
            Q.BIZ_MILEAGE, Q.STAFF_COMMUTE_MILES_TOTAL
    };

    Q[] TRAVEL_CO2E_HDRS = new Q[] { Q.PATIENT_AND_VISITOR_MILEAGE_CO2E,
            Q.BIZ_MILEAGE_CO2E, Q.STAFF_COMMUTE_MILES_CO2E
    };

    Q[] ENERGY_CO2E_HDRS = new Q[] {
            Q.ELEC_CO2E, Q.OWNED_BUILDINGS_GAS,
            Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL,
            Q.STEAM_CO2E,Q.HOT_WATER_CO2E, Q.ELEC_RENEWABLE_CO2E,
            Q.EXPORTED_THERMAL_ENERGY_CO2E
    };

    Q[] ENERGY_HDRS = new Q[] {
            Q.ELEC_USED, Q.GAS_USED,Q. OIL_USED,
            Q.COAL_USED, Q.STEAM_USED, Q. HOT_WATER_USED, Q.ELEC_RENEWABLE,
            Q.EXPORTED_THERMAL_ENERGY
    };

    Q[] ORG_HDRS = new Q[] { Q.FLOOR_AREA, Q.NO_STAFF };

    Q[] SUMMARY_SCOPE_HDRS = new Q[] {
            Q.SCOPE_1, Q.SCOPE_2, Q.SCOPE_3
    };

    Q[] ANAESTHETIC_GASES_CO2E_HDRS = new Q[] {
            Q.DESFLURANE_CO2E, Q.ISOFLURANE_CO2E, Q.SEVOFLURANE_CO2E,
            Q.NITROUS_OXIDE_CO2E, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E,
            Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E
    };

    Q[] SCOPE_1_HDRS = new Q[] {
            Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL,
            Q.LEASED_ASSETS_ENERGY_USE,
            Q.OWNED_VEHICLES, Q.ANAESTHETIC_GASES_CO2E };

    Q[] SCOPE_2_HDRS = new Q[] { Q.NET_THERMAL_ENERGY_CO2E, Q.NET_ELEC_CO2E };

    Q[] SCOPE_3_HDRS = new Q[] { Q.PROCUREMENT_CO2E, Q.COMMISSIONING_CO2E, Q.SCOPE_3_TRAVEL, Q.SCOPE_3_WASTE, Q.SCOPE_3_WATER, Q.SCOPE_3_ENERGY_WTT };

    Q[] SCOPE_3_TRAVEL_HDRS = new Q[] {
        Q.TRAVEL_CO2E, Q.BIZ_MILEAGE_ROAD_CO2E,
        Q.BIZ_MILEAGE_RAIL_CO2E, Q.BIZ_MILEAGE_AIR_CO2E,
        Q.FUEL_WTT,
        Q.PATIENT_AND_VISITOR_MILEAGE_CO2E,
        Q.STAFF_COMMUTE_MILES_CO2E
    };

    Q[] SDU_PROFILE_CO2E_HDRS = new Q[] {
            Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL,
            Q.LEASED_ASSETS_ENERGY_USE, Q.OWNED_VEHICLES,
            Q.ANAESTHETIC_GASES_CO2E,
            Q.ELEC_CO2E, Q.STEAM_CO2E,
            Q.WASTE_AND_WATER_CO2E,
            Q.BIZ_SVCS_CO2E, Q.CAPITAL_CO2E, Q.COMMISSIONING_CO2E,
            Q.CONSTRUCTION_CO2E, Q.CATERING_CO2E, Q.FREIGHT_CO2E,
            Q.ICT_CO2E, Q.CHEM_AND_GAS_CO2E, Q. MED_INSTR_CO2E,
            Q.OTHER_MANUFACTURED_CO2E, Q.OTHER_PROCUREMENT_CO2E,
            Q.PAPER_CO2E, Q.PHARMA_CO2E, Q.TRAVEL_CO2E, Q.SCOPE_3_ENERGY_WTT
    };

    Q[] BIOMASS_CO2E_WTT_HDRS = new Q[] {
            Q.WOOD_LOGS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_PELLETS_WTT_CO2E
    };

    Q[] BIOMASS_CO2E_NOSCOPE_HDRS = new Q[] {
            Q.WOOD_LOGS_CO2E, Q.WOOD_CHIPS_CO2E, Q.WOOD_PELLETS_CO2E
    };

    Q[] SDU_PROFILE_HDRS = new Q[] {
            Q.BIZ_SVCS_SPEND, Q.CAPITAL_SPEND, Q.CONSTRUCTION_SPEND,
            Q.CATERING_SPEND, Q.FREIGHT_SPEND, Q.ICT_SPEND, Q.CHEM_AND_GAS_SPEND,
            Q.OTHER_MANUFACTURED_SPEND, Q.MED_INSTR_SPEND, Q.OTHER_SPEND,
            Q.PAPER_SPEND, Q.PHARMA_SPEND, Q.TRAVEL_SPEND, Q.COMMISSIONING_SPEND
    };

    Q[] ECLASS_PROFILE_HDRS = new Q[] {
            Q.PROVISIONS_CO2E, Q.STAFF_CLOTHING_CO2E,
            Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E,
            Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E, Q.DRESSINGS_CO2E,
            Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E, Q.PATIENTS_APPLIANCES_CO2E,
            Q.CHEMICALS_AND_REAGENTS_CO2E, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E,
            Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E,
            Q.LAB_EQUIPT_AND_SVCS_CO2E, Q.FUEL_LIGHT_POWER_WATER_CO2E,
            Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E,
            Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E, Q.PURCHASED_HEALTHCARE_CO2E,
            Q.GARDENING_AND_FARMING_CO2E, Q.FURNITURE_FITTINGS_CO2E,
            Q.HARDWARE_CROCKERY_CO2E, Q.BEDDING_LINEN_AND_TEXTILES_CO2E,
            Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E,
            Q.TRANSPORTATION_CO2E,
            Q.REC_EQUIPT_AND_SOUVENIRS_CO2E,
            Q.CONSULTING_SVCS_AND_EXPENSES_CO2E
    };

    Q[] CORE_CO2E_HDRS = new Q[] {
            /* All energy */
            Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL,
            Q.OWNED_BUILDINGS_COAL, Q.NET_ELEC_CO2E, Q.NET_THERMAL_ENERGY_CO2E,
            Q.SCOPE_3_BIOMASS, Q.WOOD_LOGS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E,
            Q.WOOD_PELLETS_WTT_CO2E,
            /* water and waste */
            Q.WASTE_AND_WATER_CO2E, Q.ANAESTHETIC_GASES_CO2E,
            /*
             * all travel excepting individual citizens (patients, visitors and
             * staff)
             */
            Q.BIZ_MILEAGE_CO2E, Q.BIZ_MILEAGE_ROAD_CO2E,
            Q.BIZ_MILEAGE_RAIL_CO2E, Q.BIZ_MILEAGE_AIR_CO2E,
            Q.OWNED_FLEET_TRAVEL_CO2E };

    Q[] CITIZEN_CO2E_HDRS = new Q[]{
            Q.STAFF_COMMUTE_MILES_CO2E,
            Q.PATIENT_AND_VISITOR_MILEAGE_CO2E
    };

    Q[] SDU_TREND_HDRS = new Q[] {
            Q.CORE_CO2E,
            Q.PROCUREMENT_CO2E,
            Q.CITIZEN_CO2E
    };

    Q[] BENCHMARK_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E,
            Q.SCOPE_3_TRAVEL,
            Q.TOTAL_PROCUREMENT_CO2E,
            Q.COMMISSIONING_CO2E
    };

    Q[] BENCHMARK_BY_POPULATION_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_POP,
            Q.TOTAL_TRAVEL_CO2E_BY_POP,
            Q.TOTAL_PROCUREMENT_CO2E_BY_POP,
            Q.TOTAL_COMMISSIONING_CO2E_BY_POP
    };

    Q[] BENCHMARK_BY_FLOOR_AREA_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_FLOOR,
            Q.TOTAL_TRAVEL_CO2E_BY_FLOOR,
            Q.TOTAL_PROCUREMENT_CO2E_BY_FLOOR,
            Q.TOTAL_COMMISSIONING_CO2E_BY_FLOOR
    };

    Q[] BENCHMARK_BY_WTE_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_WTE,
            Q.TOTAL_TRAVEL_CO2E_BY_WTE,
            Q.TOTAL_PROCUREMENT_CO2E_BY_WTE,
            Q.TOTAL_COMMISSIONING_CO2E_BY_WTE
    };

    Q[] BENCHMARK_BY_BEDS_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_BEDS,
            Q.TOTAL_TRAVEL_CO2E_BY_BEDS,
            Q.TOTAL_PROCUREMENT_CO2E_BY_BEDS,
            Q.TOTAL_COMMISSIONING_CO2E_BY_BEDS
    };

    Q[] BENCHMARK_BY_PATIENT_CONTACTS_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_PATIENT_CONTACT,
            Q.TOTAL_TRAVEL_CO2E_BY_PATIENT_CONTACT,
            Q.TOTAL_PROCUREMENT_CO2E_BY_PATIENT_CONTACT,
            Q.TOTAL_COMMISSIONING_CO2E_BY_PATIENT_CONTACT
    };

    Q[] BENCHMARK_BY_OPEX_HDRS = new Q[] {
            Q.TOTAL_ENERGY_CO2E_BY_OPEX,
            Q.TOTAL_TRAVEL_CO2E_BY_OPEX,
            Q.TOTAL_PROCUREMENT_CO2E_BY_OPEX,
            Q.TOTAL_COMMISSIONING_CO2E_BY_OPEX
    };

    Q[] QUALITATIVE_QS = new Q[] {
            Q.ADAPTATION_PERF,
            Q.ENERGY_PERF,
            Q.TRAJECTORY_PERF,
            Q.TRAVEL_PERF,
            Q.WASTE_PERF,
            Q.WASTE_CTXT,
            Q.WATER_PERF,
            Q.WATER_CTXT
    };

    public String ANALYSE_BY_E_CLASS = "0-E-Class";
}
