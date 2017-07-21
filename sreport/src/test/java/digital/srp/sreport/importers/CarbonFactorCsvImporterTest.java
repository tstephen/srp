package digital.srp.sreport.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import digital.srp.sreport.model.CarbonFactor;

public class CarbonFactorCsvImporterTest {

    @Test
    public void testImport() {
        try {
            List<CarbonFactor> factors = new CarbonFactorCsvImporter()
                    .readCarbonFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(82*10, factors.size());

            // assert expected values of first record
            // Electricity generated,Electricity: UK,kWh,2,0.46673,0.49608,0.49381,0.48531,0.45205,0.46002,0.44548,0.49426,0.46219,0.41205
            assertEquals("Electricity generated", factors.get(0).category());
            // name is run thru StringUtils.toConst
            assertEquals("ELECTRICITY_UK", factors.get(0).name());
            assertEquals("kWh", factors.get(0).unit());
            assertEquals("2", factors.get(0).scope());
            assertEquals("2007-08", factors.get(0).applicablePeriod());
            assertTrue(new BigDecimal("0.46673").compareTo(factors.get(0).value()) == 0);
            assertEquals("", factors.get(0).comments());

            assertEquals("Electricity generated", factors.get(1).category());
            assertEquals("ELECTRICITY_UK", factors.get(1).name());
            assertEquals("kWh", factors.get(1).unit());
            assertEquals("2", factors.get(1).scope());
            assertEquals("2008-09", factors.get(1).applicablePeriod());
            assertTrue(new BigDecimal("0.49608").compareTo(factors.get(1).value()) == 0);
            assertEquals("", factors.get(1).comments());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

}
