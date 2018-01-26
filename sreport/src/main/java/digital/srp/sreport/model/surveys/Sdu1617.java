package digital.srp.sreport.model.surveys;

import java.util.ArrayList;
import java.util.Arrays;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;

/**
 *
 * @author Tim Stephenson
 */
public class Sdu1617 {

    public static final String PERIOD = "2016-17";
    public static final String ID = "SDU-"+PERIOD;

    public static Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questionEnums(
                        Q.ORG_NAME,
                        Q.ORG_CODE,
                        Q.ORG_NICKNAME,
                        Q.ORG_TYPE,
                        Q.FLOOR_AREA,
                        Q.POPULATION,
                        Q.NO_STAFF,
                        Q.OCCUPIED_BEDS,
                        Q.NO_PATIENT_CONTACTS,
                        Q.PATIENT_CONTACT_MEASURE
                );

        SurveyCategory catPolicy = new SurveyCategory()
                .name("Policy")
                .questionEnums(
                        Q.SDMP_CRMP,
                        Q.SDMP_BOARD_REVIEW_WITHIN_12_MONTHS,
                        Q.SDMP_MISSION_STATEMENT,
                        Q.HEALTHY_TRANSPORT_PLAN,
                        Q.PROMOTE_HEALTHY_TRAVEL,
                        Q.PROC_ENV_ASSESSMENT,
                        Q.PROC_SOC_ASSESSMENT,
                        Q.PROC_SUPPLIER_SUSTAINABILITY,
                        Q.STRATEGIC_SUSTAINABILITY_PARTNERSHIPS,
                        Q.STRATEGIC_SUSTAINABILITY_PARTNERS,
                        Q.GCC_USER,
                        Q.LAST_GCC_DATE,
                        Q.LAST_GCC_SCORE,
                        Q.PROMOTE_SUSTAINABILITY_TO_STAFF,
                        Q.ECLASS_USER,
                        Q.QUANT_ES_IMPACTS,
                        Q.QUANT_TRAVEL_IMPACTS,
                        Q.MOD_SLAVERY,
                        Q.SIA
                );

        SurveyCategory catPerf = new SurveyCategory()
                .name("Performance")
                .questionEnums(
                        Q.CARBON_REDUCTION_TARGET,
                        Q.CARBON_REDUCTION_BASELINE_USED,
                        Q.CARBON_REDUCTION_BASE_YEAR,
                        Q.CARBON_REDUCTION_TARGET_PCT,
                        Q.CARBON_REDUCTION_DEADLINE_YEAR,
                        Q.BOARD_LEAD_FOR_SUSTAINABILITY,
                        Q.BOARD_SUSTAINABILITY_AS_RISK,
                        Q.ADAPTATION_PLAN_INC,
                        Q.CCG1_SERVED, Q.CCG2_SERVED, Q.CCG3_SERVED,
                        Q.CCG4_SERVED, Q.CCG5_SERVED, Q.CCG6_SERVED,
                        Q.PROVIDER1_COMMISSIONED, Q.PROVIDER2_COMMISSIONED,
                        Q.PROVIDER3_COMMISSIONED, Q.PROVIDER4_COMMISSIONED,
                        Q.PROVIDER5_COMMISSIONED, Q.PROVIDER6_COMMISSIONED,
                        Q.PROVIDER7_COMMISSIONED, Q.PROVIDER8_COMMISSIONED
                );

        SurveyCategory catFinancial = new SurveyCategory()
                .name("Spend")
                .questionEnums(
                        // TODO could be calculated? Or used as check?
                        Q.TOTAL_ENERGY_COST,
                        Q.WATER_AND_SEWAGE_COST,
                        Q.WASTE_RECYLING_COST,
                        Q.OP_EX,
                        Q.TOTAL_BIZ_TRAVEL_COST,
                        Q.NON_PAY_SPEND,
                        Q.CAPITAL_SPEND
                );

