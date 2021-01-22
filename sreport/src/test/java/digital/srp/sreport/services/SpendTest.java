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

public class SpendTest {

    private static final String OP_EX = "571733";
    private static final String NON_PAY_SPEND = "567180";
    private static final String CAPITAL_SPEND = "0";

    private static final String ORG_CODE = "ZZ1";
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
    public void testExplicitlyZeroCapitalSpend() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD)
                .org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.OP_EX).response(OP_EX));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.NON_PAY_SPEND).response(NON_PAY_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.CAPITAL_SPEND).response(CAPITAL_SPEND));
        rtn.getAnswers().add(
                new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_CO2E));

        svc.crunchCapitalSpend(PERIOD, rtn, "Clinical Commissioning Group",
                new BigDecimal(NON_PAY_SPEND));

        // check inputs unchanged
        assertEquals(new BigDecimal(OP_EX),
                rtn.answer(PERIOD, Q.OP_EX).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(NON_PAY_SPEND), rtn
                .answer(PERIOD, Q.NON_PAY_SPEND).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(CAPITAL_SPEND), rtn
                .answer(PERIOD, Q.CAPITAL_SPEND).get().responseAsBigDecimal());

        assertEquals(new BigDecimal("0"),
                rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
    }

}
