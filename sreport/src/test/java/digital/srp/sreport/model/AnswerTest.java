package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

public class AnswerTest {

    @Test
    public void testGetEmptyResponseAsBigDecimal() {
        assertEquals(BigDecimal.ZERO, new Answer().responseAsBigDecimal());
    }
    
    @Test
    public void testGetResponseAsBigDecimal() {
        assertEquals(new BigDecimal("1.23"), new Answer().response("1.23").responseAsBigDecimal());
    }

    @Test
    public void testGetEmptyResponseAsString() {
        assertNull(new Answer().response());
    }
    
    @Test
    public void testGetResponseAsString() {
        assertEquals("Foo", new Answer().response("Foo").response());
    }
}
