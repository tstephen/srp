package digital.srp.sreport.web;

import java.util.Collections;
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

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.views.SurveyCategoryViews;
import digital.srp.sreport.repositories.SurveyCategoryRepository;

/**
 * REST web service for accessing category categories.
 */
@Controller
@RequestMapping(value = "/survey-categories")
public class SurveyCategoryController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyCategoryController.class);

    @Autowired 
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected  SurveyCategoryRepository catRepo;

    /**
     * Return just the specified category.
     * 
     * @return The specified category.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(SurveyCategoryViews.Detailed.class)
    @Transactional
    public @ResponseBody SurveyCategory findById(
            @PathVariable("id") Long catId) {
        LOGGER.info(String.format("findById %1$s", catId));

        SurveyCategory cat = catRepo.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(SurveyCategory.class, catId));

        return addLinks(cat);
    }

    /**
     * Return a list of categories, optionally paged.
     * 
     * @return categories.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(SurveyCategoryViews.Summary.class)
    public @ResponseBody List<SurveyCategory> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List categories"));

        List<SurveyCategory> list;
        if (limit == null) {
            list = catRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = catRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s categories", list.size()));

        return list;
    }
    
    /**
     * Delete an existing survey category.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long catId) {
        catRepo.deleteById(catId);
    }

    private SurveyCategory addLinks(SurveyCategory cat) {
        return cat.links(Collections.singletonList(Link.of(
                getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + cat.id())));
    }
}
