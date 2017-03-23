package digital.srp.sreport.model.surveys;

import java.util.Arrays;

import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;

/**
 * 
 * @author Tim Stephenson
 */
public class Sdu1617 implements SduQuestions {
    
    public static final String ID = "Sdu-201617";

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(ORG_NAME).label("Name of organisation").required(true),
                        new SurveyQuestion().name(ORG_CODE).label("Organisation code e.g. RAA").required(true),
                        new SurveyQuestion().name(ORG_NICKNAME).label("Abbreviation or nick name of organisation used").required(false),
                        // TODO how to support split?
                        new SurveyQuestion().name(ORG_TYPE).label("Organisation type").required(true),
                        new SurveyQuestion().name(FLOOR_AREA).label("Total gross internal floor space").required(true).unit("m2"),
                        new SurveyQuestion().name(POPULATION).label("Registered population or population served").required(true).unit("people"),
                        new SurveyQuestion().name(NO_STAFF).label("Total no. of staff employed").required(true).unit("WTE"),
                        new SurveyQuestion().name(NO_BEDS).label("Total no. occupied beds").required(true),
                        new SurveyQuestion().name(NO_PATIENT_CONTACTS).label("Total Patient Contacts").required(true)
                ));

        SurveyCategory catPolicy = new SurveyCategory()
                .name("Policy")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(SDMP_CRMP).label("Does your organisation have a current* Board-approved Sustainable Development Management Plan (SDMP) or Carbon Reduction Management Plan (CRMP)? ").type("yesno").required(true),
                        // TODO recommend record date OR Within this reporting period
                        new SurveyQuestion().name(SDMP_BOARD_REVIEWED_WITHIN12_MONTHS).label("Was the SDMP reviewed or approved by the board in the last 12 months?").required(true).type("yesno"),
                        new SurveyQuestion().name(SDMP_MISSION_STATEMENT).label("If your SDMP has a sustainability mission statement, what is it?").required(false),
                        new SurveyQuestion().name(HEALTHY_TRANSPORT_PLAN).label("Does your organisation have a healthy or green transport plan?").required(true).type("yesno"),
                        new SurveyQuestion().name(PROMOTE_HEALTHY_TRAVEL).label("Does your organisation promote healthy travel?").required(true).type("yesno"),
                        new SurveyQuestion().name(PROCUREMENT_ENVIRONMENTAL_ASSESSMENT).label("Do your commissioning, tendering and procurement processes include: An assessment of the environmental impacts?").required(true).type("yesno"),
                        new SurveyQuestion().name(PROCUREMENT_SOCIAL_ASSESSMENT).label("Do your commissioning, tendering and procurement processes include: An assessment of the social impacts?").required(true).type("yesno"),
                        new SurveyQuestion().name(PROCUREMENT_SUPPLIER_SUSTAINABILITY).label("Do your commissioning, tendering and procurement processes include: A consideration of the suppliers' sustainability policies?").required(true).type("yesno"),
                        new SurveyQuestion().name(STRATEGIC_SUSTAINABILITY_PARTNERSHIPS).label("Are you in a strategic partnership with other organisations on sustainability?").required(true).type("yesno"),
                        new SurveyQuestion().name(STRATEGIC_SUSTAINABILITY_PARTNERS).label("Who are they?").required(true), // TODO surely must be optional
                        new SurveyQuestion().name(GCC_USER).label("Does your organisation use the Good Corporate Citizenship (GCC) tool?").required(true).type("yesno"),
                        new SurveyQuestion().name(LAST_GCC_DATE).label("When was your last GCC self assessment?").required(true), // TODO surely must be optional
                        new SurveyQuestion().name(LAST_GCC_SCORE).label("What was your score?").required(true), // TODO surely must be optional
                        new SurveyQuestion().name(PROMOTE_SUSTAINABILITY_TO_STAFF).label("Does your organisation promote sustainability to its employees?").required(true).type("yesno"),
                        new SurveyQuestion().name(ECLASS_USER).label("Do you use eClass for procurement?").required(true).type("yesno"),
                        new SurveyQuestion().name(QUANT_ES_IMPACTS).label("What are the qualitative or quantified social and environmental impacts?").required(true),
                        new SurveyQuestion().name(QUANT_TRAVEL_IMPACTS).label("Does your organisation quantify the health impacts of travel and transport?").required(true).type("yesno"),
                        new SurveyQuestion().name(MOD_SLAVERY).label("If your organisation has a Modern Slavery statement, what is it?").required(true).type("yesno"),
                        new SurveyQuestion().name(SIA).label("Does your business case process include a Sustainability Impact Assessment?").required(true).type("yesno")
                ));

        SurveyCategory catPerf = new SurveyCategory()
                .name("Performance")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(CARBON_REDUCTION_TARGET).label("Does your organisation have its own carbon reduction target?").required(true).type("yesno"),
                        // TODO recommend do not need both the following use null date as none
                        new SurveyQuestion().name(CARBON_REDUCTION_BASELINE_USED).label("Does your organisation use a baseline year?").required(true).type("yesno"),
                        new SurveyQuestion().name(CARBON_REDUCTION_BASE_YEAR).label("Which year is it?").required(true), // TODO surely optional
                        new SurveyQuestion().name(CARBON_REDUCTION_TARGET_PCT).label("What is the percentage reduction target?").required(true).unit("%"),
                        new SurveyQuestion().name(CARBON_REDUCTION_DEADLINE_YEAR).label("What is the deadline year for this target?").required(true),
                        new SurveyQuestion().name(PATIENT_CONTACT_MEASURE).label("Define the measure you have used for patient contacts").required(true),
                        new SurveyQuestion().name(BOARD_LEAD_FOR_SUSTAINABILITY).label("Is there a Board Level lead for Sustainability on your Board?").required(true).type("yesno"),
                        new SurveyQuestion().name(BOARD_SUSTAINABILITY_AS_RISK).label("Does your board consider sustainability issues as part of its risk management process?").required(true).type("yesno"),
                        new SurveyQuestion().name(ADAPTATION_PLAN_INCLUDED).label("Do your board approved plans address the potential need to adapt the delivery of your organisation's activities and organisation's infrastructure as a result of climate change and adverse weather events?").required(true).type("yesno"),
                        new SurveyQuestion().name(CCGS_SERVED).label("We provide services to the following CCGs").required(true),
                        new SurveyQuestion().name(ENERGY_PERF).label("Please insert a commentary on your performance in the area of energy").required(true),
                        new SurveyQuestion().name(ENERGY_CONTEXT).label("Please insert a commentary on contextual information e.g. projects, intiatives etc. related to energy use").required(true),
                        new SurveyQuestion().name(TRAVEL_PERF).label("Please insert a commentary on your performance in the area of travel").required(true),
                        new SurveyQuestion().name(TRAVEL_CONTEXT).label("Please insert a commentary on contextual information e.g. projects, intiatives etc. related to travel").required(true),
                        new SurveyQuestion().name(WASTE_PERF).label("Please insert a commentary on your performance in the area of waste").required(true),
                        new SurveyQuestion().name(WASTE_CONTEXT).label("Please insert a commentary on contextual information e.g. projects, intiatives etc. related to waste").required(true),
                        new SurveyQuestion().name(WATER_PERF).label("Please insert a commentary on your performance in the area of water").required(true),
                        new SurveyQuestion().name(WATER_CONTEXT).label("Please insert a commentary on contextual information e.g. projects, intiatives etc. related to water").required(true),
                        new SurveyQuestion().name(TRAJECTORY_PERF).label("Please insert a commentary on your performance on the trajectory").required(true),
                        new SurveyQuestion().name(BENCHMARK_PERF).label("Please insert a commentary on your performance in the area of benchmarking").required(true),
                        new SurveyQuestion().name(ADAPTATION_PERF).label("Please insert a commentary on your performance in the area of adaptation").required(true)
                ));
        
        SurveyCategory catFinancial = new SurveyCategory()
                .name("Spend")
                .questions(Arrays.asList(
                        // TODO could be calculated? Or used as check?
                        new SurveyQuestion().name(TOTAL_ENERGY_COST).label("Total Energy Cost (all energy supplies)").required(false).unit("£"),
                        new SurveyQuestion().name(WATER_COST).label("Water & Sewage Cost").required(false).unit("£"),
                        new SurveyQuestion().name(WASTE_RECYLING_COST).label("Waste recycling, recovery and preparing for re-use cost").required(false).unit("£"),
                        new SurveyQuestion().name(OP_EX).label("Operating Expenditure").required(false).unit("£,000"),
                        new SurveyQuestion().name(NON_PAY_SPEND).label("Non-pay spend").required(false).unit("£,000"),
                        new SurveyQuestion().name(CAPITAL_SPEND).label("Capital Spend").required(false).unit("£,000")
                ));

        SurveyCategory catEnergy = new SurveyCategory()
                .name("Energy")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(ELEC_USED).label("Electricity Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(GAS_USED).label("Gas Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(OIL_USED).label("Oil Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(COAL_USED).label("Coal Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(ELEC_USED_LOCAL).label("Electricity Consumed - local").required(false).unit("kWh"),
                        new SurveyQuestion().name(STEAM_USED).label("Steam Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(HOT_WATER_USED).label("Hot Water Consumed").required(false).unit("kWh"),
                        new SurveyQuestion().name(ELEC_USED_RENEWABLE).label("Electricity Consumed - renewable").required(false).unit("kWh"),
                        new SurveyQuestion().name(RENEWABLE_USED).label("Non-fossil fuel Consumed - renewable").required(false).unit("kWh"),
                        new SurveyQuestion().name(ELEC_EXPORTED).label("Total exported electricity").required(false).unit("kWh"),
                        new SurveyQuestion().name(WATER_VOL).label("Water Volume").required(false).unit("m3")
                ));
        
        SurveyCategory catWaste = new SurveyCategory()
                .name("Waste")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(OTHER_RECOVERY_WEIGHT).label("Other Recovery weight").required(false).unit("tonnes"),
                        new SurveyQuestion().name(INCINERATION_WEIGHT).label("Incineration disposal weight").required(false).unit("tonnes"),
                        new SurveyQuestion().name(LANDFILL_WEIGHT).label("Landfill disposal weight").required(false).unit("tonnes"),
                        new SurveyQuestion().name(RECYCLING_WEIGHT).label("Waste Recycling weight").required(false).unit("tonnes")
                ));

        SurveyCategory catWater = new SurveyCategory()
                .name("Water")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(WASTE_WATER).label("Waste water").required(false).unit("m3")
                ));
        
        SurveyCategory catBizTravel = new SurveyCategory()
                .name("Business Travel")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(BIZ_MILEAGE_ROAD).label("Business Mileage - Road").required(false).unit("miles"),
                        new SurveyQuestion().name(OWNED_FLEET_TRAVEL).label("Organisation Owned Fleet/Pool Road Travel").required(false).unit("miles"),
                        new SurveyQuestion().name(LEASED_FLEET_TRAVEL).label("Non-organisation Owned Fleet/Pool Road Travel (Leased, hired etc.)").required(false).unit("miles"),
                        new SurveyQuestion().name(RAIL_MILES).label("Rail").required(false).unit("miles"),
                        new SurveyQuestion().name(DOMESTIC_AIR_MILES).label("Air - Domestic").required(false).unit("miles"),
                        new SurveyQuestion().name(SHORT_HAUL_AIR_MILES).label("Air - Short Haul International Flights").required(false).unit("miles"),
                        new SurveyQuestion().name(LONG_HAUL_AIR_MILES).label("Air - Long Haul International Flights").required(false).unit("miles")
                ));
        
        SurveyCategory catOtherTravel = new SurveyCategory()
                .name("Other Travel")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(PATIENT_MILEAGE).label("Patient Transport Mileage").required(false).unit("miles"),
                        new SurveyQuestion().name(VISITOR_MILEAGE).label("Patient and Visitor Travel").required(false).unit("miles"),
                        new SurveyQuestion().name(TOTAL_EMPLOYEES).label("Total Employees in Organisation").required(false),
                        new SurveyQuestion().name(STAFF_COMMUTE_MILES).label("Staff commute - Average annual distance travelled by road to work").required(false).unit("miles"),
                        // NOTE: calculate "Total Employee Commute for Organisation by road") by summing 2 above
                        new SurveyQuestion().name("healthImpactOfTravel").label("Health impacts of travel and transport associated with provider activities").required(false).unit("QALY")
                ));
        
        SurveyCategory catGases = new SurveyCategory()
                .name("Gases")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(DESFLURANE).label("Desflurane - liquid").required(false).unit("litres"),
                        new SurveyQuestion().name(ISOFLURANE).label("Isoflurane - liquid").required(false).unit("litres"),
                        new SurveyQuestion().name(SEVOFLURANE).label("Sevoflurane - liquid").required(false).unit("litres"),
                        new SurveyQuestion().name(NITROUS_OXIDE).label("Nitrous oxide - gas").required(false).unit("litres"),
                        new SurveyQuestion().name(PORTABLE_NITROUS_OXIDE_MIX).label("Portable nitrous oxide and oxygen 50/50 split - gas").required(false).unit("litres"),
                        new SurveyQuestion().name(PORTABLE_NITROUS_OXIDE_MIX_MATERNITY).label("Maternity Manifold nitrous oxide and oxygen 50/50 split - gas").required(false).unit("litres")
                        // Calculate: "Total: Nitrous oxide with oxygen 50/50 split - gas"
                        // Calculate: Total: Converted to nitrous oxide only - gas
                ));
        
        SurveyCategory catAdditional = new SurveyCategory()
                .name("Additional")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(CHP_ELECTRICAL_OUTPUT).label("Total electrical energy output of the CHP system/s").required(false).unit("kWh"),
                        new SurveyQuestion().name(EXPORTED_THERMAL_ENERGY).label("Total exported thermal energy").required(false).unit("kWh"),
                        new SurveyQuestion().name(WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood logs)").required(false).unit("kWh"),
                        new SurveyQuestion().name(WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood chips)").required(false).unit("kWh"),
                        new SurveyQuestion().name(WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION).label("Non-fossil fuel Consumed - renewable (wood pellets)").required(false).unit("kWh"),
                        new SurveyQuestion().name(ELEC_OWNED_RENEWABLE_CONSUMPTION).label("Electricity Consumed - renewable (Solar Panels)").required(false).unit("kWh"),
                        new SurveyQuestion().name(LEASED_ASSETS_ENERGY_USE).label("Leased Assets Energy Use(Upstream - Gas, Coal & Electricity)").required(false).unit("tCO2e"),
                        new SurveyQuestion().name(GREEN_TARIFF_ADDITIONAL_PCT).label("Percentage of green tariff supply proven as additional").required(false).unit("%"),
                        new SurveyQuestion().name(THIRD_PARTY_ADDITIONAL_PCT).label("Percentage of third party owned supply proven as additional").required(false).unit("%"),
                        new SurveyQuestion().name(LEASED_ASSETS_ENERGY_USE).label("Leased Assets Energy Use(Upstream - Gas, Coal & Electricity)").required(false).unit("tCO2e")
                ));
        
        SurveyCategory catSpendProfile = new SurveyCategory()
                .name("Spend Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().name(BIZ_SVC_SPEND).label("Business services").required(false).unit("kWh"),
                        new SurveyQuestion().name(CONSTRUCTION_SPEND).label("Construction").required(false).unit("kWh"),
                        new SurveyQuestion().name(CATERING_SPEND).label("Food and catering").required(false).unit("kWh"),
                        new SurveyQuestion().name(FREIGHT_SPEND).label("Freight transport").required(false).unit("kWh"),
                        new SurveyQuestion().name(ICT_SPEND).label("Information and communication technologies").required(false).unit("kWh"),
                        new SurveyQuestion().name(CHEM_AND_GAS_SPEND).label("Manufactured fuels chemicals and gases").required(false).unit("kWh"),
                        new SurveyQuestion().name(MED_INSTRUMENTS_SPEND).label("Medical Instruments /equipment").required(false).unit("kWh"),
                        new SurveyQuestion().name(OTHER_MANUFACTURED_SPEND).label("Other manufactured products").required(false).unit("kWh"),
                        new SurveyQuestion().name(OTHER_SPEND).label("Other procurement").required(false).unit("kWh"),
                        new SurveyQuestion().name(PAPER_SPEND).label("Paper products").required(false).unit("kWh"),
                        new SurveyQuestion().name(PHARMA_SPEND).label("Pharmaceuticals").required(false).unit("m3"),
                        new SurveyQuestion().name(TRAVEL_SPEND).label("Travel").required(false).unit("kWh"),
                        new SurveyQuestion().name(COMMISSIONING_SPEND).label("Commissioning").required(false).unit("m3")
                ));
        
        Survey survey = new Survey().name(ID).status("Draft")
                .applicablePeriod("2016-17")
                .categories(Arrays.asList(catOrg, catPolicy, catPerf,
                        catFinancial, catEnergy, catWaste,
                        catWater, catBizTravel, catOtherTravel, catGases,
                        catAdditional, catSpendProfile));
        return survey;
    }
}