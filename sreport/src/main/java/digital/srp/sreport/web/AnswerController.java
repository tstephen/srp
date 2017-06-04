package digital.srp.sreport.web;

import java.util.ArrayList;
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

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Criterion;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.AnswerViews;
import digital.srp.sreport.repositories.AnswerRepository;

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

        Answer answer = answerRepo.findOne(answerId);

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
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = answerRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s answers", list.size()));

        return list;
    }

    @RequestMapping(value = "/findByCriteria", method = RequestMethod.POST)
    @JsonView(AnswerViews.Detailed.class)
    public @ResponseBody List<Answer> findByCriteria(
            @RequestBody List<Criterion> criteria,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List answers by criteria %1$s", criteria));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Answer> cq = builder.createQuery(Answer.class);
        Root<Answer> answer = cq.from(Answer.class);
        Join<Answer, Question> questions = answer.join("question");
        Join<Answer, SurveyReturn> surveyReturns = answer.join("surveyReturns");
//        cq.select(answer);

        Predicate predicate = builder.conjunction();

        for (Criterion criterion : criteria) {
            switch (criterion.getField()) {
            case "org":
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                surveyReturns.get("org"),
                                criterion.getValue().toString()));
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
            default:
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                answer.get(criterion.getField()),
                                criterion.getValue()));
            }
        }
        cq.where(predicate);

        // TODO how to ensure result set is not too large? No count option
        List<Answer> result = entityManager.createQuery(cq).getResultList();
        LOGGER.info("... found {} answers", result.size());
        return result;
    }

    /**
     * Return a list of answers for a given survey, optionally paged.
     *
     * @return answers.
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
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = answerRepo.findPageByPeriod(pageable, period);
        }
        LOGGER.info(String.format("Found %1$s answers", list.size()));

        return list;
    }

    /**
     * Delete an existing answer.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long answerId) {
        answerRepo.delete(answerId);
    }

    private Answer addLinks(Answer answer) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + answer.id()));

        return answer.links(links);
    }
}
