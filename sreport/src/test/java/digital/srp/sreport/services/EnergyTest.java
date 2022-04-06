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
package digital.srp.sreport.services;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class EnergyTest {
    // ELEC
    private static final String ELEC_USED = "8865457";
    private static final String ELEC_EXPORTED = "554091";
    private static final String ELEC_3RD_PTY_RENEWABLE_USED = "1100182";
    private static final String ELEC_USED_GREEN_TARIFF = "1108182";

    // THERMAL
    private static final String GAS_USED = "17555360";
    private static final String OIL_USED = "3511072";
    private static final String COAL_USED = "1755536";
    private static final String STEAM_USED = "1773091";
    private static final String HOT_WATER_USED = "1773091";
    private static final String ORG_CODE = "ZZ1";
    private static final String PERIOD = "2017-18";
    private static final String PERIOD_2019_20 = "2019-20";
    private static final String PERIOD_2020_21 = "2020-21";
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeAll
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
        svc.energyEmissionsService = new EnergyEmissionsService();
        svc.healthChecker = new HealthChecker(new MemoryAnswerFactory());
    }

    @Test
    public void testElec0PctGreenEmissions2017() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED).response(ELEC_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_EXPORTED).response(ELEC_EXPORTED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED_GREEN_TARIFF).response(ELEC_USED_GREEN_TARIFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GREEN_TARIFF_ADDITIONAL_PCT).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_3RD_PTY_RENEWABLE_USED).response(ELEC_3RD_PTY_RENEWABLE_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.THIRD_PARTY_ADDITIONAL_PCT).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_OWNED_RENEWABLE_USED_SDU).response("8000"));

        initCalculatedAnswers(rtn, PERIOD);

        svc.crunchElectricityUsed(PERIOD, rtn);
        assertEquals(new BigDecimal("3952"), rtn.answer(PERIOD, Q.ELEC_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("497"), rtn.answer(PERIOD, Q.ELEC_WTT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("494"), rtn.answer(PERIOD, Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("490"), rtn.answer(PERIOD, Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("142"), rtn.answer(PERIOD, Q.ELEC_EXPORTED_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("3810"), rtn.answer(PERIOD, Q.NET_ELEC_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2216364"), rtn.answer(PERIOD, Q.ELEC_TOTAL_RENEWABLE_USED).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("984"), rtn.answer(PERIOD, Q.ELEC_RENEWABLE_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(new BigDecimal(ELEC_USED), rtn.answer(PERIOD, Q.ELEC_USED).get().responseAsBigDecimal());
    }

    @Test
    public void testElec0PctGreenEmissions2016() {
        String period = "2016-17";
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(period).org(ORG_CODE);
        String elecUsed = "8777680";
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_USED).response(elecUsed));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_EXPORTED).response(ELEC_EXPORTED));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_USED_GREEN_TARIFF).response("1097210"));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.GREEN_TARIFF_ADDITIONAL_PCT).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_3RD_PTY_RENEWABLE_USED).response("1097210"));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.THIRD_PARTY_ADDITIONAL_PCT).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_OWNED_RENEWABLE_USED_SDU).response("0"));

        initCalculatedAnswers(rtn, period);

        svc.crunchElectricityUsed(period, rtn);
        assertEquals(new BigDecimal("567"), rtn.answer(period, Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("567"), rtn.answer(period, Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("4397"), rtn.answer(period, Q.NET_ELEC_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2194420"), rtn.answer(period, Q.ELEC_TOTAL_RENEWABLE_USED).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1134"), rtn.answer(period, Q.ELEC_RENEWABLE_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(new BigDecimal(elecUsed), rtn.answer(period, Q.ELEC_USED).get().responseAsBigDecimal());
    }

    protected void initCalculatedAnswers(SurveyReturn rtn, String period) {
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_TOTAL_RENEWABLE_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_NON_RENEWABLE_GREEN_TARIFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_NON_RENEWABLE_3RD_PARTY));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_RENEWABLE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.ELEC_EXPORTED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(period).question(Q.NET_ELEC_CO2E));
    }

    @Test
    public void testElec50PctGreenEmissions() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED).response(ELEC_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_EXPORTED).response(ELEC_EXPORTED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED_GREEN_TARIFF).response(ELEC_USED_GREEN_TARIFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GREEN_TARIFF_ADDITIONAL_PCT).response("50"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_3RD_PTY_RENEWABLE_USED).response(ELEC_3RD_PTY_RENEWABLE_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.THIRD_PARTY_ADDITIONAL_PCT).response("50"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_OWNED_RENEWABLE_USED_SDU).response("8000"));

        initCalculatedAnswers(rtn, PERIOD);

        svc.crunchElectricityUsed(PERIOD, rtn);
        assertEquals(new BigDecimal("3952"), rtn.answer(PERIOD, Q.ELEC_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("247"), rtn.answer(PERIOD, Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("245"), rtn.answer(PERIOD, Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("142"), rtn.answer(PERIOD, Q.ELEC_EXPORTED_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("3810"), rtn.answer(PERIOD, Q.NET_ELEC_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2216364"), rtn.answer(PERIOD, Q.ELEC_TOTAL_RENEWABLE_USED).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("492"), rtn.answer(PERIOD, Q.ELEC_RENEWABLE_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(new BigDecimal(ELEC_USED), rtn.answer(PERIOD, Q.ELEC_USED).get().responseAsBigDecimal());
    }

    @Test
    public void testThermalEnergyCalcs() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GAS_USED).response(GAS_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OIL_USED).response(OIL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COAL_USED).response(COAL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STEAM_USED).response(STEAM_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.HOT_WATER_USED).response(HOT_WATER_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.EXPORTED_THERMAL_ENERGY).response("100"));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_BY_WTE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS_GAS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS_OIL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS_COAL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STEAM_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.HOT_WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_TOTAL_RENEWABLE_USED));

        svc.crunchOwnedBuildings(PERIOD, rtn);
        assertEquals(new BigDecimal("3722"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_GAS).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1119"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_OIL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("658"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_COAL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(new BigDecimal(COAL_USED), rtn.answer(PERIOD, Q.COAL_USED).get().responseAsBigDecimal());
    }

    @Test
    public void testThermalEnergyRVJCalcs() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD_2020_21).org("RVJ");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.GAS_USED).response(GAS_USED));
        String oilUsed1920 = "583708";
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2019_20).question(Q.OIL_USED).response(oilUsed1920));
        String oilUsed2021 = "1048078";
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OIL_USED).response(oilUsed2021));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.TOTAL_ENERGY));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.TOTAL_ENERGY_BY_WTE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.TOTAL_ENERGY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2019_20).question(Q.OWNED_BUILDINGS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_BUILDINGS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_BUILDINGS_GAS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2019_20).question(Q.OWNED_BUILDINGS_OIL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_BUILDINGS_OIL));

        svc.crunchOwnedBuildings(PERIOD_2019_20, rtn);
        svc.crunchOwnedBuildings(PERIOD_2020_21, rtn);
        assertEquals(new BigDecimal("186"), rtn.answer(PERIOD_2019_20, Q.OWNED_BUILDINGS_OIL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("334"), rtn.answer(PERIOD_2020_21, Q.OWNED_BUILDINGS_OIL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(new BigDecimal(oilUsed1920), rtn.answer(PERIOD_2019_20, Q.OIL_USED).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(oilUsed2021), rtn.answer(PERIOD_2020_21, Q.OIL_USED).get().responseAsBigDecimal());
    }

    @Test
    public void testEnergyCostChange() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ENERGY_COST_CHANGE_PCT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_COST).response("12345678"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PeriodUtil.previous(PERIOD)).question(Q.TOTAL_ENERGY_COST).response("12342270"));

        svc.crunchEnergyCostChange(PERIOD, rtn);

        assertEquals(new BigDecimal("0.028"), rtn.answer(PERIOD, Q.ENERGY_COST_CHANGE_PCT).get().responseAsBigDecimal().setScale(3, RoundingMode.HALF_UP));
    }
}
