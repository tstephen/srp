package digital.srp.sreport.model.returns;

public class Eric1617 implements EricDataSet, EricQuestions {
    
    public static final String PERIOD = "2016-17";

    public static final String NAME = "ERIC-"+PERIOD;
    
    /*
     * Combination of 
     * - HospitalEstatesAndFacilitiesStatistics_11133
     */
    public static final String DATA_FILE = "/data/ERIC-"+PERIOD+".csv";
    public static final String[] HEADERS = { 
            ORG_CODE,
            ORG_NAME,
            COMMISSIONING_REGION,
            ORG_TYPE,
            NO_ACUTE_SITES,
            NO_SPECIALIST_SITES,
            NO_MIXED_SITES,
            NO_MENTAL_HEALTH_SITES,
            NO_LD_SITES,
            NO_MENTAL_HEALTH_LD_SITES,
            NO_COMMUNITY_HOSPITAL_SITES,
            NO_OTHER_INPATIENT_SITES,
            NO_OUTPATIENT_SITES,
            NO_SUPPORT_SITES,
            NO_OTHER_SITES,
            NO_NHS_PROPERTY_SVCS_SITES,
            ESTATES_DEV_STRATEGY,
            HEALTHY_TRANSPORT_PLAN,
            BOARD_ADAPTATION_PLAN,
            SDMP_CRMP,
            CARBON_REDUCTION_TARGET,
            PFA_ASSESSMENT,
            PFA_ACTION_PLAN,
            CAPITAL_NEW_BUILD,
            CAPITAL_IMPROVING_EXISTING,
            CAPITAL_EQUIPMENT,
            PRIVATE_SECTOR,
            BACKLOG_MAINTENANCE_VAL,
            CIP_ACTUAL_VAL,
            CIP_PLANNED_VAL,
            INCOME_CATERING,
            INCOME_LAUNDRY,
            INCOME_OTHER,
            NO_RIDDOR_INCIDENTS,
            NO_FM_INCIDENTS,
            NO_FM_CLINICAL_INCIDENTS,
            OVERHEATING_EVENTS,
            PCT_TEMP_MONITORED,
            NO_FIRES, 
            NO_FALSE_ALARMS,
            NO_DEATHS_FROM_FIRE,
            NO_INJURIES_FROM_FIRE,
            NO_INJURIES_DURING_EVACUATION,
            "Hard FM (Estates) costs (£)", "Soft FM (Hotel services) costs (£)",
            "Estates and facilities finance costs (£)",
            "Maintenance service costs (£)",
            "Income received for area leased out for retail sales (£)",
            "Area leased out for retail sales (m²)",
            "Gross internal floor area (m²)", "Occupied floor area (m²)",
            "NHS estate occupied floor area (%)", "Site heated volume (m³)",
            "Building footprint (m²)", "Site land area (Hectare)",
            "Clinical space (m²)", "Non-clinical space (m²)",
            "Not functionally suitable - occupied floor area (%)",
            "Not functionally suitable - patient occupied floor area (%)",
            "Floor area - empty (%)", "Floor area - under used (%)",
            "Single bedrooms for patients with en-suite facilities (No.)",
            "Single bedrooms for patients without en-suite facilities (No.)",
            "Age profile - 2015 to 2024 (%)", "Age profile - 2005 to 2014 (%)",
            "Age profile - 1995 to 2004 (%)", "Age profile - 1985 to 1994 (%)",
            "Age profile - 1975 to 1984 (%)", "Age profile - 1965 to 1974 (%)",
            "Age profile - 1955 to 1964 (%)", "Age profile - 1948 to 1954 (%)",
            "Age profile - pre 1948 (%)",
            "Age profile - total (must equal 100%) (%)",
            "Cost to eradicate high risk backlog (£)",
            "Cost to eradicate significant risk backlog (£)",
            "Cost to eradicate moderate risk backlog (£)",
            "Cost to eradicate low risk backlog (£)",
            "Risk adjusted backlog cost (£)", "Cost to eradicate backlog (£)",
            "Energy costs (all energy supplies) (£)",
            "Electricity consumed (kWh)", "Gas consumed (kWh)",
            "Oil consumed (kWh)", "Coal consumed (kWh)", "Steam consumed (kWh)",
            "Hot water consumed (kWh)",
            "Electricity consumed - renewable (kWh)",
            "Non-fossil fuel consumed - renewable (kWh)",
            "Fossil energy input to CHP system/s (kWh)",
            "Thermal energy output of CHP system/s (kWh)",
            "Electrical energy output of CHP system/s (kWh)",
            "Exported electricity (kWh)", "Exported thermal energy (kWh)",
            "Water cost (£)", "Sewage cost (£)",
            "Water volume (including borehole) (m³)", "Clinical waste cost (£)",
            "Clinical waste volume (Tonnes)", "Special waste cost (£)",
            "Special waste volume (Tonnes)", "Domestic waste cost (£)",
            "Waste recycling, recovery cost (£)",
            "Parking spaces available (No.)",
            "Designated disabled parking spaces (No.)",
            "Average fee charged per hour for patient/visitor parking (£)",
            "Average fee charged per hour for staff parking (£)",
            "Cleaning service cost (£)", "Cleaning staff (WTE)",
            "Cost of cleaning occupied floor area assessed as Red/Very High Risk (£)",
            "Occupied floor area assessed as Red/Very High Risk (%)",
            "Required standard for occupied floor area assessed as Red/Very High Risk (%)",
            "Achieved standard for occupied floor area identified as Red/Very High Risk (%)",
            "Cost of cleaning occupied floor area assessed as Amber/High and Significant Risk (£)",
            "Occupied floor area assessed as Amber/High and Significant Risk (%)",
            "Required standard for occupied floor area assessed as Amber/High and Significant Risk (%)",
            "Achieved standard for occupied floor area identified as Amber/High and Significant Risk (%)",
            "Cost of cleaning occupied floor area assessed as Green/Low Risk (£)",
            "Occupied floor area assessed as Green/Low Risk (%)",
            "Required standard for occupied floor area assessed as Green/Low Risk (%)",
            "Achieved standard for occupied floor area identified as Green/Low Risk (%)",
            "Cost of cleaning the occupied floor area not requiring regular cleaning (£)",
            "Occupied floor area not requiring regular cleaning (%)",
            "Inpatient food service cost (£)",
            "Inpatient main meals requested (No.)",
            "Cost of feeding one inpatient per day (inpatient meal day) (£)",
            "Laundry and linen service cost (£)", "Pieces per annum (No.)",
            "Portering service cost (£)", "Portering staff (WTE)"
    };
    
    @Override
    public String getDataFile() {
        return DATA_FILE;
    }
    @Override
    public String[] getHeaders() {
        return HEADERS;
    }
    @Override
    public String getPeriod() {
        return PERIOD;
    }

}
