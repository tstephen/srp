package digital.srp.sreport.services;

//import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.internal.ClasspathSurveyReturnHelper;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.surveys.SduQuestions;

public class CruncherTest {

    private static final int YEARS_TO_CALC = 4;

    private  static final String[] ANAESTHETIC_GASES_CO2E = { "5,840",
            "6,430", "10,500", "13,100", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PHARMA_CO2E = { "94.6", "104", "113",
            "116", "0", "0", "0", "0", "0", "0" };

    private  static final String[] PAPER_CO2E = { "997", "1,100", "1,190",
            "1,230", "0", "0", "0", "0", "0", "0" };

    private  static final String[] OTHER_MANUFACTURED_CO2E = { "508", "558",
            "608", "624", "0", "0", "0", "0", "0", "0" };

    private  static final String[] MED_INSTR_CO2E = { "4,450", "4,890",
            "5,330", "5,470", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CHEM_AND_GAS_CO2E = { "1,220", "1,340",
            "1,460", "1,500", "0", "0", "0", "0", "0", "0" };

    private  static final String[] ICT_CO2E = { "379", "416", "453", "466",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] FREIGHT_CO2E = { "2,140", "2,350",
            "2,560", "2,630", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CATERING_CO2E = { "1,430", "1,570",
            "1,720", "1,760", "0", "0", "0", "0", "0", "0" };

    private  static final String[] CONSTRUCTION_CO2E = { "829", "911",
            "992", "1,020", "0", "0", "0", "0", "0", "0" };

    private  static final String[] BIZ_SVCS_CO2E = { "4,180", "4,600",
            "5,010", "5,140", "0", "0", "0", "0", "0", "0" };

    private  static final String[] WASTE_AND_WATER_CO2E = { "88.3", "96.7",
            "105", "118", "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_3_WASTE = { "23.3", "25.7", "28.2", "33.3",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_3_WATER = { "65", "71", "77", "85",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] WATER_TREATMENT_CO2E = { "43", "47",
            "51", "57", "0", "0", "0", "0", "0", "0" };

    private  static final String[] WATER_CO2E = { "22", "24", "26", "28",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_2 = { "668", "794", "781", "880",
            "0", "0", "0", "0", "0", "0" };

    private  static final String[] NET_ELEC_CO2E = { "422", "498", "495",
            "539", "0", "0", "0", "0", "0", "0" };

    private  static final String[] NET_THERMAL_ENERGY_CO2E = { "246", "295",
            "286", "341", "0", "0", "0", "0", "0", "0" };

    private  static final String[] SCOPE_1 = { "8,180", "8,990", "13,300",
            "16,300", "0", "0", "0", "0", "0", "0" };

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

    private  static final String[] OWNED_VEHICLES = { "142", "158", "172",
            "187", "0", "0", "0", "0", "0", "0" };

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

    private boolean debug = false;

    private static AnswerFactory answerFactory;

    private static ClasspathSurveyReturnHelper helper;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;
    private static Cruncher cruncher;

    @BeforeClass
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        cruncher = new Cruncher(cfactors, wfactors);
        answerFactory = new MemoryAnswerFactory();

        helper = new ClasspathSurveyReturnHelper();
    }

    private void assertEquals(String message, String expected, String actual) {
        if (debug && !expected.equals(actual)) {
            System.err.println(String.format("%1$s. Expected %2$s, but got %3$s", message, expected, actual));
        } else {
            Assert.assertEquals(message,  expected, actual);
        }
    }

    @Test
    public void testFindCFactorByName() {
        Assert.assertEquals(new BigDecimal("0.183997"),
                cruncher.cFactor(CarbonFactors.NATURAL_GAS, "2016-17").value());
    }

    @Test
    public void testCrunchRDR() {
        SurveyReturn rdr = helper.readSurveyReturn("RDR");
        SurveyReturn rtn = cruncher.calculate(rdr, YEARS_TO_CALC, answerFactory);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                4);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ORG_CODE),
                        "RDR", rtn.answer(period, Q.ORG_CODE).orElseThrow(() -> new IllegalStateException()).response());
            }

