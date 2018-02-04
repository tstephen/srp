package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class PaperTest {

    private static final String PAPER_CO2E = "98.4";
    private static final String PAPER_SPEND = "250000";
    private static final String PAPER_USED = "106";
    private static final String PERIOD = "2017-18";
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeClass
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testCalcPaperCo2e() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_SPEND).response(PAPER_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_USED).response(PAPER_USED));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_CO2E));

        svc.crunchPaperCo2e(PERIOD, rtn);
        assertEquals(PAPER_SPEND, rtn.answer(PERIOD, Q.PAPER_SPEND).get().response());
        assertEquals(PAPER_USED, rtn.answer(PERIOD, Q.PAPER_USED).get().response());
        assertEquals(new BigDecimal(PAPER_CO2E), rtn.answer(PERIOD, Q.PAPER_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
    }
}
