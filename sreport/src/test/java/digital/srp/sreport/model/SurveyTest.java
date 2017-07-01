package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import digital.srp.sreport.model.returns.EricQuestions;

public class SurveyTest implements EricQuestions{
    
    /**
     * 
     */
    @Test
    public void testBusinessDataEquality() {
        SurveyCategory catOrg = new SurveyCategory()
                .id(1l)
                .name("Organisation")
                .questionEnums(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg.questionEnums().size());

        SurveyCategory catPolicy = new SurveyCategory()
                .id(2l)
                .name("Policy")
                .questionEnums(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy.questionEnums().size());
        
        Survey survey1 = new Survey().id(1l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey1.categories().size());
        assertEquals(2, survey1.categories().get(0).questionEnums().size());
        assertEquals(2, survey1.categories().get(1).questionEnums().size());
        
        SurveyCategory catOrg2 = new SurveyCategory()
                .id(2l)
                .name("Organisation")
                .questionEnums(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg2.questionEnums().size());

        SurveyCategory catPolicy2 = new SurveyCategory()
                .id(3l)
                .name("Policy")
                .questionEnums(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy2.questionEnums().size());
        
        Survey survey2 = new Survey().id(2l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg2, catPolicy2));
        assertEquals(2, survey2.categories().size());
        assertEquals(2, survey2.categories().get(0).questionEnums().size());
        assertEquals(2, survey2.categories().get(1).questionEnums().size());
  
        assertEquals(survey1, survey2);
    }

}
