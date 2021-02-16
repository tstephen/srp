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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.api.exceptions.ResultSetTooLargeException;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Criterion;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;

/**
 * REST web service for accessing answers.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/answers")
public class AnswerController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AnswerController.class);

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected SurveyReturnRepository rtnRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    @Value("${srp.reporting.maxRows:5000}")
    private int maxAnswers;

    /**
     * @return The specified answer.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody EntityModel<Answer> findById(
            @PathVariable("id") Long answerId) {
        LOGGER.info("findById {}", answerId);

        return addLinks(answerRepo.findById(answerId)
                .orElseThrow(() -> new ObjectNotFoundException(Answer.class, answerId)));
    }

    // TODO remove or require paging
    /**
     * @return answers.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<Answer>> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List answers");

        List<Answer> list;
        if (limit == null) {
            list = answerRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = answerRepo.findPage(pageable);
        }
        LOGGER.info("Found {} answers", list.size());

        addQuestionCategory(list);
        return addLinks(list);
    }

    private void addQuestionCategory(List<Answer> list) {
        List<SurveyCategory> categories = catRepo.findAll();
        for (Answer answer : list) {
            answer.question().categories(new ArrayList<String>());
            for (SurveyCategory cat : categories) {
                LOGGER.debug("Does {} contain {}?", cat.name(), answer.question().name());
                if (cat.questionEnums().contains(answer.question().q())) {
                    answer.question().categories().add(cat.name());
                }
            }
            if (answer.question().categories().size() == 0) {
                answer.question().categories().add("Unspecified");
            }
        }
    }

    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/findByCriteria", method = RequestMethod.POST)
    public @ResponseBody List<EntityModel<Answer>> findByCriteria(
            @RequestBody List<Criterion> criteria,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit)
    throws Exception {
        LOGGER.info("List answers by criteria {}", criteria);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Answer> cq = builder.createQuery(Answer.class);
        Root<Answer> answer = cq.from(Answer.class);
        Join<Answer, Question> questions = answer.join("question");
        Join<Answer, SurveyReturn> surveyReturns = answer.join("surveyReturns");

        Predicate predicate = builder.conjunction();

        for (Criterion criterion : criteria) {
            switch (criterion.getField()) {
            case "category":
                Optional<SurveyCategory> category = catRepo.findByName(
                        criterion.getValue());
                if (category.isPresent()) {
                    predicate = builder.and(predicate,
                            questions.get("name").in(((Object[]) category.get()
                                    .questionNames().split(","))));
                }
                break;
            case "org":
            case "organisation":
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                surveyReturns.get("org"),
                                criterion.getValue().toString()));
                break;
            case "orgType":
            case "organisation type":
                predicate = builder.and(
                        predicate,
                        surveyReturns.get("org")
                                .in(getOrgsMatching(Q.ORG_TYPE).toArray()));
                break;
            case "period":
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                answer.get("applicablePeriod"),
                                criterion.getValue().toString()));
                break;
            case "question":
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                questions.get("name"),
                                criterion.getValue().toString()));
                break;
            case "region":
                predicate = builder.and(
                        predicate,
                        surveyReturns.get("org")
                                .in(getOrgsMatching(Q.COMMISSIONING_REGION).toArray()));
                break;
            default:
                // e.g. status
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                answer.get(criterion.getField()),
                                criterion.getValue()));
            }
        }
        cq.where(predicate);
        LOGGER.debug("... built query: {}", predicate);

        List<Answer> list = entityManager.createQuery(cq).setMaxResults(maxAnswers).getResultList();
        LOGGER.info("... found {} answers", list.size());

        if (list.size() > maxAnswers) {
            throw new ResultSetTooLargeException(criteria, list.size());
        } else {
            addQuestionCategory(list);
            return addLinks(list);
        }
    }

    private HashSet<String> getOrgsMatching(Q q) {
        HashSet<String> orgs = new HashSet<String>();
        List<Answer> answers = answerRepo.findByQuestion(q.code());
        for (Answer a : answers) {
            for (SurveyReturn rtn : a.surveyReturns()) {
                orgs.add(rtn.org());
            }
        }
        return orgs;
    }

    /**
     * @return list of answers for a given period, optionally paged.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @RequestMapping(value = "/findByPeriod/{period}", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<Answer>> findByPeriod(
            @PathVariable("period") String period,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List answers by period {}", period);

        List<Answer> list;
        if (limit == null) {
            list = answerRepo.findByPeriod(period);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = answerRepo.findPageByPeriod(pageable, period);
        }
        LOGGER.info("Found {} answers", list.size());

        addQuestionCategory(list);
        return addLinks(list);
    }

    /**
     * @return answer to the given question for the specified return and period.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/findByReturnPeriodAndQ/{rtn}/{period}/{q}", method = RequestMethod.GET)
    public @ResponseBody EntityModel<Answer> findByReturnPeriodAndQ(
            @PathVariable("rtn") Long rtnId,
            @PathVariable("period") String period,
            @PathVariable("q") String q) {
        LOGGER.info("find answer by rtn {}, period {} and q {}", rtnId, period, q);

        List<Answer> list = answerRepo.findByReturnPeriodAndQ(rtnId, period, q);
        Answer a;
        switch (list.size()) {
        case 0:
            Question question = qRepo.findByName(q)
                    .orElseThrow(() -> new ObjectNotFoundException(Question.class, q));
            SurveyReturn rtn = rtnRepo.findById(rtnId)
                    .orElseThrow(() -> new ObjectNotFoundException(SurveyReturn.class, rtnId));
            a = new Answer().question(question).addSurveyReturn(rtn)
                    .applicablePeriod(period);
            if (q.equals(Q.ORG_CODE.code())) {
                a.response(rtn.org());
            }
            answerRepo.save(a);
            break;
        case 1:
            a = list.get(0);
            break;
        default:
            list.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
            a = list.get(0);
            break;
        }

        return addLinks(a);
    }

    /**
     * Delete an existing answer.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long answerId) {
        answerRepo.deleteById(answerId);
    }

    protected List<EntityModel<Answer>> addLinks(final List<Answer> list) {
        ArrayList<EntityModel<Answer>> entities = new ArrayList<EntityModel<Answer>>();
        for (Answer answer : list) {
            entities.add(addLinks(answer));
        }
        return entities;
    }

    protected EntityModel<Answer> addLinks(final Answer answer) {
        return EntityModel.of(answer,
                linkTo(methodOn(AnswerController.class).findById(answer.id()))
                        .withSelfRel());
    }
}
