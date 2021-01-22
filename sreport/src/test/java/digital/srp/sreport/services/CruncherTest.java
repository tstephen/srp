package digital.srp.sreport.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.internal.ClasspathSurveyReturnHelper;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.surveys.Sdu1617;
import digital.srp.sreport.model.surveys.Sdu2021;
import digital.srp.sreport.model.surveys.SduQuestions;

@ExtendWith(SpringExtension.class)
public class CruncherTest {

    private static final String SURVEY_FORMAT = "SDU-%1$s";

    private static final int YEARS_TO_CALC = 4;

    private  static final String[] ANAESTHETIC_GASES_CO2E = { "5,840",
            "6,430", "10,500", "13,100", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PHARMA_CO2E = { "94.6", "104", "113",
            "116", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PAPER_CO2E = { "960", "1,050", "1,150",
            "1,180", "0", "0", "0", "0", "0", "0" };

    private  static final String[] OTHER_MANUFACTURED_CO2E = { "469", "515",
            "561", "576", "0", "0", "0", "0", "0", "0" };

    private  static final String[] MED_INSTR_CO2E = { "4,330", "4,750",
            "5,180", "5,320", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CHEM_AND_GAS_CO2E = { "1,220", "1,340",
            "1,460", "1,500", "0", "0", "0", "0", "0", "0" };

    private  static final String[] ICT_CO2E = { "367", "403", "439", "451",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] FREIGHT_CO2E = { "2,050", "2,260",
            "2,460", "2,520", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CATERING_CO2E = { "1,400", "1,530",
            "1,670", "1,720", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CONSTRUCTION_CO2E = { "810", "890",
            "969", "996", "0", "0", "0", "0", "0", "0" };

    private  static final String[] BIZ_SVCS_CO2E = { "4,070", "4,470",
            "4,870", "5,010", "0", "0", "0", "0", "0", "0" };

    private  static final String[] WASTE_AND_WATER_CO2E = { "92.3", "101",
            "111", "124", "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_3_WASTE = { "27.5", "30.2", "33.2", "39.2",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_3_WATER = { "64.8", "71.2", "77.5", "85.2",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] WATER_TREATMENT_CO2E = { "42.9", "47.2",
            "51.4", "57.1", "0", "0", "0", "0", "0", "0" };

    private  static final String[] WATER_CO2E = { "21.9", "24", "26.1", "28.1",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_2 = { "668", "794", "781", "880",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] NET_ELEC_CO2E = { "422", "498", "495",
            "539", "0", "0", "0", "0", "0", "0" };

    private  static final String[] NET_THERMAL_ENERGY_CO2E = { "246", "295",
            "286", "341", "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_1 = { "8,260", "9,090", "13,400",
            "16,400", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E = {
            "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PORTABLE_NITROUS_OXIDE_MIX_CO2E = {
            "1.39", "1.53", "1.67", "1.95", "0", "0", "0", "0", "0", "0" };

    private  static final String[] NITROUS_OXIDE_CO2E = { "2.24", "2.46",
            "2.8", "3.3", "0", "0", "0", "0", "0", "0" };

    private  static final String[] SEVOFLURANE_CO2E = { "594", "653", "791",
            "752", "0", "0", "0", "0", "0", "0" };

    private  static final String[] ISOFLURANE_CO2E = { "1,530", "1,680",
            "2,290", "3,050", "0", "0", "0", "0", "0", "0" };

    private  static final String[] DESFLURANE_CO2E = { "3,720", "4,090",
            "7,440", "9,300", "0", "0", "0", "0", "0", "0" };

    private  static final String[] OWNED_FLEET_TRAVEL_CO2E = { "228", "254", "276",
            "301", "0", "0", "0", "0", "0", "0" };

    private  static final String[] OWNED_BUILDINGS_GAS = { "2,190", "2,410",
            "2,640", "2,960", "0", "0", "0", "0", "0", "0" };

    private  static final String[] OTHER_PROCUREMENT_CO2E = { "0", "0", "0",
            "0", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PROVISIONS_CO2E = { "970", "970", "970",
            "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] STAFF_CLOTHING_CO2E = { "580", "580",
            "580", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E = {
            "870", "870", "870", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E = {
            "1,720", "1,720", "1,720", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] DRESSINGS_CO2E = { "7,700", "7,700",
            "7,700", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] MEDICAL_AND_SURGICAL_EQUIPT_CO2E = {
            "1,800", "1,800", "1,800", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] PATIENTS_APPLIANCES_CO2E = { "10,800",
            "10,800", "10,800", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] CHEMICALS_AND_REAGENTS_CO2E = { "6,080",
            "6,080", "6,080", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] DENTAL_AND_OPTICAL_EQUIPT_CO2E = {
            "2,700", "2,700", "2,700", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E = {
            "3,000", "3,000", "3,000", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] LABORATORY_EQUIPMENT_AND_SERVICES_CO2E = {
            "3,300", "3,300", "3,300", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E = {
            "5,880", "5,880", "5,880", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] BLDG_AND_ENG_PROD_AND_SVCS_CO2E = {
            "6,370", "6,370", "6,370", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] PURCHASED_HEALTHCARE_CO2E = { "4,760",
            "4,760", "4,760", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] GARDENING_AND_FARMING_CO2E = { "40,200",
            "40,200", "40,200", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] FURNITURE_FITTINGS_CO2E = { "7,680",
            "7,680", "7,680", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] HARDWARE_CROCKERY_CO2E = { "9,860",
            "9,860", "9,860", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] BEDDING_LINEN_AND_TEXTILES_CO2E = {
            "5,760", "5,760", "5,760", "0", "0", "0", "0", "0", "0" };
    private  static final String[] OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E = {
            "10,100", "10,100", "10,100", "0", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] REC_EQUIPT_AND_SOUVENIRS_CO2E = {
            "5,600", "5,600", "5,600", "0", "0", "0", "0", "0", "0", "0" };
    private  static final String[] CONSULTING_SVCS_AND_EXPENSES_CO2E = {
            "6,510", "6,510", "6,510", "0", "0", "0", "0", "0", "0", "0" };

    private static ClasspathSurveyReturnHelper helper;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;
    private static Cruncher cruncher;

    private static HealthChecker healthChecker;

    @BeforeAll
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        cruncher = new Cruncher(cfactors, wfactors);
        cruncher.energyEmissionsService = new EnergyEmissionsService();
        cruncher.roadEmissionsService = new RoadEmissionsService();
        cruncher.wasteEmissionsService = new WasteEmissionsService();
        healthChecker = new HealthChecker(new MemoryAnswerFactory());
        cruncher.healthChecker = healthChecker;
        helper = new ClasspathSurveyReturnHelper();
    }

    @Test
    public void testFindCFactorByNameAndPeriod() {
        assertEquals(new BigDecimal("0.183997"),
                cruncher.cFactor(CarbonFactors.NATURAL_GAS, Sdu1617.PERIOD).value());
    }

    @Test
    public void testFindGasTotalCFactorByNameAndPeriod() {
        assertEquals(new BigDecimal("0.207780"),
                cruncher.cFactor(CarbonFactors.GAS_TOTAL, Sdu2021.PERIOD).value());
    }

    @Test
    public void testCrunchRDR() {
        SurveyReturn rdr = helper.readSurveyReturn("RDR")
                .orElseThrow(IllegalStateException::new);
        Survey survey = new Survey()
                .applicablePeriod(rdr.applicablePeriod())
                .name(rdr.getName().substring(0, rdr.getName().length()-4));
        rdr.survey(survey);
        healthChecker.ensureInitialised(YEARS_TO_CALC,
                String.format(SURVEY_FORMAT, rdr.applicablePeriod()), rdr);
        SurveyReturn rtn = cruncher.calculate(rdr, YEARS_TO_CALC);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                4);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals("RDR", rtn.answer(period, Q.ORG_CODE)
                        .orElseThrow(() -> new IllegalStateException()).response());
            }

            // SCOPE 1: DIRECT
            assertEquals(OWNED_BUILDINGS_GAS[i],
                    rtn.answer(period, Q.OWNED_BUILDINGS_GAS).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(OWNED_FLEET_TRAVEL_CO2E[i],
                    rtn.answer(period, Q.OWNED_FLEET_TRAVEL_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());

            // ANAESTHETIC GASES
            assertEquals(DESFLURANE_CO2E[i],
                    rtn.answer(period, Q.DESFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(ISOFLURANE_CO2E[i],
                    rtn.answer(period, Q.ISOFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(SEVOFLURANE_CO2E[i],
                    rtn.answer(period, Q.SEVOFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(NITROUS_OXIDE_CO2E[i],
                    rtn.answer(period, Q.NITROUS_OXIDE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PORTABLE_NITROUS_OXIDE_MIX_CO2E[i],
                    rtn.answer(period, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E[i],
                    rtn.answer(period,
                            Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            // Total the 6 above
            assertEquals(ANAESTHETIC_GASES_CO2E[i],
                    rtn.answer(period, Q.ANAESTHETIC_GASES_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());

            assertEquals(SCOPE_1[i], rtn.answer(period, Q.SCOPE_1).orElseThrow(() -> new IllegalStateException()).response3sf());
            // END SCOPE 1

            // // SCOPE 2: INDIRECT
            assertEquals(NET_THERMAL_ENERGY_CO2E[i],
                    rtn.answer(period, Q.NET_THERMAL_ENERGY_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(NET_ELEC_CO2E[i],
                    rtn.answer(period, Q.NET_ELEC_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(SCOPE_2[i], rtn.answer(period, Q.SCOPE_2).orElseThrow(() -> new IllegalStateException()).response3sf());

            // SCOPE 3: INDIRECT (SUPPLY CHAIN)
            assertEquals(WATER_CO2E[i],
                    rtn.answer(period, Q.WATER_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(WATER_TREATMENT_CO2E[i],
                    rtn.answer(period, Q.WATER_TREATMENT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(SCOPE_3_WATER[i],
                    rtn.answer(period, Q.SCOPE_3_WATER).orElseThrow(() -> new IllegalStateException()).response3sf());
            // RDR 15-16 example has 29 but questions don't match, this is a
            // subset
            assertEquals(SCOPE_3_WASTE[i],
                    rtn.answer(period, Q.SCOPE_3_WASTE).orElseThrow(() -> new IllegalStateException()).response3sf());

            // These numbers don't match the RDR 15-16 spreadsheet, but close.
            // Rounding? Or slightly updated factors?
            assertEquals(WASTE_AND_WATER_CO2E[i],
                    rtn.answer(period, Q.WASTE_AND_WATER_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(BIZ_SVCS_CO2E[i],
                    rtn.answer(period, Q.BIZ_SVCS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(CONSTRUCTION_CO2E[i],
                    rtn.answer(period, Q.CONSTRUCTION_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(CATERING_CO2E[i],
                    rtn.answer(period, Q.CATERING_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(FREIGHT_CO2E[i],
                    rtn.answer(period, Q.FREIGHT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(ICT_CO2E[i], rtn.answer(period, Q.ICT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(CHEM_AND_GAS_CO2E[i],
                    rtn.answer(period, Q.CHEM_AND_GAS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(MED_INSTR_CO2E[i],
                    rtn.answer(period, Q.MED_INSTR_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(OTHER_MANUFACTURED_CO2E[i],
                    rtn.answer(period, Q.OTHER_MANUFACTURED_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(OTHER_PROCUREMENT_CO2E[i],
                    rtn.answer(period, Q.OTHER_PROCUREMENT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PAPER_CO2E[i],
                    rtn.answer(period, Q.PAPER_AND_CARD_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PHARMA_CO2E[i],
                    rtn.answer(period, Q.PHARMA_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
        }
    }

    /**
     * Tests the E-Class method of CO2e calculations.
     */
    @Test
    public void testCrunchRJ1() {
        SurveyReturn rj1 = helper.readSurveyReturn("RJ1")
                .orElseThrow(IllegalStateException::new);
        Survey survey = new Survey()
                .applicablePeriod(rj1.applicablePeriod())
                .name(rj1.getName().substring(0, rj1.getName().length()-4));
        rj1.survey(survey);
        healthChecker.ensureInitialised(YEARS_TO_CALC,
                String.format(SURVEY_FORMAT, rj1.applicablePeriod()), rj1);
        SurveyReturn rtn = cruncher.calculate(rj1, YEARS_TO_CALC);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                YEARS_TO_CALC);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            System.out.println("  ASSERTING RESULTS FOR "  + period);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals("RJ1",
                        rtn.answer(period, Q.ORG_CODE)
                                .orElseThrow(() -> new IllegalStateException())
                                .response());
                // ECLASS_USER must be true to get procurement estimates based
                // on the E-Class method
                String eClassUser = rtn.answer(period, Q.ECLASS_USER)
                        .orElseThrow(() -> new IllegalStateException()).response();
                assertTrue(Boolean.parseBoolean(eClassUser)
                        || SduQuestions.ANALYSE_BY_E_CLASS.equals(eClassUser),
                        String.format("Incorrect value of %2$s for period %1$s",
                                period, Q.ECLASS_USER));
            }

            assertEquals(PROVISIONS_CO2E[i],
                    rtn.answer(period, Q.PROVISIONS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(STAFF_CLOTHING_CO2E[i],
                    rtn.answer(period, Q.STAFF_CLOTHING_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E[i],
                    rtn.answer(period, Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E[i],
                    rtn.answer(period, Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(DRESSINGS_CO2E[i],
                    rtn.answer(period, Q.DRESSINGS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(MEDICAL_AND_SURGICAL_EQUIPT_CO2E[i],
                    rtn.answer(period, Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PATIENTS_APPLIANCES_CO2E[i],
                    rtn.answer(period, Q.PATIENTS_APPLIANCES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(CHEMICALS_AND_REAGENTS_CO2E[i],
                    rtn.answer(period, Q.CHEMICALS_AND_REAGENTS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(DENTAL_AND_OPTICAL_EQUIPT_CO2E[i],
                    rtn.answer(period, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E[i],
                    rtn.answer(period,
                            Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(LABORATORY_EQUIPMENT_AND_SERVICES_CO2E[i],
                    rtn.answer(period, Q.LAB_EQUIPT_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E[i],
                    rtn.answer(period, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(BLDG_AND_ENG_PROD_AND_SVCS_CO2E[i],
                    rtn.answer(period, Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(PURCHASED_HEALTHCARE_CO2E[i],
                    rtn.answer(period, Q.PURCHASED_HEALTHCARE_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(GARDENING_AND_FARMING_CO2E[i],
                    rtn.answer(period, Q.GARDENING_AND_FARMING_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(FURNITURE_FITTINGS_CO2E[i],
                    rtn.answer(period, Q.FURNITURE_FITTINGS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(HARDWARE_CROCKERY_CO2E[i],
                    rtn.answer(period, Q.HARDWARE_CROCKERY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(BEDDING_LINEN_AND_TEXTILES_CO2E[i],
                    rtn.answer(period, Q.BEDDING_LINEN_AND_TEXTILES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E[i],
                    rtn.answer(period,
                            Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(REC_EQUIPT_AND_SOUVENIRS_CO2E[i],
                    rtn.answer(period, Q.REC_EQUIPT_AND_SOUVENIRS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(CONSULTING_SVCS_AND_EXPENSES_CO2E[i],
                    rtn.answer(period, Q.CONSULTING_SVCS_AND_EXPENSES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
        }
    }

    /**
     * Tests that <em>both</em> E-Class and SDU profiles are calculated from 2020-21.
     */
    @Test
    public void testCrunchZZ1() {
        SurveyReturn zz1 = helper.readSurveyReturn("ZZ1")
                .orElseThrow(IllegalStateException::new);
        Survey survey = new Survey()
                .applicablePeriod(zz1.applicablePeriod())
                .name(zz1.getName().substring(0, zz1.getName().length()-4));
        zz1.survey(survey);
        healthChecker.ensureInitialised(YEARS_TO_CALC,
                String.format(SURVEY_FORMAT, zz1.applicablePeriod()), zz1);
        SurveyReturn rtn = cruncher.calculate(zz1, YEARS_TO_CALC);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                YEARS_TO_CALC);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            System.out.println("  ASSERTING RESULTS FOR "  + period);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals("ZZ1",
                        rtn.answer(period, Q.ORG_CODE)
                                .orElseThrow(() -> new IllegalStateException())
                                .response());
            }

            // check have new simplified procurement categories from both eClass and SDU methods
            assertThat(rtn.answer(period, Q.SDU_MEDICINES_AND_CHEMICALS)
                    .orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal(),
                    is(Matchers.greaterThanOrEqualTo(BigDecimal.ZERO)));
            assertThat(rtn.answer(period, Q.EC_MEDICINES_AND_CHEMICALS)
                    .orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal(),
                    is(Matchers.greaterThan(BigDecimal.ZERO)));
            assertThat(rtn.answer(period, Q.SDU_MEDICINES_AND_CHEMICALS_CO2E)
                    .orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal(),
                    is(Matchers.greaterThanOrEqualTo(BigDecimal.ZERO)));
            assertThat(rtn.answer(period, Q.EC_MEDICINES_AND_CHEMICALS_CO2E)
                    .orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal(),
                    is(Matchers.greaterThan(BigDecimal.ZERO)));
            assertThat(rtn.answer(period, Q.ECLASS_SPEND)
                    .orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal(),
                    is(Matchers.greaterThan(BigDecimal.ZERO)));
        }
    }

}
