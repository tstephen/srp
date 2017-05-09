package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        assertEquals("1,930", rtn.answer(Q.OWNED_BUILDINGS_GAS, rtn.applicablePeriod()).response3sf());
        assertEquals("142", rtn.answer(Q.OWNED_VEHICLES, rtn.applicablePeriod()).response3sf());
        
        assertEquals("3,720", rtn.answer(Q.DESFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1,530", rtn.answer(Q.ISOFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("594", rtn.answer(Q.SEVOFLURANE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("2.24", rtn.answer(Q.NITROUS_OXIDE_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("1.39", rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("0", rtn.answer(Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E, rtn.applicablePeriod()).response3sf());
        // Total the 6 above
        assertEquals("5,840", rtn.answer(Q.ANAESTHETIC_GASES_CO2E, rtn.applicablePeriod()).response3sf());

        assertEquals("7,910", rtn.answer(Q.SCOPE_1, rtn.applicablePeriod()).response3sf());
        
        assertEquals("224", rtn.answer(Q.NET_THERMAL_ENERGY_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("339", rtn.answer(Q.NET_ELEC_CO2E, rtn.applicablePeriod()).response3sf());
        assertEquals("563", rtn.answer(Q.SCOPE_2, rtn.applicablePeriod()).response3sf());
    }

    private SurveyReturn readSurveyReturn(String org) {
        InputStream is = null;
        try {
            String dataFile = String.format(DATA_FILE, org.toLowerCase());
            is = getClass().getResourceAsStream(dataFile);
            assertNotNull(String.format("Unable to find test data at %1$s",
                    dataFile), is);

             return objectMapper.readValue(is, SurveyReturn.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

}
