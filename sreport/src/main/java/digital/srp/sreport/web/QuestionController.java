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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.views.QuestionViews;
import digital.srp.sreport.repositories.QuestionRepository;

/**
 * REST web service for accessing questions.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/questions")
public class QuestionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionController.class);

    @Autowired 
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected  QuestionRepository questionRepo;
    
    /**
     * Return just the specified question.
     * 
     * @return The specified question.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(QuestionViews.Detailed.class)
    @Transactional
    public @ResponseBody Question findById(
            @PathVariable("id") Long questionId) {
        LOGGER.info(String.format("findById %1$s", questionId));

        Question question = questionRepo.findOne(questionId);

        return addLinks(question);
    }

    /**
     * Return just the specified question.
     * 
     * @return The specified question.
     */
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(QuestionViews.Detailed.class)
    @Transactional
    public @ResponseBody Question findByName(
            @PathVariable("name") String name) {
        LOGGER.info(String.format("findByName %1$s", name));

        Question question = questionRepo.findByName(name);
        // use logger for force child load
        LOGGER.info(String.format("Found question with id %1$d named %2$s", 
                question.id(), question.name()));

        return question;
    }
    
    /**
     * Return a list of questions, optionally paged.
     * 
     * @return questions.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(QuestionViews.Summary.class)
    public @ResponseBody List<Question> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List questions"));

        List<Question> list;
        if (limit == null) {
            list = questionRepo.findAll();
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = questionRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s questions", list.size()));

        return list;
    }
    
    /**
     * Delete an existing question.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long questionId) {
        questionRepo.delete(questionId);
    }

    private Question addLinks(Question question) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + question.id()));
        
        return question.links(links);
    }
}
