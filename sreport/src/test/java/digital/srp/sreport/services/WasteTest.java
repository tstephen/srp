package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import digital.srp.sreport.model.surveys.Sdu1718;
import digital.srp.sreport.model.surveys.Sdu2021;

public class WasteTest {

    private static final String INCINERATION_CO2E = "110.00";
    private static final String INCINERATION_CO2E_2020_21 = "110.00";
    private static final String LANDFILL_CO2E = "34.45";
    private static final String LANDFILL_CO2E_2020_21 = "44.78";
    private static final String OTHER_RECOVERY_CO2E = "13.06";
    private static final String RECYCLING_CO2E = "15.23";
    private static final String RECYCLING_CO2E_2020_21 = "14.92";
    private static final String DURABLES_INTERNAL_REUSE_CO2E = "0.00";
    private static final String DURABLES_EXTERNAL_REUSE_CO2E = "0.00";
    private static final String PAPER_USED_CO2E = "0.00";
    private static final String WASTE_CLINICAL_INCINERATED_CO2E = "2.40";
    private static final String ALT_WASTE_DISPOSAL_WEIGHT_CO2E = "0.48";
    private static final String WASTE_OFFENSIVE_CO2E = "4.80";
    private static final String WASTE_FOOD_CO2E = "0.12";
    private static final String WASTE_TEXTILES_CO2E = "0.02";
    private static final String WASTE_PROCESSED_ON_SITE_CO2E = "0.00";
    private static final String WEEE_WEIGHT_CO2E = "0.02";

    private static final String OTHER_RECOVERY_WEIGHT = "600.00";
    private static final String RECYCLING_WEIGHT = "700.00";
    private static final String INCINERATION_WEIGHT = "500.00";
    private static final String LANDFILL_WEIGHT = "100.00";
    private static final String DURABLES_INTERNAL_REUSE = "5.00";
    private static final String DURABLES_EXTERNAL_REUSE = "5.00";
    private static final String PAPER_USED = "0.50";
    private static final String WASTE_CLINICAL_INCINERATED = "10.00";
    private static final String ALT_WASTE_DISPOSAL_WEIGHT = "2.00";
    private static final String WASTE_OFFENSIVE = "20.00";
    private static final String WASTE_FOOD = "12.00";
    private static final String WASTE_TEXTILES = "0.80";
    private static final String WASTE_PROCESSED_ON_SITE = "1.50";
    private static final String WEEE_WEIGHT = "1.00";
    private static final String SCOPE_3_WASTE = "159.68";
    private static final String SCOPE_3_WASTE_2020_21 = "177.54";

    private static final String PERIOD = Sdu1718.PERIOD;
    private static final String PERIOD_2020_21 = Sdu2021.PERIOD;
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeClass
    public static void setUpClass() {
        try {
            cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
            wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
            svc = new Cruncher(cfactors, wfactors);
            svc.wasteEmissionsService = new WasteEmissionsService();
        } catch (IOException e) {
            e.printStackTrace();
            fail("unable to read data");
        }
    }

