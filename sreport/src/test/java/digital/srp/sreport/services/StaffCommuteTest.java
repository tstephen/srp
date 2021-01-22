package digital.srp.sreport.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
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

public class StaffCommuteTest {

    private static final String STAFF_COMMUTE_PP = "961";
    private static final String STAFF_COMMUTE_TOTAL = "4790426";
    private static final String NO_STAFF = "4987";
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
    public void testEstimateStaffCommute() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_STAFF).response(NO_STAFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STAFF_COMMUTE_MILES_PP).response(STAFF_COMMUTE_PP));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STAFF_COMMUTE_MILES_TOTAL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STAFF_COMMUTE_MILES_CO2E));

        assertEquals(NO_STAFF, rtn.answer(PERIOD, Q.NO_STAFF).get().response());
        assertEquals(STAFF_COMMUTE_PP, rtn.answer(PERIOD, Q.STAFF_COMMUTE_MILES_PP).get().response());
        svc.crunchStaffCommuteCo2e(PERIOD, rtn);
        assertEquals("4792507", rtn.answer(PERIOD, Q.STAFF_COMMUTE_MILES_TOTAL).get().response());
        assertEquals(new BigDecimal("1708"), rtn.answer(PERIOD, Q.STAFF_COMMUTE_MILES_CO2E).get().responseAsBigDecimal());
    }

    @Test
    public void testStaffCommuteProvidedByHott() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STAFF_COMMUTE_MILES_TOTAL).response(STAFF_COMMUTE_TOTAL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STAFF_COMMUTE_MILES_CO2E));

        assertEquals(STAFF_COMMUTE_TOTAL, rtn.answer(PERIOD, Q.STAFF_COMMUTE_MILES_TOTAL).get().response());
        svc.crunchStaffCommuteCo2e(PERIOD, rtn);
        assertEquals(new BigDecimal("1707"), rtn.answer(PERIOD, Q.STAFF_COMMUTE_MILES_CO2E).get().responseAsBigDecimal());
    }
}
