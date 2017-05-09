package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import digital.srp.sreport.model.returns.EricQuestions;

public class SurveyReturnTest implements EricQuestions{
    
    @Test
    public void testBusinessDataEquality() {
        Question q1 = new Question().q(Q.ORG_NAME).name("orgName").label(ORG_NAME).required(true);
        SurveyCategory catOrg = new SurveyCategory()
                .id(1l)
                .name("Organisation")
                .questionCodes(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg.questionCodes().size());

        SurveyCategory catPolicy = new SurveyCategory()
                .id(2l)
                .name("Policy")
                .questionCodes(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy.questionCodes().size());
        
        Survey survey1 = new Survey().id(1l).applicablePeriod("2016-17")
                .categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey1.categories().size());
        assertEquals(2, survey1.categories().get(0).questionCodes().size());
        assertEquals(2, survey1.categories().get(1).questionCodes().size());
        
        SurveyCategory catOrg2 = new SurveyCategory()
                .id(2l)
                .name("Organisation")
                .questionCodes(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg2.questionCodes().size());

        SurveyCategory catPolicy2 = new SurveyCategory()
                .id(3l)
                .name("Policy")
                .questionCodes(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy2.questionCodes().size());
        
        Survey survey2 = new Survey().id(2l).applicablePeriod("2016-17")
                .categories(Arrays.asList(catOrg2, catPolicy2));
        assertEquals(2, survey2.categories().size());
        assertEquals(2, survey2.categories().get(0).questionCodes().size());
        assertEquals(2, survey2.categories().get(1).questionCodes().size());
  
        assertEquals(survey1, survey2);
        
        SurveyReturn return1 = new SurveyReturn().id(1l).name("15-16 return for ACME")
                .answers(Arrays.asList(
                        new Answer().id(1l).question(q1).response("response")
                )).revision(new Short("1"));
        SurveyReturn return2 = new SurveyReturn().id(2l).name("15-16 return for ACME")
                .answers(Arrays.asList(
                        new Answer().id(1l).question(q1).response("response")
                )).revision(new Short("2"));
        assertEquals(return1, return2);
    }

}
