/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.macc.maths.Finance;
import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.macc.web.OrganisationTypeController;

public class OrganisationInterventionTest {

    private static final BigDecimal NO_STAFF_GP = BigDecimal.valueOf(16.1);
    private static final BigDecimal ACTIVE_TRAVEL_UP_FRONT = new BigDecimal(
            40 * 1200000);
    private static final BigDecimal ACTIVE_TRAVEL_ANNUAL_IN = new BigDecimal(
            416012596);
    private static final BigDecimal ACTIVE_TRAVEL_ANNUAL_OUT = new BigDecimal(0);
    private static final BigDecimal NO_STAFF_NATIONAL = new BigDecimal(1200000);
    private static final BigDecimal ACTIVE_TRAVEL_UPTAKE = new BigDecimal(0.25);
    protected static final String TENANT_ID = "sdu";
    protected static OrganisationTypeController svc;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        svc = new OrganisationTypeController();
    }

    @Test
    public void testGPBuildingFabric() {
        InterventionType interventionType = getBuildingFabricInterventionType();
        OrganisationType orgType = getGPOrgType();

        Intervention intervention = new Intervention();
        intervention.setInterventionType(interventionType);
        intervention.setOrganisationType(orgType);
        intervention.setShareOfTotal(6.51f);

        // Organisation should be initialised with defaults
        OrganisationIntervention plan = new OrganisationIntervention();
        plan.setOrganisationType(orgType);
        plan.setIntervention(intervention);

        log(interventionType, orgType, plan);

        assertEquals(0.09, SignificantFiguresFormat.asDouble(plan
                .getTonnesCo2eSavedTargetYear()), 0.1);
        assertEquals(0, plan.getTargetYearSavings().floatValue(), 0.1);

    }

    @Test
    public void testGPHeatingUpgrade() {
        InterventionType interventionType = getHeatingUpgradeInterventionType();
        OrganisationType orgType = getGPOrgType();

        Intervention intervention = new Intervention();
        intervention.setInterventionType(interventionType);
        intervention.setOrganisationType(orgType);
        intervention.setShareOfTotal(6.51f);

        // Organisation should be initialised with defaults
        OrganisationIntervention plan = new OrganisationIntervention();
        plan.setOrganisationType(orgType);
        plan.setIntervention(intervention);

        log(interventionType, orgType, plan);

        assertEquals(0.149, SignificantFiguresFormat.asDouble(plan
                .getTonnesCo2eSavedTargetYear()), 0.1);
        assertEquals(0, plan.getTargetYearSavings().floatValue(), 0.1);
    }

    @Test
    public void testGPActiveTravel() {
        InterventionType interventionType = getActiveTravelInterventionType();
        OrganisationType orgType = getGPOrgType();

        Intervention intervention = new Intervention();
        intervention.setInterventionType(interventionType);
        intervention.setOrganisationType(orgType);

        // Organisation should be initialised with defaults
        OrganisationIntervention plan = new OrganisationIntervention();
        plan.setOrganisationType(orgType);
        plan.setIntervention(intervention);
        plan.setUnitCount(NO_STAFF_GP.doubleValue());

        log(interventionType, orgType, plan);

        BigDecimal staffRatio = NO_STAFF_GP.setScale(8).divide(
                NO_STAFF_NATIONAL, RoundingMode.HALF_DOWN);
        assertEquals(ACTIVE_TRAVEL_UP_FRONT
                .multiply(ACTIVE_TRAVEL_UPTAKE)
                .multiply(staffRatio).doubleValue(),
                SignificantFiguresFormat.round(plan.getCashOutflowUpFront())
                        .doubleValue(), 10);
        assertEquals(ACTIVE_TRAVEL_ANNUAL_IN
                .multiply(ACTIVE_TRAVEL_UPTAKE)
                .multiply(staffRatio).doubleValue(),
                SignificantFiguresFormat.asDouble(
                        plan.getAnnualCashInflowsTargetYear()).doubleValue(),
                10);
        assertEquals(ACTIVE_TRAVEL_ANNUAL_OUT
                .multiply(ACTIVE_TRAVEL_UPTAKE)
                        .multiply(staffRatio).doubleValue(),
                SignificantFiguresFormat.asDouble(
                        plan.getAnnualCashOutflowsTargetYear()).doubleValue(),
                10);
    }

    @Test
    public void testGPAsthmaInhalers() {
        InterventionType interventionType = getAsthmaInhalerInterventionType();
        OrganisationType orgType = getGPOrgType();

        Intervention intervention = new Intervention();
        intervention.setInterventionType(interventionType);
        intervention.setOrganisationType(orgType);
        intervention.setShareOfTotal(100f);

        // Organisation should be initialised with defaults
        OrganisationIntervention plan = new OrganisationIntervention();
        plan.setOrganisationType(orgType);
        plan.setIntervention(intervention);

        assertEquals(
                intervention
                        .getUnitCount()
                        .divide(new BigDecimal(orgType.getCount()),
                                BigDecimal.ROUND_HALF_DOWN).longValue(), plan
                        .getUnitCount().longValue());
        log(interventionType, orgType, plan);

        assertEquals(42.8, SignificantFiguresFormat.asDouble(plan
                .getTonnesCo2eSavedTargetYear()), 0.1);

        assertEquals(0, plan.getTargetYearSavings().floatValue(), 0.1);
    }

    private void log(InterventionType interventionType,
            OrganisationType orgType, OrganisationIntervention plan) {
        System.out.println(String.format("  %1$s (NATIONAL)", interventionType
                .getName().toUpperCase()));
        System.out.println(String.format("    capital costs: %1$f",
                interventionType.getCashOutflowsUpFront()));
        System.out.println(String.format("    annual costs: %1$f",
                interventionType.getAnnualCashOutflows()));
        System.out.println(String.format("    annual cost savings: %1$f",
                interventionType.getAnnualCashInflows()));
        System.out.println(String.format("    %1$s:%2$d",
                interventionType.getUnit(), interventionType.getUnitCount()));
        System.out.println("    org count:" + orgType.getCount());
        System.out.println("    Tonnes CO2e saved in target year:"
                + interventionType.getTonnesCo2eSavedTargetYear());
        System.out.println("    Pounds saved in target year: "
                + interventionType.getTargetYearSavings());
        System.out.println("    Pounds saved in target year NPV: "
                + Finance.presentValue(interventionType.getTargetYearSavings(),
                        5, 0.035));

        System.out.println(String.format("  %1$s (PER ORG)", interventionType
                .getName().toUpperCase()));
        System.out.println(String.format("    capital costs: %1$f",
                plan.getCashOutflowUpFront()));
        System.out.println(String.format("    annual costs: %1$f",
                plan.getAnnualCashOutflowsTargetYear()));
        System.out.println(String.format("    annual costs savings: %1$f",
                plan.getAnnualCashInflowsTargetYear()));
        System.out.println("    annual costs savings NPV: "
                + Finance.presentValue(plan.getAnnualCashInflowsTargetYear(),
                        5, 0.035));
        System.out.println(String.format("    %1$s:%2$d",
                interventionType.getUnit(), plan.getUnitCount().longValue()));
        System.out.println("    proportion of total:"
                + plan.getProportionOfUnits());
        System.out.println("    Tonnes CO2e saved in target year:"
                + plan.getTonnesCo2eSavedTargetYear());
    }

    @Test
    public void testGPPharmaWaste() {
        InterventionType interventionType = getPharmaWasteInterventionType();
        OrganisationType orgType = getGPOrgType();

        Intervention intervention = new Intervention();
        intervention.setInterventionType(interventionType);
        intervention.setOrganisationType(orgType);
        intervention.setShareOfTotal(69.86f);

        // Organisation should be initialised with defaults
        OrganisationIntervention plan = new OrganisationIntervention();
        plan.setOrganisationType(orgType);
        plan.setIntervention(intervention);

        log(interventionType, orgType, plan);

        assertEquals(0.70, SignificantFiguresFormat.asDouble(plan
                .getTonnesCo2eSavedTargetYear()), 0.1);
        assertEquals(-401,
                SignificantFiguresFormat.asDouble(plan.getTargetYearSavings()),
                0.1);
    }

    private OrganisationType getGPOrgType() {
        OrganisationType orgType = new OrganisationType();
        orgType.setName("GP Surgery");
        orgType.setTenantId(TENANT_ID);
        // orgType.setNoOfStaff(16.1);
        // http://www.bma.org.uk/-/media/files/pdfs/news%2520views%2520analysis/press%2520briefings/pressbriefinggeneralpracticeintheuk_july2014_v2.pdf
        orgType.setCount(7962);
        return orgType;
    }

    private InterventionType getBuildingFabricInterventionType() {
        InterventionType interventionType = new InterventionType();
        interventionType
                .setName("Building fabric - glazing, insulation & draft proofing");
        interventionType.setLifetime((short) 20);
        interventionType.setUptake((short) 20);
        interventionType.setScaling(0.36f);
        interventionType.setCashOutflowsUpFront(new BigDecimal(340000));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(205f));
        interventionType.setUnitCount(26361575);
        interventionType.setUnit("Floor area");
        System.out.println(" tCO2e saved in target year: "
                + interventionType.getTonnesCo2eSavedTargetYear());
        assertEquals(11401, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 25);
        return interventionType;
    }

    private InterventionType getHeatingUpgradeInterventionType() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Heating Upgrade");
        interventionType.setLifetime((short) 20);
        interventionType.setUptake((short) 20);
        interventionType.setScaling(0.36f);
        interventionType.setCashOutflowsUpFront(new BigDecimal(100000));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(328f));
        interventionType.setUnitCount(26361575);
        interventionType.setUnit("Floor area");
        assertEquals(18248, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 30);
        return interventionType;
    }

    private InterventionType getActiveTravelInterventionType() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Active staff travel");
        interventionType.setLifetime((short) 5);
        // interventionType.setLeadTime((short) 10);
        interventionType.setUptake((short) 25);
        interventionType.setScaling(100f);
        interventionType.setCashOutflowsUpFront(ACTIVE_TRAVEL_UP_FRONT);
        interventionType.setAnnualCashInflows(ACTIVE_TRAVEL_ANNUAL_IN);
        interventionType.setAnnualCashOutflows(ACTIVE_TRAVEL_ANNUAL_OUT);
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(16736.0f));
        interventionType.setUnitCount(NO_STAFF_NATIONAL.intValue());
        interventionType.setUnit("No. of staff");
        System.out.println(" tCO2e saved in target year: "
                + interventionType.getTonnesCo2eSavedTargetYear());
        return interventionType;
    }

    private InterventionType getAsthmaInhalerInterventionType() {
        InterventionType interventionType = new InterventionType();
        interventionType
                .setName("Prescribing non-propellant inhalers for asthma");
        interventionType.setLifetime((short) 5);
        interventionType.setUptake((short) 25);
        interventionType.setScaling(100f);
        interventionType.setCashOutflowsUpFront(new BigDecimal(1000000));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1364370.0f));
        interventionType.setUnitCount(7300000);
        interventionType.setUnit("Inhalers prescribed");
        System.out.println(" tCO2e saved in target year: "
                + interventionType.getTonnesCo2eSavedTargetYear());
        return interventionType;
    }

    private InterventionType getPharmaWasteInterventionType() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Reducing medicine waste");
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 75);
        interventionType.setScaling(100f);
        interventionType.setAnnualCashInflows(new BigDecimal(50000000));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(9377.0f));
        interventionType.setUnitCount(1064600000);
        interventionType.setUnit("Prescriptions issued");
        System.out.println(" tCO2e saved in target year: "
                + interventionType.getTonnesCo2eSavedTargetYear());
        assertEquals(7032l, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue());
        return interventionType;
    }

}