        SurveyCategory catEnergy = new SurveyCategory()
                .name("Energy")
                .questionEnums(
                        Q.ELEC_USED,
                        Q.GAS_USED,
                        Q.OIL_USED,
                        Q.COAL_USED,
                        Q.STEAM_USED,
                        Q.HOT_WATER_USED,
                        Q.ELEC_USED_GREEN_TARIFF,
                        Q.ELEC_3RD_PTY_RENEWABLE_USED,
                        Q.ELEC_RENEWABLE_USED,
                        Q.ELEC_EXPORTED
                );

        SurveyCategory catWaste = new SurveyCategory()
                .name("Waste")
                .questionEnums(
                        Q.DURABLES_INTERNAL_REUSE,
                        Q.DURABLES_EXTERNAL_REUSE,
                        Q.PAPER_USED,
                        Q.OTHER_RECOVERY_WEIGHT,
                        Q.INCINERATION_WEIGHT,
                        Q.LANDFILL_WEIGHT,
                        Q.RECYCLING_WEIGHT
                );

        SurveyCategory catWater = new SurveyCategory()
                .name("Water")
                .questionEnums(
                        Q.WATER_VOL,
                        Q.WASTE_WATER
                );

        SurveyCategory catBizTravel = new SurveyCategory()
                .name("Business Travel")
                .questionEnums(
                        Q.BIZ_MILEAGE_ROAD,
                        Q.FLEET_ROAD_MILES,
                        Q.BIZ_MILEAGE_ROAD_COST,
                        Q.OWNED_LEASED_LOW_CARBON_MILES,
                        Q.OWNED_LEASED_LOW_CARBON_COST,
                        Q.RAIL_MILES,
                        Q.RAIL_COST,
                        Q.BUS_COST,
                        Q.BUS_MILES,
                        Q.TAXI_COST,
                        Q.TAXI_MILES,
                        Q.CYCLE_MILES,
                        Q.DOMESTIC_AIR_MILES,
                        Q.SHORT_HAUL_AIR_MILES,
                        Q.LONG_HAUL_AIR_MILES
                );

        SurveyCategory catOtherTravel = new SurveyCategory()
                .name("Other Travel")
                .questionEnums(
                        Q.PATIENT_MILEAGE,
                        Q.VISITOR_MILEAGE,
                        Q.STAFF_COMMUTE_MILES_PP,
                        Q.STAFF_COMMUTE_MILES_TOTAL,
                        Q.HEALTH_IMPACT_OF_TRAVEL
                );

        SurveyCategory catGases = new SurveyCategory()
                .name("Gases")
                .questionEnums(
                        Q.DESFLURANE,
                        Q.ISOFLURANE,
                        Q.SEVOFLURANE,
                        Q.NITROUS_OXIDE,
                        Q.PORTABLE_NITROUS_OXIDE_MIX,
                        Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY
                );

        SurveyCategory catAdditional = new SurveyCategory()
                .name("Additional")
                .questionEnums(
                        Q.CHP_ELECTRICAL_OUTPUT,
                        Q.EXPORTED_THERMAL_ENERGY,
                        Q.WOOD_LOGS_USED,
                        Q.WOOD_CHIPS_USED,
                        Q.WOOD_PELLETS_USED,
                        Q.ELEC_OWNED_RENEWABLE_USED_SDU,
                        Q.LEASED_ASSETS_ENERGY_USE,
                        Q.GREEN_TARIFF_ADDITIONAL_PCT,
                        Q.THIRD_PARTY_ADDITIONAL_PCT
                );

        SurveyCategory catSpendProfile = new SurveyCategory()
                .name("Spend Profile")
                .questionEnums(SduQuestions.SPEND_HDRS);

