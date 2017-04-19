package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

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
                .created(new Date().getTime())
                .createdBy("tstephen")
                .lastUpdated(new Date().getTime());

        CarbonFactor factor2 = new CarbonFactor()
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .value(new BigDecimal("0.46673"))
                .applicablePeriod("2007-08")
                .scope("2");
        
        assertEquals(factor1, factor2);
        assertTrue(Collections.singletonList(factor1).contains(factor2));
    }

}
