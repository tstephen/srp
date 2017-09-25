package digital.srp.sreport.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.Cruncher;

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
    protected Cruncher cruncher;
    
    @Autowired
    protected SurveyRepository surveyRepo; 
    
    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected QuestionRepository qRepo;
    
    @Autowired
    protected AnswerRepository answerRepo;
    
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
     * @return returns. May be more than one because occasionally returns are restated.
     */
    @RequestMapping(value = "/findBySurveyNameAndOrg/{surveyName}/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    public @ResponseBody List<SurveyReturn> findBySurveyAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("findBySurveyAndOrg %1$s/%2$s", surveyName, org));

        List<SurveyReturn> returns = returnRepo.findBySurveyAndOrg(surveyName, org);
        if (returns.size()==0) { 
            returns.add(createBlankReturn(surveyName, org, applicablePeriod(surveyName)));
        }

        return addLinks(returns);
    }

    /**
     * Returns matching the specified survey, organisation and period.
     * 
     * @return returns. May be more than one because occasionally returns are restated.
     */
//    @RequestMapping(value = "/findBySurveyOrgAndPeriod/{surveyName}/{org}/{period}", method = RequestMethod.GET)
//    @JsonView(SurveyReturnViews.Detailed.class)
//    @Transactional
//    public @ResponseBody List<SurveyReturn> findBySurveyOrgAndPeriod(
//            @PathVariable("surveyName") String surveyName,
//            @PathVariable("org") String org,
//            @PathVariable("period") String period) {
//        LOGGER.info(String.format("findBySurveyOrgAndPeriod %1$s/%2$s/%3$s", surveyName, org, period));
//
//        List<SurveyReturn> returns = returnRepo.findBySurveyOrgAndPeriod(surveyName, org);
//        if (returns.size()==0) { 
//            returns.add(createBlankReturn(surveyName, org, period));
//        }
//
//        return addLinks(returns);
//    }

    protected String applicablePeriod(String surveyName) {
        return surveyName.substring(surveyName.indexOf('-')+1);
    }

    protected SurveyReturn createBlankReturn(String surveyName, String org, String period) {
        LOGGER.info(String.format("createBlankReturn of %1$s for %2$s", surveyName, org));
        Survey requested = surveyRepo.findByName(surveyName);
        Set<Answer> emptyAnswers = new HashSet<Answer>();

        SurveyReturn rtn = new SurveyReturn()
                .name(String.format("%1$s-%2$s", requested.name(),org))
                .survey(requested)
                .org(org)
                .applicablePeriod(requested.applicablePeriod())
                .answers(emptyAnswers);
        returnRepo.save(rtn);
        for (Question q : requested.questions()) {
            Answer answer = new Answer().question(q).addSurveyReturn(rtn)
                    .applicablePeriod(requested.applicablePeriod());
            if (q.q().equals(Q.ORG_CODE)) {
                answer.response(org);
            }
            emptyAnswers.add(answer);
        }
        
        return addLinks(rtn);
    }

    @RequestMapping(value = "/findCurrentBySurveyNameAndOrg/{surveyName}/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    public @ResponseBody SurveyReturn findCurrentBySurveyNameAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("findCurrentBySurveyNameAndOrg %1$s, %2$s", surveyName, org));

        List<SurveyReturn> returns = findBySurveyAndOrg(surveyName, org);
        returns.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
        SurveyReturn rtn = returns.get(returns.size()-1);
        LOGGER.info("Found {} returns for {},{} returning revision {}", returns.size(), surveyName, org, rtn.revision());

//        rtn.answer(Q.ORG_CODE, rtn.applicablePeriod())
//                .orElse(rtn.initAnswer(rtn, qRepo.findByName(Q.ORG_CODE.name()), rtn.applicablePeriod()))
//                .response(org);
        rtn = saveCalculations(cruncher.calculate(rtn));
        
        return addLinks(rtn);
    }

    @RequestMapping(value = "/importEric/{surveyName}/{org}", method = RequestMethod.GET)
    @JsonView(SurveyReturnViews.Detailed.class)
    @Transactional
    public @ResponseBody SurveyReturn importEricAnswers(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        SurveyReturn rtn  = findCurrentBySurveyNameAndOrg(surveyName, org);
        List<Survey> surveys = surveyRepo.findEricSurveys();
        for (Survey survey : surveys) {
            returnRepo.importAnswers(rtn.id(), rtn.org(), survey.name());
        }
        return findCurrentBySurveyNameAndOrg(surveyName, org);
    }

    @Transactional//(Transactional.TxType.REQUIRES_NEW)
    private SurveyReturn saveCalculations(SurveyReturn rtn) {
        return returnRepo.save(rtn);
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
    public @ResponseBody void update(
            @PathVariable("id") Long returnId,
            @RequestBody SurveyReturn updatedReturn) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        if (!StatusType.Draft.name().equals(existing.status())) {
            throw new IllegalStateException(String.format(
                    "The return %1$d:%2$s has been submitted, you may no longer update. If you've recognised a mistake please re-state the return.", 
                    returnId, existing.name()));
        }
        int changeCount = createOrMergeAnswers(updatedReturn, existing);
//        NullAwareBeanUtils.copyNonNullProperties(updatedReturn, existing);
        if (changeCount > 0) {
            existing.setLastUpdated(new Date());
        }
//        SurveyReturn savedReturn = 
        returnRepo.save(existing);
//        addLinks(Collections.singletonList(savedReturn));
//        return savedReturn;
    }

    private int createOrMergeAnswers(SurveyReturn updatedReturn,
            SurveyReturn existing) {
        int changeCount = 0;
        for (Answer answer : updatedReturn.answers()) {
            Answer existingAnswer = existing.answer(answer.question().q(), answer.applicablePeriod())
                    .orElse(existing.createEmptyAnswer(answer.question(), answer.applicablePeriod()));
            if (answer.question().q() == Q.ORG_NAME) {
                System.out.println("Pay attention");
            }
            if (existingAnswer.question().id() == null) {
                Question q;
                try {
                    q = qRepo.findByName(answer.question().name());
                } catch (Throwable e) {
                    LOGGER.error("Data issue, more than one question named {}: ", answer.question().q());
                    throw e;
                }
                existingAnswer = answerRepo.save(answer.question(q).addSurveyReturn(existing));
                existing.answers().add(existingAnswer);
                changeCount++;
            } else if (answer.response() != null && !answer.response().equals(existingAnswer.response())) {
                // note that Hibernate / Spring Data will skip any update if not actually needed
                changeCount++;
                answerRepo.save(existingAnswer.response(answer.response()).addSurveyReturn(existing));
            } else {
                ;
                //LOGGER.debug("No change to {}", answer.question().name());
            }
        }
        return changeCount;
    }
    
    /**
     * Re-stating a return preserves the existing one and saves the updates as a new revision.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}/restate", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void restate(
            @PathVariable("id") Long returnId,
            @RequestBody SurveyReturn updatedRtn) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        existing.status(StatusType.Superceded.name());
        for (Answer a : existing.answers()) {
            a.status(StatusType.Superceded.name());
        }
        returnRepo.save(existing);
        
        SurveyReturn restatedRtn = new SurveyReturn()
                .name(updatedRtn.name())
                .org(updatedRtn.org())
                .status(StatusType.Draft.name())
                .applicablePeriod(updatedRtn.applicablePeriod())
                .revision(new Integer(updatedRtn.revision()+1).shortValue())
                .survey(existing.survey());
//        SurveyReturn restatedRtn = new SurveyReturn(updatedRtn.name(), 
//                updatedRtn.org(), StatusType.Draft.name(), updatedRtn.applicablePeriod(),
//                new Integer(updatedRtn.revision()+1).shortValue())
//                .survey(existing.survey());
        for (Answer a : updatedRtn.answers()) {
            restatedRtn.answers().add(new Answer()
                    .applicablePeriod(a.applicablePeriod())
                    .revision(new Integer(a.revision()+1).shortValue())
                    .status(StatusType.Draft.name())
                    .response(a.response())
                    .question(a.question())
                    .addSurveyReturn(restatedRtn));
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
        switch (StatusType.valueOf(status)) {
        case Published:
            publish(survey);
            break;
        case Submitted:
            submit(survey);
            break;
        default:
            String msg = String.format("Setting return %1$d to %2$s is not allowed", returnId, status);
            throw new IllegalArgumentException(msg );
        }

        returnRepo.save(survey);
    }

    protected void submit(SurveyReturn survey) {
        survey.status(StatusType.Submitted.name());
        for (Answer a : survey.answers()) {
            // Cannot overwrite published answers without calling restate
            if (!a.status().equals(StatusType.Published.name())) {
                a.setStatus(StatusType.Submitted.name());
            } else {
                LOGGER.warn("Cannot set status of answer {} to {} because it's been published", a.id(), a.status());
            }
        }
    }

    protected void publish(SurveyReturn survey) {
        survey.status(StatusType.Published.name());
        for (Answer a : survey.answers()) {
            a.setStatus(StatusType.Published.name());
        }
    }

    /**
     * Run all calculations based on the return inputs.
     */
    @RequestMapping(value = "/{returnId}/calculate", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody void calculate(
            @PathVariable("returnId") Long returnId) {
        LOGGER.info(String.format("Running calculations for %1$s", returnId));

        SurveyReturn rtn = returnRepo.findOne(returnId);
        
        rtn = cruncher.calculate(rtn);

        returnRepo.save(rtn);
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
