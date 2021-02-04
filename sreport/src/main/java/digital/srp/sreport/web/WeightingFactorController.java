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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
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

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
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
                factor.createdBy("Inst");
                createInternal(factor);
            }
        }
    }

    /**
     * @return The specified weighting factor.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody WeightingFactor findById(
            @PathVariable("id") Long factorId) {
        LOGGER.info(String.format("findById %1$s", factorId));

        WeightingFactor factor = wfactorRepo.findById(factorId)
                .orElseThrow(()-> new ObjectNotFoundException(WeightingFactor.class, factorId));

        return addLinks(factor);
    }

    /**
     * @return The specified weighting factor.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody WeightingFactor findByName(
            @PathVariable("name") String name) {
        LOGGER.info(String.format("findByName %1$s", name));

        WeightingFactor factor = wfactorRepo.findByOrgType(name);

        return factor;
    }

    /**
     * @return List if weighting factors, optionally paged.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(WeightingFactorViews.Summary.class)
    public @ResponseBody List<WeightingFactor> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List weighting factors"));

        List<WeightingFactor> list;
        if (limit == null) {
            list = wfactorRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = wfactorRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s carbon factors", list.size()));

        return list;
    }

    /**
     * @return The newly-created weighting factor.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @RequestBody WeightingFactor factor) {

        createInternal(factor);

        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromController(getClass());
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("id", factor.id().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    protected void createInternal(WeightingFactor factor) {
        factor.created(new Date());
        factor = wfactorRepo.save(factor);
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
        wfactorRepo.deleteById(cfactorId);
    }

    private WeightingFactor addLinks(WeightingFactor cfactor) {
        return cfactor.links(Collections.singletonList(Link
                .of(getClass().getAnnotation(RequestMapping.class).value()[0]
                        + "/" + cfactor.id())));
    }
}
