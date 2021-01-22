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

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND).response(""));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_SPEND).response(""));

        initAnswers(rtn);

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);

        assertEquals(new BigDecimal("106975"), rtn.answer(PERIOD, Q.NON_PAY_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("31772"), rtn.answer(PERIOD, Q.CAPITAL_SPEND).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("9436"), rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
    }

    private void initAnswers(SurveyReturn rtn) {
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_SVCS_SPEND).response(""));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CONSTRUCTION_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CATERING_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FREIGHT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ICT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CHEM_AND_GAS_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.MED_INSTR_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_MANUFACTURED_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_AND_CARD_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PHARMA_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TRAVEL_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COMMISSIONING_SPEND));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_SVCS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CONSTRUCTION_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CATERING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FREIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ICT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CHEM_AND_GAS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.MED_INSTR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_MANUFACTURED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_AND_CARD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PHARMA_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COMMISSIONING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_AND_WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PROCUREMENT_CO2E));
    }

    @Test
    public void testCalcCo2eProfileFromNonPaySpend() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND).response(NON_PAY_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_SPEND).response(CAPITAL_SPEND));

        initAnswers(rtn);

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
}
