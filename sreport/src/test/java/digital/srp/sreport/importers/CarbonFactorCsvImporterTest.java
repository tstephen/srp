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

    private static final int NO_OF_YEARS_INCLUDED = 11;

    @Test
    public void testImport() {
        try {
            List<CarbonFactor> factors = new CarbonFactorCsvImporter()
                    .readCarbonFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(117 * NO_OF_YEARS_INCLUDED, factors.size());

            // assert expected values of first record
            // Electricity generated,Electricity: UK,kWh,2,0.46673,0.49608,0.49381,0.48531,0.45205,0.46002,0.44548,0.49426,0.46219,0.41205,0.35156,0.28307
            CarbonFactor elecFactor0708 = factors.get(0);
            assertEquals("Electricity generated", elecFactor0708.category());
            // name is run thru StringUtils.toConst
            assertEquals("ELECTRICITY_UK", elecFactor0708.name());
            assertEquals("kWh", elecFactor0708.unit());
            assertEquals("2", elecFactor0708.scope());
            assertEquals("2007-08", elecFactor0708.applicablePeriod());
            assertTrue(new BigDecimal("0.46673").compareTo(elecFactor0708.value()) == 0);
            assertEquals("", elecFactor0708.comments());

            CarbonFactor elecFactor0809 = factors.get(1);
            assertEquals("Electricity generated", elecFactor0809.category());
            assertEquals("ELECTRICITY_UK", elecFactor0809.name());
            assertEquals("kWh", elecFactor0809.unit());
            assertEquals("2", elecFactor0809.scope());
            assertEquals("2008-09", elecFactor0809.applicablePeriod());
            assertTrue(new BigDecimal("0.49608").compareTo(elecFactor0809.value()) == 0);
            assertEquals("", elecFactor0809.comments());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

}
