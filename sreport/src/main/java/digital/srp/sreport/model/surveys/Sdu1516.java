package digital.srp.sreport.model.surveys;

import java.util.Arrays;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;

/**
 * 
 * @author Tim Stephenson
 */
public class Sdu1516 {
    
    public static final String ID = "Sdu-201516";

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.ORG_NAME).label("Name of organisation").required(true),
                        new SurveyQuestion().q(Q.ORG_CODE).label("Organisation code e.g. RAA").required(true),
                        new SurveyQuestion().q(Q.ORG_NICKNAME).label("Abbreviation or nick name of organisation used").required(false),
                        // TODO how to support split?
                        new SurveyQuestion().q(Q.ORG_TYPE).label("Organisation type").required(true),
                        new SurveyQuestion().q(Q.FLOOR_AREA).label("Total gross internal floor space").required(true).unit("m2"),
                        new SurveyQuestion().q(Q.POPULATION).label("Registered population or population served").required(true).unit("people"),
                        new SurveyQuestion().q(Q.NO_STAFF).label("Total no. of staff employed").required(true).unit("WTE"),
                        new SurveyQuestion().q(Q.NO_BEDS).label("Total no. occupied beds").required(true),
                        new SurveyQuestion().q(Q.NO_PATIENT_CONTACTS).label("Total Patient Contacts").required(true)
                ));

        SurveyCategory catPolicy = new SurveyCategory()
                .name("Policy")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.SDMP_CRMP).label("Does your organisation have a current* Board-approved Sustainable Development Management Plan (SDMP) or Carbon Reduction Management Plan (CRMP)? ").type("yesno").required(true),
                        // TODO recommend record date OR Within this reporting period
                        new SurveyQuestion().q(Q.SDMP_BOARD_REVIEW_WITHIN_12_MONTHS).label("Was the SDMP reviewed or approved by the board in the last 12 months?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.SDMP_MISSION_STATEMENT).label("If your SDMP has a sustainability mission statement, what is it?").required(false),
                        new SurveyQuestion().q(Q.HEALTHY_TRANSPORT_PLAN).label("Does your organisation have a healthy or green transport plan?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.PROMOTE_HEALTHY_TRAVEL).label("Does your organisation promote healthy travel?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.PROC_ENV_ASSESSMENT).label("Do your commissioning, tendering and procurement processes include: An assessment of the environmental impacts?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.PROC_SOC_ASSESSMENT).label("Do your commissioning, tendering and procurement processes include: An assessment of the social impacts?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.PROC_SUPPLIER_SUSTAINABILITY).label("Do your commissioning, tendering and procurement processes include: A consideration of the suppliers' sustainability policies?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.STRATEGIC_SUSTAINABILITY_PARTNERSHIPS).label("Are you in a strategic partnership with other organisations on sustainability?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.STRATEGIC_SUSTAINABILITY_PARTNERS).label("Who are they?").required(true), // TODO surely must be optional
                        new SurveyQuestion().q(Q.GCC_USER).label("Does your organisation use the Good Corporate Citizenship (GCC) tool?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.LAST_GCC_DATE).label("When was your last GCC self assessment?").required(true), // TODO surely must be optional
                        new SurveyQuestion().q(Q.LAST_GCC_SCORE).label("What was your score?").required(true), // TODO surely must be optional
                        new SurveyQuestion().q(Q.PROMOTE_SUSTAINABILITY_TO_STAFF).label("Does your organisation promote sustainability to its employees?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.ECLASS_USER).label("Do you use eClass for procurement?").required(true).type("yesno")
                ));

        SurveyCategory catPerf = new SurveyCategory()
                .name("Performance")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_TARGET).label("Does your organisation have its own carbon reduction target?").required(true).type("yesno"),
                        // TODO recommend do not need both the following use null date as none
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_BASELINE_USED).label("Does your organisation use a baseline year?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_BASE_YEAR).label("Which year is it?").required(true), // TODO surely optional
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_TARGET_PCT).label("What is the percentage reduction target?").required(true).unit("%"),
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_DEADLINE_YEAR).label("What is the deadline year for this target?").required(true),
                        new SurveyQuestion().q(Q.PATIENT_CONTACT_MEASURE).label("Define the measure you have used for patient contacts").required(true),
                        new SurveyQuestion().q(Q.BOARD_LEAD_FOR_SUSTAINABILITY).label("Is there a Board Level lead for Sustainability on your Board?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.BOARD_LEAD_FOR_SUSTAINABILITY).label("Does your board consider sustainability issues as part of its risk management process?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.ADAPTATION_PLAN_INC).label("Do your board approved plans address the potential need to adapt the delivery of your organisation's activities and organisation's infrastructure as a result of climate change and adverse weather events?").required(true).type("yesno"),
                        new SurveyQuestion().q(Q.CCGS_SERVED).label("We provide services to the following CCGs").required(true)
                ));
        
        SurveyCategory catFinancial = new SurveyCategory()
                .name("Spend")
                .questions(Arrays.asList(
                        // TODO could be calculated? Or used as check?
                        new SurveyQuestion().q(Q.TOTAL_ENERGY_COST).label("Total Energy Cost (all energy supplies)").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.WATER_COST).label("Water & Sewage Cost").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.WASTE_RECYLING_COST).label("Waste recycling, recovery and preparing for re-use cost").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.OP_EX).label("Operating Expenditure").required(false).type("number").unit("£,000"),
                        new SurveyQuestion().q(Q.NON_PAY_SPEND).label("Non-pay spend").required(false).type("number").unit("£,000"),
                        new SurveyQuestion().q(Q.NON_PAY_SPEND).label("Capital Spend").required(false).type("number").unit("£,000")
                ));

        SurveyCategory catEnergy = new SurveyCategory()
                .name("Energy")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.ELEC_USED).label("Electricity Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.GAS_USED).label("Gas Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.OIL_USED).label("Oil Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.COAL_USED).label("Coal Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.ELEC_USED_LOCAL).label("Electricity Consumed - local").type("number").required(false).unit("kWh"),
                        new SurveyQuestion().q(Q.STEAM_USED).label("Steam Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.HOT_WATER_USED).label("Hot Water Consumed").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.ELEC_USED_RENEWABLE).label("Electricity Consumed - renewable").type("number").required(false).unit("kWh"),
                        new SurveyQuestion().q(Q.RENEWABLE_USED).label("Non-fossil fuel Consumed - renewable").type("number").required(false).unit("kWh"),
                        new SurveyQuestion().q(Q.ELEC_EXPORTED).label("Total exported electricity").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.WATER_VOL).label("Water Volume").required(false).type("number").unit("m3")
                ));
        
        SurveyCategory catWaste = new SurveyCategory()
                .name("Waste")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT).label("High Temperature Disposal Waste with Energy Recovery").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.HIGH_TEMP_DISPOSAL_WEIGHT).label("High Temperature Disposal Waste").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.NON_BURN_WEIGHT).label("Non Burn Treatment Disposal Waste").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.WEEE_WEIGHT).label("Waste Electrical and Electronic Equipment (WEEE)").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.REUSE_WEIGHT).label("Preparing for re-use").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.COMPOSTED_WEIGHT).label("Composted").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.LANDFILL_WEIGHT).label("Landfill disposal weight").required(false).type("number").unit("tonnes"),
                        new SurveyQuestion().q(Q.RECYCLING_WEIGHT).label("Waste Recycling weight").required(false).type("number").unit("tonnes")
                ));

        SurveyCategory catWater = new SurveyCategory()
                .name("Water")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.WASTE_WATER).label("Waste water").required(false).unit("m3")
                ));
        
        SurveyCategory catBizTravel = new SurveyCategory()
                .name("Business Travel")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.BIZ_MILEAGE_ROAD).label("Business Mileage - Road").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.OWNED_FLEET_TRAVEL).label("Organisation Owned Fleet/Pool Road Travel").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.LEASED_FLEET_TRAVEL).label("Non-organisation Owned Fleet/Pool Road Travel (Leased, hired etc.)").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.RAIL_MILES).label("Rail").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.DOMESTIC_AIR_MILES).label("Air - Domestic").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.SHORT_HAUL_AIR_MILES).label("Air - Short Haul International Flights").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.LONG_HAUL_AIR_MILES).label("Air - Long Haul International Flights").required(false).type("number").unit("miles")
                ));
        
        SurveyCategory catOtherTravel = new SurveyCategory()
                .name("Other Travel")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.PATIENT_MILEAGE).label("Patient Transport Mileage").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.VISITOR_MILEAGE).label("Patient and Visitor Travel").required(false).type("number").unit("miles"),
                        new SurveyQuestion().q(Q.TOTAL_EMPLOYEES).label("Total Employees in Organisation").required(false).type("number"),
                        new SurveyQuestion().q(Q.STAFF_COMMUTE_MILES_PP).label("Staff commute - Average annual distance travelled by road to work").required(false).type("number").unit("miles")
                        // NOTE: calculate "Total Employee Commute for Organisation by road") by summing 2 above
                ));
        
        SurveyCategory catGases = new SurveyCategory()
                .name("Gases")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.DESFLURANE).label("Desflurane - liquid").required(false).type("number").unit("litres"),
                        new SurveyQuestion().q(Q.ISOFLURANE).label("Isoflurane - liquid").required(false).type("number").unit("litres"),
                        new SurveyQuestion().q(Q.ISOFLURANE).label("Sevoflurane - liquid").required(false).type("number").unit("litres"),
                        new SurveyQuestion().q(Q.NITROUS_OXIDE).label("Nitrous oxide - gas").required(false).type("number").unit("litres"),
                        new SurveyQuestion().q(Q.PORTABLE_NITROUS_OXIDE_MIX).label("Portable nitrous oxide and oxygen 50/50 split - gas").required(false).type("number").unit("litres")
                        // Calculate: "Total: Nitrous oxide with oxygen 50/50 split - gas"
                        // Calculate: Total: Converted to nitrous oxide only - gas
                ));
        
        SurveyCategory catAdditional = new SurveyCategory()
                .name("Additional")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.CHP_ELECTRICAL_OUTPUT).label("Total electrical energy output of the CHP system/s").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.EXPORTED_THERMAL_ENERGY).label("Total exported thermal energy").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood logs)").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood chips)").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood pellets)").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.ELEC_OWNED_RENEWABLE_CONSUMPTION).label("Electricity Consumed - renewable (Solar Panels)").required(false).type("number").unit("kWh"),
                        new SurveyQuestion().q(Q.LEASED_ASSETS_ENERGY_USE).label("Leased Assets Energy Use(Upstream - Gas, Coal & Electricity)").required(false).type("number").unit("tCO2e")
                ));
        
        SurveyCategory catSpendProfile = new SurveyCategory()
                .name("Spend Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.BIZ_SVC_SPEND).label("Business services").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.CONSTRUCTION_SPEND).label("Construction").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.CATERING_SPEND).label("Food and catering").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.FREIGHT_SPEND).label("Freight transport").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.ICT_SPEND).label("Information and communication technologies").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.CHEM_AND_GAS_SPEND).label("Manufactured fuels chemicals and gases").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.MED_INSTRUMENTS_SPEND).label("Medical Instruments /equipment").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.OTHER_MANUFACTURED_SPEND).label("Other manufactured products").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.OTHER_SPEND).label("Other procurement").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.PAPER_SPEND).label("Paper products").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.PHARMA_SPEND).label("Pharmaceuticals").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.TRAVEL_SPEND).label("Travel").required(false).type("number").unit("£"),
                        new SurveyQuestion().q(Q.COMMISSIONING_SPEND).label("Commissioning").required(false).type("number").unit("£")
                ));
        
        Survey survey = new Survey().name(ID).status("Draft")
                .applicablePeriod("2015-16")
                .categories(Arrays.asList(catOrg, catPolicy, catPerf,
                        catFinancial, catEnergy, catWaste,
                        catWater, catBizTravel, catOtherTravel, catGases,
                        catAdditional, catSpendProfile));
        return survey;
    }
}