            // SCOPE 1: DIRECT
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OWNED_BUILDINGS_GAS),
                    OWNED_BUILDINGS_GAS[i],
                    rtn.answer(period, Q.OWNED_BUILDINGS_GAS).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OWNED_VEHICLES),
                    OWNED_VEHICLES[i],
                    rtn.answer(period, Q.OWNED_VEHICLES).orElseThrow(() -> new IllegalStateException()).response3sf());

            // ANAESTHETIC GASES
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.DESFLURANE_CO2E),
                    DESFLURANE_CO2E[i],
                    rtn.answer(period, Q.DESFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ISOFLURANE_CO2E),
                    ISOFLURANE_CO2E[i],
                    rtn.answer(period, Q.ISOFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SEVOFLURANE_CO2E),
                    SEVOFLURANE_CO2E[i],
                    rtn.answer(period, Q.SEVOFLURANE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NITROUS_OXIDE_CO2E),
                    NITROUS_OXIDE_CO2E[i],
                    rtn.answer(period, Q.NITROUS_OXIDE_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E),
                    PORTABLE_NITROUS_OXIDE_MIX_CO2E[i],
                    rtn.answer(period, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E),
                    PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E[i],
                    rtn.answer(period,
                            Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            // Total the 6 above
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ANAESTHETIC_GASES_CO2E),
                    ANAESTHETIC_GASES_CO2E[i],
                    rtn.answer(period, Q.ANAESTHETIC_GASES_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());

            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_1),
                    SCOPE_1[i], rtn.answer(period, Q.SCOPE_1).orElseThrow(() -> new IllegalStateException()).response3sf());
            // END SCOPE 1

            // // SCOPE 2: INDIRECT
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NET_THERMAL_ENERGY_CO2E),
                    NET_THERMAL_ENERGY_CO2E[i],
                    rtn.answer(period, Q.NET_THERMAL_ENERGY_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NET_ELEC_CO2E),
                    NET_ELEC_CO2E[i],
                    rtn.answer(period, Q.NET_ELEC_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_2),
                    SCOPE_2[i], rtn.answer(period, Q.SCOPE_2).orElseThrow(() -> new IllegalStateException()).response3sf());

            // SCOPE 3: INDIRECT (SUPPLY CHAIN)
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WATER_CO2E),
                    WATER_CO2E[i],
                    rtn.answer(period, Q.WATER_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WATER_TREATMENT_CO2E),
                    WATER_TREATMENT_CO2E[i],
                    rtn.answer(period, Q.WATER_TREATMENT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_3_WATER),
                    SCOPE_3_WATER[i],
                    rtn.answer(period, Q.SCOPE_3_WATER).orElseThrow(() -> new IllegalStateException()).response3sf());
            // RDR 15-16 example has 29 but questions don't match, this is a
            // subset
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_3_WASTE),
                    SCOPE_3_WASTE[i],
                    rtn.answer(period, Q.SCOPE_3_WASTE).orElseThrow(() -> new IllegalStateException()).response3sf());

            // ECLASS_USER must be false or missing to get SDU procurement
            // estimates
            assertTrue(!cruncher.isEClassUser(rtn));
            // These numbers don't match the RDR 15-16 spreadsheet, but close.
            // Rounding? Or slightly updated factors?
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WASTE_AND_WATER_CO2E),
                    WASTE_AND_WATER_CO2E[i],
                    rtn.answer(period, Q.WASTE_AND_WATER_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.BIZ_SVCS_CO2E),
                    BIZ_SVCS_CO2E[i],
                    rtn.answer(period, Q.BIZ_SVCS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CONSTRUCTION_CO2E),
                    CONSTRUCTION_CO2E[i],
                    rtn.answer(period, Q.CONSTRUCTION_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CATERING_CO2E),
                    CATERING_CO2E[i],
                    rtn.answer(period, Q.CATERING_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.FREIGHT_CO2E),
                    FREIGHT_CO2E[i],
                    rtn.answer(period, Q.FREIGHT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ICT_CO2E),
                    ICT_CO2E[i], rtn.answer(period, Q.ICT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CHEM_AND_GAS_CO2E),
                    CHEM_AND_GAS_CO2E[i],
                    rtn.answer(period, Q.CHEM_AND_GAS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.MED_INSTR_CO2E),
                    MED_INSTR_CO2E[i],
                    rtn.answer(period, Q.MED_INSTR_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OTHER_MANUFACTURED_CO2E),
                    OTHER_MANUFACTURED_CO2E[i],
                    rtn.answer(period, Q.OTHER_MANUFACTURED_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OTHER_PROCUREMENT_CO2E),
                    OTHER_PROCUREMENT_CO2E[i],
                    rtn.answer(period, Q.OTHER_PROCUREMENT_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PAPER_AND_CARD_CO2E),
                    PAPER_CO2E[i],
                    rtn.answer(period, Q.PAPER_AND_CARD_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PHARMA_CO2E),
                    PHARMA_CO2E[i],
                    rtn.answer(period, Q.PHARMA_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
        }
    }

    /**
     * Tests the E-Class method of CO2e calculations.
     */
    @Test
    public void testCrunchRJ1() {
        SurveyReturn rj1 = helper.readSurveyReturn("RJ1");
        SurveyReturn rtn = cruncher.calculate(rj1, YEARS_TO_CALC, answerFactory);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                YEARS_TO_CALC);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            System.out.println("  ASSERTING RESULTS FOR "  + period);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals(
                        String.format("Incorrect value of %2$s for period %1$s",
                                period, Q.ORG_CODE),
                        "RJ1", rtn.answer(period, Q.ORG_CODE).orElseThrow(() -> new IllegalStateException()).response());
                // ECLASS_USER must be true to get procurement estimates based
                // on the E-Class method
                String eClassUser = rtn.answer(period, Q.ECLASS_USER)
                        .orElseThrow(() -> new IllegalStateException()).response();
                assertTrue(
                        String.format("Incorrect value of %2$s for period %1$s",
                                period, Q.ECLASS_USER),
                        Boolean.parseBoolean(eClassUser)
                                || SduQuestions.ANALYSE_BY_E_CLASS.equals(eClassUser));
            }

            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.PROVISIONS_CO2E),
                    PROVISIONS_CO2E[i],
                    rtn.answer(period, Q.PROVISIONS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.STAFF_CLOTHING_CO2E),
                    STAFF_CLOTHING_CO2E[i],
                    rtn.answer(period, Q.STAFF_CLOTHING_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E),
                    PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E[i],
                    rtn.answer(period, Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E),
                    PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E[i],
                    rtn.answer(period, Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.DRESSINGS_CO2E),
                    DRESSINGS_CO2E[i],
                    rtn.answer(period, Q.DRESSINGS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E),
                    MEDICAL_AND_SURGICAL_EQUIPT_CO2E[i],
                    rtn.answer(period, Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.PATIENTS_APPLIANCES_CO2E),
                    PATIENTS_APPLIANCES_CO2E[i],
                    rtn.answer(period, Q.PATIENTS_APPLIANCES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.CHEMICALS_AND_REAGENTS_CO2E),
                    CHEMICALS_AND_REAGENTS_CO2E[i],
                    rtn.answer(period, Q.CHEMICALS_AND_REAGENTS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E),
                    DENTAL_AND_OPTICAL_EQUIPT_CO2E[i],
                    rtn.answer(period, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period,
                            Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E),
                    IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E[i],
                    rtn.answer(period,
                            Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.LAB_EQUIPT_AND_SVCS_CO2E),
                    LABORATORY_EQUIPMENT_AND_SERVICES_CO2E[i],
                    rtn.answer(period, Q.LAB_EQUIPT_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E),
                    HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E[i],
                    rtn.answer(period, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E),
                    BLDG_AND_ENG_PROD_AND_SVCS_CO2E[i],
                    rtn.answer(period, Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.PURCHASED_HEALTHCARE_CO2E),
                    PURCHASED_HEALTHCARE_CO2E[i],
                    rtn.answer(period, Q.PURCHASED_HEALTHCARE_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.GARDENING_AND_FARMING_CO2E),
                    GARDENING_AND_FARMING_CO2E[i],
                    rtn.answer(period, Q.GARDENING_AND_FARMING_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.FURNITURE_FITTINGS_CO2E),
                    FURNITURE_FITTINGS_CO2E[i],
                    rtn.answer(period, Q.FURNITURE_FITTINGS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.HARDWARE_CROCKERY_CO2E),
                    HARDWARE_CROCKERY_CO2E[i],
                    rtn.answer(period, Q.HARDWARE_CROCKERY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.BEDDING_LINEN_AND_TEXTILES_CO2E),
                    BEDDING_LINEN_AND_TEXTILES_CO2E[i],
                    rtn.answer(period, Q.BEDDING_LINEN_AND_TEXTILES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period,
                            Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E),
                    OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E[i],
                    rtn.answer(
                            period,
                            Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E).orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.REC_EQUIPT_AND_SOUVENIRS_CO2E),
                    REC_EQUIPT_AND_SOUVENIRS_CO2E[i],
                    rtn.answer(period, Q.REC_EQUIPT_AND_SOUVENIRS_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
            assertEquals(
                    String.format("Incorrect value of %2$s for period %1$s",
                            period, Q.CONSULTING_SVCS_AND_EXPENSES_CO2E),
                    CONSULTING_SVCS_AND_EXPENSES_CO2E[i],
                    rtn.answer(period, Q.CONSULTING_SVCS_AND_EXPENSES_CO2E)
                            .orElseThrow(() -> new IllegalStateException()).response3sf());
        }
    }

}
