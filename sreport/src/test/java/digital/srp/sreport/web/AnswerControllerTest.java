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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import digital.srp.sreport.mocks.MockAnswerRepository;
import digital.srp.sreport.mocks.MockQuestionRepository;
import digital.srp.sreport.mocks.MockSurveyCategoryRepository;
import digital.srp.sreport.mocks.MockSurveyReturnRepository;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Sdu1920;

public class AnswerControllerTest {
    private AnswerController svc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new AnswerController();
        svc.answerRepo = new MockAnswerRepository();
        svc.catRepo = new MockSurveyCategoryRepository();
        svc.qRepo = new MockQuestionRepository();
        svc.rtnRepo = new MockSurveyReturnRepository();
    }

    @Test
    public void testLifecycle() {
        // prep
        Question question = createSingleQuestion();
        Survey survey = getMinimalSurvey(Sdu1920.PERIOD, question);
        SurveyReturn rtn = createMinimalReturn(survey);
        Answer answer = createSingleAnswer(rtn, question);
        svc.answerRepo.save(answer);
        svc.rtnRepo.save(rtn.answers(Collections.singleton(answer)));
        assertEquals(1, svc.rtnRepo.findAll().size());
        assertEquals(1, svc.rtnRepo.findAll().get(0).answers().size());
        assertEquals("123.45", svc.rtnRepo.findAll().get(0).answers().stream().findFirst().get().response());

        // list all
        List<EntityModel<Answer>> answers = svc.list(1, 10);
        assertEquals(1, answers.size());

        // findByReturnPeriodAndQ
        EntityModel<Answer> answerFound = svc.findByReturnPeriodAndQ(rtn.id(), survey.applicablePeriod(), question.q().name());
        assertEquals(answer.response(), answerFound.getContent().response());
        assertEquals("/answers/1", answerFound.getLinks().getLink("self").get().getHref());

        // find by id
        answerFound = svc.findById(answer.id());
        assertEquals(answer.response(), answerFound.getContent().response());
        assertEquals("/answers/1", answerFound.getLinks().getLink("self").get().getHref());

        // find by period
        List<EntityModel<Answer>> answersFound = svc.findByPeriod(survey.applicablePeriod(), 1, 10);
        assertEquals(answer.response(), answersFound.get(0).getContent().response());
        assertEquals("/answers/1", answersFound.get(0).getLinks().getLink("self").get().getHref());
    }

    private Question createSingleQuestion() {
        return new Question().q(Q.ELEC_USED);
    }

    private Survey getMinimalSurvey(String period, Question question) {
        return new Survey().name("SDU-" + period)
                .applicablePeriod(period)
                .categories(new SurveyCategory().questionNames(question.name()));
    }

    private SurveyReturn createMinimalReturn(Survey survey) {
        return new SurveyReturn().id(1l)
                .survey(survey)
                .applicablePeriod(survey.applicablePeriod());
    }

    private Answer createSingleAnswer(SurveyReturn rtn, Question question) {
        return new Answer().id(1l)
                .surveyReturns(Collections.singleton(rtn))
                .applicablePeriod(rtn.applicablePeriod())
                .question(question.q())
                .response(new BigDecimal("123.45"));
    }
}
