package digital.srp.sreport.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SurveyReturnControllerTest {

    private SurveyReturnController svc = new SurveyReturnController();

    @Test
    public void testInferPeriodFromName() {
        assertEquals("2016-17", svc.applicablePeriod("SDU-2016-17"));
        assertEquals("2015-16", svc.applicablePeriod("ERIC-2015-16"));
    }

}
