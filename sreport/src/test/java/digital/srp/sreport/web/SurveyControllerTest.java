package digital.srp.sreport.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import digital.srp.sreport.Application;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.surveys.Eric1516;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SurveyControllerTest {

    @Autowired
    private SurveyController svc;
    
    @Test
    public void givenNoPreq_whenFirstLoad_thenDefaultSurveys() {
        List<Survey> surveys = svc.list(null, null);
        assertNotNull(surveys);
        assertEquals(2, surveys.size()); // current ERIC and current SDU
        
        // check have expected summary
        assertEquals(Eric1516.ID, surveys.get(0).name());
        assertEquals(StatusType.Published.name(), surveys.get(0).status());
        assertEquals("2015-16", surveys.get(0).applicablePeriod());
        assertNotNull(surveys.get(0).created());
        assertNotNull(surveys.get(0).lastUpdated());
        try {
            System.out.println(surveys.get(0).categories().size());
            fail("Should not have categories in summary list");
        } catch (Exception e) {
            ; // expected
        }
        
        Survey eric1516 = svc.findById(surveys.get(0).id());
        assertNotNull(eric1516);
        assertEquals(Eric1516.ID, eric1516.name());
        assertEquals(StatusType.Published.name(), eric1516.status());
        assertEquals("2015-16", eric1516.applicablePeriod());
        assertNotNull(eric1516.created());
        assertNotNull(eric1516.lastUpdated());
        assertEquals(7, eric1516.categories().size());
    }

}
