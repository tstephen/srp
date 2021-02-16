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
package digital.srp.sreport.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import digital.srp.sreport.mocks.MockAnswerRepository;
import digital.srp.sreport.mocks.MockQuestionRepository;
import digital.srp.sreport.mocks.MockSurveyCategoryRepository;
import digital.srp.sreport.mocks.MockSurveyRepository;
import digital.srp.sreport.mocks.MockSurveyReturnRepository;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Sdu1718;

public class SurveyReturnControllerTest {
    private static final String ORG = "ZZ1";
    private SurveyReturnController svc;
    private AnswerController answerSvc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new SurveyReturnController();
        svc.answerRepo = new MockAnswerRepository();
        svc.qRepo = new MockQuestionRepository();
        svc.returnRepo = new MockSurveyReturnRepository();
        svc.surveyRepo = new MockSurveyRepository();

        answerSvc = new AnswerController();
        answerSvc.answerRepo = svc.answerRepo;
        answerSvc.catRepo = new MockSurveyCategoryRepository();
        answerSvc.qRepo = svc.qRepo;
        answerSvc.rtnRepo = svc.returnRepo;
    }

    @Test
    public void testInferPeriodFromName() {
        assertEquals("2016-17", svc.applicablePeriod("SDU-2016-17"));
        assertEquals("2015-16", svc.applicablePeriod("ERIC-2015-16"));
    }

    @Test
    public void testLifecycle() {
        // prep
        Question question = createSingleQuestion();
        Survey survey = getMinimalSurvey(Sdu1718.PERIOD, question);
        svc.surveyRepo.save(survey);

        // test initialisation
        EntityModel<SurveyReturn> rtnCreated = svc.findCurrentEntityBySurveyNameAndOrg(survey.name(), ORG);
        assertEquals(1, rtnCreated.getContent().answers().size());
        assertEquals(survey.applicablePeriod(),
                rtnCreated.getContent()
                        .answer(survey.applicablePeriod(), question.q())
                        .get().applicablePeriod());
        assertEquals("/returns/1", rtnCreated.getLinks().getLink("self").get().getHref());
        assertEquals(1, svc.list(1, 10).size());

        // update and check save
        svc.updateAnswer(rtnCreated.getContent().id(), question.name(), survey.applicablePeriod(), "100");
        EntityModel<SurveyReturn> rtnFound = svc.findById(rtnCreated.getContent().id());
        assertEquals(1, svc.list(1, 10).size());

        // submit
        rtnFound = svc.setStatus(rtnCreated.getContent().id(), StatusType.Submitted.name(), "fred@bedrock.com");
        assertEquals(StatusType.Submitted.name(), rtnFound.getContent().status());
        assertEquals(StatusType.Submitted.name(),
                rtnFound.getContent().answer(survey.applicablePeriod(), question.q()).get().status());
        assertEquals(1, svc.list(1, 10).size());

        // publish
        rtnFound = svc.setStatus(rtnCreated.getContent().id(), StatusType.Published.name(), "fred@bedrock.com");
        assertEquals(StatusType.Published.name(), rtnFound.getContent().status());
        assertEquals(StatusType.Published.name(),
                rtnFound.getContent().answer(survey.applicablePeriod(), question.q()).get().status());
        assertEquals(1, svc.list(1, 10).size());

        // delete
        svc.delete(rtnCreated.getContent().id());
        assertEquals(0, svc.list(1, 10).size());
    }

    private Question createSingleQuestion() {
        return new Question().q(Q.ELEC_USED);
    }

    Survey getMinimalSurvey(String period, Question question) {
        return new Survey().name("SDU-" + period)
                .applicablePeriod(period)
                .categories(new SurveyCategory().questionNames(question.name()));
    }
}
