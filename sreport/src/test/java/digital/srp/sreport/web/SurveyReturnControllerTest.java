package digital.srp.sreport.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import digital.srp.sreport.model.SurveyAnswer;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Eric1516;
import digital.srp.sreport.model.surveys.Sdu1516;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SurveyReturnControllerTest {

    private static final String[] ANSWERS = {
            "WORCESTERSHIRE HEALTH AND CARE NHS TRUST", "R1A", "COMMUNITY", "MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION",
            "0","0","1","5","1","0","4","1","27","2","46",
            "Yes","No","Yes","Yes","2. Target included but not on track to be met","3. Assessed but not approved by the organisation's board","3. Action plan produced but not approved by the organisations board",
            "2","864","738","28.33","0","3","106","612","503","156","0","2",
            "234","190","1","531","511","0","0","2","408","0",
            "8","11","0",
            "15","24","0","0","0"
    };
    
    @Autowired
    private SurveyController surveyController;
    
    @Autowired
    private SurveyReturnController svc;

    @Test
    public void givenNoPreq_whenFirstLoad_thenListEricReturns() {
        List<Survey> surveys = surveyController.list(null, null);
        assertNotNull(surveys);
        assertEquals(Eric1516.ID, surveys.get(0).name());
        
        List<SurveyReturn> returns = surveyController.listReturns(
                surveys.get(0).id(), null, null);
        assertNotNull(returns);
        assertEquals(239, returns.size()); // ERIC 15-16 returns imported
    }
    
    @Test
    public void testLifecycle() {
        // list SDU yields 0
        List<SurveyReturn> returns = svc.findByOrg("RDR");
        assertNotNull(returns);
        assertEquals(1, returns.size());
        assertEquals(Eric1516.ID, returns.get(0).survey().name());
        
        // GET should create new empty record because none found
        SurveyReturn rtn = svc.findCurrentBySurveyAndOrg(Sdu1516.ID, "RDR");
        assertNotNull(rtn);
        assertEquals(StatusType.Draft.name(), rtn.status());
        assertEquals(Short.valueOf("1"), rtn.revision());
        for (SurveyAnswer a : rtn.answers()) {
            assertNull(a.response());
            a.response(ANSWERS[rtn.answers().indexOf(a)]);
        }
        
        // UPDATE
        svc.update(rtn.id(), rtn);
        SurveyReturn rtn2 = svc.findCurrentBySurveyAndOrg(Sdu1516.ID, "RDR");
        for (SurveyAnswer a : rtn2.answers()) {
            assertEquals(ANSWERS[rtn.answers().indexOf(a)], a.response());
            a.response("REVISED"+ANSWERS[rtn.answers().indexOf(a)]);
        }
        
        // SUBMIT
        svc.setStatus(rtn.id(), StatusType.Submitted.name());
        SurveyReturn rtn3 = svc.findCurrentBySurveyAndOrg(Sdu1516.ID, "RDR");
        assertEquals(StatusType.Submitted.name(), rtn3.status());
        
        // SUBMIT REVISED
        try {
            svc.update(rtn2.id(), rtn2);
            fail("Should not be able to update a submitted return");
        } catch (IllegalStateException e) {
            ; // expected
        }
        svc.restate(rtn2.id(), rtn2);
        SurveyReturn rtn4 = svc.findCurrentBySurveyAndOrg(Sdu1516.ID, "RDR");
        assertNotEquals(rtn.id(), rtn4.id());
        assertEquals(StatusType.Draft.name(), rtn4.status());
        assertEquals(Short.valueOf("2"), rtn4.revision());
        
        List<SurveyReturn> returns2 = svc.findBySurveyAndOrg(Sdu1516.ID, "RDR");
        assertEquals(2, returns2.size());
    }

}
