package digital.srp.sreport.web;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.api.exceptions.SReportException;
import digital.srp.sreport.importers.EricCsvImporter;
import digital.srp.sreport.importers.QuestionCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.EricDataSet;
import digital.srp.sreport.model.returns.EricDataSetFactory;
import digital.srp.sreport.model.surveys.Eric1516;
import digital.srp.sreport.model.surveys.Sdu1617;
import digital.srp.sreport.model.surveys.Sdu1718;
import digital.srp.sreport.model.surveys.Sdu1819;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.HistoricDataMerger;

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

    protected DateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.Z");

    @Autowired
    protected HistoricDataMerger historicDataMerger;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected SurveyCategoryRepository surveyCategoryRepo;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showStatus(Model model) throws IOException {
        LOGGER.info("showStatus");

        model.addAttribute("answers", answerRepo.count());
        model.addAttribute("questions", qRepo.count());
        model.addAttribute("surveys", surveyRepo.count());

        model.addAttribute("returns", returnRepo.count());
        // TODO can get this streaming returns but fetching all returns is prohibitive
//        List<SurveyReturn> returns = returnRepo.findAll();
//        String[] orgs = returns.stream().map(p -> p.org()).collect(Collectors.joining()).split(",");
//        model.addAttribute("orgs", orgs.length);

        addMetaData(model);
        return "jsonStatus";
    }

    private void addMetaData(Model model) {
        model.addAttribute("now", isoFormatter.format(new Date()));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initAndShowStatus(Model model) throws IOException {
        LOGGER.info(String.format("initAndShowStatus"));

        initQuestions(model);
        initSurveys(model);

        String[] ericDataSets = { "ERIC-2016-17", "ERIC-2015-16", "ERIC-2014-15", "ERIC-2013-14" };
        for (String ericDataSet : ericDataSets) {
            initEricReturns(ericDataSet);
        }

        return showStatus(model);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showQuestionStatus(Model model) throws IOException {
        long questions = qRepo.count();
        LOGGER.info("showQuestionStatus: found: {}", questions);
        model.addAttribute("questions", questions);
        addMetaData(model);
        return "jsonStatus";
    }

    @RequestMapping(value = "/questions", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initQuestions(Model model) throws IOException {
        LOGGER.info("initQuestions");
        List<Question> questions = new QuestionCsvImporter().readQuestions();
        LOGGER.info("questions to import found: {}", questions.size());
        List<Question> existingQuestions = qRepo.findAll();
        LOGGER.info("existing questions found: {}", existingQuestions.size());
        for (Question question : questions) {
            if (findMatchingQ(question, existingQuestions) == null) {
                LOGGER.info("found new question: {}, attempting to save...", question.name());
                try {
                    qRepo.save(question);
                } catch (Exception e) {
                    LOGGER.error("unable to save new question: {}\n  ({})", question.name(), e.getMessage());
                }
            }
        }
        return showQuestionStatus(model);
    }

    private Question findMatchingQ(Question question, List<Question> existingQuestions) {
        for (Question q : existingQuestions) {
            if (q.name().equals(question.name())) {
                return q;
            }
        }
        return null;
    }

    @RequestMapping(value = "/surveys", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showSurveysStatus(Model model) throws IOException {
        model.addAttribute("surveys", surveyRepo.count());
        addMetaData(model);
        return "jsonStatus";
    }

    @RequestMapping(value = "/surveys", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initSurveys(Model model) throws IOException {
        Survey[] expectedSurveys = { Eric1516.getSurvey(), Sdu1617.getSurvey(),
                Sdu1718.getSurvey(), Sdu1819.getSurvey() };
        for (Survey expected : expectedSurveys) {
            initSurvey(expected);
        }

        return showSurveysStatus(model);
    }

    private void initSurvey(Survey expected) {
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
                    catRepo.save(existingCat.questionEnums(cat.questionEnums()));
                }
            }
        }
    }

    @RequestMapping(value = "/surveys/{surveyName}", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showSurveyStatus(@PathVariable("surveyName") String surveyName, Model model) throws IOException, Exception {
        Survey survey = surveyRepo.findByName(surveyName);
        model.addAttribute("surveys", survey == null ? 0 : 1);

        // If not found will throw exception & be translated into 404 above
        // so never get here
        model.addAttribute("questions", qRepo.countBySurveyName(surveyName));
        List<SurveyCategory> cats = surveyCategoryRepo.findBySurveyName(surveyName);

        String[] qNames = cats.stream().map(p -> p.questionNames()).collect(Collectors.joining(",")).split(",");
        model.addAttribute("answers", answerRepo.countBySurveyName(surveyName, qNames));
        model.addAttribute("returns", returnRepo.countBySurveyName(surveyName));
        addMetaData(model);

        return "jsonStatus";
    }

    @RequestMapping(value = "/surveys/{surveyName}", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initSurveyAndShowStatus(@PathVariable("surveyName") String surveyName, Model model) throws IOException, Exception {
        initSurvey(lookupSurvey(surveyName));
        return showSurveyStatus(surveyName, model);
    }

    private Survey lookupSurvey(String surveyName) throws ObjectNotFoundException {
        switch (surveyName) {
        case "ERIC-2015-16":
            return Eric1516.getSurvey();
        case "SDU-2016-17":
            return Sdu1617.getSurvey();
        case "SDU-2017-18":
            return Sdu1718.getSurvey();
        case "SDU-2018-19":
            return Sdu1819.getSurvey();
        default:
            throw new ObjectNotFoundException(Survey.class,
                    String.format("No survey named %1$s is known", surveyName));
        }
    }

    @RequestMapping(value = "/answers/{ericDataSet}", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody String initEricReturns(@PathVariable("ericDataSet") String ericDataSet) {
        List<SurveyReturn> existingReturns = returnRepo.findBySurveyName(ericDataSet);
        LOGGER.debug(" ... {} existing returns", existingReturns.size());

        EricDataSet ericDS = EricDataSetFactory.getInstance(ericDataSet);
        try {
            List<SurveyReturn> returns = new EricCsvImporter().readEricReturns(ericDS);
            LOGGER.info("Found {} {} returns to import...", returns.size(), ericDataSet);
            Survey survey = surveyRepo.findByName(ericDataSet);
            if (survey == null) {
                survey = new Survey().name(ericDataSet).applicablePeriod(returns.get(0).applicablePeriod());
                survey = surveyRepo.save(survey);
            }
            importReturns(survey, existingReturns, returns);
        } catch (Throwable e) {
            String msg = String.format("Unable to load ERIC data from %1$s", ericDS.getDataFile());
            LOGGER.error(msg, e);
            throw new SReportException(msg, e);
        }
        return "jsonStatus";
    }

    private void importReturns(Survey survey,
            List<SurveyReturn> existingReturns, List<SurveyReturn> returns) {
        for (SurveyReturn surveyReturn : returns) {
            surveyReturn.survey(survey);
            SurveyReturn existingReturn = findBySurveyAndOrg(existingReturns, surveyReturn);
            if (existingReturn == null) {
                saveReturnAndAnswers(surveyReturn);
            } else {
                LOGGER.debug("Starting to add answers for org: {}", surveyReturn.org());
                long count = historicDataMerger.merge(surveyReturn.answers(), existingReturn);
                LOGGER.debug("... added {} answers for org: {}", count, surveyReturn.org());
            }
        }
    }

    private Question findQ(Answer answer) {
        LOGGER.info("findQ {}", answer.question().q().name());
        try {
            Question q = qRepo.findByName(answer.question().name());
            if (q == null) {
                String msg = String.format("Missing question %1$s. You must create all questions before attempting to import returns that use them",
                        answer.question().q().name());
                throw new IllegalStateException(msg);
            }
            return q;
        } catch (Exception e) {
            String msg = String.format("Cannot read question %1$s because: %2$s",
                    answer.question().q().name(), e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    private void saveReturnAndAnswers(SurveyReturn surveyReturn) {
        LOGGER.info("Save new return {} for {} with {} answers", surveyReturn.name(), surveyReturn.org(), surveyReturn.answers().size());
        for (Answer answer : surveyReturn.answers()) {
            answer.question(findQ(answer));
            answer.addSurveyReturn(surveyReturn);
        }
        returnRepo.save(surveyReturn);
    }

    private SurveyReturn findBySurveyAndOrg(List<SurveyReturn> existingReturns,
            SurveyReturn surveyReturn) {
        for (SurveyReturn ret : existingReturns) {
            if (ret.name().equals(surveyReturn.name())
                    && ret.org().equals(surveyReturn.org())) {
                LOGGER.info(String.format("Skipping insert of return for %1$s because %2$d matches name and org", surveyReturn.name(), ret.id()));
                return ret;
            }
        }
        return null;
    }

}
