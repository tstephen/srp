package digital.srp.sreport.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.api.exceptions.ResultSetTooLargeException;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Criterion;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.AnswerViews;
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
     * Return just the specified answer.
     *
     * @return The specified answer.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(AnswerViews.Detailed.class)
    @Transactional
    public @ResponseBody Answer findById(
            @PathVariable("id") Long answerId) {
        LOGGER.info(String.format("findById %1$s", answerId));

        Answer answer = answerRepo.findById(answerId)
                .orElseThrow(() -> new ObjectNotFoundException(Answer.class, answerId));

        return addLinks(answer);
    }

    // TODO remove or require paging
    /**
     * Return a list of answers, optionally paged.
     *
     * @return answers.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(AnswerViews.Summary.class)
    public @ResponseBody List<Answer> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List answers"));

        List<Answer> list;
        if (limit == null) {
            list = answerRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = answerRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s answers", list.size()));

        addQuestionCategory(list);
        return list;
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

    @RequestMapping(value = "/findByCriteria", method = RequestMethod.POST)
    @JsonView(AnswerViews.Detailed.class)
    public @ResponseBody List<Answer> findByCriteria(
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
                SurveyCategory category = catRepo.findByName(
                        criterion.getValue());

                predicate = builder.and(
                        predicate,
                        questions.get("name").in(
                                    ((Object[]) category.questionNames().split(","))));
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
            return list;
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
    @RequestMapping(value = "/findByPeriod/{period}", method = RequestMethod.GET)
    @JsonView(AnswerViews.Detailed.class)
    public @ResponseBody List<Answer> findByPeriod(
            @PathVariable("period") String period,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List answers by period %1$s", period));

        List<Answer> list;
        if (limit == null) {
            list = answerRepo.findByPeriod(period);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = answerRepo.findPageByPeriod(pageable, period);
        }
        LOGGER.info(String.format("Found %1$s answers", list.size()));

        addQuestionCategory(list);
        return list;
    }

    /**
     * @return answer to the given question for the specified return and period.
     */
    @RequestMapping(value = "/findByReturnPeriodAndQ/{rtn}/{period}/{q}", method = RequestMethod.GET)
    @JsonView(AnswerViews.Detailed.class)
    public @ResponseBody Answer findByReturnPeriodAndQ(
            @PathVariable("rtn") Long rtnId,
            @PathVariable("period") String period,
            @PathVariable("q") String q) {
        LOGGER.info("find answer by rtn {}, period {} and q {}", rtnId, period, q);

        List<Answer> list = answerRepo.findByReturnPeriodAndQ(rtnId, period, q);
        Answer a;
        switch (list.size()) {
        case 0:
            Question question = qRepo.findByName(q);
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

        return a;
    }

    /**
     * Delete an existing answer.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long answerId) {
        answerRepo.deleteById(answerId);
    }

    private Answer addLinks(Answer answer) {
        return answer.links(Collections.singletonList(Link
                .of(getClass().getAnnotation(RequestMapping.class).value()[0]
                        + "/" + answer.id())));
    }
}
