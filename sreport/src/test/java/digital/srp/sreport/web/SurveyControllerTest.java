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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import digital.srp.sreport.mocks.MockQuestionRepository;
import digital.srp.sreport.mocks.MockSurveyCategoryRepository;
import digital.srp.sreport.mocks.MockSurveyRepository;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.surveys.Sdu1920;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.services.QuestionService;
import digital.srp.sreport.services.SurveyService;

public class SurveyControllerTest {
    private static SurveyController svc;

    @BeforeAll
    public static void setUpClass() throws IOException {
        svc = new SurveyController();
        QuestionRepository questionRepo = new MockQuestionRepository();
        svc.questionRepo = questionRepo;
        QuestionService questionSvc = new QuestionService(questionRepo);
        questionSvc.initQuestions();
        SurveyRepository surveyRepo = new MockSurveyRepository();
        SurveyCategoryRepository catRepo = new MockSurveyCategoryRepository();
        SurveyService surveySvc = new SurveyService(questionSvc, questionRepo, surveyRepo, catRepo);
        svc.surveySvc = surveySvc;
        svc.surveyRepo = surveyRepo;
    }

    @Test
    public void testLifecycle() {

        // test initialisation
        assertEquals(0, svc.list(1, 10).size());
        svc.init();
        List<EntityModel<Survey>> surveys = svc.list(1, 10);
        assertEquals(6, surveys.size());
        assertTrue(surveys.stream().allMatch(
                (s) -> StatusType.Published.name().equals(s.getContent().status())));
        assertEquals("/surveys/1", surveys.get(0).getLinks().getLink("self").get().getHref());

        // check finders
        EntityModel<Survey> survey = svc.findByName(Sdu1920.ID);
        assertEquals(18, survey.getContent().categories().size());
        assertEquals("/surveys/5", survey.getLinks().getLink("self").get().getHref());

        survey = svc.findById(survey.getContent().id());
        assertEquals("/surveys/5", survey.getLinks().getLink("self").get().getHref());

        // delete
        surveys.stream().forEach((s) -> svc.delete(s.getContent().id()));
        assertEquals(0, svc.list(1, 10).size());
    }
}
