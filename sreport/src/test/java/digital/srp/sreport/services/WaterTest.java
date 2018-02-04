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

public class WaterTest {

    private static final String WATER_CO2E = "191.0";
    private static final String WATER_TREATMENT_CO2E = "314.0";
    private static final String TOTAL_WATER_CO2E = "505.0";
    private static final String WATER_TREATMENT_CO2E2 = "283.0";
    private static final String TOTAL_WATER_CO2E2 = "474.0";
    private static final String WATER_VOL = "554091";
    private static final String WASTE_WATER_INPUT = "400000";

    private static final String WASTE_WATER_CALC = "443272.80";
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
        assertEquals(new BigDecimal(WATER_CO2E), rtn.answer(PERIOD, Q.WATER_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WATER_TREATMENT_CO2E), rtn.answer(PERIOD, Q.WATER_TREATMENT_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(TOTAL_WATER_CO2E), rtn.answer(PERIOD, Q.SCOPE_3_WATER).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalcWaterCo2eFromSupplyAndWaste() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_VOL).response(WATER_VOL));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_WATER).response(WASTE_WATER_INPUT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WATER_TREATMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER));

        svc.crunchScope3Water(PERIOD, rtn);
        assertEquals(WATER_VOL, rtn.answer(PERIOD, Q.WATER_VOL).get().response());
        assertEquals(WASTE_WATER_INPUT, rtn.answer(PERIOD, Q.WASTE_WATER).get().response());
        assertEquals(new BigDecimal(WATER_CO2E), rtn.answer(PERIOD, Q.WATER_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WATER_TREATMENT_CO2E2), rtn.answer(PERIOD, Q.WATER_TREATMENT_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(TOTAL_WATER_CO2E2), rtn.answer(PERIOD, Q.SCOPE_3_WATER).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
    }
}
