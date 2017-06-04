package digital.srp.sreport.model.returns;

public class Eric1415 implements EricDataSet {

    private static final String PERIOD = "2014-15";
    
    public static final String NAME = "ERIC-" + PERIOD;
    
    /*
     * Combination of 
     * - HospitalEstatesAndFacilitiesStatistics_11133
     */
    public static final String DATA_FILE = "/data/ERIC-" + PERIOD +".csv";
    public static final String[] HEADERS = {
            "Organisation Code",
            "Organisation Name", "Commissioning Region", "Organisation Type",
            "Hard FM (Estates) costs (£)", "Soft FM (Hotel Services) costs (£)",
            "Estates and facilities finance costs (£)",
            "Maintenance service costs (£)",
            "Income received for area leased out for retail sales (£)",
            "Area leased out for retail sales (m²)",
            "Income from services provided to other organisations (£)",
            "Gross internal site floor area (m²)", "Occupied floor area (m²)",
            "NHS estate occupied floor area (%)", "Site heated volume (m³)",
            "Building footprint (m²)", "Site land area (Hectare)",
            "Patient occupied floor area (m²)",
            "Non-patient occupied floor area (m²)",
            "Not functionally suitable - occupied floor area (%)",
            "Not functionally suitable - patient occupied floor area (%)",
            "Un-utilised space (%)", "Available beds (No.)",
            "Occupied beds (No.)",
            "Single bedrooms for patients with en-suite facilities (No.)",
            "Single bedrooms for patients without en-suite facilities (No.)",
            "Age profile - 2015 to present (%)",
            "Age profile - 2005 to 2014 (%)", "Age profile - 1995 to 2004 (%)",
            "Age profile - 1985 to 1994 (%)", "Age profile - 1975 to 1984 (%)",
            "Age profile - 1965 to 1974 (%)", "Age profile - 1955 to 1964 (%)",
            "Age profile - 1948 to 1954 (%)", "Age profile - pre 1948 (%)",
            "Age profile - total (must equal 100%) (%)",
            "Cost to eradicate high risk backlog (£)",
            "Cost to eradicate significant risk backlog (£)",
            "Cost to eradicate moderate risk backlog (£)",
            "Cost to eradicate low risk backlog (£)",
            "Risk adjusted backlog cost (£)",
            "CHP units operated on the site (No.)",
            "Full load rating of the electrical generator plant (kW)",
            "Fossil energy input to the CHP system/s (kWh)",
            "Thermal energy output of the CHP system/s (kWh)",
            "Electrical energy output of the CHP system/s (kWh)",
            "Exported electricity (kWh)", "Exported thermal energy (kWh)",
            "Energy cost (all energy supplies) (£)",
            "Electricity consumed - utility (kWh)",
            "Gas consumed - utility (kWh)", "Oil  consumed - utility (kWh)",
            "Coal consumed - utility (kWh)",
            "Electricity consumed - local (kWh)",
            "Steam consumed - local (kWh)", "Hot water consumed - local (kWh)",
            "Electricity consumed - renewable (kWh)",
            "Non-fossil fuel consumed - renewable (kWh)",
            "Water and sewage cost (£)",
            "Water volume (including borehole) (m³)", "Waste cost (£)",
            "Preparing for re-use volume (Tonnes)",
            "Waste recycling volume (Tonnes)", "Other recovery volume (Tonnes)",
            "High temperature disposal waste weight (Tonnes)",
            "High temperature disposal waste weight involving combustion with energy recovery (Tonnes)",
            "Non burn treatment (alternative treatment plant) disposal waste weight (Tonnes)",
            "Landfill disposal waste weight (Tonnes)",
            "Waste electrical and electronic equipment (WEEE) weight (Tonnes)",
            "Parking spaces available (No.)",
            "Designated disabled parking spaces (No.)",
            "Average fee charged per hour for patient/visitor parking (£)",
            "Average fee charged per hour for staff parking (£)",
            "Cleaning service cost (£)", "Cleaning staff (WTE)",
            "In-patient food services cost (£)",
            "In-patient main meals requested (No.)",
            "Cost of feeding one in-patient per day (in-patient meal day) (£)",
            "Laundry and linen service cost (£)", "Pieces per annum (No.)",
            "Portering (internal patient transport) service cost (£)",
            "Portering staff (WTE)", "PFI Unitary charges (£)"
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
