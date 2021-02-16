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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.SReportException;
import digital.srp.sreport.importers.EricCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.EricDataSet;
import digital.srp.sreport.model.returns.EricDataSetFactory;
import digital.srp.sreport.model.surveys.SurveyFactory;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.HistoricDataMerger;
import digital.srp.sreport.services.QuestionService;
import digital.srp.sreport.services.SurveyService;

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

    @Autowired
    protected QuestionService questionSvc;

    @Autowired
    protected SurveyService surveySvc;

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showStatus(final Model model) throws IOException {
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
        return "json-summary";
    }

    private void addMetaData(final Model model) {
        model.addAttribute("now", isoFormatter.format(new Date()));
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initAndShowStatus(final Model model) throws IOException {
        LOGGER.info(String.format("initAndShowStatus"));

        initQuestions(model);
        initSurveys(model);

        String[] ericDataSets = { "ERIC-2016-17", "ERIC-2015-16", "ERIC-2014-15", "ERIC-2013-14" };
        for (String ericDataSet : ericDataSets) {
            initEricReturns(ericDataSet);
        }

        return showStatus(model);
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/questions", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showQuestionStatus(final Model model) throws IOException {
        long questions = qRepo.count();
        LOGGER.info("showQuestionStatus: found: {}", questions);
        model.addAttribute("questions", questions);
        addMetaData(model);
        return "json-status";
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/questions", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initQuestions(final Model model) throws IOException {
        questionSvc.initQuestions();
        return showQuestionStatus(model);
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/surveys", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showSurveysStatus(final Model model) throws IOException {
        model.addAttribute("surveys", surveyRepo.count());
        addMetaData(model);
        return "json-status";
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/surveys", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initSurveys(final Model model) throws IOException {
        surveySvc.initSurveys();

        return showSurveysStatus(model);
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/surveys/{surveyName}", method = RequestMethod.GET, headers = "Accept=application/json")
    public String showSurveyStatus(
            @PathVariable("surveyName") final String surveyName,
            Model model) throws IOException, Exception {
        LOGGER.info("showSurveyStatus: {}", surveyName);
        Survey survey = surveyRepo.findByName(surveyName);
        model.addAttribute("surveys", survey == null ? 0 : 1);

        Long qCount = qRepo.countBySurveyName(surveyName);
        model.addAttribute("questions", qCount == null ? 0 : qCount);

        List<SurveyCategory> cats = surveyCategoryRepo.findBySurveyName(surveyName);
        String[] qNames = cats.stream().map(p -> p.questionNames()).collect(Collectors.joining(",")).split(",");
        Long answerCount = answerRepo.countBySurveyName(surveyName, qNames);
        model.addAttribute("answers", answerCount == null ? 0 : answerCount);

        Long returnCount = returnRepo.countBySurveyName(surveyName);
        model.addAttribute("returns", returnCount == null ? 0 : returnCount);
        addMetaData(model);

        return "json-summary";
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/surveys/{surveyName}", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initSurveyAndShowStatus(
            @PathVariable("surveyName") final String surveyName,
            Model model) throws IOException, Exception {
        LOGGER.info("initSurveyAndShowStatus: {}", surveyName);
        surveySvc.initSurvey(SurveyFactory.getInstance(surveyName).getSurvey());
        return showSurveyStatus(surveyName, model);
    }

    @RolesAllowed(SrpRoles.ADMIN)
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
        return "json-status";
    }

    private void importReturns(Survey survey,
            List<SurveyReturn> existingReturns, List<SurveyReturn> returns) {
        for (SurveyReturn surveyReturn : returns) {
            surveyReturn.survey(survey);
            Optional<SurveyReturn> existingReturn = findBySurveyAndOrg(existingReturns, surveyReturn);
            if (existingReturn.isPresent()) {
                LOGGER.debug("Starting to add answers for org: {}", surveyReturn.org());
                long count = historicDataMerger.merge(surveyReturn.answers(), existingReturn.get());
                LOGGER.debug("... added {} answers for org: {}", count, surveyReturn.org());
            } else {
                saveReturnAndAnswers(surveyReturn);
            }
        }
    }

    private Question findQ(Answer answer) {
        LOGGER.info("findQ {}", answer.question().q().name());
        try {
            Optional<Question> q = qRepo.findByName(answer.question().name());
            if (!q.isPresent()) {
                String msg = String.format("Missing question %1$s. You must create all questions before attempting to import returns that use them",
                        answer.question().q().name());
                throw new IllegalStateException(msg);
            }
            return q.get();
        } catch (IllegalStateException e) {
            throw e;
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

    private Optional<SurveyReturn> findBySurveyAndOrg(List<SurveyReturn> existingReturns,
            SurveyReturn surveyReturn) {
        for (SurveyReturn ret : existingReturns) {
            if (ret.name().equals(surveyReturn.name())
                    && ret.org().equals(surveyReturn.org())) {
                LOGGER.info(String.format("Skipping insert of return for %1$s because %2$d matches name and org", surveyReturn.name(), ret.id()));
                return Optional.of(ret);
            }
        }
        return Optional.empty();
    }

}