        SurveyCategory catEClassProfile = new SurveyCategory()
                .name("E-Class Profile")
                .questionEnums(
                        Q.PROVISIONS, Q.STAFF_CLOTHING,
                        Q.PATIENTS_CLOTHING_AND_FOOTWEAR,
                        Q.PHARMA_BLOOD_PROD_AND_MED_GASES, Q.DRESSINGS,
                        Q.MEDICAL_AND_SURGICAL_EQUIPT, Q.PATIENTS_APPLIANCES,
                        Q.CHEMICALS_AND_REAGENTS, Q.DENTAL_AND_OPTICAL_EQUIPT,
                        Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS,
                        Q.LAB_EQUIPT_AND_SVCS, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS,
                        Q.BLDG_AND_ENG_PROD_AND_SVCS, Q.PURCHASED_HEALTHCARE,
                        Q.GARDENING_AND_FARMING, Q.FURNITURE_FITTINGS,
                        Q.HARDWARE_CROCKERY, Q.BEDDING_LINEN_AND_TEXTILES,
                        Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY,
                        Q.REC_EQUIPT_AND_SOUVENIRS,
                        Q.CONSULTING_SVCS_AND_EXPENSES
                );
        SurveyCategory catQual = new SurveyCategory()
                .name("Qualitative questions")
                .questionEnums(SduQuestions.QUALITATIVE_QS);
        Survey survey = new Survey().name(ID).status("Draft")
                .applicablePeriod("2016-17")
                .categories(catOrg, catPolicy, catPerf,
                        catFinancial, catEnergy, catWaste,
                        catWater, catBizTravel, catOtherTravel, catGases,
                        catAdditional, catEClassProfile, catSpendProfile, catQual);
        return survey;
    }

    public static Q[] getDerivedQs() {
        ArrayList<Q> list = new ArrayList<Q>();
        list.addAll(Arrays.asList(SduQuestions.SDU_PROFILE_HDRS));
        list.addAll(Arrays.asList(SduQuestions.FOOTPRINT_PCT_HDRS));
        list.add(Q.WASTE_WATER);
        list.addAll(Arrays.asList(SduQuestions.WATER_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.WASTE_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.TRAVEL_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.ENERGY_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SUMMARY_SCOPE_HDRS));
        list.add(Q.SCOPE_ALL);
        list.addAll(Arrays.asList(SduQuestions.ANAESTHETIC_GASES_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SCOPE_1_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SCOPE_2_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SCOPE_3_HDRS));
        list.add(Q.OWNED_BUILDINGS);
        list.add(Q.SCOPE_3_BIOMASS_WTT);
        list.addAll(Arrays.asList(SduQuestions.BIOMASS_CO2E_WTT_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BIOMASS_CO2E_NOSCOPE_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SDU_PROFILE_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.ECLASS_PROFILE_HDRS));
        list.addAll(Arrays.asList(SduQuestions.CORE_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.ECLASS_PROFILE_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BIZ_MILEAGE_HDRS));
        list.add(Q.BIZ_MILEAGE);
        list.addAll(Arrays.asList(SduQuestions.TRAVEL_HDRS));
        list.addAll(Arrays.asList(SduQuestions.CITIZEN_CO2E_HDRS));
        list.addAll(Arrays.asList(SduQuestions.SDU_TREND_HDRS));
        list.add(Q.TOTAL_CO2E);
        list.add(Q.TOTAL_CO2E_BY_POP);
        list.add(Q.TOTAL_CO2E_BY_FLOOR);
        list.add(Q.TOTAL_CO2E_BY_WTE);
        list.add(Q.TOTAL_CO2E_BY_BEDS);
        list.add(Q.TOTAL_CO2E_BY_PATIENT_CONTACT);
        list.add(Q.TOTAL_CO2E_BY_OPEX);
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_POPULATION_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_FLOOR_AREA_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_WTE_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_BEDS_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_PATIENT_CONTACTS_HDRS));
        list.add(Q.NON_PAY_SPEND);
        list.addAll(Arrays.asList(SduQuestions.SPEND_HDRS));
        list.addAll(Arrays.asList(SduQuestions.BENCHMARK_BY_OPEX_HDRS));
        list.add(Q.TOTAL_ENERGY);
        list.add(Q.TOTAL_ENERGY_COST);
        list.add(Q.ENERGY_COST_CHANGE_PCT);
        list.add(Q.TOTAL_ENERGY_BY_WTE);
        list.add(Q.WATER_VOL_BY_WTE);
        list.add(Q.NO_STAFF);
        list.addAll(Arrays.asList(SduQuestions.QUALITATIVE_QS));
        return list.toArray(new Q[list.size()]);
    }
}
