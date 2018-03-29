package digital.srp.sreport.importers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.EricDataSet;
import digital.srp.sreport.model.returns.EricQuestions;

public class EricCsvImporter implements EricQuestions {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EricCsvImporter.class);

    private List<Question> questions;

    public List<SurveyReturn> readEricReturns(EricDataSet ericDataSet) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(ericDataSet.getDataFile()))) {
            return readEricReturns(isr, ericDataSet.getHeaders(), ericDataSet.getPeriod());
        }
    }

    public List<SurveyReturn> readEricReturns(Reader in, String[] headers, String period)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers).withDelimiter('$'));
        LOGGER.info(String.format("readEricReturns"));

        questions = new ArrayList<Question>();
        for (String hdr : headers) {
            questions.add(new Question().q(lookupQuestion(hdr)));
        }
        LOGGER.debug("Found {} questions", questions.size());

        List<SurveyReturn> surveyResponses = new ArrayList<SurveyReturn>();
        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            if (record.getRecordNumber() > 2) { // skip headers
                Set<Answer> surveyAnswers = new HashSet<Answer>();
                String org = record.get(0);
                for (int i = 0; i < record.size(); i++) {
                    surveyAnswers.add(new Answer()
                            .question(getQuestion(i)).response(record.get(i))
                            .applicablePeriod(period)
                            .status(StatusType.Published.name()));
                }

                surveyResponses.add(new SurveyReturn()
                        .name(String.format("ERIC-%1$s-%2$s", period, org))
                        .applicablePeriod(period)
                        .org(org)
                        .status(StatusType.Published.name())
                        .answers(surveyAnswers));
            }
        }
        parser.close();

        return surveyResponses;
    }

    private Q lookupQuestion(String hdr) {
        switch (hdr) {

        case "Organisation Code":
            return Q.ORG_CODE;
        case "Organisation Name":
            return Q.ORG_NAME;
        case "Commissioning Region":
            return Q.COMMISSIONING_REGION;
        case "Organisation Type":
            return Q.ORG_TYPE;
        case "Number of sites - General acute hospital (No.)":
            return Q.NO_ACUTE_SITES;
        case "Number of sites - Specialist hospital (acute only) (No.)":
            return Q.NO_SPECIALIST_SITES;
        case "Number of sites - Mixed service hospital (No.)":
            return Q.NO_MIXED_SITES;
        case "Number of sites - Mental Health (including Specialist services) (No.)":
            return Q.NO_MENTAL_HEALTH_SITES;
        case "Number of sites - Learning Disabilities (No.)":
            return Q.NO_LD_SITES;
        case "Number of sites - Mental Health and Learning Disabilities (No.)":
            return Q.NO_MENTAL_HEALTH_LD_SITES;
        case "Number of sites - Community hospital (with inpatient beds) (No.)":
            return Q.NO_COMMUNITY_HOSPITAL_SITES;
        case "Number of sites - Other inpatient (No.)":
            return Q.NO_OTHER_INPATIENT_SITES;
        case "Number of sites - Non inpatient (No.)":
            return Q.NO_OUTPATIENT_SITES;
        case "Number of sites - Support facilities (No.)":
            return Q.NO_SUPPORT_SITES;
        case "Number of sites - Unreported sites (No.)":
            return Q.NO_OTHER_SITES;
        case "Estates Development Strategy (Yes/No)":
            return Q.ESTATES_DEV_STRATEGY;
        case "Healthy transport plan (Yes/No)":
            return Q.HEALTHY_TRANSPORT_PLAN;
        case "Board approved Adaptation Plan (Yes/No)":
            return Q.BOARD_ADAPTATION_PLAN;
        case "Sustainable Development Management Plan/Carbon Reduction Management Plan (Yes/No)":
            return Q.SDMP_CRMP;
        case "Carbon reduction target (Select)":
            return Q.CARBON_REDUCTION_TARGET;
        case "NHS Premises and Facilities Assurance - Assessment/Approval (Select)":
            return Q.PFA_ASSESSMENT;
        case "NHS Premises and Facilities Assurance - action plan (Select)":
            return Q.PFA_ACTION_PLAN;
        case "Value of contracted out services (£)":
            return Q.CONTRACTING_OUT_VAL;
        case "Percentage of hard FM (estates) and soft FM (hotel services) contracted out (%)":
            return Q.CONTRACTING_OUT_PCT;
        case "Capital investment for new build (£)":
            return Q.CAPITAL_NEW_BUILD;
        case "Capital investment for improving existing buildings (£)":
            return Q.CAPITAL_IMPROVING_EXISTING;
        case "Capital investment for equipment (£)":
            return Q.CAPITAL_EQUIPMENT;
        case "Private Sector investment (£)":
            return Q.PRIVATE_SECTOR;
        case "Investment to reduce backlog maintenance (£)":
            return Q.BACKLOG_MAINTENANCE_VAL;
        case "Cost to meet NHS Premises and Facilities Assurance action plan (£)":
            return Q.PFA_ACTION_PLAN_VAL;
        case "Non-emergency patient transport (£)":
            return Q.NON_EMERGENCY_TRANSPORT_VAL;
        case "Income from services provided to other organisations - catering (£)":
            return Q.INCOME_CATERING;
        case "Income from services provided to other organisations - laundry and linen (£)":
            return Q.INCOME_LAUNDRY;
        case "Income from services provided to other organisations (£)":
            return Q.INCOME_ALL;
        case "Income from services provided to other organisations - other (£)":
            return Q.INCOME_OTHER;
        case "RIDDOR incidents (No.)":
            return Q.NO_RIDDOR_INCIDENTS;
        case "Estates and facilities related incidents (No.)":
            return Q.NO_FM_INCIDENTS;
        case "Clinical service incidents caused by estates and infrastructure failure (No.)":
            return Q.NO_FM_CLINICAL_INCIDENTS;
        case "Fires recorded (No.)":
            return Q.NO_FIRES;
        case "False alarms (No.)":
            return Q.NO_FALSE_ALARMS;
        case "Number of deaths resulting from fire(s) (No.)":
            return Q.NO_FIRE_DEATHS;
        case "Number of people injured resulting from fire(s) (No.)":
            return Q.NO_FIRE_INJURIES;
        case "Number of patients sustaining injuries during evacuation (No.)":
            return Q.NO_EVAC_INJURIES;
        case "Hard FM (Estates) costs (£)":
        case "Other Hard FM (Estates) costs (£)":
            return Q.HARD_FM_COSTS;
        case "Soft FM (Hotel Services) costs (£)":
        case "Soft FM (Hotel services) costs (£)":
        case "Other Soft FM (Hotel Services) costs (£)":
            return Q.SOFT_FM_COSTS;
        case "Estates and facilities finance costs (£)":
            return Q.FM_FINANCE_COSTS;
        case "Maintenance service costs (£)":
        case "Estates and property maintenance (£)":
            return Q.MAINT_COSTS;
        case "Grounds and gardens maintenance (£)":
            return Q.GROUNDS_COSTS;
        case "Income received for area leased out for retail sales (£)":
            return Q.INCOME_RETAIL;
        case "Area leased out for retail sales (m²)":
            return Q.AREA_RETAIL;
        case "Gross internal site floor area (m²)":
        case "Gross internal floor area (m²)":
            return Q.FLOOR_AREA;
        case "Occupied floor area (m²)":
            return Q.OCCUPIED_FLOOR_AREA;
        case "NHS estate occupied floor area (%)":
            return Q.NHS_OCCUPIED_FLOOR_AREA;
        case "Site heated volume (m³)":
            return Q.HEATED_VOL;
        case "Building footprint (m²)":
            return Q.BLDG_FOOTPRINT;
        case "Site footprint (m²)":
        case "Site land area (Hectare)":
        case "Land area owned (Hectares)":
            return Q.SITE_FOOTPRINT;
        case "Land area not delivering services (Hectares)":
            return Q.SITE_UNUSED_FOOTPRINT;
        case "Patient occupied floor area (m²)":
            return Q.PATIENT_OCCUPIED_FLOOR_AREA;
        case "Non-patient occupied floor area (m²)":
            return Q.NON_PATIENT_OCCUPIED_FLOOR_AREA;
        case "Clinical space (m²)":
            return Q.CLINICAL_FLOOR_AREA;
        case "Non-clinical space (m²)":
            return Q.NON_CLINICAL_FLOOR_AREA;
        case "Not functionally suitable - occupied floor area (%)":
            return Q.UNSUITABLE_OCCUPIED_FLOOR_AREA;
        case "Not functionally suitable - patient occupied floor area (%)":
            return Q.UNSUITABLE_PATIENT_OCCUPIED_FLOOR_AREA;
        case "Un-utilised space (%)":
        case "Floor area - empty (%)":
            return Q.EMPTY_FLOOR_AREA;
        case "Floor area - under used (%)":
            return Q.UNDER_USED_FLOOR_AREA;
        case "Available beds (No.)":
            return Q.AVAIL_BEDS;
        case "Occupied beds (No.)":
            return Q.OCCUPIED_BEDS;
        case "Single bedrooms for patients with en-suite facilities (No.)":
            return Q.SINGLE_BED_WITH_EN_SUITE;
        case "Single bedrooms for patients without en-suite facilities (No.)":
            return Q.SINGLE_BED_WITHOUT_EN_SUITE;
        case "Age profile - 2015 to present (%)":
        case "Age profile - 2015 to 2024 (%)":
            return Q.AGE_PROFILE_2015_2024;
        case "Age profile - 2005 to present (%)":
        case "Age profile - 2005 to 2014 (%)":
            return Q.AGE_PROFILE_2005_2014;
        case "Age profile - 1995 to 2004 (%)":
            return Q.AGE_PROFILE_1995_2004;
        case "Age profile - 1985 to 1994 (%)":
            return Q.AGE_PROFILE_1985_1994;
        case "Age profile - 1975 to 1984 (%)":
            return Q.AGE_PROFILE_1975_1984;
        case "Age profile - 1965 to 1974 (%)":
            return Q.AGE_PROFILE_1965_1974;
        case "Age profile - 1955 to 1964 (%)":
            return Q.AGE_PROFILE_1955_1964;
        case "Age profile - 1948 to 1954 (%)":
            return Q.AGE_PROFILE_1948_1954;
        case "Age profile - pre 1948 (%)":
            return Q.AGE_PROFILE_PRE_1948;
        case "Age profile - total (must equal 100%) (%)":
            return Q.AGE_PROFILE_TOTAL;
        case "Cost to eradicate high risk backlog (£)":
            return Q.ERADICATE_HIGH_RISK_BACKLOG_COST;
        case "Cost to eradicate significant risk backlog (£)":
            return Q.ERADICATE_SIG_RISK_BACKLOG_COST;
        case "Cost to eradicate moderate risk backlog (£)":
            return Q.ERADICATE_MOD_RISK_BACKLOG_COST;
        case "Cost to eradicate low risk backlog (£)":
            return Q.ERADICATE_LOW_RISK_BACKLOG_COST;
        case "Risk adjusted backlog cost (£)":
            return Q.RISK_ADJUSTED_BACKLOG_COST;
        case "Cost to eradicate backlog (£)":
            return Q.ERADICATE_BACKLOG_COST;
        case "Energy cost (all energy supplies) (£)":
        case "Total energy cost (all energy supplies, utility, local and renewable) (£)":
        case "Energy costs (all energy supplies) (£)":
            return Q.TOTAL_ENERGY_COST;
        case "Electricity consumed - utility (kWh)":
        case "Electricity consumed (kWh)":
            return Q.ELEC_USED;
        case "Gas consumed - utility (kWh)":
        case "Gas consumed (kWh)":
            return Q.GAS_USED;
        case "Oil  consumed - utility (kWh)":
        case "Oil consumed (kWh)":
            return Q.OIL_USED;
        case "Coal consumed (kWh)":
        case "Coal consumed - utility (kWh)":
            return Q.COAL_USED;
        case "Steam consumed - local (kWh)":
        case "Steam consumed (kWh)":
            return Q.STEAM_USED;
        case "Hot water consumed - local (kWh)":
        case "Hot water consumed (kWh)":
            return Q.HOT_WATER_USED;
        case "Electricity consumed - local (kWh)": 
            return Q.ELEC_USED_LOCAL;
        case "Electricity consumed - renewable (kWh)":
            return Q.ELEC_RENEWABLE_USED;
        case "Electricity consumed - green energy tariff (kWh)":
            return Q.ELEC_USED_GREEN_TARIFF;
        case "Electricity consumed - third party owned renewable (kWh)":
            return Q.ELEC_3RD_PTY_RENEWABLE_USED;
        case "Electrical energy output of owned onsite renewables (kWh)":
            return Q.ELEC_OWNED_RENEWABLE_USED;
        case "Non-fossil fuel consumed - renewable (kWh)":
            return Q.RENEWABLE_USED;
        case "Fossil energy input to the CHP system/s (kWh)":
        case "Total fossil energy input to the CHP system/s (kWh)":
        case "Fossil energy input to CHP system/s (kWh)":
            return Q.FOSSIL_USED_IN_CHP;
        case "Thermal energy output of the CHP system/s (kWh)":
        case "Total thermal energy output of the CHP system/s (kWh)":
        case "Thermal energy output of CHP system/s (kWh)":
            return Q.CHP_THERMAL_OUTPUT;
        case "Electrical energy output of the CHP system/s (kWh)":
        case "Total electrical energy output of the CHP system/s (kWh)":            
        case "Electrical energy output of CHP system/s (kWh)":
            return Q.CHP_ELECTRICAL_OUTPUT;
        case "Exported electricity (kWh)":
        case "Total exported electricity for the site (kWh)":
            return Q.ELEC_EXPORTED;
        case "CHP units operated on the site (No.)":
        case "Number of CHP units operated on the site (No.)":
            return Q.NO_CHP_UNITS;
        case "Total exported thermal energy for the site (kWh)":
        case "Exported thermal energy (kWh)":
            return Q.EXPORTED_THERMAL_ENERGY;
        case "Full load rating of the electrical generator plant (kW)":
        case "Total full load rating of the electrical generator plant (kW)":
            return Q.ELEC_GENERATION;
        case "Water cost (£)":
            return Q.WATER_COST;
        case "Water and sewage cost (£)":
            return Q.WATER_AND_SEWAGE_COST;
        case "Sewage cost (£)":
            return Q.SEWAGE_COST;
        case "Water volume (including borehole) (m³)":
            return Q.WATER_VOL;
        case "Clinical waste cost (£)":
            return Q.CLINICAL_WASTE_COST;
        case "Clinical waste volume (Tonnes)":
            return Q.CLINICAL_WASTE_WEIGHT;
        case "Special waste cost (£)":
            return Q.SPECIAL_WASTE_COST;
        case "Special waste volume (Tonnes)":
            return Q.SPECIAL_WASTE_WEIGHT;
        case "Domestic waste cost (£)":
            return Q.DOMESTIC_WASTE_COST;
        case "Waste recycling, recovery cost (£)":
        case "Waste reycling cost (£)":
            return Q.WASTE_RECYLING_COST;
        case "Waste reycling volume (Tonnes)":
            return Q.WASTE_RECYLING_WEIGHT;
        case "Incineration disposal cost (£)":
            return Q.WASTE_INCINERATION_COST;
        case "Incineration disposal volume (Tonnes)":
            return Q.WASTE_INCINERATION_WEIGHT;
        case "High temperature disposal waste weight involving combustion with energy recovery (Tonnes)":
            return Q.HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT;
        case "High temperature disposal waste weight (Tonnes)":
            return Q.HIGH_TEMP_DISPOSAL_WEIGHT;
        case "Non burn treatment (alternative treatment plant) disposal waste weight (Tonnes)":
            return Q.ALT_WASTE_DISPOSAL_WEIGHT;
        case "Waste electrical and electronic equipment (WEEE) weight (Tonnes)":
            return Q.WEEE_WEIGHT;
        case "Preparing for re-use volume (Tonnes)":
            return Q.REUSE_WEIGHT;
        case "Landfill disposal waste weight (Tonnes)":
        case "Landfill disposal volume (Tonnes)":
            return Q.LANDFILL_WEIGHT;
        case "Landfill disposal cost (£)":
            return Q.LANDFILL_COST;
        case "Other recovery volume (Tonnes)":
            return Q.OTHER_RECOVERY_WEIGHT;
        case "Other recovery cost (£)":
            return Q.OTHER_RECOVERY_COST;
        case "Waste recycling volume (Tonnes)":
            return Q.RECYCLING_WEIGHT;
        case "Waste recycling, recovery and preparing for re-use cost (£)":
            return Q.WASTE_RECYLING_COST;
        case "Waste cost (£)":
        case "Total waste cost (£)":
            return Q.TOTAL_WASTE_COST;
        case "Patient/visitor concession scheme provided (Yes/No/None)": 
            return Q.PATIENT_AND_VISITOR_CONCESSION_SCHEME;
        case "Total parking spaces available (No.)":
        case "Parking spaces available (No.)":
            return Q.PARKING_SPACES;
        case "Designated parking spaces available for patients/visitors (No.)":
        case "Designated disabled parking spaces (No.)":
            return Q.DISABLED_PARKING_SPACES;
        case "Designated parking spaces available for staff (No.)":
            return Q.DISABLED_STAFF_PARKING_SPACES;
        case "Average fee charged per hour for patient/visitor parking (£)":
            return Q.AVG_PARKING_FEE;
        case "Average fee charged per hour for Staff parking (£)":
        case "Average fee charged per hour for staff parking (£)":
            return Q.AVG_STAFF_PARKING_FEE;
        case "Cleaning service cost (£)":
            return Q.CLEANING_COST;
        case "Cleaning staff (WTE)":
            return Q.NO_CLEANING_STAFF;            
        case "Cost of cleaning occupied floor area assessed as Red/Very High Risk (£)":
            return Q.VH_RISK_CLEANING_COST;
        case "Occupied floor area assessed as Red/Very High Risk (%)":
            return Q.VH_RISK_CLEANING_AREA;
        case "Required standard for occupied floor area assessed as Red/Very High Risk (%)":
            return Q.VH_RISK_CLEANING_AREA_REQD_PCT;
        case "Achieved standard for occupied floor area identified as Red/Very High Risk (%)":
            return Q.VH_RISK_CLEANING_AREA_ACTUAL_PCT;
        case "Cost of cleaning occupied floor area assessed as Amber/High and Significant Risk (£)":
            return Q.HS_RISK_CLEANING_COST;
        case "Occupied floor area assessed as Amber/High and Significant Risk (%)":
            return Q.HS_RISK_CLEANING_AREA;
        case "Required standard for occupied floor area assessed as Amber/High and Significant Risk (%)":
            return Q.HS_RISK_CLEANING_AREA_REQD_PCT;
        case "Achieved standard for occupied floor area identified as Amber/High and Significant Risk (%)":
            return Q.HS_RISK_CLEANING_AREA_ACTUAL_PCT;
        case "Cost of cleaning occupied floor area assessed as Green/Low Risk (£)":
            return Q.L_RISK_CLEANING_COST;
        case "Occupied floor area assessed as Green/Low Risk (%)":
            return Q.L_RISK_CLEANING_AREA;
        case "Required standard for occupied floor area assessed as Green/Low Risk (%)":
            return Q.L_RISK_CLEANING_AREA_REQD_PCT;
        case "Achieved standard for occupied floor area identified as Green/Low Risk (%)":
            return Q.L_RISK_CLEANING_AREA_ACTUAL_PCT;
        case "Cost of cleaning the occupied floor area not requiring regular cleaning (£)":
            return Q.INFREQ_CLEANING_COST;
        case "Occupied floor area not requiring regular cleaning (%)":
            return Q.INFREQ_CLEANING_AREA;
        case "In-patient food services cost (£)":
        case "Gross cost of in-patient services (£)":
        case "Inpatient food service cost (£)":
            return Q.CATERING_SPEND;
        case "In-patient main meals requested (No.)":
        case "Total in-patient main meals requested (No.)":
        case "Inpatient main meals requested (No.)":
            return Q.NO_MEALS;
        case "Cost of feeding one in-patient per day (in-patient meal day) (£)":
        case "Cost of feeding one inpatient per day (inpatient meal day) (£)":
            return Q.PER_PATIENT_DAY_MEAL_COST;
        case "Ward food waste - unserved meals (%)":
            return Q.WARD_FOOD_WASTE;
        case "Laundry and linen service cost (£)":
            return Q.LAUNDRY_COST;
        case "Pieces per annum (No.)":
            return Q.NO_LAUNDRY_ITEMS;
        case "Portering (internal patient transport) service cost (£)":
        case "Portering service cost (£)":
            return Q.PORTERING_COST;
        case "Portering staff (WTE)":
            return Q.NO_PORTERING_STAFF;
        case "PFI Unitary charges (£)":
            return Q.PFI_CHARGES;
        case NO_NHS_PROPERTY_SVCS_SITES:
            return Q.NO_NHS_PROPERTY_SVCS_SITES;
        case CIP_ACTUAL_VAL:
            return Q.CIP_ACTUAL_VAL;
        case CIP_PLANNED_VAL:
            return Q.CIP_PLANNED_VAL;
        case OVERHEATING_EVENTS:
            return Q.OVERHEATING_EVENTS;
        case PCT_TEMP_MONITORED:
            return Q.PCT_TEMP_MONITORED;
        case "Electro Bio Medical Equipment maintenance cost (£)":
            return Q.ELECTRO_BIO_MEDICAL_EQUIPT;
        case "Isolation rooms (No.)":
            return Q.ISOLATION_ROOMS;
        case "Cost to eradicate Safety related Critical Infrastructure Risk (£)":
            return Q.COST_SAFETY_RISK;
        case "Cost to eradicate non-compliance related Critical Infrastructure Risk (£)":
            return Q.COST_COMPLIANCE_RISK;
        case "Cost to eradicate continuity related Critical Infrastructure Risk (£)":
            return Q.COST_CONTINUITY_RISK;
        case "CHP unit/s size (Watts)":
            return Q.CHP_SIZE;
        case "CHP unit/s efficiency (%)":
            return Q.CHP_EFFICIENCY;
        case "Peak electrical load (MW)":
            return Q.ELEC_PEAK_LOAD;
        case "Maximum electrical load (MW)":
            return Q.ELEC_MAX_LOAD;
        }
        throw new IllegalArgumentException(
                String.format("ERIC data not expected to include %1$s", hdr));
    }

    private Question getQuestion(int i) {
        return questions.get(i);
    }

}
