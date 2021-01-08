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

public class BenchmarkingTest {

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;
    private static Cruncher svc;

    private static HealthChecker healthChecker;
    private static final String ORG_CODE = "ZZ1";
    private static final String PERIOD = "2019-20";
    private static final BigDecimal floorArea = new BigDecimal("100.00");
    private static final String metricName = "floorArea";

    private static final BigDecimal totalEnergyCo2e = new BigDecimal("1000000");

    private static final BigDecimal totalTravelCo2e = new BigDecimal("2000000");

    private static final BigDecimal totalProcurementCo2e = new BigDecimal("3000000");

    private static final BigDecimal totalCoreCo2e = new BigDecimal("4000000");

    private static final BigDecimal totalCommissioningCo2e = new BigDecimal("5000000");

    private static final BigDecimal totalCommunityCo2e = new BigDecimal("6000000");

    private static final BigDecimal totalProcurement2017Co2e = new BigDecimal("7000000");

    private static final BigDecimal totalCo2e = new BigDecimal("8000000");

    @BeforeClass
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
        svc.energyEmissionsService = new EnergyEmissionsService();
        svc.roadEmissionsService = new RoadEmissionsService();
        svc.wasteEmissionsService = new WasteEmissionsService();
        healthChecker = new HealthChecker(new MemoryAnswerFactory());
        svc.healthChecker = healthChecker;
    }

    @Test
    public void testFullBenchmarking() {
        SurveyReturn rtn = getSurveyIncEmissions();
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_ENERGY_CO2E).response(totalEnergyCo2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_TRAVEL_CO2E).response(totalTravelCo2e));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_ENERGY_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_TRAVEL_CO2E_BY_FLOOR));

        svc.benchmarkPerMetric(PERIOD, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                floorArea, metricName);

        // check inputs unchanged
        assertEquals(totalProcurementCo2e, rtn
                .answer(PERIOD, Q.TOTAL_PROCUREMENT_CO2E).get().responseAsBigDecimal());

        assertCurrentBenchmarks(rtn);
        assertEquals(new BigDecimal("10000000"),
                rtn.answer(PERIOD, Q.TOTAL_ENERGY_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("20000000"),
                rtn.answer(PERIOD, Q.TOTAL_TRAVEL_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
    }

    private SurveyReturn getSurveyIncEmissions() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD)
                .org(ORG_CODE);
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_PROCUREMENT_CO2E).response(totalProcurementCo2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_CORE_CO2E).response(totalCoreCo2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_COMMISSIONING_CO2E).response(totalCommissioningCo2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_COMMUNITY_CO2E).response(totalCommunityCo2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_PROCUREMENT_2017_CO2E).response(totalProcurement2017Co2e));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_CO2E).response(totalCo2e));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_CORE_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_COMMUNITY_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_PROCUREMENT_2017_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_COMMISSIONING_CO2E_BY_FLOOR));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD)
                .question(Q.TOTAL_PROCUREMENT_CO2E_BY_FLOOR));

        return rtn;
    }

    private void assertCurrentBenchmarks(SurveyReturn rtn) {
        assertEquals(new BigDecimal("30000000"),
                rtn.answer(PERIOD, Q.TOTAL_PROCUREMENT_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("40000000"),
                rtn.answer(PERIOD, Q.TOTAL_CORE_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("50000000"),
                rtn.answer(PERIOD, Q.TOTAL_COMMISSIONING_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("60000000"),
                rtn.answer(PERIOD, Q.TOTAL_COMMUNITY_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("70000000"),
                rtn.answer(PERIOD, Q.TOTAL_PROCUREMENT_2017_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("80000000"),
                rtn.answer(PERIOD, Q.TOTAL_CO2E_BY_FLOOR).get().responseAsBigDecimal()
                        .setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    public void testToleranceOfMissingDeprecatedBenchmarking() {
        SurveyReturn rtn = getSurveyIncEmissions();

        svc.benchmarkPerMetric(PERIOD, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                floorArea, metricName);

        // check inputs unchanged
        assertEquals(totalProcurementCo2e, rtn
                .answer(PERIOD, Q.TOTAL_PROCUREMENT_CO2E).get().responseAsBigDecimal());

        assertCurrentBenchmarks(rtn);
}

}
