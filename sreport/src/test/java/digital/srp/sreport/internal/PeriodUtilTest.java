package digital.srp.sreport.internal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
public class PeriodUtilTest {

    @Test
    public void testPrevious() {
        assertEquals("2015-16", PeriodUtil.previous("2016-17"));
    }

    @Test
    public void testNext() {
        assertEquals("2017-18", PeriodUtil.next("2016-17"));
    }

    @Test
    public void testFillBackwards() {
        String[] expected = { "2016-17", "2015-16" };
        assertEquals(Arrays.asList(expected),
                PeriodUtil.fillBackwards("2016-17", 2));
    }

    @Test
    public void testPeriodsSince2007Inclusive() {
        assertEquals(11, PeriodUtil.periodsSinceInc("2017-18", "2007-08"));
    }

    @Test
    public void test2017Before2018() {
        assertTrue(PeriodUtil.before("2017-18", "2018-19"));
    }

    @Test
    public void test2017Equals2017() {
        assertTrue(PeriodUtil.equals("2017-18", "2017-18"));
    }

    @Test
    public void test2018After2017() {
        assertTrue(PeriodUtil.after("2018-19", "2017-18"));
    }
}
