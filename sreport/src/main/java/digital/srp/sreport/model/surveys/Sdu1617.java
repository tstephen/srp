package digital.srp.sreport.model.surveys;

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
                .questionCodes(
                        Q.ORG_NAME,
                        Q.ORG_CODE,
                        Q.ORG_NICKNAME,
                        Q.ORG_TYPE,
                        Q.FLOOR_AREA,
                        Q.POPULATION,
                        Q.NO_STAFF,
                        Q.NO_BEDS,
                        Q.NO_PATIENT_CONTACTS,
                        Q.PATIENT_CONTACT_MEASURE
                );

        SurveyCategory catPolicy = new SurveyCategory()
                .name("Policy")
                .questionCodes(
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
                .questionCodes(
                        Q.CARBON_REDUCTION_TARGET,
                        Q.CARBON_REDUCTION_BASELINE_USED,
                        Q.CARBON_REDUCTION_BASE_YEAR,
                        Q.CARBON_REDUCTION_TARGET_PCT,
                        Q.CARBON_REDUCTION_DEADLINE_YEAR,
                        Q.BOARD_LEAD_FOR_SUSTAINABILITY,
                        Q.BOARD_SUSTAINABILITY_AS_RISK,
                        Q.ADAPTATION_PLAN_INC,
                        Q.CCGS_SERVED,
                        Q.PROVIDERS_COMMISSIONED
                );
        
        SurveyCategory catFinancial = new SurveyCategory()
                .name("Spend")
                .questionCodes(
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
                .questionCodes(
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
                .questionCodes(
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
                .questionCodes(
                        Q.WATER_VOL,
                        Q.WASTE_WATER
                );
        
        SurveyCategory catBizTravel = new SurveyCategory()
                .name("Business Travel")
                .questionCodes(
                        Q.PERSONAL_ROAD_MILES,
                        Q.FLEET_ROAD_MILES,
                        Q._3RD_PTY_ROAD_MILES,
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
                .questionCodes(
                        Q.PATIENT_MILEAGE,
                        Q.VISITOR_MILEAGE,
                        Q.TOTAL_EMPLOYEES,
                        Q.STAFF_COMMUTE_MILES_PP,
                        Q.STAFF_COMMUTE_MILES_TOTAL,
                        Q.HEALTH_IMPACT_OF_TRAVEL
                );
        
        SurveyCategory catGases = new SurveyCategory()
                .name("Gases")
                .questionCodes(
                        Q.DESFLURANE,
                        Q.ISOFLURANE,
                        Q.SEVOFLURANE,
                        Q.NITROUS_OXIDE,
                        Q.PORTABLE_NITROUS_OXIDE_MIX,
                        Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY,
                        Q.PORTABLE_NITROUS_OXIDE_MIX_TOTAL,
                        Q.PORTABLE_NITROUS_OXIDE_TOTAL
                );
        
        SurveyCategory catAdditional = new SurveyCategory()
                .name("Additional")
                .questionCodes(
                        Q.CHP_ELECTRICAL_OUTPUT,
                        Q.EXPORTED_THERMAL_ENERGY,
                        Q.WOOD_LOGS_USED,
                        Q.WOOD_CHIPS_USED,
                        Q.WOOD_PELLETS_USED,
                        Q.ELEC_OWNED_RENEWABLE_USED,
                        Q.LEASED_ASSETS_ENERGY_USE,
                        Q.GREEN_TARIFF_ADDITIONAL_PCT,
                        Q.THIRD_PARTY_ADDITIONAL_PCT
                );
        
        SurveyCategory catSpendProfile = new SurveyCategory()
                .name("Spend Profile")
                .questionCodes(
                        Q.BIZ_SVC_SPEND,
                        Q.CONSTRUCTION_SPEND,
                        Q.CATERING_SPEND,
                        Q.FREIGHT_SPEND,
                        Q.ICT_SPEND,
                        Q.CHEM_AND_GAS_SPEND,
                        Q.MED_INSTRUMENTS_SPEND,
                        Q.OTHER_MANUFACTURED_SPEND,
                        Q.OTHER_SPEND,
                        Q.PAPER_SPEND,
                        Q.PHARMA_SPEND,
                        Q.TRAVEL_SPEND,
                        Q.COMMISSIONING_SPEND
                );
//        SurveyCategory catCalcs = new SurveyCategory().name("Calculations");
        
        Survey survey = new Survey().name(ID).status("Draft")
                .applicablePeriod("2016-17")
                .categories(catOrg, catPolicy, catPerf,
                        catFinancial, catEnergy, catWaste,
                        catWater, catBizTravel, catOtherTravel, catGases,
                        catAdditional, catSpendProfile);
        return survey;
    }
}
