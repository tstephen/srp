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
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class WaterTest {

    private static final String WATER_CO2E = "191";
    private static final String WATER_TREATMENT_CO2E = "314";
    private static final String TOTAL_WATER_CO2E = "504";
    private static final String WATER_TREATMENT_CO2E2 = "283";
    private static final String TOTAL_WATER_CO2E2 = "474";
    private static final String WATER_VOL = "554091";
    private static final String WASTE_WATER_INPUT = "400000";

    private static final String WASTE_WATER_CALC = "443272.80";
    private static final String PERIOD = "2017-18";
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeAll
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testCalcWaterCo2eFromSupplyAlone() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_VOL).response(WATER_VOL));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_WATER));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_TREATMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER));

        svc.crunchScope3Water(PERIOD, rtn);
        assertEquals(WATER_VOL, rtn.answer(PERIOD, Q.WATER_VOL).get().response());
        assertEquals(WASTE_WATER_CALC, rtn.answer(PERIOD, Q.WASTE_WATER).get().response());
        assertEquals(new BigDecimal(WATER_CO2E), rtn.answer(PERIOD, Q.WATER_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WATER_TREATMENT_CO2E), rtn.answer(PERIOD, Q.WATER_TREATMENT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(TOTAL_WATER_CO2E), rtn.answer(PERIOD, Q.SCOPE_3_WATER).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalcTrustWaterCo2eFromSupplyAndWaste() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_VOL).response(WATER_VOL));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_WATER).response(WASTE_WATER_INPUT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_TREATMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER));

        svc.crunchScope3Water(PERIOD, rtn);
        assertEquals(WATER_VOL, rtn.answer(PERIOD, Q.WATER_VOL).get().response());
        assertEquals(WASTE_WATER_INPUT, rtn.answer(PERIOD, Q.WASTE_WATER).get().response());
        assertEquals(new BigDecimal(WATER_CO2E), rtn.answer(PERIOD, Q.WATER_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WATER_TREATMENT_CO2E2), rtn.answer(PERIOD, Q.WATER_TREATMENT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(TOTAL_WATER_CO2E2), rtn.answer(PERIOD, Q.SCOPE_3_WATER).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalcCcgWaterCo2eFromSupplyAndWaste() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ2");
        final String WATER_VOL = "559";
        final String WASTE_WATER_INPUT = "524";

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_VOL).response(WATER_VOL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_WATER).response(WASTE_WATER_INPUT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_TREATMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER));

        svc.crunchScope3Water(PERIOD, rtn);
        assertEquals(WATER_VOL, rtn.answer(PERIOD, Q.WATER_VOL).get().response());
        assertEquals(WASTE_WATER_INPUT, rtn.answer(PERIOD, Q.WASTE_WATER).get().response());

        assertEquals(new BigDecimal("0.19"), rtn.answer(PERIOD, Q.WATER_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("0.37"), rtn.answer(PERIOD, Q.WATER_TREATMENT_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("0.56"), rtn.answer(PERIOD, Q.SCOPE_3_WATER).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
    }
}
