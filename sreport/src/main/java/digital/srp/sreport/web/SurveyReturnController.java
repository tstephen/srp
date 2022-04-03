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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.DefaultCompletenessValidator;
import digital.srp.sreport.services.HistoricDataMerger;

/**
 * REST web service for accessing survey returns.
 *
 * @author Tim Stephenson
 */
@RestController
@RequestMapping(value = "/returns")
public class SurveyReturnController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyReturnController.class);

    @Value("${kp.application.base-uri:http://localhost:8080}")
    protected String baseUrl;

    @Autowired
    protected Calculator cruncher;

    @Autowired
    protected HistoricDataMerger historicDataMerger;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected QuestionRepository qRepo;

    protected List<Question> questions;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected DefaultCompletenessValidator validator = new DefaultCompletenessValidator();

    protected void initCache() {
        questions = qRepo.findAll();
        LOGGER.info("Cached {} questions", questions.size());
    }

    /**
     * Refresh the cache.
     *
     * @param returnId The return this answer belongs to.
     * @param q The name of the question this answer belongs to.
     * @param period The period of this answer, if omitted the period of the return is assumed.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/refreshCache", method = RequestMethod.POST)
    public @ResponseBody void refreshCache() throws IOException {
        initCache();
    }

    /**
     * Return a single survey return with the specified id.
     *
     * @return the specified survey.
     */
    @JsonView(SurveyReturnViews.Detailed.class)
    @GetMapping(value = "/{id}")
    @Transactional
    public @ResponseBody EntityModel<SurveyReturn> findById(@PathVariable("id") Long returnId) {
        LOGGER.info("findById {}", returnId);

        SurveyReturn rtn = returnRepo.findById(returnId)
                .orElseThrow(()-> new ObjectNotFoundException(SurveyReturn.class, returnId));
        // use logger for force child load
        LOGGER.info(String.format(
                "Found return with id %1$d with status %2$s and containing %3$d answers",
                rtn.id(), rtn.status(), rtn.answers().size()));

        return addLinks(rtn);
    }

    /**
     * Returns matching the specified survey and organisation.
     *
     * @return returns. May be more than one because occasionally returns are restated.
     */
    @JsonView(SurveyReturnViews.Detailed.class)
    @GetMapping(value = "/findBySurveyName/{surveyName}")
    @Transactional
    public @ResponseBody List<EntityModel<SurveyReturn>> findBySurvey(
            @PathVariable("surveyName") String surveyName) {
        LOGGER.info("findCurrentBySurvey {}", surveyName);
        return addLinks(returnRepo.findBySurveyName(surveyName));
    }

    /**
     * Returns matching the specified survey and organisation.
     *
     * @return returns. May be more than one because occasionally returns are restated.
     */
    @JsonView(SurveyReturnViews.Detailed.class)
    @GetMapping(value = "/findBySurveyNameAndOrg/{surveyName}/{org}")
    public @ResponseBody List<EntityModel<SurveyReturn>> findEntityBySurveyAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        return addLinks(findBySurveyAndOrg(surveyName, org));
    }

    protected List<SurveyReturn> findBySurveyAndOrg(String surveyName, String org) {
        LOGGER.info("findBySurveyAndOrg {}/{}", surveyName, org);

        List<SurveyReturn> returns = returnRepo.findBySurveyAndOrg(surveyName, org);
        if (returns.size()==0) {
            returns.add(createBlankReturn(surveyName, org, applicablePeriod(surveyName)));
        }

        return returns;
    }

    protected String applicablePeriod(String surveyName) {
        return surveyName.substring(surveyName.indexOf('-')+1);
    }

    protected synchronized SurveyReturn createBlankReturn(String surveyName, String org, String period) {
        LOGGER.info("createBlankReturn of {} for {}, {}", surveyName, org, period);

        try {
            Survey requested = surveyRepo.findByName(surveyName);
            SurveyReturn rtn = new SurveyReturn()
                    .name(String.format("%1$s-%2$s", requested.name(), org))
                    .survey(requested)
                    .org(org)
                    .applicablePeriod(requested.applicablePeriod())
                    .answers(new HashSet<Answer>());
            ensureInitialized(requested, rtn);
            importEricAnswers(surveyName, org);
            importLastYearIfExists(rtn);

            return rtn;
        } catch (NullPointerException e) {
            String msg = String.format(
                    "Unable to create blank return for %1$s of %2$s",
                    surveyName, org);
            LOGGER.error(msg);
            LOGGER.error("  have {} surveys currently", surveyRepo.count());
            for (Survey s : surveyRepo.findAll()) {
                LOGGER.error("  {}: {}", s.id(), s.name());
            }
            throw new IllegalStateException(msg);
        }
    }

    private void importLastYearIfExists(SurveyReturn rtn) {
        String srcPeriod = PeriodUtil.previous(rtn.survey().applicablePeriod());
        String srcRtnName = String.format("SDU-%1$s-%2$s", srcPeriod, rtn.org());
        importFromOtherReturn(srcRtnName, rtn);
    }

    protected void ensureInitialized(Survey requested, SurveyReturn rtn) {
        if (questions == null) initCache();
        for (SurveyCategory cat : requested.categories()) {
            for (Q q : cat.questionEnums()) {
                if (!rtn.answer(rtn.applicablePeriod(), q).isPresent()) {
                    Optional<Question> question = findQuestion(q.name());
                    LOGGER.debug("Return {}/{} contains {}? {}",
                            rtn.applicablePeriod(), rtn.org(), q, question.isPresent());
                    Answer answer = new Answer()
                            .question(question.orElseThrow(() -> new ObjectNotFoundException(Q.class, q)))
                            .addSurveyReturn(rtn)
                            .applicablePeriod(requested.applicablePeriod());
                    if (q.equals(Q.ORG_CODE)) {
                        answer.response(rtn.org());
                    }
                    if (LOGGER.isDebugEnabled()) {
                        for (Answer a : rtn.answers()) {
                            LOGGER.debug("  creating answer to {} in {}/{}",
                                    a.question().name(), rtn.applicablePeriod(),
                                    rtn.org());
                        }
                    }
                    rtn.answers().add(answer);
                }
            }
        }
        returnRepo.save(rtn);
    }

    @GetMapping(value = "/findCurrentBySurveyNameAndOrg/{surveyName}/{org}")
    public @ResponseBody EntityModel<SurveyReturn> findCurrentEntityBySurveyNameAndOrg(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info("findCurrentBySurveyNameAndOrg {}, {}", surveyName, org);

        return addLinks(validator
                .validate(findCurrentBySurveyNameAndOrg(surveyName, org)));
    }

    protected SurveyReturn findCurrentBySurveyNameAndOrg(String surveyName, String org) {
        if (org.length() > 3) {
            org = lookupOrgCode(org);
        }

        return getLatestRevision(findBySurveyAndOrg(surveyName, org));
    }

    protected SurveyReturn getLatestRevision(List<SurveyReturn> list) {
        if (list.size() == 0) {
            return null;
        }
        list.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
        SurveyReturn rtn = list.get(list.size()-1);
        LOGGER.info("Found {} returns for {},{} returning revision {} (id:{})",
                list.size(), rtn.survey().name(), rtn.org(), rtn.revision(),
                rtn.id());
        return rtn;
    }

    private String lookupOrgCode(String orgName) {
        List<Answer> answer = answerRepo.findByOrgName(orgName);
        if (answer.size() == 0) {
            throw new ObjectNotFoundException(SurveyReturn.class, orgName);
        }
        return answer.get(0).surveyReturns().iterator().next().org();
    }

    @GetMapping(value = "/importEric/{surveyName}/{org}")
    @Transactional
    public @ResponseBody SurveyReturn importEricAnswers(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        long count = 0;
        long start = System.currentTimeMillis();
        SurveyReturn trgtRtn = getLatestRevision(returnRepo.findBySurveyAndOrg(surveyName, org));

        List<Survey> surveys = surveyRepo.findEricSurveys();
        for (Survey survey : surveys) {
            count += importFromOtherReturn(survey.name(), trgtRtn);
        }
        LOGGER.info("Importing ERIC data added {} records and took {}ms", count, (System.currentTimeMillis()-start));

        return getLatestRevision(returnRepo.findBySurveyAndOrg(surveyName, org));
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @PostMapping(value = "/import/{sourceReturnName}/{targetReturnName}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void importFromOtherReturn(
            @PathVariable("sourceReturnName") String srcRtnName,
            @PathVariable("targetReturnName") String trgtRtnName) {
        SurveyReturn trgtRtn = getLatestRevision(returnRepo.findByName(trgtRtnName));
        importFromOtherReturn(srcRtnName, trgtRtn);
    }

    @Transactional
    protected long importFromOtherReturn(String srcRtnName, SurveyReturn trgtRtn) {
        long count = 0;
        long start = System.currentTimeMillis();
        SurveyReturn rtn = getLatestRevision(returnRepo.findByName(srcRtnName));
        Optional<SurveyReturn> srcRtn = Optional.ofNullable(rtn);
        if (srcRtn.isPresent()) {
            Set<Answer> answersToImport = answerRepo.findAnswersToImport(trgtRtn.id(), srcRtn.get().id());
            LOGGER.info("Found {} answers to import from {} to {}", answersToImport.size(), srcRtnName, trgtRtn.id());
            count += historicDataMerger.merge(answersToImport, trgtRtn);
            LOGGER.info("Importing from {} added {} records and took {}ms", srcRtnName, count, (System.currentTimeMillis()-start));
        } else {
            LOGGER.warn("No return {} found to import from for {} ({})", srcRtnName, trgtRtn.name(), trgtRtn.id());
        }
        return count;
    }

    /**
     * @return list of survey returns, optionally paged.
     */
    @JsonView(SurveyReturnViews.Summary.class)
    @RolesAllowed(SrpRoles.ANALYST)
    @GetMapping(value = "/")
    public @ResponseBody List<EntityModel<SurveyReturn>> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List returns");

        List<SurveyReturn> list;
        if (limit == null) {
            list = returnRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = returnRepo.findPage(pageable);
        }
        LOGGER.info("Found {} returns", list.size());

        return addLinks(list);
    }

    /**
     * Return a list of survey returns for a single organisation.
     *
     * @return survey returns.
     */
    @JsonView(SurveyReturnViews.Summary.class)
    @RolesAllowed(SrpRoles.ANALYST)
    @GetMapping(value = "/findByOrg/{org}")
    public @ResponseBody List<EntityModel<SurveyReturn>> findByOrg(
            @PathVariable("org") String org) {
        LOGGER.info("List returns for {}", org);

        List<SurveyReturn> list = returnRepo.findByOrg(org);
        LOGGER.info("Found {} returns", list.size());

        return addLinks(list);
    }

    /**
     * Update an existing return with a single answer.
     *
     * <p>The period of the return is assumed.
     *
     * @param returnId The return this answer belongs to.
     * @param q The name of the question this answer belongs to.
     *
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(value = "/{id}/answers/{q}", consumes = "application/json")
    public @ResponseBody void updateCurrentAnswer(
            @PathVariable("id") Long returnId,
            @PathVariable("q") String q,
            @RequestBody String updatedAns) {
        SurveyReturn existing = returnRepo.findById(returnId)
                .orElseThrow(()-> new ObjectNotFoundException(SurveyReturn.class, returnId));

        updateAnswer(existing, q, existing.applicablePeriod(), updatedAns);
    }

    /**
     * Update an existing return with a single answer.
     *
     * @param returnId The return this answer belongs to.
     * @param q The name of the question this answer belongs to.
     * @param period The period of this answer, if omitted the period of the return is assumed.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(value = "/{id}/answers/{q}/{period}", consumes = "application/json")
    public @ResponseBody void updateAnswer(
            @PathVariable("id") Long returnId,
            @PathVariable("q") String q,
            @PathVariable("period") String period,
            @RequestBody String updatedAns) {
        SurveyReturn existing = returnRepo.findById(returnId)
                .orElseThrow(()-> new ObjectNotFoundException(SurveyReturn.class, returnId));

        updateAnswer(existing, q, period, updatedAns);
    }

    protected void updateAnswer(SurveyReturn rtn, String q,
            String period, String updatedAns) {
        LOGGER.info("Updating answer to {} for {} in {} to {}", q, rtn.org(), period, updatedAns);
        Optional<Answer> answer = rtn.answer(period, Q.valueOf(q));
        if (answer.isPresent()) {
            answer.get().response(updatedAns).derived(false);
        } else {
            LOGGER.warn("Creating new answer to {} for {} in {}", q, rtn.org(), period);
            Optional<Question> existingQ = findQuestion(q);
            if (existingQ.isPresent()) {
                rtn.initAnswer(rtn, period, existingQ.get())
                        .response(updatedAns).derived(false);
            } else {
                throw new ObjectNotFoundException(Question.class, q);
            }
        }
        DefaultCompletenessValidator validator = new DefaultCompletenessValidator() ;
        validator.validate(rtn);
        returnRepo.save(rtn);
    }

    protected Optional<Question> findQuestion(String q) {
        for (Question question : questions) {
            if (q.equals(question.q().name())) {
                return Optional.of(question);
            }
        }
        return Optional.empty();
    }

    /**
     * Re-stating a return preserves the existing one and saves the updates as a new revision.
     */
    @JsonView(SurveyReturnViews.Summary.class)
    @PostMapping(value = "/{id}/restate", consumes = { "application/json" })
    @Transactional
    public @ResponseBody EntityModel<SurveyReturn> restate(
            @PathVariable("id") Long returnId) {
        SurveyReturn existing = returnRepo.findById(returnId)
                .orElseThrow(()-> new ObjectNotFoundException(SurveyReturn.class, returnId));
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
        return addLinks(returnRepo.save(returnRepo.save(restatedRtn)));
    }

    /**
     * Change the status the return has reached.
     * @return The updated return
     */
    @JsonView(SurveyReturnViews.Summary.class)
    @PostMapping(value = "/{returnId}/status/{status}", consumes = "application/json")
    public @ResponseBody EntityModel<SurveyReturn> setStatus(
            @PathVariable("returnId") Long returnId,
            @PathVariable("status") String status,
            @RequestHeader("X-RunAs") String runAs) {
        LOGGER.info("Setting survey {} to status {}", returnId, status);

        SurveyReturn survey = returnRepo.findById(returnId)
                .orElseThrow(()-> new ObjectNotFoundException(SurveyReturn.class, returnId))
                .status(status);
        switch (StatusType.valueOf(status)) {
        case Published:
            publish(survey);
            break;
        case Submitted:
            submit(survey, runAs);
            break;
        default:
            String msg = String.format("Setting return %1$d to %2$s is not allowed", returnId, status);
            throw new IllegalArgumentException(msg );
        }

        return addLinks(returnRepo.save(survey));
    }

    protected void submit(SurveyReturn survey, String submitter) {
        survey.setUpdatedBy(submitter);
        survey.setSubmittedBy(submitter);
        survey.setSubmittedDate(new Date());
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
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    @Transactional
    public @ResponseBody void delete(
            @PathVariable("id") Long returnId) {
        returnRepo.deleteById(returnId);
    }

    protected List<EntityModel<SurveyReturn>> addLinks(List<SurveyReturn> returns) {
        List<EntityModel<SurveyReturn>> entities = new ArrayList<EntityModel<SurveyReturn>>();
        for (SurveyReturn rtn : returns) {
            entities.add(addLinks(rtn));
        }
        return entities;
    }

    protected EntityModel<SurveyReturn> addLinks(SurveyReturn rtn) {
        EntityModel<SurveyReturn> model = EntityModel.of(rtn,
                linkTo(methodOn(SurveyReturnController.class).findById(rtn.id())).withSelfRel()
                );
        model.add(linkTo(methodOn(SurveyController.class).findById(rtn.survey().id())).withRel("survey"));
        return model;
    }
}
