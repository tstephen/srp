package digital.srp.sreport.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import digital.srp.sreport.model.WeightingFactor;

public class WeightingFactorImporterTest {

    @Test
    public void testImport() {
        try {
            List<WeightingFactor> factors = new WeightingFactorImporter()
                    .readWeightingFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(23*WeightingFactorImporter.NO_ORG_TYPES, factors.size());

            // assert expected values of 5th record
            // Business services,898385085,36484424,430605458,106399,63999801,153255016,63999801,5913115,240138,7892636,700,421243,1008715,421243
            int fifthAcuteIdx = 5*WeightingFactorImporter.NO_ORG_TYPES;
            assertEquals("Acute", factors.get(fifthAcuteIdx).orgType());
            assertEquals("Business services", factors.get(fifthAcuteIdx).category());
            assertEquals(new BigDecimal("898385085"), factors.get(fifthAcuteIdx).carbonValue());
            assertEquals(new BigDecimal("5913115000"), factors.get(fifthAcuteIdx).moneyValue());
            assertEquals(new BigDecimal("0.152"), factors.get(fifthAcuteIdx).intensityValue());

            assertEquals("Mental health learning disability", factors.get(fifthAcuteIdx+5).orgType());
            assertEquals("Business services", factors.get(fifthAcuteIdx+5).category());
            assertEquals(new BigDecimal("153255016"), factors.get(fifthAcuteIdx+5).carbonValue());
            assertEquals(new BigDecimal("1008715000"), factors.get(fifthAcuteIdx+5).moneyValue());
            assertEquals(new BigDecimal("0.152"), factors.get(fifthAcuteIdx).intensityValue());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

}
