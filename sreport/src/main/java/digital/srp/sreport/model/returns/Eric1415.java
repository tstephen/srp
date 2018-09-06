package digital.srp.sreport.model.returns;

public class Eric1415 implements EricDataSet {

    public static final String PERIOD = "2014-15";

    public static final String NAME = "ERIC-" + PERIOD;

    /*
     * Combination of Trust and Site data from
     * - HospitalEstatesAndFacilitiesStatistics_11133
     */
    public static final String DATA_FILE = "/data/ERIC-" + PERIOD +".csv";

    public static final String[] HEADERS = {
            "Organisation Code",
            "Organisation Name",
            "Commissioning Region",
            "Organisation Type",
            "Number of sites - General acute hospital (No.)",
            "Number of sites - Multi-service hospital (No.)",
            "Number of sites - Short term non-acute hospital (No.)",
            "Number of sites - Long stay hospital (No.)",
            "Number of sites - Specialist hospital (No.)",
            "Number of sites - Community hospital (No.)",
            "Number of sites - Non in-patient facilities (No.)",
            "Number of sites - Non-hospital (patient) (No.)",
            "Number of sites - Support facilities (No.)",
            "Number of sites - Not reported (No.)",
            "Total number of sites (No.)", "Area of sites not reported (m²)",
            "Estates Development Strategy (Yes/No)",
            "Healthy transport plan (Yes/No)",
            "Board approved Adaptation Plan (Yes/No)",
            "Board approved Sustainable Development Management Plan (SDMP) (Yes/No)",
            "Carbon reduction target ()", "NHS Premises Assurance Model ()",
            "NHS Premises Assurance Model - Action Plan ()",
            "Value of contracted out services (£)",
            "Percentage of hard FM (estates) and soft FM (hotel services) contracted out (%)",
            "Capital investment for new build (£)",
            "Capital investment for improving existing buildings (£)",
            "Capital investment for equipment (£)",
            "Private Sector investment (£)",
            "Investment to reduce backlog maintenance (£)",
            "Patient taxi cost (£)", "Income from building and land sales (£)",
            "Building and land area sold (Hectare)",
            "Forecast income from building and land sales for the next financial year (£)",
            "Forecast building and land area to be sold for the next finacial year (Hectare)",
            "Fires recorded (No.)", "False alarms (No.)",
            "Number of deaths resulting from fire(s) (No.)",
            "Number of people injured resulting from fire(s) (No.)",
            "Number of patients sustaining injuries during evacuation (No.)",
            "Hard FM (Estates) costs (£)",
            "Soft FM (Hotel Services) costs (£)",
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
            "Gas consumed - utility (kWh)", "Oil consumed - utility (kWh)",
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
            "Portering staff (WTE)" };

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
