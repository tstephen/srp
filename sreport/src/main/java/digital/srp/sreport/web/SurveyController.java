package digital.srp.sreport.web;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    protected  SurveyRepository surveyRepo;

    @Autowired
    protected  SurveyCategoryRepository catRepo;
    
    @Autowired
    protected  QuestionRepository questionRepo;
    
    @Autowired
    protected  SurveyReturnRepository returnRepo;
    
    /**
     * Return just the specified survey.
     * 
     * @return The specified survey.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(SurveyViews.Detailed.class)
    @Transactional
    public @ResponseBody Survey findById(
            @PathVariable("id") Long surveyId) {
        LOGGER.info(String.format("findById %1$s", surveyId));

        Survey survey = surveyRepo.findOne(surveyId);
        // use logger for force child load
        LOGGER.info(String.format("Found survey with id %1$d named %2$s and with %3$d categories of questions", survey.id(), survey.name(), survey.categories().size()));

        return addLinks(survey);
    }

    /**
     * Return just the specified survey.
     * 
     * @return The specified survey.
     */
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(SurveyViews.Detailed.class)
    @Transactional
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
     * Return a list of surveys, optionally paged.
     * 
     * @return surveys.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(SurveyViews.Summary.class)
    public @ResponseBody List<Survey> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List surveys"));

        List<Survey> list;
        if (limit == null) {
            list = surveyRepo.findAll();
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = surveyRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s surveys", list.size()));

        return list;
    }
    
    /**
     * Return a list of survey returns, optionally paged.
     * 
     * @return survey returns.
     */
    @RequestMapping(value = "/{id}/returns", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Summary.class)
    public @ResponseBody List<SurveyReturn> listReturns(
            @PathVariable("id") Long surveyId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List returns"));

        List<SurveyReturn> list;
        if (limit == null) {
            list = returnRepo.findBySurvey(surveyId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = returnRepo.findPageBySurvey(surveyId, pageable);
        }
        LOGGER.info(String.format("Found %1$s returns", list.size()));

        return list;
    }

    /**
     * Create a new survey.
     * 
     * @return
     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public @ResponseBody ResponseEntity<?> create(
//            @RequestBody Survey survey) {
//
//        createInternal(survey);
//
//        UriComponentsBuilder builder = MvcUriComponentsBuilder
//                .fromController(getClass());
//        HashMap<String, String> vars = new HashMap<String, String>();
//        vars.put("id", survey.id().toString());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
//        return new ResponseEntity(headers, HttpStatus.CREATED);
//    }

//    @Transactional
//    protected void createInternal(Survey survey) {
////        ArrayList<SurveyCategory> newCats = new ArrayList<SurveyCategory>(survey.categories().size());
//        for (SurveyCategory cat : survey.categories()) {
//            if (cat.name()==null) {
//                System.err.println("No name for cat : "+cat);
//            }
//            cat.survey(survey);
////            SurveyCategory foundCat = catRepo.findOne(cat.id());
////            if (foundCat == null) {
////                catRepo.save(cat);
////                ArrayList<SurveyQuestion> newQs = new ArrayList<SurveyQuestion>(cat.questions().size());
//                for (SurveyQuestion q : cat.questions()) {
////                    SurveyQuestion foundQ = qRepo.findOne(q.id());
////                    if (foundQ == null) {
//                        q.category(cat);
////                        newQs.add(qRepo.save(q));
////                    } else {
////                        newQs.add(foundQ);
////                    }
//                }
////                cat.questions(newQs);
////                newCats.add(catRepo.save(cat));
////            } else {
////                newCats.add(foundCat);
////            }
//        }
////        survey.categories(newCats);
////        for (SurveyQuestion q : survey.questions()) {
////            qRepo.save(q);
////        }
//        survey = surveyRepo.save(survey);
//    }

    /**
     * Update an existing survey.
     */
//    @ResponseStatus(value = HttpStatus.NO_CONTENT)
//    @RequestMapping(value = "/{idOrName}", method = RequestMethod.PUT, consumes = { "application/json" })
//    @Transactional
//    public @ResponseBody void update(
//            @PathVariable("idOrName") String idOrName,
//            @RequestBody Survey updatedSurvey) {
//        
//        Survey survey = null;
//        try {
//            Long surveyId = Long.parseLong(idOrName);
//            survey = surveyRepo.findOne(surveyId);
//        } catch (NumberFormatException e) {
//            LOGGER.info("Cannot parse survey id from '{}', assume it's a name", idOrName);
//            survey = surveyRepo.findByName(idOrName);
//        }
//        LOGGER.info("Updated survey '{}' has {} questions, compared to {}", 
//                idOrName, updatedSurvey.questions().size(), survey.questions().size());
//        
//        for (SurveyCategory uCat : updatedSurvey.categories()) {
//            SurveyCategory cat = survey.category(uCat.name());
//            if (cat == null) {
//                uCat.survey(survey);
//                cat = catRepo.save(uCat);
//                survey.categories().add(cat);
//            }
//            for (SurveyQuestion uq : uCat.questions()) {
//                SurveyQuestion q = survey.question(uq.name());
//                if (q == null) {
//                    uq.category(cat);
//                    q = qRepo.save(uq);
//                    cat.questions().add(q);
//                } else {
//                    q.category(cat).hint(uq.hint()).label(uq.label()).required(uq.required()).type(uq.type()).unit(uq.unit());
//                }
//            }
//        }
//        LOGGER.info("... survey now has {} questions", survey.questions().size());
//
//        surveyRepo.save(survey);
//    }

    /**
     * Change the status the survey has reached.
     */
    @RequestMapping(value = "/{surveyId}/status", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody void setStatus(
            @PathVariable("surveyId") Long surveyId,
            @RequestBody String status) {
        LOGGER.info(String.format("Setting survey %1$s to status %2$s",
                surveyId, status));

        Survey survey = surveyRepo.findOne(surveyId).status(status);
        surveyRepo.save(survey);
    }

    /**
     * Delete an existing survey.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long surveyId) {
        surveyRepo.delete(surveyId);
    }

    private Survey addLinks(Survey survey) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + survey.id()));
        
        return survey.links(links);
    }
}
