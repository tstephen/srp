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
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class CruncherTest {

    private static final String DATA_FILE = "/returns/%1$s.json";

    private static ObjectMapper objectMapper;
    
    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;
    private static Cruncher cruncher;

    @BeforeClass
    public static void setUpClass() throws IOException {
        objectMapper = new ObjectMapper();
        cfactors = new CarbonFactorCsvImporter()
                .readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter()
                .readWeightingFactors();
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
        assertEquals("RDR", rtn.answer(Q.ORG_CODE, rtn.applicablePeriod()).response());

        // SCOPE 1: DIRECT
        assertEquals("1,930", rtn.answer(Q.OWNED_BUILDINGS_GAS, rtn.applicablePeriod()).response3sf());
        assertEquals("142", rtn.answer(Q.OWNED_VEHICLES, rtn.applicablePeriod()).response3sf());
        
        // ANAESTHETIC GASES
        assertEquals("3,720", rtn.answer(Q.DESFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1,530", rtn.answer(Q.ISOFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("594", rtn.answer(Q.SEVOFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("2.24", rtn.answer(Q.NITROUS_OXIDE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1.39", rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("0", rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E, rtn.applicablePeriod()).response3sf());
        // Total the 6 above
        assertEquals("5,840", rtn.answer(Q.ANAESTHETIC_GASES_CO2E, rtn.applicablePeriod()).response3sf());

        assertEquals("7,910", rtn.answer(Q.SCOPE_1, rtn.applicablePeriod()).response3sf());
        // END SCOPE 1
        
        // SCOPE 2: INDIRECT
        assertEquals("224", rtn.answer(Q.NET_THERMAL_ENERGY_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("339", rtn.answer(Q.NET_ELEC_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("563", rtn.answer(Q.SCOPE_2, rtn.applicablePeriod()).response3sf());
        
        // SCOPE 3: INDIRECT (SUPPLY CHAIN)
        assertEquals("22", rtn.answer(Q.WATER_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("43", rtn.answer(Q.WATER_TREATMENT_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("65", rtn.answer(Q.SCOPE_3_WATER, rtn.applicablePeriod()).response3sf());
        // RDR 15-16 example has 29 but questions don't match, this is a subset
        assertEquals("3", rtn.answer(Q.SCOPE_3_WASTE, rtn.applicablePeriod()).response3sf());
        
        // ECLASS_USER must be false or missing to get SDU procurement estimates
        assertTrue(!Boolean.parseBoolean(rtn.answer(Q.ECLASS_USER, rtn.applicablePeriod()).response()));
        // These numbers don't match the RDR 15-16 spreadsheet, but close. Rounding? Or slightly updated factors?
        assertEquals("68", rtn.answer(Q.WASTE_AND_WATER_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("4,180", rtn.answer(Q.BIZ_SVCS_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("829", rtn.answer(Q.CONSTRUCTION_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1,430", rtn.answer(Q.CATERING_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("2,140", rtn.answer(Q.FREIGHT_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("379", rtn.answer(Q.ICT_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1,220", rtn.answer(Q.CHEM_AND_GAS_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("4,450", rtn.answer(Q.MED_INSTR_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("508", rtn.answer(Q.OTHER_MANUFACTURED_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("0", rtn.answer(Q.OTHER_PROCUREMENT_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("997", rtn.answer(Q.PAPER_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("94.6", rtn.answer(Q.PHARMA_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("2,370", rtn.answer(Q.TRAVEL_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1,250", rtn.answer(Q.COMMISSIONING_CO2E, rtn.applicablePeriod()).response3sf());

        assertEquals("8,720", rtn.answer(Q.CORE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("17,500", rtn.answer(Q.PROCUREMENT_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("122", rtn.answer(Q.CITIZEN_CO2E, rtn.applicablePeriod()).response3sf());
        
    }

    private SurveyReturn readSurveyReturn(String org) {
        String dataFile = String.format(DATA_FILE, org.toLowerCase());
        try (InputStream is = getClass().getResourceAsStream(dataFile)) {
            assertNotNull(String.format("Unable to find test data at %1$s",
                    dataFile), is);

             return objectMapper.readValue(is, SurveyReturn.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }

}
