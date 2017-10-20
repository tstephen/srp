package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.sreport.api.CompletenessValidator;
import digital.srp.sreport.internal.ClasspathSurveyReturnHelper;
import digital.srp.sreport.model.returns.EricQuestions;
import digital.srp.sreport.services.DefaultCompletenessValidator;

public class SurveyReturnTest implements EricQuestions{

    private static final String TEST_DATA_CODE = "RD1";
    private static final String TEST_DATA_NAME = "RUH";
    private static final String TEST_DATA_PERIOD = "2016-17";

    private static ClasspathSurveyReturnHelper helper;
    private static CompletenessValidator validator;

    @BeforeClass
    public static void setUpClass() throws IOException {
        helper = new ClasspathSurveyReturnHelper();
        validator = new DefaultCompletenessValidator();
    }

    @Test
    public void testAnswerUniqueForOrgPeriodAndQuestion() {
        SurveyReturn rtn = new SurveyReturn(TEST_DATA_NAME, TEST_DATA_CODE,
                StatusType.Draft.name(), TEST_DATA_PERIOD, (short) 1);
        Answer answer1 = new Answer().applicablePeriod(TEST_DATA_PERIOD)
                .question(Q.ORG_CODE).response(TEST_DATA_CODE);
        Answer answer2 = new Answer().applicablePeriod(TEST_DATA_PERIOD)
                .question(Q.ORG_CODE).response(TEST_DATA_CODE);
        rtn.answers().add(answer1);
        rtn.answers().add(answer2);
        assertEquals(1, rtn.answers().size());
        rtn.answers().add(answer2);
        assertEquals(1, rtn.answers().size());
        Answer answer3 = new Answer().applicablePeriod(TEST_DATA_PERIOD)
                .question(Q.ORG_NAME).response(TEST_DATA_NAME);
        rtn.answers().add(answer3);
        assertEquals(2, rtn.answers().size());
    }


    @Test
    public void testBusinessDataEquality() {
        Question q1 = new Question().q(Q.ORG_NAME).name("orgName").label(ORG_NAME).required(true);
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

        Survey survey1 = new Survey().id(1l).applicablePeriod(TEST_DATA_PERIOD)
                .categories(Arrays.asList(catOrg, catPolicy));
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

        Survey survey2 = new Survey().id(2l).applicablePeriod(TEST_DATA_PERIOD)
                .categories(Arrays.asList(catOrg2, catPolicy2));
        assertEquals(2, survey2.categories().size());
        assertEquals(2, survey2.categories().get(0).questionEnums().size());
        assertEquals(2, survey2.categories().get(1).questionEnums().size());

        assertEquals(survey1, survey2);

        SurveyReturn return1 = new SurveyReturn().id(1l).name("15-16 return for ACME")
                .answers(Collections.singleton(
                        new Answer().id(1l).question(q1).response("response")
                )).revision(new Short("1"));
        SurveyReturn return2 = new SurveyReturn().id(2l).name("15-16 return for ACME")
                .answers(Collections.singleton(
                        new Answer().id(1l).question(q1).response("response")
                )).revision(new Short("2"));
        assertEquals(return1, return2);
    }

    @Test
    public void testComplete() {
        SurveyReturn rdr = helper.readSurveyReturn("RDR");
        validator.validate(rdr);
        assertEquals(0, rdr.completeness().size());
    }

    @Test
    public void testIncomplete() {
        SurveyReturn rj1 = helper.readSurveyReturn("RJ1");
        validator.validate(rj1);
        System.out.println("Completeness: "+rj1.completeness());
        assertEquals(1, rj1.completeness().size());
        assertTrue(rj1.completeness().contains("Insufficient periods specified"));
    }

}
