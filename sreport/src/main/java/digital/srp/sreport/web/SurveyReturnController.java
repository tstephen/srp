package digital.srp.sreport.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.AuthUtils;
import digital.srp.sreport.internal.NullAwareBeanUtils;
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
import digital.srp.sreport.services.DefaultCompletenessValidator;

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

    @Value("${spring.data.rest.base-path}")
    protected String baseUrl;

    @Autowired
    protected Calculator cruncher;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected DefaultCompletenessValidator validator = new DefaultCompletenessValidator();

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

        if (org.length() > 3) {
            org = lookupOrgCode(org);
        }

        List<SurveyReturn> returns = findBySurveyAndOrg(surveyName, org);
        returns.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
        SurveyReturn rtn = returns.get(returns.size()-1);
        LOGGER.info("Found {} returns for {},{} returning revision {}", returns.size(), surveyName, org, rtn.revision());

        validator.validate(rtn);
        return addLinks(rtn);
    }

    private String lookupOrgCode(String orgName) {
        List<Answer> answer = answerRepo.findByOrgName(orgName);
        if (answer.size() == 0) {
            throw new ObjectNotFoundException(SurveyReturn.class, orgName);
        }
        return answer.get(0).surveyReturns().iterator().next().org();
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
        NullAwareBeanUtils.copyNonNullProperties(updatedReturn, existing, "answers");
        if (changeCount > 0) {
            existing.setLastUpdated(new Date());
        }
        DefaultCompletenessValidator validator = new DefaultCompletenessValidator() ;
        validator.validate(existing);
        returnRepo.save(existing);
    }

    /**
     * Update an existing return with a single answer.
     *
     * <p>This is primarily intended for qualitative, textual information
     * for the current period, therefore does not require period to be specified.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}/answers/{q}", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody void updateAnswer(
            @PathVariable("id") Long returnId,
            @PathVariable("q") String q,
            @RequestBody String updatedAns) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        if (existing == null) {
            throw new ObjectNotFoundException(SurveyReturn.class, returnId);
        } else if (!StatusType.Draft.name().equals(existing.status())) {
            throw new IllegalStateException(String.format(
                    "The return %1$d:%2$s has been submitted, you may no longer update. If you've recognised a mistake please re-state the return.",
                    returnId, existing.name()));
        }
        Optional<Answer> answer = existing.answer(existing.applicablePeriod(), Q.valueOf(q));
        if (answer.isPresent()) {
            answer.get().response(updatedAns).derived(false);
        } else {
            Question existingQ = qRepo.findByName(q);
            if (existingQ == null) {
                throw new ObjectNotFoundException(Question.class, q);
            } else {
                existing.initAnswer(existing, existing.applicablePeriod(), existingQ)
                        .response(updatedAns).derived(false);
            }
        }
        DefaultCompletenessValidator validator = new DefaultCompletenessValidator() ;
        validator.validate(existing);
        returnRepo.save(existing);
    }

    private int createOrMergeAnswers(SurveyReturn updatedReturn,
            SurveyReturn existing) {
        int changeCount = 0;
        for (Answer answer : updatedReturn.answers()) {
            Answer existingAnswer = existing.answer(answer.applicablePeriod(), answer.question().q())
                    .orElse(existing.createEmptyAnswer(answer.applicablePeriod(), answer.question()));
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
            }
        }
        return changeCount;
    }

    /**
     * Re-stating a return preserves the existing one and saves the updates as a new revision.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}/restate", method = RequestMethod.POST, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void restate(
            @PathVariable("id") Long returnId) {
        SurveyReturn existing = returnRepo.findOne(returnId);
        existing.status(StatusType.Superseded.name());
        for (Answer a : existing.answers()) {
            a.status(StatusType.Superseded.name());
        }
        returnRepo.save(existing);

        SurveyReturn restatedRtn = new SurveyReturn()
                .name(existing.name())
                .org(existing.org())
                .status(StatusType.Draft.name())
                .applicablePeriod(existing.applicablePeriod())
                .revision(new Integer(existing.revision()+1).shortValue())
                .survey(existing.survey());
        for (Answer a : existing.answers()) {
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
     * @return
     */
    @RequestMapping(value = "/{returnId}/status/{status}", method = RequestMethod.POST, consumes = "application/json")
    @JsonView(SurveyReturnViews.Detailed.class)
    public @ResponseBody SurveyReturn setStatus(
            @PathVariable("returnId") Long returnId,
            HttpServletRequest req,
            @PathVariable("status") String status) {
        LOGGER.info(String.format("Setting survey %1$s to status %2$s",
                returnId, status));

        SurveyReturn survey = returnRepo.findOne(returnId).status(status);
        switch (StatusType.valueOf(status)) {
        case Published:
            publish(survey);
            break;
        case Submitted:
            submit(survey, req.getHeader("X-RunAs"));
            break;
        default:
            String msg = String.format("Setting return %1$d to %2$s is not allowed", returnId, status);
            throw new IllegalArgumentException(msg );
        }

        return returnRepo.save(survey);
    }

    protected void submit(SurveyReturn survey, String submitter) {
        survey.updatedBy(submitter == null ? AuthUtils.getUserId() : submitter);
        survey.submittedBy(submitter == null ? AuthUtils.getUserId() : submitter);
        survey.submittedDate(new Date());
        survey.status(StatusType.Submitted.name());
        for (Answer a : survey.answers()) {
            // Cannot overwrite published answers without calling restate
            if (!a.status().equals(StatusType.Published.name()) && !a.status().equals(StatusType.Superseded.name())) {
                a.setStatus(StatusType.Submitted.name());
            } else {
                LOGGER.warn("Cannot set status of answer {} to {} because it's been published or superseded", a.id(), a.status());
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
