package digital.srp.sreport.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.views.WeightingFactorViews;
import digital.srp.sreport.repositories.WeightingFactorRepository;

/**
 * REST web service for accessing carbon factors.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/wfactors")
public class WeightingFactorController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(WeightingFactorController.class);

    @Autowired 
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected  WeightingFactorRepository wfactorRepo;

    @PostConstruct
    protected void init() throws IOException {
        List<WeightingFactor> existingFactors = wfactorRepo.findAll();
        List<WeightingFactor> factors = new WeightingFactorCsvImporter().readWeightingFactors();
        
        for (WeightingFactor factor : factors) {
            if (existingFactors.contains(factor)) {
                LOGGER.info(String.format(
                        "Skip import of existing factor: %1$s for: %2$s",
                        factor.orgType(), factor.applicablePeriod()));
            } else {
                wfactorRepo.save(factor);
            }
        }
    }
    
    /**
     * Return just the specified cfactor.
     * 
     * @return The specified cfactor.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody WeightingFactor findById(
            @PathVariable("id") Long cfactorId) {
        LOGGER.info(String.format("findById %1$s", cfactorId));

        WeightingFactor cfactor = wfactorRepo.findOne(cfactorId);

        return addLinks(cfactor);
    }

    /**
     * Return just the specified cfactor.
     * 
     * @return The specified cfactor.
     */
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody WeightingFactor findByName(
            @PathVariable("name") String name) {
        LOGGER.info(String.format("findByName %1$s", name));

        WeightingFactor cfactor = wfactorRepo.findByOrgType(name);

        return cfactor;
    }
    
    /**
     * Return a list of carbon factors, optionally paged.
     * 
     * @return carbon factors.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Summary.class)
    public @ResponseBody List<WeightingFactor> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List carbon factors"));

        List<WeightingFactor> list;
        if (limit == null) {
            list = wfactorRepo.findAll();
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = wfactorRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s carbon factors", list.size()));

        return list;
    }
    
    /**
     * Create a new cfactor.
     * 
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @RequestBody WeightingFactor cfactor) {

        createInternal(cfactor);

        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromController(getClass());
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("id", cfactor.id().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    protected void createInternal(WeightingFactor cfactor) {
        cfactor = wfactorRepo.save(cfactor);
    }

    /**
     * Update an existing cfactor.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void update(
            @PathVariable("id") Long cfactorId,
            @RequestBody WeightingFactor updatedWeightingFactor) {
//        WeightingFactor cfactor = cfactorRepo.findOne(cfactorId);
//
//        NullAwareBeanUtils.copyNonNullProperties(updatedOrder, cfactor, "id");

        wfactorRepo.save(updatedWeightingFactor);
    }


    /**
     * Delete an existing cfactor.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long cfactorId) {
        wfactorRepo.delete(cfactorId);
    }

    private WeightingFactor addLinks(WeightingFactor cfactor) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(getClass().getAnnotation(RequestMapping.class).value()[0] + "/" + cfactor.id()));
        
        return cfactor.links(links);
    }
}
