package digital.srp.sreport.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CarbonFactorTest {
    
    @Test
    public void testBusinessDataEquality() {
        CarbonFactor factor1 = new CarbonFactor()
                .id(1l)
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .value(new BigDecimal("0.46673"))
                .applicablePeriod("2007-08")
                .scope("2")
                .created(new Date())
                .createdBy("tstephen")
                .lastUpdated(new Date());

        CarbonFactor factor2 = new CarbonFactor()
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .value(new BigDecimal("0.46673"))
                .applicablePeriod("2007-08")
                .scope("2");
        
        assertEquals(factor1, factor2);
        List<CarbonFactor> existingFactors = Collections.singletonList(factor1);
        assertTrue(existingFactors.contains(factor2));

        CarbonFactor factor3 = new CarbonFactor()
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .applicablePeriod("2019-20")
                .scope("2");
        assertTrue(!existingFactors.contains(factor3));
    }

}
