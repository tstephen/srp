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
package digital.srp.sreport.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.surveys.SurveyFactory;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;

@Service
public class SurveyService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyService.class);

    @Autowired
    protected final QuestionService questionSvc;

    @Autowired
    protected final QuestionRepository questionRepo;

    @Autowired
    protected final SurveyRepository surveyRepo;

    @Autowired
    protected final SurveyCategoryRepository catRepo;

    public SurveyService(final QuestionService questionSvc,
            final QuestionRepository questionRepo,
            final SurveyRepository surveyRepo,
            final SurveyCategoryRepository catRepo) {
        this.questionSvc = questionSvc;
        this.questionRepo = questionRepo;
        this.catRepo = catRepo;
        this.surveyRepo = surveyRepo;
    }

    public void initSurvey(Survey expected) {
        Survey survey = surveyRepo.findByName(expected.name());
        if (survey == null) {
            LOGGER.warn("Could not find expected survey {}, attempt to create",
                    expected);
            expected = surveyRepo.save(expected);
        } else {
            LOGGER.debug("Expected survey {} found, checking categories",
                    expected);
            for (SurveyCategory cat : expected.categories()) {
                Optional<SurveyCategory> existingCat = catRepo
                        .findBySurveyAndCategory(expected.name(), cat.name());
                if (existingCat.isPresent()) {
                    catRepo.save(
                            existingCat.get().questionEnums(cat.questionEnums()));
                } else {
                    catRepo.save(cat.survey(survey));
                }
            }
        }
        questionSvc.initQuestions();
        List<String> missingQs = new ArrayList<String>();
        for (SurveyCategory cat : expected.categories()) {
            for (String qName : cat.getQuestionNames().split(",")) {
                Optional<Question> q = questionRepo.findByName(qName);
                if (!q.isPresent()) {
                    LOGGER.error("Need to init question: '{}' for '{}'", qName, expected.name());
                    missingQs.add(qName);
                } else {
                    cat.questions().add(q.get());
                }
            }
        }
        if (missingQs.size() > 0) {
            throw new IllegalStateException(String.format(
                    "Need to init question(s): '%1$s'", missingQs.toString()));
        }
    }

    public void initSurveys() {
        Survey[] expectedSurveys = SurveyFactory.getExpectedSurveys();
        for (Survey expected : expectedSurveys) {
            initSurvey(expected);
        }
    }
}
