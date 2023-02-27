/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.api.CompletenessValidator;
import digital.srp.sreport.internal.ClasspathSurveyReturnHelper;
import digital.srp.sreport.model.returns.EricQuestions;
import digital.srp.sreport.model.surveys.Sdu1718;
import digital.srp.sreport.services.DefaultCompletenessValidator;
import digital.srp.sreport.services.HealthChecker;
import digital.srp.sreport.services.MemoryAnswerFactory;

public class SurveyReturnTest implements EricQuestions{

    private static final String TEST_DATA_CODE = "RD1";
    private static final String TEST_DATA_NAME = "RUH";
    private static final String TEST_DATA_PERIOD = "2016-17";

    private static ClasspathSurveyReturnHelper helper;
    private static CompletenessValidator completenessValidator;
    private static HealthChecker healthCheck;

    @BeforeAll
    public static void setUpClass() throws IOException {
        helper = new ClasspathSurveyReturnHelper();
        completenessValidator = new DefaultCompletenessValidator();
        healthCheck = new HealthChecker(new MemoryAnswerFactory());
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
                )).revision(Short.valueOf("1"));
        SurveyReturn return2 = new SurveyReturn().id(2l).name("15-16 return for ACME")
                .answers(Collections.singleton(
                        new Answer().id(1l).question(q1).response("response")
                )).revision(Short.valueOf("2"));
        assertEquals(return1, return2);
    }

    @Test
    public void testComplete() {
        SurveyReturn rdr = helper.readSurveyReturn("RDR")
                .orElseThrow(IllegalStateException::new);
        completenessValidator.validate(rdr);
        assertEquals(0, rdr.completeness().size());
    }

    @Test
    public void testDuplicateDetection() {
        SurveyReturn rtn = helper.readSurveyReturn("RDR2")
                .orElseThrow(IllegalStateException::new)
                .survey(Sdu1718.getInstance().getSurvey());

        assertEquals(4, rtn.getIncludedPeriods().size());

        // There are 10 answers in the test data but ORG_CODE 2017-18 is
        // eliminated by the HashSet which does not allow duplicates and hash
        // code does not include id (to give business data equality)
        // (beware if ever tempted to change to List)
        assertEquals(9, rtn.answers().size());

        Set<ConstraintViolation<SurveyReturn>> issues = healthCheck.validate(rtn);
        System.out.println("Issues: ");
        for (ConstraintViolation<SurveyReturn> issue : issues) {
            System.out.println("  "+issue);
        }

        // ELEC used has two different values so that's a problem
        assertEquals(1, issues.size());
        ConstraintViolation<SurveyReturn> issue1 = issues.iterator().next();
        @SuppressWarnings("unchecked")
        Set<Answer> answers = (Set<Answer>) issue1.getLeafBean();
        assertEquals(2, answers.size());
        assertTrue(answers.stream().anyMatch(new AnswerIdPredicate(10785l)));
        assertTrue(answers.stream().anyMatch(new AnswerIdPredicate(10786l)));
    }

    class AnswerIdPredicate implements Predicate<Answer> {
        private final Long _id;
        public AnswerIdPredicate(long id) {
            _id = id;
        }

        public boolean test(Answer a) {
            return a.id().equals(_id);
        }
    }


}
