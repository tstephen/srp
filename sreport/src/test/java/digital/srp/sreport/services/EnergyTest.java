package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.Format;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class EnergyTest {

    private static final String PERIOD = "2017-18";
    private static Cruncher svc;
    private static Format _3sfFormat;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeClass
    public static void setUpClass() throws IOException {
        _3sfFormat = new SignificantFiguresFormat();
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testEnergyCalcs() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("R0A");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_BY_WTE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS_COAL));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_STAFF).response("2000"));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED).response("200000"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GAS_USED).response("300000"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OIL_USED).response("400"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COAL_USED).response("100"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STEAM_USED).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.HOT_WATER_USED).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_RENEWABLE).response("0"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.EXPORTED_THERMAL_ENERGY).response("100"));

        svc.crunchOwnedBuildings(PERIOD, rtn);
        assertEquals("0.032", _3sfFormat.format(rtn.answer(PERIOD, Q.OWNED_BUILDINGS_COAL).get().responseAsBigDecimal()));

        svc.calcEnergyConsumption(PERIOD, rtn);
        assertEquals("100", _3sfFormat.format(rtn.answer(PERIOD, Q.COAL_USED).get().responseAsBigDecimal()));
    }

    @Test
    public void testEnergyCostChange() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("R0A");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ENERGY_COST_CHANGE_PCT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_COST).response("12345678"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PeriodUtil.previous(PERIOD)).question(Q.TOTAL_ENERGY_COST).response("12342270"));

        svc.crunchEnergyCostChange(PERIOD, rtn);

        assertEquals("0.028", _3sfFormat.format(rtn.answer(PERIOD, Q.ENERGY_COST_CHANGE_PCT).get().responseAsBigDecimal()));
    }
}
