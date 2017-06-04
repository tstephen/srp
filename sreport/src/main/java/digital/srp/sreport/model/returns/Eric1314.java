package digital.srp.sreport.model.returns;

public class Eric1314 implements EricDataSet {

    private static final String PERIOD = "2013-14";
    
    public static final String NAME = "ERIC-" + PERIOD;
    
    /*
     * Combination of 
     * - HospitalEstatesAndFacilitiesStatistics_9902
     */
    public static final String DATA_FILE = "/data/ERIC-" + PERIOD + ".csv";
    public static final String[] HEADERS = {
            "Organisation Code",
            "Organisation Name", "Commissioning Region", "Organisation Type",
            "Gross internal site floor area (m²)", "Occupied floor area (m²)",
            "NHS estate occupied floor area (%)", "Site heated volume (m³)",
            "Site footprint (m²)", "Site land area (Hectare)",
            "Patient occupied floor area (m²)",
            "Non-patient occupied floor area (m²)",
            "Not functionally suitable - occupied floor area (%)",
            "Not functionally suitable - patient occupied floor area (%)",
            "Un-utilised space (%)", "Available beds (No.)",
            "Occupied beds (No.)",
            "Single bedrooms for patients with en-suite facilities (No.)",
            "Single bedrooms for patients without en-suite facilities (No.)",
            "Age profile - 2005 to present (%)",
            "Age profile - 1995 to 2004 (%)", "Age profile - 1985 to 1994 (%)",
            "Age profile - 1975 to 1984 (%)", "Age profile - 1965 to 1974 (%)",
            "Age profile - 1955 to 1964 (%)", "Age profile - 1948 to 1954 (%)",
            "Age profile - pre 1948 (%)",
            "Age profile - total (must equal 100%) (%)",
            "Cost to eradicate high risk backlog (£)",
            "Cost to eradicate significant risk backlog (£)",
            "Cost to eradicate moderate risk backlog (£)",
            "Cost to eradicate low risk backlog (£)",
            "Risk adjusted backlog cost (£)",
            "Number of CHP units operated on the site (No.)",
            "Total full load rating of the electrical generator plant (kW)",
            "Total fossil energy input to the CHP system/s (kWh)",
            "Total thermal energy output of the CHP system/s (kWh)",
            "Total electrical energy output of the CHP system/s (kWh)",
            "Total exported electricity for the site (kWh)",
            "Total exported thermal energy for the site (kWh)",
            "Electricity consumed - utility (kWh)",
            "Gas consumed - utility (kWh)", "Oil  consumed - utility (kWh)",
            "Coal consumed - utility (kWh)",
            "Electricity consumed - local (kWh)",
            "Steam consumed - local (kWh)", "Hot water consumed - local (kWh)",
            "Electricity consumed - renewable (kWh)",
            "Non-fossil fuel consumed - renewable (kWh)",
            "Total energy cost (all energy supplies, utility, local and renewable) (£)",
            "Water volume (including borehole) (m³)",
            "Water and sewage cost (£)",
            "High temperature disposal waste weight (Tonnes)",
            "Total waste cost (£)",
            "Non burn treatment (alternative treatment plant) disposal waste weight (Tonnes)",
            "Landfill disposal waste weight (Tonnes)",
            "Waste electrical and electronic equipment (WEEE) weight (Tonnes)",
            "Preparing for re-use volume (Tonnes)",
            "Other recovery volume (Tonnes)", "Waste recycling volume (Tonnes)",
            "Waste recycling, recovery and preparing for re-use cost (£)",
            "Patient/visitor concession scheme provided (Yes/No/None)",
            "Total parking spaces available (No.)",
            "Designated parking spaces available for patients/visitors (No.)",
            "Designated disabled parking spaces (No.)",
            "Designated parking spaces available for staff (No.)",
            "Average fee charged per hour for patient/visitor parking (£)",
            "Average fee charged per hour for Staff parking (£)",
            "Cost of feeding one in-patient per day (in-patient meal day) (£)",
            "Ward food waste - unserved meals (%)",
            "Total in-patient main meals requested (No.)",
            "Gross cost of in-patient services (£)" 
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
