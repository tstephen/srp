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

public class SduProfileTest {

    private static final String NON_PAY_SPEND = "107740";

    private static final String CAPITAL_SPEND = "35350";
    private static final String CAPITAL_CO2E = "10499.0";
    private static final String WATER_CO2E = "191.0";
    private static final String WASTE_CO2E = "314.0";

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
    public void testCalcCo2eProfile() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND).response(NON_PAY_SPEND));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_SPEND).response(CAPITAL_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER).response(WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WASTE).response(WASTE_CO2E));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_SVCS_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CONSTRUCTION_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CATERING_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FREIGHT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ICT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CHEM_AND_GAS_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.MED_INSTR_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_MANUFACTURED_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_SPEND));
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
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PHARMA_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COMMISSIONING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_AND_WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PROCUREMENT_CO2E));

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);
        assertEquals(CAPITAL_SPEND, rtn.answer(PERIOD, Q.CAPITAL_SPEND).get().response());
        assertEquals(new BigDecimal(CAPITAL_CO2E), rtn.answer(PERIOD, Q.CAPITAL_CO2E).get().responseAsBigDecimal().setScale(1, RoundingMode.HALF_UP));
    }
}
