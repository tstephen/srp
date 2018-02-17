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
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class EnergyTest {

    private static final String NO_STAFF = "4987";
    private static final String ELEC_USED = "8865457";
    private static final String ELEC_EXPORTED = "554091";
    private static final String ELEC_3RD_PTY_RENEWABLE_USED = "1100182";
    private static final String ELEC_USED_GREEN_TARIFF = "1108182";
    private static final String GREEN_TARIFF_ADDITIONAL_PCT = "50";
    private static final String THIRD_PARTY_ADDITIONAL_PCT = "50";

    private static final String GAS_USED = "17730913";
    private static final String OIL_USED = "3546183";
    private static final String COAL_USED = "1773091";
    private static final String STEAM_USED = "1773091";
    private static final String HOT_WATER_USED = "1773091";
    private static final String ORG_CODE = "ZZ1";
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
    public void testElecEmissions() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_STAFF).response(NO_STAFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED).response(ELEC_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_EXPORTED).response(ELEC_EXPORTED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_USED_GREEN_TARIFF).response(ELEC_USED_GREEN_TARIFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GREEN_TARIFF_ADDITIONAL_PCT).response(GREEN_TARIFF_ADDITIONAL_PCT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_3RD_PTY_RENEWABLE_USED).response(ELEC_3RD_PTY_RENEWABLE_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.THIRD_PARTY_ADDITIONAL_PCT).response(THIRD_PARTY_ADDITIONAL_PCT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_OWNED_RENEWABLE_USED_SDU).response("8000"));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_TOTAL_RENEWABLE_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_NON_RENEWABLE_GREEN_TARIFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_NON_RENEWABLE_3RD_PARTY));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_RENEWABLE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_EXPORTED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NET_ELEC_CO2E));

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
    public void testEnergyCalcs() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_STAFF).response(NO_STAFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.GAS_USED).response(GAS_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OIL_USED).response(OIL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COAL_USED).response(COAL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.STEAM_USED).response(STEAM_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.HOT_WATER_USED).response(HOT_WATER_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ELEC_OWNED_RENEWABLE_USED_SDU).response("0"));
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
        assertEquals(new BigDecimal("3759"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_GAS).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1159"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_OIL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("664"), rtn.answer(PERIOD, Q.OWNED_BUILDINGS_COAL).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        svc.calcEnergyConsumption(PERIOD, rtn);

        // check inputs unchanged
        assertEquals(new BigDecimal(COAL_USED), rtn.answer(PERIOD, Q.COAL_USED).get().responseAsBigDecimal());
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
