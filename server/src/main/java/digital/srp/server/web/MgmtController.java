package digital.srp.server.web;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import digital.srp.sreport.importers.EricCsvImporter;
import digital.srp.sreport.importers.QuestionCsvImporter;
import digital.srp.sreport.internal.SReportException;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.Eric1516;
import digital.srp.sreport.model.surveys.Sdu1617;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import link.omny.custmgmt.repositories.AccountRepository;

/**
 * REST web service for managing application data.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/admin/data-mgmt")
public class MgmtController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MgmtController.class);

    @Autowired
    protected QuestionRepository qRepo;
    
    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    @Autowired
    protected AnswerRepository answerRepo;
    
    @Autowired
    protected SurveyReturnRepository returnRepo;
    
    @Autowired
    protected AccountRepository accountRepo;
    
    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    public String initAndShowStatus(Model model) throws IOException {
        LOGGER.info(String.format("initAndShowStatus"));

        initSurveys();
        initReturns();
        
        model.addAttribute("questions", qRepo.findAll());
        model.addAttribute("surveys", surveyRepo.findAll());
        model.addAttribute("answers", answerRepo.findAll());
        model.addAttribute("returns", returnRepo.findAll());
        model.addAttribute("orgs", accountRepo.findAllForTenant("sdu"));
        
        return "jsonStatus";
    }
    
    protected void initSurveys() throws IOException {
        List<Question> questions = new QuestionCsvImporter().readQuestions();
        List<Question> existingQuestions = qRepo.findAll();
        for (Question question : questions) {
            if (findMatchingQ(question, existingQuestions) == null) {
                qRepo.save(question);
            }
        }
        
        Survey[] expectedSurveys = { digital.srp.sreport.model.surveys.Eric1516.getSurvey(), Sdu1617.getSurvey() };
        for (Survey expected : expectedSurveys) {
            Survey survey = surveyRepo.findByName(expected.name());
            if (survey == null) {
                LOGGER.warn(String.format(
                        "Could not find expected survey %1$s, attempt to create",
                        expected));
                surveyRepo.save(expected);
            } else {
                LOGGER.debug(String.format("Expected survey %1$s found, checking categories", expected));
                for (SurveyCategory cat : expected.categories()) {
                    SurveyCategory existingCat = catRepo.findBySurveyAndCategory(expected.name(), cat.name());
                    if (existingCat == null) { 
                        catRepo.save(cat.survey(survey));
                    } else {
                        catRepo.save(existingCat.questionCodes(cat.questionCodes()));
                    }
                }
            }
        }
    }
    
    private Question findMatchingQ(Question question, List<Question> existingQuestions) {
        for (Question q : existingQuestions) {
            if (q.name().equals(question.name())) {
                return q;
            }
        }
        return null;
    }
    
    protected void initReturns() {
        Survey survey = surveyRepo.findByName(digital.srp.sreport.model.surveys.Eric1516.ID);
        LOGGER.debug("Found survey definition {} ({} containing {} questions", survey.name(), survey.id(), survey.questionCodes().size());
        List<SurveyReturn> existingReturns = returnRepo.findBySurveyName(digital.srp.sreport.model.surveys.Eric1516.ID);
        LOGGER.debug(" ... {} existing returns", existingReturns.size());
        
        try {
            List<SurveyReturn> returns = new EricCsvImporter().readEricReturns();
            LOGGER.info("Found {} {} returns to import...", returns.size(), survey.name());
            for (SurveyReturn surveyReturn : returns) {
                surveyReturn.survey(survey);
                
                if (!findBySurveyAndOrg(existingReturns, surveyReturn)) {
                    // merge persisted questions to answers
                    for (Answer answer: surveyReturn.answers()) {
                        Question q = qRepo.findByName(answer.question().name());
                        if (q == null) {
                            System.out.println("WTF");
                        }
                        answer.question(q);
                    }
                    answerRepo.save(surveyReturn.answers());
                    for (Answer answer : surveyReturn.answers()) {
                        answer.addSurveyReturn(surveyReturn);
                    }
                    returnRepo.save(surveyReturn);
                }
            }
        } catch (Throwable e) {
            String msg = String.format("Unable to load ERIC data from %1$s", Eric1516.DATA_FILE);
            LOGGER.error(msg, e);
            throw new SReportException(msg, e);
        }
    }
    
    private boolean findBySurveyAndOrg(List<SurveyReturn> existingReturns,
            SurveyReturn surveyReturn) {
        for (SurveyReturn ret : existingReturns) {
            if (ret.name().equals(surveyReturn.name()) 
                    && ret.org().equals(surveyReturn.org())) {
                LOGGER.info(String.format("Skipping insert of return for %1$s because %2$d matches name and org", surveyReturn.name(), ret.id()));
                return true;
            }
        }
        return false;
    }

}
