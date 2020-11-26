package digital.srp.sreport.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;

@Service
public class SurveyService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyService.class);

    @Autowired
    protected QuestionService questionSvc;

    @Autowired
    protected QuestionRepository questionRepo;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    public void initSurvey(final Survey expected) throws IOException {
        Survey survey = surveyRepo.findByName(expected.name());
        if (survey == null) {
            LOGGER.warn(String.format(
                    "Could not find expected survey %1$s, attempt to create",
                    expected));
            surveyRepo.save(expected);
        } else {
            LOGGER.debug("Expected survey {} found, checking categories",
                    expected);
            for (SurveyCategory cat : expected.categories()) {
                SurveyCategory existingCat = catRepo
                        .findBySurveyAndCategory(expected.name(), cat.name());
                if (existingCat == null) {
                    catRepo.save(cat.survey(survey));
                } else {
                    catRepo.save(
                            existingCat.questionEnums(cat.questionEnums()));
                }
            }
        }
        questionSvc.initQuestions();
        List<String> missingQs = new ArrayList<String>();
        for (SurveyCategory cat : expected.categories()) {
            for (String qName : cat.getQuestionNames().split(",")) {
                Question q = questionRepo.findByName(qName);
                if (q == null) {
                    LOGGER.error("Need to init question: '{}'", qName);
                    missingQs.add(qName);
                }
                cat.questions().add(q);
            }
        }
        if (missingQs.size() > 0) {
            throw new IllegalStateException(String.format(
                    "Need to init question(s): '%1$s'", missingQs.toString()));
        }
    }
}
