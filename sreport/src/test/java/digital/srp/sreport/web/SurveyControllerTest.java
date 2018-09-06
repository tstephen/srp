package digital.srp.sreport.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import digital.srp.sreport.Application;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.surveys.Eric1516;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class SurveyControllerTest {

    @Autowired
    private SurveyController svc;

    @Test
    @Ignore
    public void givenNoPreq_whenCrud_thenOk() {

    }

    @Test
    public void givenNoPreq_whenFirstLoad_thenDefaultSurveys() {
        List<Survey> surveys = svc.list(null, null);
        assertNotNull(surveys);
        assertEquals(6, surveys.size()); // 4 current ERIC and 2 current SDU
        
        // check have expected summary
        Survey eric1516 = surveys.get(2);
        assertEquals(Eric1516.ID, eric1516.name());
        assertEquals(StatusType.Published.name(), eric1516.status());
        assertEquals("2015-16", eric1516.applicablePeriod());
        assertNotNull(eric1516.created());
        assertNotNull(eric1516.lastUpdated());
        try {
            System.out.println(eric1516.categories().size());
            fail("Should not have categories in summary list");
        } catch (Exception e) {
            ; // expected
        }
        
        eric1516 = svc.findById(eric1516.id());
        assertNotNull(eric1516);
        assertEquals(Eric1516.ID, eric1516.name());
        assertEquals(StatusType.Published.name(), eric1516.status());
        assertEquals("2015-16", eric1516.applicablePeriod());
        assertNotNull(eric1516.created());
        assertNotNull(eric1516.lastUpdated());
        assertEquals(7, eric1516.categories().size());
    }

}
