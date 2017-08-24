package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class CruncherTest {

    private /* static final */String[] ANAESTHETIC_GASES_CO2E = { "5,840",
            "6,430", "10,500", "13,100", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] CITIZEN_CO2E = { "122", "135", "125",
            "157", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] PROCUREMENT_CO2E = { "17,500", "19,200",
            "20,900", "21,500", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] CORE_CO2E = { "8,720", "9,650", "13,900",
            "17,000", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] COMMISSIONING_CO2E = { "1,250", "1,370",
            "1,490", "1,530", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] TRAVEL_CO2E = { "2,370", "2,610",
            "2,840", "2,920", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] PHARMA_CO2E = { "94.6", "104", "113",
            "116", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] PAPER_CO2E = { "997", "1,100", "1,190",
            "1,230", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] OTHER_MANUFACTURED_CO2E = { "508", "558",
            "608", "624", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] MED_INSTR_CO2E = { "4,450", "4,890",
            "5,330", "5,470", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] CHEM_AND_GAS_CO2E = { "1,220", "1,340",
            "1,460", "1,500", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] ICT_CO2E = { "379", "416", "453", "466",
            "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] FREIGHT_CO2E = { "2,140", "2,350",
            "2,560", "2,630", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] CATERING_CO2E = { "1,430", "1,570",
            "1,720", "1,760", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] CONSTRUCTION_CO2E = { "829", "911",
            "992", "1,020", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] BIZ_SVCS_CO2E = { "4,180", "4,600",
            "5,010", "5,140", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] WASTE_AND_WATER_CO2E = { "68", "74",
            "80", "90", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] SCOPE_3_WASTE = { "3", "3", "3", "5",
            "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] SCOPE_3_WATER = { "65", "71", "77", "85",
            "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] WATER_TREATMENT_CO2E = { "43", "47",
            "51", "57", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] WATER_CO2E = { "22", "24", "26", "28",
            "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] SCOPE_2 = { "563", "666", "653", "743",
            "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] NET_ELEC_CO2E = { "339", "398", "394",
            "435", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] NET_THERMAL_ENERGY_CO2E = { "224", "268",
            "259", "308", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] SCOPE_1 = { "7,910", "8,710", "13,000",
            "16,000", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E = {
            "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] PORTABLE_NITROUS_OXIDE_MIX_CO2E = {
            "1.39", "1.53", "1.67", "1.95", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] NITROUS_OXIDE_CO2E = { "2.24", "2.46",
            "2.8", "3.3", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] SEVOFLURANE_CO2E = { "594", "653", "791",
            "752", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] ISOFLURANE_CO2E = { "1,530", "1,680",
            "2,290", "3,050", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] DESFLURANE_CO2E = { "3,720", "4,090",
            "7,440", "9,300", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] OWNED_VEHICLES = { "142", "157", "171",
            "188", "0", "0", "0", "0", "0", "0" };

    private /* static final */String[] OWNED_BUILDINGS_GAS = { "1,930", "2,120",
            "2,290", "2,680", "0", "0", "0", "0", "0", "0" };

    private /* static final */String DATA_FILE = "/returns/%1$s.json";

    private /* static final */String[] OTHER_PROCUREMENT_CO2E = { "0", "0", "0",
            "0", "0", "0", "0", "0", "0", "0" };

    private static ObjectMapper objectMapper;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;
    private static Cruncher cruncher;

    @BeforeClass
    public static void setUpClass() throws IOException {
        objectMapper = new ObjectMapper();
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        cruncher = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testFindCFactorByName() {
        assertEquals(new BigDecimal("0.183997"),
                cruncher.cFactor(CarbonFactors.NATURAL_GAS, "2016-17").value());
    }

    @Test
    public void testCrunchRDR() {
        SurveyReturn rdr = readSurveyReturn("RDR");
        SurveyReturn rtn = cruncher.calculate(rdr);

        assertNotNull(rtn);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                4);
        for (int i = 0; i < periods.size(); i++) {
            String period = periods.get(i);
            // Performance and Policy only relevant to current year
            if (i == 0) {
                assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ORG_CODE),
                        "RDR", rtn.answer(Q.ORG_CODE, period).response());
            }

            // SCOPE 1: DIRECT
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OWNED_BUILDINGS_GAS),
                    OWNED_BUILDINGS_GAS[i],
                    rtn.answer(Q.OWNED_BUILDINGS_GAS, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OWNED_VEHICLES),
                    OWNED_VEHICLES[i],
                    rtn.answer(Q.OWNED_VEHICLES, period).response3sf());

            // ANAESTHETIC GASES
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.DESFLURANE_CO2E),
                    DESFLURANE_CO2E[i],
                    rtn.answer(Q.DESFLURANE_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ISOFLURANE_CO2E),
                    ISOFLURANE_CO2E[i],
                    rtn.answer(Q.ISOFLURANE_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SEVOFLURANE_CO2E),
                    SEVOFLURANE_CO2E[i],
                    rtn.answer(Q.SEVOFLURANE_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NITROUS_OXIDE_CO2E),
                    NITROUS_OXIDE_CO2E[i],
                    rtn.answer(Q.NITROUS_OXIDE_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E),
                    PORTABLE_NITROUS_OXIDE_MIX_CO2E[i],
                    rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E, period)
                            .response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E),
                    PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E[i],
                    rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E,
                            period).response3sf());
            // Total the 6 above
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ANAESTHETIC_GASES_CO2E),
                    ANAESTHETIC_GASES_CO2E[i],
                    rtn.answer(Q.ANAESTHETIC_GASES_CO2E, period).response3sf());

            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_1),
                    SCOPE_1[i], rtn.answer(Q.SCOPE_1, period).response3sf());
            // END SCOPE 1

            // // SCOPE 2: INDIRECT
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NET_THERMAL_ENERGY_CO2E),
                    NET_THERMAL_ENERGY_CO2E[i],
                    rtn.answer(Q.NET_THERMAL_ENERGY_CO2E, period)
                            .response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.NET_ELEC_CO2E),
                    NET_ELEC_CO2E[i],
                    rtn.answer(Q.NET_ELEC_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_2),
                    SCOPE_2[i], rtn.answer(Q.SCOPE_2, period).response3sf());

            // SCOPE 3: INDIRECT (SUPPLY CHAIN)
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WATER_CO2E),
                    WATER_CO2E[i],
                    rtn.answer(Q.WATER_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WATER_TREATMENT_CO2E),
                    WATER_TREATMENT_CO2E[i],
                    rtn.answer(Q.WATER_TREATMENT_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_3_WATER),
                    SCOPE_3_WATER[i],
                    rtn.answer(Q.SCOPE_3_WATER, period).response3sf());
            // RDR 15-16 example has 29 but questions don't match, this is a
            // subset
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.SCOPE_3_WASTE),
                    SCOPE_3_WASTE[i],
                    rtn.answer(Q.SCOPE_3_WASTE, period).response3sf());

            // ECLASS_USER must be false or missing to get SDU procurement
            // estimates
            assertTrue(!Boolean.parseBoolean(
                    rtn.answer(Q.ECLASS_USER, period).response()));
            // These numbers don't match the RDR 15-16 spreadsheet, but close.
            // Rounding? Or slightly updated factors?
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.WASTE_AND_WATER_CO2E),
                    WASTE_AND_WATER_CO2E[i],
                    rtn.answer(Q.WASTE_AND_WATER_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.BIZ_SVCS_CO2E),
                    BIZ_SVCS_CO2E[i],
                    rtn.answer(Q.BIZ_SVCS_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CONSTRUCTION_CO2E),
                    CONSTRUCTION_CO2E[i],
                    rtn.answer(Q.CONSTRUCTION_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CATERING_CO2E),
                    CATERING_CO2E[i],
                    rtn.answer(Q.CATERING_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.FREIGHT_CO2E),
                    FREIGHT_CO2E[i],
                    rtn.answer(Q.FREIGHT_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.ICT_CO2E),
                    ICT_CO2E[i], rtn.answer(Q.ICT_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CHEM_AND_GAS_CO2E),
                    CHEM_AND_GAS_CO2E[i],
                    rtn.answer(Q.CHEM_AND_GAS_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.MED_INSTR_CO2E),
                    MED_INSTR_CO2E[i],
                    rtn.answer(Q.MED_INSTR_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OTHER_MANUFACTURED_CO2E),
                    OTHER_MANUFACTURED_CO2E[i],
                    rtn.answer(Q.OTHER_MANUFACTURED_CO2E, period)
                            .response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.OTHER_PROCUREMENT_CO2E),
                    OTHER_PROCUREMENT_CO2E[i],
                    rtn.answer(Q.OTHER_PROCUREMENT_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PAPER_CO2E),
                    PAPER_CO2E[i],
                    rtn.answer(Q.PAPER_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PHARMA_CO2E),
                    PHARMA_CO2E[i],
                    rtn.answer(Q.PHARMA_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.TRAVEL_CO2E),
                    TRAVEL_CO2E[i],
                    rtn.answer(Q.TRAVEL_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.COMMISSIONING_CO2E),
                    COMMISSIONING_CO2E[i],
                    rtn.answer(Q.COMMISSIONING_CO2E, period).response3sf());

            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CORE_CO2E),
                    CORE_CO2E[i],
                    rtn.answer(Q.CORE_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.PROCUREMENT_CO2E),
                    PROCUREMENT_CO2E[i],
                    rtn.answer(Q.PROCUREMENT_CO2E, period).response3sf());
            assertEquals(String.format("Incorrect value of %2$s for period %1$s", period, Q.CITIZEN_CO2E),
                    CITIZEN_CO2E[i],
                    rtn.answer(Q.CITIZEN_CO2E, period).response3sf());
        }
    }

    private SurveyReturn readSurveyReturn(String org) {
        String dataFile = String.format(DATA_FILE, org.toLowerCase());
        try (InputStream is = getClass().getResourceAsStream(dataFile)) {
            assertNotNull(
                    String.format("Unable to find test data at %1$s", dataFile),
                    is);

            return objectMapper.readValue(is, SurveyReturn.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }

}
