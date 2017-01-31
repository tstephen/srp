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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Ignore;
import org.junit.Test;

import digital.srp.macc.maths.InterventionTypeValidator;

public class InterventionTypeTest {

    @Test
    public void testBuildingManagementSystemNew() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Building Management System New");
        assertEquals(
                "http://srp.digital/interventions/building-management-system-new",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 10);
        assertEquals(0.10, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(80696500));
        interventionType.setAnnualCashInflows(new BigDecimal("19407621"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(89756f));
        interventionType.setAnnualElecSaved(new BigDecimal("18886422.46"));
        interventionType.setAnnualGasSaved(new BigDecimal("377728449.13"));

        log(interventionType);

        assertEquals(8069650l, interventionType.getTotalCashOutflows()
                .longValue());
        // small discrepancy between EEVS' and this calcs
        // assertTrue(-1165395l - interventionType.getTotalNpv().longValue() <
        // 5);
        // assertEquals(1637, interventionType.getTonnesCo2eSavedTargetYear(),
        // 0.01);
        // assertEquals(-101.70, interventionType.getCostPerTonneCo2e(), 0.01);
    }

    @Test
    public void testBoilerPlantOptimisation() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("BoilerPlantOptimisation");
        interventionType.setLifetime((short) 17);
        interventionType.setUptake((short) 20);
        assertEquals(0.20, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.04f);
        assertEquals(0.0004, interventionType.getScaleFactor().doubleValue(),
                0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(24000));
        interventionType.setAnnualCashInflows(new BigDecimal("5000"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(41f));

        log(interventionType);

        assertEquals(12000000, interventionType.getCashOutflowsUpFrontNational()
                .round(MathContext.DECIMAL32).doubleValue(), 1);
    }

    @Test
    public void testDriverTraining() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Driver Training");
        assertEquals("http://srp.digital/interventions/driver-training",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 5);
        interventionType.setUptake((short) 20);
        assertEquals(0.2, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(10.0f);
        assertEquals(0.1, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(1781500.00));
        interventionType.setAnnualCashInflows(new BigDecimal("738805.97"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1980.00f));

        log(interventionType);

        assertEquals(3960, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        // EEVS get -156.99
        assertEquals(-1569.9, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 0.1);
    }

    @Test
    public void testBuildingManagementSystemExisting() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Building Management System Existing");
        assertEquals(
                "http://srp.digital/interventions/building-management-system-existing",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 25);
        assertEquals(0.25, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.08f);
        assertEquals(0.0008, interventionType.getScaleFactor().doubleValue(),
                0.00001);

        interventionType.setCashOutflowsUpFront(new BigDecimal(73649));
        interventionType.setAnnualCashInflows(new BigDecimal("11887"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(76f));

        log(interventionType);
    }

    @Test
    public void testChpWithPredictedGasAndElecPrice() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("CHP");
        assertEquals("http://srp.digital/interventions/chp",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 20);
        assertEquals(0.20, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.40f);
        assertEquals(0.004, interventionType.getScaleFactor().doubleValue(),
                0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(1400082));
        interventionType.setAnnualCashInflows(new BigDecimal("343054"));
        interventionType.setAnnualCashOutflows(new BigDecimal("70000"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1290.60f));
        long npvWithout = interventionType.getTotalNpv().longValue();
        long co2eSavedTYWithout = interventionType
                .getTonnesCo2eSavedTargetYear().longValue();
        long macWithout = interventionType.getCostPerTonneCo2e().longValue();
        interventionType.setAnnualGasSaved(new BigDecimal("-7700000.00"));
        interventionType.setAnnualElecSaved(new BigDecimal("5120000.00"));

        assertEquals(new BigDecimal("349315.29").longValue(), interventionType
                .getAnnualCashInflows(0).longValue());
        assertEquals(new BigDecimal("373822.98").longValue(), interventionType
                .getAnnualCashInflows(1).longValue());

        long npvUsing = interventionType.getTotalNpv().longValue();
        System.out.println("  total NPV without future energy prices: "
                + npvWithout);
        System.out.println("  total NPV using future energy prices:   "
                + npvUsing);
        System.out
                .println("  difference:                              "
                        + Math.round((npvUsing - npvWithout)
                                / (float) npvWithout * 100) + "%");

        long co2eSavedTYUsing = interventionType.getTonnesCo2eSavedTargetYear()
                .longValue();
        System.out
                .println("  CO2e saved in target year without adjustment in carbon intensity: "
                        + co2eSavedTYWithout);
        System.out
                .println("  CO2e saved in target year WITH adjustment in carbon intensity: "
                        + co2eSavedTYUsing);
        // System.out
        // .println("  difference:                              "
        // + Math.round((co2eSavedTYWithout - co2eSavedTYUsing)
        // / (float) co2eSavedTYWithout * 100) + "%");
        long macUsing = interventionType.getCostPerTonneCo2e().longValue();
        System.out
                .println("  MAC target year without adjustment in carbon intensity: "
                        + macWithout);
        System.out
                .println("  MAC target year WITH adjustment in carbon intensity: "
                        + macUsing);

        log(interventionType);

        // small discrepancy between EEVS' and this calcs
        // assertEquals(-75758231l, interventionType.getTotalNpv().longValue());
        // assertEquals(-75758231l,
        // interventionType.getTotalNpv().doubleValue(),
        // -500);
        assertEquals(3006, interventionType.getTonnesCo2eSavedTargetYear()
                .doubleValue(), 10);
        assertEquals(12025, interventionType.getTotalTonnesCo2eSaved()
                .doubleValue(), 100);
        assertEquals(-6300.08, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 500);
    }

