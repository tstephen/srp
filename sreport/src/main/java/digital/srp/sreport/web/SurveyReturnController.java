package digital.srp.sreport.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
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

import digital.srp.sreport.internal.EricCsvImporter;
import digital.srp.sreport.internal.SReportException;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyAnswer;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.returns.Eric1516;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.repositories.SurveyReturnRepository;

/**
 * REST web service for accessing returned returns.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/returns")
public class SurveyReturnController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyReturnController.class);
    
    @Value("${spring.data.rest.baseUri}")
    protected String baseUrl;
    
    @Autowired
    protected SurveyController surveyController; 
    
    @Autowired
    protected SurveyReturnRepository returnRepo;

    @PostConstruct
    protected void init() {
        Survey survey = surveyController.findByName(digital.srp.sreport.model.surveys.Eric1516.ID);
        List<SurveyReturn> existingReturns = findBySurvey(digital.srp.sreport.model.surveys.Eric1516.ID);
        
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(getClass().getResourceAsStream(Eric1516.DATA_FILE));
            List<SurveyReturn> returns = new EricCsvImporter()
                    .readEricReturns(isr, Eric1516.HEADERS);
            LOGGER.info(String.format("Found %1$d ERIC returns, saving...", returns.size()));
            for (SurveyReturn surveyReturn : returns) {
                surveyReturn.applicablePeriod("2015-16")
                        .name(surveyReturn.org()+" 2015-16")
                        .survey(survey);
                
                // merge persisted questions to answers
                for (SurveyAnswer answer: surveyReturn.answers()) {
                    answer.question(findMatchingQ(survey, answer))
                            .surveyReturn(surveyReturn);
                }
                
                if (!findBySurveyAndOrg(existingReturns, surveyReturn)) {
                    returnRepo.save(surveyReturn);
                }
            }
        } catch (IOException e) {
            String msg = String.format("Unable to load ERIC data from %1$s", Eric1516.DATA_FILE);
            LOGGER.error(msg, e);
            throw new SReportException(msg, e);
        } catch (Throwable e) {
            String msg = String.format("Unable to load ERIC data from %1$s", Eric1516.DATA_FILE);
            LOGGER.error(msg, e);
//            throw new SReportException(msg, e);
        } finally { 
            try {
                isr.close();
            } catch (Exception e) {
                ;
            }
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

    private SurveyQuestion findMatchingQ(Survey survey, SurveyAnswer answer) {
        for (SurveyCategory cat : survey.categories()) {
            for (SurveyQuestion q : cat.questions()) {
                if (q.label().equals(answer.question().label())) {
                    return q;
                }
            }
        }
        throw new IllegalArgumentException(String.format(
                "Answer %1$s responds to the unknown question %2$s",
                answer.id(), answer.question().label()));
    }

    /**
     * Return a single survey return with the specified id.
     * 
     * @return the specified survey.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    @Transactional
    public @ResponseBody SurveyReturn findById(@PathVariable("id") Long returnId) {
        LOGGER.info(String.format("findById %1$s", returnId));

        SurveyReturn rtn = returnRepo.findOne(returnId);
        // use logger for force child load
        LOGGER.info(String.format("Found return with id %1$d with status %2$s and containing %3$d answers", rtn.id(), rtn.status(), rtn.answers().size()));

        return addLinks(rtn);
    }
    
    /**
     * Returns matching the specified survey and organisation.
     * 
     * @return returns. May be more than one because occasionally returns are restated. .
     */
    @RequestMapping(value = "/findBySurveyName/{surveyName}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    @Transactional
    public @ResponseBody List<SurveyReturn> findBySurvey(
            @PathVariable("surveyName") String surveyName) {
        LOGGER.info(String.format("findCurrentBySurvey %1$s", surveyName));

        List<SurveyReturn> returns = returnRepo.findBySurveyName(surveyName);

        return addLinks(returns);
    }
    
    /**
     * Returns matching the specified survey and organisation.
     * 
     * @return returns. May be more than one because occasionally returns are restated. .
     */
    @RequestMapping(value = "/findBySurveyNameAndOrg/{surveyName}/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    @Transactional
    public @ResponseBody List<SurveyReturn> findBySurveyAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("findBySurveyAndOrg %1$s", surveyName));

        List<SurveyReturn> returns = returnRepo.findBySurveyAndOrg(surveyName, org);
//        // use logger for force child load
//        LOGGER.info(String.format("Found survey with id %1$d named %2$s and with %3$d categories totalling %4$d questions", 
//                survey.id(), survey.name(), survey.categories().size(),
//                survey.questions().size()));
        if (returns.size()==0) { 
            returns.add(createBlankReturn(surveyName, org));
        }

        return addLinks(returns);
    }

    protected SurveyReturn createBlankReturn(String surveyName, String org) {
        LOGGER.info(String.format("createBlankReturn of %1$s for %2$s", surveyName, org));
        Survey requested = surveyController.findByName(surveyName);
        List<SurveyAnswer> emptyAnswers = new ArrayList<SurveyAnswer>();

        SurveyReturn rtn = new SurveyReturn()
                .name(String.format("%1$s %2$s", requested.name(),org))
                .survey(requested)
                .org(org)
                .applicablePeriod(requested.applicablePeriod())
                .answers(emptyAnswers);
        returnRepo.save(rtn);
        for (SurveyQuestion q : requested.questions()) {
            SurveyAnswer answer = new SurveyAnswer().question(q).surveyReturn(rtn);
            if (q.name().equals("orgCode")) {
                answer.response(org);
            }
            emptyAnswers.add(answer);
        }
        
        return addLinks(rtn);
    }

    @RequestMapping(value = "/findCurrentBySurveyNameAndOrg/{surveyName}/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    @Transactional
    public @ResponseBody SurveyReturn findCurrentBySurveyAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("findBySurveyAndOrg %1$s", surveyName));

        List<SurveyReturn> returns = findBySurveyAndOrg(surveyName, org);
        returns.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
        SurveyReturn rtn = returns.get(returns.size()-1);
        LOGGER.debug(String.format("Found %1$d returns for %2$s,%3$s; returning revision %4$d", returns.size(), surveyName, org, rtn.revision()));

        return addLinks(rtn);
    }
    
    /**
     * Return a list of survey returns, optionally paged.
     * 
     * @return survey returns.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Summary.class)
    public @ResponseBody List<SurveyReturn> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List returns"));

        List<SurveyReturn> list;
        if (limit == null) {
            list = returnRepo.findAll();
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = returnRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s returns", list.size()));

        return addLinks(list);
    }

    /**
     * Return a list of survey returns for a single organisation.
     * 
     * @return survey returns.
     */
    @RequestMapping(value = "/findByOrg/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Summary.class)
    public @ResponseBody List<SurveyReturn> findByOrg(
            @PathVariable("org") String org) {
        LOGGER.info(String.format("List returns for %1$s", org));

        List<SurveyReturn> list = returnRepo.findByOrg(org);
        LOGGER.info(String.format("Found %1$s returns", list.size()));

        return addLinks(list);
    }
    
    /**
     * Create a new survey response.
     * 
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @RequestBody SurveyReturn surveyReturn) {

        returnRepo.save(surveyReturn);

        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromController(getClass());
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("id", surveyReturn.id().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing return.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void update(
            @PathVariable("id") Long returnId,
            @RequestBody SurveyReturn updatedReturn) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        if (StatusType.Submitted.name().equals(existing.status())) {
            throw new IllegalStateException(String.format(
                    "The return %1$d:%2$s has been submitted, you may no longer update. If you've recognised a mistake please re-state the return.", 
                    returnId, existing.name()));
        }
        updatedReturn.survey(existing.survey());
        for (SurveyAnswer answer : updatedReturn.answers()) {
            answer.surveyReturn(updatedReturn);
        }
        returnRepo.save(updatedReturn);
    }
    
    /**
     * Re-stating a return preserves the existing one and saves the updates as a new revision.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}/restate", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void restate(
            @PathVariable("id") Long returnId,
            @RequestBody SurveyReturn updatedReturn) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        existing.status(StatusType.Superceded.name());
        returnRepo.save(existing);
        
        SurveyReturn restatedRtn = createBlankReturn(updatedReturn.survey().name(), updatedReturn.org())
                .revision((short) (existing.revision()+1));
        for (int i = 0; i < restatedRtn.answers().size(); i++) {
            restatedRtn.answers().get(i)
                    .response(updatedReturn.answers().get(i).response());
        }
        returnRepo.save(restatedRtn);
    }

    /**
     * Change the status the return has reached.
     */
    @RequestMapping(value = "/{returnId}/status", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody void setStatus(
            @PathVariable("returnId") Long returnId,
            @RequestBody String status) {
        LOGGER.info(String.format("Setting survey %1$s to status %2$s",
                returnId, status));

        SurveyReturn survey = returnRepo.findOne(returnId).status(status);
        returnRepo.save(survey);
    }

    /**
     * Delete an existing return.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(
            @PathVariable("id") Long returnId) {
        returnRepo.delete(returnId);
    }

    private List<SurveyReturn> addLinks(List<SurveyReturn> returns) {
        for (SurveyReturn rtn : returns) {
            addLinks(rtn);
        }
        return returns;
    }
    
    private SurveyReturn addLinks(SurveyReturn rtn) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(baseUrl+getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + rtn.id()));
        
        return rtn.links(links);
    }
}