    @Test
    public void testCalcTrustWasteCo2ePre2020() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.INCINERATION_WEIGHT).response(INCINERATION_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.LANDFILL_WEIGHT).response(LANDFILL_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_RECOVERY_WEIGHT).response(OTHER_RECOVERY_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RECYCLING_WEIGHT).response(RECYCLING_WEIGHT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.INCINERATION_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.LANDFILL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_RECOVERY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RECYCLING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WASTE));

        svc.crunchScope3Waste(PERIOD, rtn);
        assertEquals(new BigDecimal(INCINERATION_CO2E), rtn.answer(PERIOD, Q.INCINERATION_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(LANDFILL_CO2E), rtn.answer(PERIOD, Q.LANDFILL_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(OTHER_RECOVERY_CO2E), rtn.answer(PERIOD, Q.OTHER_RECOVERY_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(RECYCLING_CO2E), rtn.answer(PERIOD, Q.RECYCLING_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));

        assertEquals(new BigDecimal(SCOPE_3_WASTE), rtn.answer(PERIOD, Q.SCOPE_3_WASTE).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(INCINERATION_WEIGHT, rtn.answer(PERIOD, Q.INCINERATION_WEIGHT).get().response());
        assertEquals(LANDFILL_WEIGHT, rtn.answer(PERIOD, Q.LANDFILL_WEIGHT).get().response());
    }


    @Test
    public void testCalcTrustWasteCo2e() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD_2020_21).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.INCINERATION_WEIGHT).response(INCINERATION_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.LANDFILL_WEIGHT).response(LANDFILL_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.RECYCLING_WEIGHT).response(RECYCLING_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_INTERNAL_REUSE).response(DURABLES_INTERNAL_REUSE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_EXTERNAL_REUSE).response(DURABLES_EXTERNAL_REUSE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.PAPER_USED).response(PAPER_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_CLINICAL_INCINERATED).response(WASTE_CLINICAL_INCINERATED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.ALT_WASTE_DISPOSAL_WEIGHT).response(ALT_WASTE_DISPOSAL_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_OFFENSIVE).response(WASTE_OFFENSIVE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_FOOD).response(WASTE_FOOD));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_TEXTILES).response(WASTE_TEXTILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_PROCESSED_ON_SITE).response(WASTE_PROCESSED_ON_SITE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WEEE_WEIGHT).response(WEEE_WEIGHT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.INCINERATION_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.LANDFILL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.RECYCLING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_INTERNAL_REUSE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_EXTERNAL_REUSE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.PAPER_USED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_CLINICAL_INCINERATED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_OFFENSIVE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_FOOD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_TEXTILES_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_PROCESSED_ON_SITE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WEEE_WEIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.SCOPE_3_WASTE));

        svc.crunchScope3Waste(PERIOD_2020_21, rtn);
        assertEquals(new BigDecimal(INCINERATION_CO2E_2020_21), rtn.answer(PERIOD_2020_21, Q.INCINERATION_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(LANDFILL_CO2E_2020_21), rtn.answer(PERIOD_2020_21, Q.LANDFILL_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(RECYCLING_CO2E_2020_21), rtn.answer(PERIOD_2020_21, Q.RECYCLING_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(DURABLES_INTERNAL_REUSE_CO2E), rtn.answer(PERIOD_2020_21, Q.DURABLES_INTERNAL_REUSE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(DURABLES_EXTERNAL_REUSE_CO2E), rtn.answer(PERIOD_2020_21, Q.DURABLES_EXTERNAL_REUSE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(PAPER_USED_CO2E), rtn.answer(PERIOD_2020_21, Q.PAPER_USED_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_CLINICAL_INCINERATED_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_CLINICAL_INCINERATED_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(ALT_WASTE_DISPOSAL_WEIGHT_CO2E), rtn.answer(PERIOD_2020_21, Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_OFFENSIVE_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_OFFENSIVE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_FOOD_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_FOOD_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_TEXTILES_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_TEXTILES_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_PROCESSED_ON_SITE_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_PROCESSED_ON_SITE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WEEE_WEIGHT_CO2E), rtn.answer(PERIOD_2020_21, Q.WEEE_WEIGHT_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(SCOPE_3_WASTE_2020_21), rtn.answer(PERIOD_2020_21, Q.SCOPE_3_WASTE).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(INCINERATION_WEIGHT, rtn.answer(PERIOD_2020_21, Q.INCINERATION_WEIGHT).get().response());
        assertEquals(LANDFILL_WEIGHT, rtn.answer(PERIOD_2020_21, Q.LANDFILL_WEIGHT).get().response());
    }

    @Test
    public void testCalcCcgWasteCo2e() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD_2020_21).org("ZZ2");
        final String RECYCLING_WEIGHT = "5";
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.INCINERATION_WEIGHT).response(INCINERATION_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.LANDFILL_WEIGHT).response(LANDFILL_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.RECYCLING_WEIGHT).response(RECYCLING_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_INTERNAL_REUSE).response(DURABLES_INTERNAL_REUSE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_EXTERNAL_REUSE).response(DURABLES_EXTERNAL_REUSE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.PAPER_USED).response(PAPER_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_CLINICAL_INCINERATED).response(WASTE_CLINICAL_INCINERATED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.ALT_WASTE_DISPOSAL_WEIGHT).response(ALT_WASTE_DISPOSAL_WEIGHT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_OFFENSIVE).response(WASTE_OFFENSIVE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_FOOD).response(WASTE_FOOD));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_TEXTILES).response(WASTE_TEXTILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_PROCESSED_ON_SITE).response(WASTE_PROCESSED_ON_SITE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WEEE_WEIGHT).response(WEEE_WEIGHT));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.INCINERATION_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.LANDFILL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.RECYCLING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_INTERNAL_REUSE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DURABLES_EXTERNAL_REUSE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.PAPER_USED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_CLINICAL_INCINERATED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_OFFENSIVE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_FOOD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_TEXTILES_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WASTE_PROCESSED_ON_SITE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.WEEE_WEIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.SCOPE_3_WASTE));

        svc.crunchScope3Waste(PERIOD_2020_21, rtn);
        assertEquals(new BigDecimal(INCINERATION_CO2E_2020_21), rtn.answer(PERIOD_2020_21, Q.INCINERATION_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(LANDFILL_CO2E_2020_21), rtn.answer(PERIOD_2020_21, Q.LANDFILL_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("0.11"), rtn.answer(PERIOD_2020_21, Q.RECYCLING_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(DURABLES_INTERNAL_REUSE_CO2E), rtn.answer(PERIOD_2020_21, Q.DURABLES_INTERNAL_REUSE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(DURABLES_EXTERNAL_REUSE_CO2E), rtn.answer(PERIOD_2020_21, Q.DURABLES_EXTERNAL_REUSE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(PAPER_USED_CO2E), rtn.answer(PERIOD_2020_21, Q.PAPER_USED_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_CLINICAL_INCINERATED_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_CLINICAL_INCINERATED_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(ALT_WASTE_DISPOSAL_WEIGHT_CO2E), rtn.answer(PERIOD_2020_21, Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_OFFENSIVE_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_OFFENSIVE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_FOOD_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_FOOD_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_TEXTILES_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_TEXTILES_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WASTE_PROCESSED_ON_SITE_CO2E), rtn.answer(PERIOD_2020_21, Q.WASTE_PROCESSED_ON_SITE_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(WEEE_WEIGHT_CO2E), rtn.answer(PERIOD_2020_21, Q.WEEE_WEIGHT_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("162.73"), rtn.answer(PERIOD_2020_21, Q.SCOPE_3_WASTE).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(RECYCLING_WEIGHT, rtn.answer(PERIOD_2020_21, Q.RECYCLING_WEIGHT).get().response());
    }

}