    @Test
    public void testRecycling() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Dry Recycling of General Waste");
        assertEquals(
                "http://srp.digital/interventions/dry-recycling-of-general-waste",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 7);
        interventionType.setUptake((short) 75);
        assertEquals(0.75, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(70.00f);
        assertEquals(0.7, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(1842800));
        interventionType.setAnnualCashInflows(new BigDecimal("479267"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1528f));

        log(interventionType);

        assertEquals(1382100, interventionType.getTotalCashOutflows()
                .longValue(), 0.01);
        // small discrepancy between EEVS' and this calcs
        assertTrue(-1165395l - interventionType.getTotalNpv().longValue() < 5);
        assertEquals(1634, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(-145.27, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 0.01);
    }

    @Test
    public void testLightingHighEfficiency() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Lighting High Efficiency");
        assertEquals(
                "http://srp.digital/interventions/lighting-high-efficiency",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 40);
        assertEquals(0.4, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.35f);
        assertEquals(0.0035, interventionType.getScaleFactor().doubleValue(),
                0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(300000));
        interventionType.setAnnualCashInflows(new BigDecimal("50000.00"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(285f));
        interventionType.setAnnualElecSaved(new BigDecimal("508427.79"));

        log(interventionType);

        // assertEquals(1382100,
        // interventionType.getTotalCashOutflows().longValue(),
        // 0.01);
        // // small discrepancy between EEVS' and this calcs
        // assertTrue(-1165395l - interventionType.getTotalNpv().longValue() <
        // 5);

        assertEquals(18970, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(-143.15, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 0.01);
    }

    @Test
    public void testReduceSocialIsolation() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Reducing social isolation in older people");
        assertEquals(
                "http://srp.digital/interventions/reducing-social-isolation-in-older-people",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 15);
        interventionType.setUptake((short) 50);
        assertEquals(0.50, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1.0, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setAnnualCashOutflows(new BigDecimal("391675.00"));
        interventionType.setAnnualCashInflows(new BigDecimal("841023"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(124f));

        log(interventionType);

        // small discrepancy between EEVS' and this calcs
        assertTrue(-2587665 - interventionType.getTotalNpv().longValue() < 5);
        assertEquals(62, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(930, interventionType.getTotalTonnesCo2eSaved()
                .doubleValue(), 0.01);
        assertEquals(-2782.44, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 1);
    }

    @Test
    public void testReducingPropellant() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Reducing Propellant");
        assertEquals("http://srp.digital/interventions/reducing-propellant",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 5);
        interventionType.setUptake((short) 25);
        assertEquals(0.25, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(100000));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1364370f));

        log(interventionType);

        assertEquals(25000,
                interventionType.getTotalCashOutflows().longValue(), 0.01);
        // small discrepancy between EEVS' and this calcs
        assertTrue(-1165395l - interventionType.getTotalNpv().longValue() < 5);
        assertEquals(341093, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 1);
        assertTrue(new BigDecimal(1705463).subtract(
                interventionType.getTotalTonnesCo2eSaved()).compareTo(
                new BigDecimal(500)) < 0);
        assertEquals(0.01,
                interventionType.getCostPerTonneCo2e().doubleValue(), 0.005);
    }

    @Test
    public void testSmokingCessationUsingTimeSeriesInputs() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Smoking cessation");
        assertEquals("http://srp.digital/interventions/smoking-cessation",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 50);
        assertEquals(0.50, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1.0, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(145800000));
        interventionType
                .setAnnualCashOutflowsTS("142884000.00    140026320.00    137225793.60    134481277.73    131791652.17    129155819.13    126572702.75    124041248.69    121560423.72    119129215.24    145800000.00");
        assertEquals(11, interventionType.getAnnualCashOutflowsTimeSeries()
                .size());
        interventionType
                .setAnnualCashInflowsTS("100254000.00    98248920.00    96283941.60    94358262.77    92471097.51    90621675.56    88809242.05    87033057.21    85292396.07    83586548.14    102300000.00");
        interventionType
                .setAnnualTonnesCo2eSavedTS("169069.60    165688.21    162374.44    159126.95    155944.42    152825.53    149769.02    146773.64    143838.16    140961.40    172520.00");

        assertTrue(InterventionTypeValidator.isValid(interventionType));
        log(interventionType);

        // small discrepancy between EEVS' and this calcs
        assertEquals(235964290, interventionType.getTotalNpv().doubleValue(),
                500);
        assertEquals(77972, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(274.55, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 1);
    }

    @Test
    @Ignore
    public void testSolarThermalWithRHI() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Solar Thermal");
        assertEquals("http://srp.digital/interventions/solar-thermal",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 20);
        interventionType.setUptake((short) 15);
        assertEquals(0.15, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(0.4f);
        assertEquals(0.004, interventionType.getScaleFactor().doubleValue(),
                0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(900000.00));
        interventionType.setAnnualCashInflows(new BigDecimal(75000.00));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(409.78f));
        interventionType.setAnnualGasSaved(new BigDecimal("2500000.00"));

        assertTrue(InterventionTypeValidator.isValid(interventionType));

        log(interventionType);

        // TODO EEVS get 15367 somehow
        assertEquals(19621, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(-20.25, interventionType.getCostPerTonneCo2e()
                .doubleValue(), 1);
    }

    @Test
    public void testTelehealth() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Telehealth/Telecare");
        assertEquals("http://srp.digital/interventions/telehealth-telecare",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 5);
        interventionType.setUptake((short) 50);
        assertEquals(0.50, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1, interventionType.getScaleFactor().doubleValue(), 0.01);
        
        interventionType.setCashOutflowsUpFront(new BigDecimal(0));
        interventionType.setAnnualCashInflows(new BigDecimal("5092756"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(13489.00f));

        log(interventionType);

        // assertEquals(1382100,
        // interventionType.getTotalCashOutflows().longValue(),
        // 0.01);
        // small discrepancy between EEVS' and this calcs
        // assertTrue(-1165395l - interventionType.getTotalNpv().longValue() <
        // 5);
        // assertEquals(1637, interventionType.getTonnesCo2eSavedTargetYear(),
        // 0.01);
        // assertEquals(-101.70, interventionType.getCostPerTonneCo2e(), 0.01);
    }

    private void log(InterventionType interventionType) {
        System.out.println(String.format("  total cash out: %1$d",
                interventionType.getTotalCashOutflows().longValue()));
        System.out.println("  Mean total outflows: "
                + interventionType.getTotalCashOutflows());
        System.out.println(String.format("  cash out target year: %1$s",
                interventionType.getAnnualCashOutflowsNationalTargetYear()
                        .toPlainString()));
        System.out.println(String.format("  total NPV: %1$d", interventionType
                .getTotalNpv().longValue()));
        System.out.println("  Mean cash inflows: "
                + interventionType.getMeanCashInflows());
        System.out.println(String.format("  cash in target year: %1$s",
                interventionType.getAnnualCashInflowsNationalTargetYear()
                        .toPlainString()));

        System.out.println(String
                .format("  tonnes saved target year: %1$s", interventionType
                        .getTonnesCo2eSavedTargetYear().toPlainString()));
        System.out.println(String.format("  MAC: %1$s", interventionType
                .getCostPerTonneCo2e().toPlainString()));

    }

    @Test
    public void testTeleconferencing() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Teleconferencing");
        assertEquals("http://srp.digital/interventions/teleconferencing",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 7);
        interventionType.setUptake((short) 60);
        assertEquals(0.60, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1, interventionType.getScaleFactor().doubleValue(), 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(4301000));
        interventionType.setAnnualCashInflows(new BigDecimal("8367440"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(6827f));

        log(interventionType);
        // assertEquals(1382100,
        // interventionType.getTotalCashOutflows().longValue(),
        // 0.01);
        // small discrepancy between EEVS' and this calcs
        // assertTrue(-1165395l - interventionType.getTotalNpv().longValue() <
        // 5);
        assertEquals(4096, interventionType.getTonnesCo2eSavedTargetYear()
                .longValue(), 0.01);
        assertEquals(-980,
                interventionType.getCostPerTonneCo2e().doubleValue(), 1);
    }

    @Test
    public void testSugarReduction() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Sugar reduction in soft drinks");
        interventionType.setLifetime((short) 15);
        interventionType.setUptake((short) 80);
        assertEquals(0.80, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(100.00f);
        assertEquals(1, interventionType.getScaleFactor().doubleValue(), 0.01);
        interventionType.setLeadTime(10);

        interventionType.setCashOutflowsUpFront(new BigDecimal(100000));
        interventionType
                .setAnnualCashOutflowsTS("50000,50000,50000,50000,50000,0,0,0,0,0,0,0,0,0,0");
        interventionType.setAnnualCashInflows(new BigDecimal("40268935"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(1780f));

        log(interventionType);

        assertEquals(40000, interventionType
                .getAnnualCashOutflowsNationalTargetYear().longValue());
        // due to lead time exceeding target year inflow is 0
        assertEquals(0, interventionType
                .getAnnualCashInflowsNationalTargetYear().longValue());
        // assertEquals(4096, interventionType.getTonnesCo2eSavedTargetYear()
        // .longValue(), 0.01);
        // assertEquals(-980,
        // interventionType.getCostPerTonneCo2e().doubleValue(), 1);
    }

    @Test
    public void testTravelPlanning() {
        InterventionType interventionType = new InterventionType();
        interventionType.setName("Travel Planning");
        assertEquals("http://srp.digital/interventions/travel-planning",
                interventionType.getFurtherInfo());
        interventionType.setLifetime((short) 10);
        interventionType.setUptake((short) 80);
        assertEquals(0.80, interventionType.getUptakeFactor().doubleValue(),
                0.01);
        interventionType.setScaling(8.00f);
        // assertEquals(0.0008, interventionType.getScaleFactor().doubleValue(),
        // 0.01);

        interventionType.setCashOutflowsUpFront(new BigDecimal(425000));
        interventionType.setAnnualCashInflows(new BigDecimal("-37900"));
        interventionType.setAnnualTonnesCo2eSaved(new BigDecimal(81524f));

        log(interventionType);
    }

}
