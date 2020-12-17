package digital.srp.sreport.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testNaturalToConst() {
        assertEquals("COAL_INDUSTRIAL", StringUtils.toConst("Coal (industrial)"));
        assertEquals("ELECTRICITY_UK", StringUtils.toConst("Electricity: UK"));
        assertEquals("GAS_FIRED_CHP", StringUtils.toConst("Gas-fired CHP"));
        assertEquals("NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT", StringUtils.toConst("Nitrous oxide with oxygen 50/50 split"));
        assertEquals("WEEE_LARGE", StringUtils.toConst("WEEE - large"));
        assertEquals("_5_LOSS", StringUtils.toConst("5% loss"));
        assertEquals("HIGH_TEMPERATURE_DISPOSAL_WASTE", StringUtils.toConst("High Temperature Disposal Waste"));
    }

    @Test
    public void testCamelCaseToConst() {
        assertEquals("FLOOR_AREA", StringUtils.camelCaseToConst("floorArea"));
    }
}
