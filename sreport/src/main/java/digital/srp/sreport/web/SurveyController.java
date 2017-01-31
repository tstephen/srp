package digital.srp.sreport.web;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Eric1516;
import digital.srp.sreport.model.surveys.Sdu1516;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.model.views.SurveyViews;
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
    protected  SurveyReturnRepository returnRepo;
    
    @PostConstruct
    protected void init() {
        String[] expectedSurveys = { Eric1516.ID, Sdu1516.ID };
        List<Survey> existingSurveys = list(null, null);
        for (String expected : expectedSurveys) {
            boolean found = false;
            for (Survey existing : existingSurveys) {
                if (expected.equals(existing.name())) {
                    found = true;
                    continue;
                }
            }
            if (found) {
                LOGGER.debug(
                        String.format("Expected survey %1$s ok", expected));
            } else {
                LOGGER.warn(String.format(
                        "Could not find expected survey %1$s, attempt to create",
                        expected));
                switch (expected) {
                case Eric1516.ID: 
                    surveyRepo.save(new Eric1516().getSurvey());
                    break;
                case Sdu1516.ID: 
                    surveyRepo.save(new Sdu1516().getSurvey());
                    break;
                default: 
                    LOGGER.warn(String.format("Do not know expected survey %1$s", expected));
                }
//                create(readSurveyResource(String.format("/surveys/%1$s.json", expected)));
            }
        }
    }
    
//    @SuppressWarnings("resource")
//    private Survey readSurveyResource(String resource) {
//        InputStream is = null;
//        try {
//            is = getClass().getResourceAsStream(resource);
//            assertNotNull(String.format("Unable to find survey definition at %1$s",
//                    resource), is);
//
//            return objectMapper.readValue(new Scanner(is).useDelimiter("\\A").next(), Survey.class);
//        } catch (IOException e) {
//            String msg = String.format("Unable to create expecetd survey '%1$s'", resource);
//            LOGGER.error(msg, e);
//            throw new SReportException(msg, e);
//        } finally {
//            try {
//                is.close();
//            } catch (Exception e) {
//            }
//        }
//    }
    
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

        return survey;
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
                survey.questions().size()));

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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @RequestBody Survey survey) {

        surveyRepo.save(survey);

        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromController(getClass());
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("id", survey.id().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing survey.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void update(
            @PathVariable("id") Long surveyId,
            @RequestBody Survey updatedSurvey) {
//        Survey survey = surveyRepo.findOne(surveyId);
//
//        NullAwareBeanUtils.copyNonNullProperties(updatedOrder, survey, "id");

        surveyRepo.save(updatedSurvey);
    }

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

//    private Link linkTo(
//            @SuppressWarnings("rawtypes") Class<? extends CrudRepository> clazz,
//            Long id) {
//        return new Link(clazz.getAnnotation(RepositoryRestResource.class)
//                .path() + "/" + id);
//    }
}
