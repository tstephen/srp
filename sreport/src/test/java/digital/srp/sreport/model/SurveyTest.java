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
                .id(1)
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().id(1).name("orgName").label(ORG_NAME).required(true),
                        new SurveyQuestion().id(2).name("orgCode").label(ORG_CODE).required(true)
                ));
        assertEquals(2, catOrg.questions().size());

        SurveyCategory catPolicy = new SurveyCategory()
                .id(2)
                .name("Policy")
                .questions(Arrays.asList(
                        new SurveyQuestion().id(3).name("sdmpCrmp").label(SDMP_CRMP).required(true),
                        new SurveyQuestion().id(4).name("adaptationPlan").label(BOARD_ADAPTATION_PLAN).required(false)
                ));
        assertEquals(2, catPolicy.questions().size());
        
        Survey survey1 = new Survey().id(1l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey1.categories().size());
        assertEquals(2, survey1.categories().get(0).questions().size());
        assertEquals(2, survey1.categories().get(1).questions().size());
        
        SurveyCategory catOrg2 = new SurveyCategory()
                .id(2)
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().id(2).name("orgName").label(ORG_NAME).required(true),
                        new SurveyQuestion().id(3).name("orgCode").label(ORG_CODE).required(true)
                ));
        assertEquals(2, catOrg2.questions().size());

        SurveyCategory catPolicy2 = new SurveyCategory()
                .id(3)
                .name("Policy")
                .questions(Arrays.asList(
                        new SurveyQuestion().id(7).name("sdmpCrmp").label(SDMP_CRMP).required(true),
                        new SurveyQuestion().id(8).name("adaptationPlan").label(BOARD_ADAPTATION_PLAN).required(false)
                ));
        assertEquals(2, catPolicy2.questions().size());
        
        Survey survey2 = new Survey().id(2l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg2, catPolicy2));
        assertEquals(2, survey2.categories().size());
        assertEquals(2, survey2.categories().get(0).questions().size());
        assertEquals(2, survey2.categories().get(1).questions().size());
  
        assertEquals(survey1, survey2);
    }

}
