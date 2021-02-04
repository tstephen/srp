package digital.srp.sreport.web;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.model.views.SurveyViews;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.SurveyService;

/**
 * REST web service for accessing surveys.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/surveys")
public class SurveyController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyController.class);

    @Autowired 
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;
    
    @Autowired
    protected QuestionRepository questionRepo;
    
    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected SurveyService surveySvc;

    @PostConstruct
    protected void init() {
        surveySvc.initSurveys();
    }

    /**
     * @return The specified survey.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(SurveyViews.Detailed.class)
    @Transactional
    public @ResponseBody Survey findById(
            @PathVariable("id") Long surveyId) {
        LOGGER.info(String.format("findById %1$s", surveyId));

        Survey survey = surveyRepo.findById(surveyId)
                .orElseThrow(() -> new ObjectNotFoundException(Survey.class, surveyId));
        // use logger for force child load
        LOGGER.info(String.format("Found survey with id %1$d named %2$s and with %3$d categories of questions", survey.id(), survey.name(), survey.categories().size()));

        return addLinks(survey);
    }

    /**
     * @return The specified survey.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(SurveyViews.Detailed.class)
    public @ResponseBody Survey findByName(
            @PathVariable("name") String name) {
        LOGGER.info(String.format("findByName %1$s", name));

        Survey survey = surveyRepo.findByName(name);
        // use logger for force child load
        LOGGER.info(String.format("Found survey with id %1$d named %2$s and with %3$d categories totalling %4$d questions", 
                survey.id(), survey.name(), survey.categories().size(),
                survey.questionCodes().size()));

        // TODO fetch all questions to optimise db access?
//        List<SurveyQuestion> surveyQuestions = qRepo.findBySurvey(survey.name());
        for (SurveyCategory cat : survey.categories()) {
            for (Q q : cat.questionEnums()) {
                cat.questions().add(questionRepo.findByName(q.name()));
            }
        }
        
        return survey;
    }
    
    /**
     * @return list of surveys, optionally paged.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(SurveyViews.Summary.class)
    public @ResponseBody List<Survey> list(
            @RequestHeader String Authorization,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List surveys"));

        List<Survey> list;
        if (limit == null) {
            list = surveyRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = surveyRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s surveys", list.size()));

        return list;
    }
    
    /**
     * @return a list of survey returns, optionally paged.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/{id}/returns", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Summary.class)
    public @ResponseBody List<SurveyReturn> listReturns(
            @PathVariable("id") Long surveyId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List returns");

        List<SurveyReturn> list;
        if (limit == null) {
            list = returnRepo.findBySurvey(surveyId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = returnRepo.findPageBySurvey(surveyId, pageable);
        }
        LOGGER.info(String.format("Found %1$s returns", list.size()));

        return list;
    }

    /**
     * Change the status the survey has reached.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{surveyId}/status", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody void setStatus(
            @PathVariable("surveyId") Long surveyId,
            @RequestBody String status) {
        LOGGER.info(String.format("Setting survey %1$s to status %2$s",
                surveyId, status));

        Survey survey = surveyRepo.findById(surveyId)
                .orElseThrow(() -> new ObjectNotFoundException(Survey.class, surveyId))
                .status(status);

        surveyRepo.save(survey);
    }

    /**
     * Delete an existing survey.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long surveyId) {
        surveyRepo.deleteById(surveyId);
    }

    private Survey addLinks(Survey survey) {
        return survey.links(Collections.singletonList(
                Link.of(getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + survey.id())));
    }
}
