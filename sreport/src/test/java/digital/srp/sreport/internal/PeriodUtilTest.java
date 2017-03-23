package digital.srp.sreport.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PeriodUtilTest {

    @Test
    public void testPrevious() {
        assertEquals("2015-16", PeriodUtil.previous("2016-17"));
    }

    @Test
    public void testNext() {
        assertEquals("2017-18", PeriodUtil.next("2016-17"));
    }
}
