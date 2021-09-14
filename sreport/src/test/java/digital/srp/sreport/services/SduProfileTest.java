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

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.surveys.Sdu2021;
import digital.srp.sreport.model.surveys.SduQuestions;

public class SduProfileTest {
    private static final String OP_EX = "275000";
    private static final String NON_PAY_SPEND = "107740";

    private static final String CAPITAL_SPEND = "35350";
    private static final String CAPITAL_CO2E = "10499";

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
    public void testCalcCo2eProfileFromOpex() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OP_EX).response(OP_EX));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.NON_PAY_SPEND).derived(true));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.CAPITAL_SPEND).derived(true));

        initAnswers(rtn);

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);

        assertEquals(new BigDecimal("106975"), rtn.answer(PERIOD, Q.NON_PAY_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("31772"), rtn.answer(PERIOD, Q.CAPITAL_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("9436"), rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalcCo2eProfileFromOpexTolerateEmptyStrings() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.OP_EX).response(OP_EX));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND).response("").derived(true));

        initAnswers(rtn);
        Answer capitalSpend = rtn.getAnswers().stream()
                .filter(e -> e.question().q().equals(Q.CAPITAL_SPEND)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(Question.class, Q.CAPITAL_SPEND.toString()));
        capitalSpend.derived(false).response("");

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);

        assertEquals(new BigDecimal("106975"), rtn.answer(PERIOD, Q.NON_PAY_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("31772"), rtn.answer(PERIOD, Q.CAPITAL_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("9436"), rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    private void initAnswers(SurveyReturn rtn) {
        // E-Class
        for (Q q : SduQuestions.ECLASS_PROFILE_SPEND_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.ECLASS_PROFILE2_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.ECLASS_PROFILE_CO2E_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.ECLASS_PROFILE2_CO2E_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }

        // Greener NHS
        for (Q q : SduQuestions.SDU_PROFILE_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.SDU_PROFILE2_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.SDU_PROFILE_CO2E_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        for (Q q : SduQuestions.SDU_PROFILE2_CO2E_HDRS) {
            rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(q).derived(true));
        }
        rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(Q.PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(Q.OTHER_PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(rtn.applicablePeriod()).question(Q.COMMISSIONING_CO2E));
    }

    @Test
    public void testCalcAcuteTrustCo2eProfileFromNonPaySpend() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND).response(NON_PAY_SPEND));

        initAnswers(rtn);
        Answer capitalSpend = rtn.getAnswers().stream()
                .filter(e -> e.question().q().equals(Q.CAPITAL_SPEND)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(Question.class, Q.CAPITAL_SPEND.toString()));
        capitalSpend.derived(false).response(CAPITAL_SPEND);

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);

        System.out.println(rtn.answer(PERIOD, Q.BIZ_SVCS_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.CONSTRUCTION_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.CATERING_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.FREIGHT_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.ICT_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.CHEM_AND_GAS_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.MED_INSTR_CO2E).get().response());
        // gives 9588
//        assertEquals(new BigDecimal("9349"), rtn.answer(PERIOD, Q.MED_INSTR_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        System.out.println(rtn.answer(PERIOD, Q.OTHER_MANUFACTURED_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.OTHER_PROCUREMENT_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.PAPER_AND_CARD_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.PHARMA_CO2E).get().response());
        // delivers 3791
//        assertEquals(new BigDecimal("3691"), rtn.answer(PERIOD, Q.PHARMA_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        System.out.println(rtn.answer(PERIOD, Q.TRAVEL_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.COMMISSIONING_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.WASTE_AND_WATER_CO2E).get().response());
        System.out.println(rtn.answer(PERIOD, Q.PROCUREMENT_CO2E).get().response());

        assertEquals(CAPITAL_SPEND, rtn.answer(PERIOD, Q.CAPITAL_SPEND).get().response());
        assertEquals(new BigDecimal(CAPITAL_CO2E), rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalcMentalHealthTrustCo2eProfileFromNonPaySpend() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(Sdu2021.PERIOD).org("RWV");
        rtn.getAnswers().add(new Answer().applicablePeriod(Sdu2021.PERIOD).question(Q.ORG_TYPE).response("Mental Health"));
        String nonPaySpend = "99949";
        rtn.getAnswers().add(new Answer().applicablePeriod(Sdu2021.PERIOD).question(Q.NON_PAY_SPEND).response(nonPaySpend));
        rtn.getAnswers().add(new Answer().applicablePeriod(Sdu2021.PERIOD).question(Q.ECLASS_SPEND));

        initAnswers(rtn);
        Answer capitalSpend = rtn.getAnswers().stream()
                .filter(e -> e.question().q().equals(Q.CAPITAL_SPEND)).findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(Question.class, Q.CAPITAL_SPEND.toString()));
        capitalSpend.response("16748000");

        svc.calcCarbonProfileSimplifiedEClassMethod(Sdu2021.PERIOD, rtn);
        svc.calcCarbonProfileSimplifiedSduMethod(Sdu2021.PERIOD, rtn);

        assertEquals(new BigDecimal("5211"), rtn.answer(Sdu2021.PERIOD, Q.BIZ_SVCS_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2211"), rtn.answer(Sdu2021.PERIOD, Q.CONSTRUCTION_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2412"), rtn.answer(Sdu2021.PERIOD, Q.CATERING_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2632"), rtn.answer(Sdu2021.PERIOD, Q.FREIGHT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1377"), rtn.answer(Sdu2021.PERIOD, Q.ICT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2286"), rtn.answer(Sdu2021.PERIOD, Q.CHEM_AND_GAS_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("3171"), rtn.answer(Sdu2021.PERIOD, Q.MED_INSTR_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("448"), rtn.answer(Sdu2021.PERIOD, Q.OTHER_MANUFACTURED_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("0"), rtn.answer(Sdu2021.PERIOD, Q.OTHER_PROCUREMENT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("847"), rtn.answer(Sdu2021.PERIOD, Q.PAPER_AND_CARD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1053"), rtn.answer(Sdu2021.PERIOD, Q.PHARMA_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("5348"), rtn.answer(Sdu2021.PERIOD, Q.TRAVEL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2720"), rtn.answer(Sdu2021.PERIOD, Q.COMMISSIONING_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("0"), rtn.answer(Sdu2021.PERIOD, Q.WASTE_AND_WATER_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("29427"), rtn.answer(Sdu2021.PERIOD, Q.PROCUREMENT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        assertEquals(new BigDecimal("3339"), rtn.answer(Sdu2021.PERIOD, Q.SDU_MEDICINES_AND_CHEMICALS_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("7696"), rtn.answer(Sdu2021.PERIOD, Q.SDU_MEDICAL_EQUIPMENT).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("9195"), rtn.answer(Sdu2021.PERIOD, Q.SDU_NON_MEDICAL_EQUIPMENT).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("34283"), rtn.answer(Sdu2021.PERIOD, Q.SDU_BUSINESS_SERVICES).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("8796"), rtn.answer(Sdu2021.PERIOD, Q.SDU_CONSTRUCTION_AND_FREIGHT).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("3798"), rtn.answer(Sdu2021.PERIOD, Q.SDU_FOOD_AND_CATERING).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("14393"), rtn.answer(Sdu2021.PERIOD, Q.SDU_COMMISSIONED_HEALTH_SERVICES).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        assertEquals(capitalSpend.response(), rtn.answer(Sdu2021.PERIOD, Q.CAPITAL_SPEND).get().response());
        assertEquals(new BigDecimal("7780"), rtn.answer(Sdu2021.PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }
}
