/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.model.returns;

public class Eric1617 implements EricDataSet, EricQuestions {

    public static final String PERIOD = "2016-17";

    public static final String NAME = "ERIC-" + PERIOD;

    public static final String DATA_FILE = "/data/ERIC-" + PERIOD + ".csv";

    public static final String[] HEADERS = {
            "Organisation Code",
            "Organisation Name", "Commissioning Region", "Organisation Type",
            "Number of sites - General acute hospital (No.)",
            "Number of sites - Specialist hospital (acute only) (No.)",
            "Number of sites - Mixed service hospital (No.)",
            "Number of sites - Mental Health (including Specialist services) (No.)",
            "Number of sites - Learning Disabilities (No.)",
            "Number of sites - Mental Health and Learning Disabilities (No.)",
            "Number of sites - Community hospital (with inpatient beds) (No.)",
            "Number of sites - Other inpatient (No.)",
            "Number of sites - Non inpatient (No.)",
            "Number of sites - Support facilities (No.)",
            "Number of sites - Unreported sites (No.)",
            "Number of sites leased from NHS Property Services (No.)",
            "Estates Development Strategy (Yes/No)",
            "Healthy transport plan (Yes/No)",
            "Board approved Adaptation Plan (Yes/No)",
            "Sustainable Development Management Plan/Carbon Reduction Management Plan (Yes/No)",
            "Carbon reduction target (Select)",
            "NHS Premises and Facilities Assurance - Assessment/Approval (Select)",
            "NHS Premises and Facilities Assurance - action plan (Select)",
            "Capital investment for new build (£)",
            "Capital investment for improving existing buildings (£)",
            "Capital investment for equipment (£)",
            "Private Sector investment (£)",
            "Investment to reduce backlog maintenance (£)",
            "Cost to meet NHS Premises and Facilities Assurance action plan (£)",
            "Estates and Facilities savings from Cost Improvement Plans (£)",
            "Estates and Facilities planned savings from Cost Improvement Plans (£)",
            "Income from services provided to other organisations - catering (£)",
            "Income from services provided to other organisations - laundry and linen (£)",
            "Income from services provided to other organisations - other (£)",
            "RIDDOR incidents (No.)",
            "Estates and facilities related incidents (No.)",
            "Clinical service incidents caused by estates and infrastructure failure (No.)",
            "Overheating occurrences triggering a risk assessment (No.)",
            "Percentage of clinical space monitored for temperatures (%)",
            "Fires recorded (No.)", "False alarms - No call out (No.)",
            "False alarms - Call out (No.)",
            "Number of deaths resulting from fire(s) (No.)",
            "Number of people injured resulting from fire(s) (No.)",
            "Number of patients sustaining injuries during evacuation (No.)",
            "Estates and facilities finance costs (£)",
            "Estates and property maintenance (£)",
            "Grounds and gardens maintenance (£)",
            "Electro Bio Medical Equipment maintenance cost (£)",
            "Other Hard FM (Estates) costs (£)",
            "Other Soft FM (Hotel Services) costs (£)",
            "Income received for area leased out for retail sales (£)",
            "Area leased out for retail sales (m²)",
            "Gross internal floor area (m²)", "Occupied floor area (m²)",
            "NHS estate occupied floor area (%)", "Site heated volume (m³)",
            "Land area owned (Hectares)",
            "Land area not delivering services (Hectares)",
            "Clinical space (m²)", "Non-clinical space (m²)",
            "Not functionally suitable - occupied floor area (%)",
            "Not functionally suitable - patient occupied floor area (%)",
            "Floor area - empty (%)", "Floor area - under used (%)",
            "Single bedrooms for patients with en-suite facilities (No.)",
            "Single bedrooms for patients without en-suite facilities (No.)",
            "Isolation rooms (No.)", "Cost to eradicate high risk backlog (£)",
            "Cost to eradicate significant risk backlog (£)",
            "Cost to eradicate moderate risk backlog (£)",
            "Cost to eradicate low risk backlog (£)",
            "Cost to eradicate Safety related Critical Infrastructure Risk (£)",
            "Cost to eradicate non-compliance related Critical Infrastructure Risk (£)",
            "Cost to eradicate continuity related Critical Infrastructure Risk (£)",
            "CHP units operated on the site (No.)", "CHP unit/s size (Watts)",
            "CHP unit/s efficiency (%)",
            "Fossil energy input to CHP system/s (kWh)",
            "Thermal energy output of CHP system/s (kWh)",
            "Electrical energy output of CHP system/s (kWh)",
            "Exported electricity (kWh)", "Exported thermal energy (kWh)",
            "Energy costs (all energy supplies) (£)",
            "Electricity consumed (kWh)", "Gas consumed (kWh)",
            "Oil consumed (kWh)", "Coal consumed (kWh)", "Steam consumed (kWh)",
            "Hot water consumed (kWh)",
            "Electricity consumed - green energy tariff (kWh)",
            "Electricity consumed - third party owned renewable (kWh)",
            "Non-fossil fuel consumed - renewable (kWh)",
            "Electrical energy output of owned onsite renewables (kWh)",
            "Peak electrical load (MW)", "Maximum electrical load (MW)",
            "Water cost (£)", "Sewage cost (£)",
            "Water volume (including borehole) (m³)",
            "Landfill disposal cost (£)", "Landfill disposal volume (Tonnes)",
            "Incineration disposal cost (£)",
            "Incineration disposal volume (Tonnes)", "Waste reycling cost (£)",
            "Waste reycling volume (Tonnes)", "Other recovery cost (£)",
            "Other recovery volume (Tonnes)", "Parking spaces available (No.)",
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
