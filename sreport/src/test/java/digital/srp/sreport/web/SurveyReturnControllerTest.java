package digital.srp.sreport.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
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
import digital.srp.sreport.model.surveys.Sdu1617;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SurveyReturnControllerTest {

    private static final Sdu1617 SDU1617 = new Sdu1617();

    private static final String ORG_CODE = "R1A";

    // NOTE this is not representative data
    private static final String[] ANSWERS = {
            "ACME NHS TRUST", ORG_CODE, "COMMUNITY", "MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION",
            "0","0","1","5","1","0","4","1","27","2","46",
            "Yes","No","Yes","Yes","2. Target included but not on track to be met","3. Assessed but not approved by the organisation's board","3. Action plan produced but not approved by the organisations board",
            "2","864","738","28.33","0","3","106","612","503","156","0","2",
            "234","190","1","531","511","0","0","2","408","0",
            "8","11","0",
            "15","24","0","0","0",
            "0","0","1","5","1","0","4","1","27","2","46",
            "Yes","No","Yes","Yes","2. Target included but not on track to be met","3. Assessed but not approved by the organisation's board","3. Action plan produced but not approved by the organisations board",
            "2","864","738","28.33","0","3","106","612","503","156","0","2",
            "234","190","1","531","511","0","0","2","408","0",
            "8","11","0",
            "15","24","0","0","0",
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

    @After
    public void tearDown() { 
        SurveyReturn rtn = svc.findCurrentBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        svc.delete(rtn.id());
    }
    
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
        List<SurveyReturn> returns = svc.findByOrg(ORG_CODE);
        assertNotNull(returns);
        assertEquals(1, returns.size());
        assertEquals(Eric1516.ID, returns.get(0).survey().name());
        
        // GET should create new empty record because none found
        SurveyReturn rtn = svc.findCurrentBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        assertNotNull(rtn);
        assertEquals(StatusType.Draft.name(), rtn.status());
        assertEquals(Short.valueOf("1"), rtn.revision());
        assertEquals(SDU1617.getSurvey().questions().size(), rtn.answers().size());
        assertTrue("Not enough test data", ANSWERS.length > rtn.answers().size());
        for (SurveyAnswer a : rtn.answers()) {
            assertNull(a.response());
            a.response(ANSWERS[rtn.answers().indexOf(a)]);
        }
        
        // UPDATE (save blank return, now with answers)
        svc.update(rtn.id(), rtn);
        SurveyReturn rtn2 = svc.findCurrentBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        ArrayList<SurveyAnswer> revisedAnswers = new ArrayList<SurveyAnswer>();
        for (SurveyAnswer a : rtn2.answers()) {
            assertEquals(ANSWERS[rtn.answers().indexOf(a)], a.response());
            revisedAnswers.add(a.response("REVISED"+ANSWERS[rtn.answers().indexOf(a)]));
        }
        assertEquals(SDU1617.getSurvey().questions().size(), revisedAnswers.size());
        rtn2 = rtn2.answers(revisedAnswers);
        
        // SUBMIT
        svc.setStatus(rtn.id(), StatusType.Submitted.name());
        SurveyReturn rtn3 = svc.findCurrentBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        assertEquals(StatusType.Submitted.name(), rtn3.status());
        assertEquals(SDU1617.getSurvey().questions().size(), rtn3.answers().size());
        
        // SUBMIT REVISED
        try {
            svc.update(rtn2.id(), rtn2);
            fail("Should not be able to update a submitted return");
        } catch (IllegalStateException e) {
            ; // expected
        }
        
        assertEquals(SDU1617.getSurvey().questions().size(), rtn2.answers().size());
        svc.restate(rtn.id(), rtn2);
        SurveyReturn rtn4 = svc.findCurrentBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        assertNotEquals(rtn.id(), rtn4.id());
        assertEquals(StatusType.Draft.name(), rtn4.status());
        assertEquals(Short.valueOf("2"), rtn4.revision());
        
        List<SurveyReturn> returns2 = svc.findBySurveyAndOrg(Sdu1617.ID, ORG_CODE);
        assertEquals(2, returns2.size());
    }

}
