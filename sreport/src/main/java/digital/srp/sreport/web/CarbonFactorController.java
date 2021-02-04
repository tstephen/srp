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
import org.springframework.web.bind.annotation.RequestHeader;
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
import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.views.CarbonFactorViews;
import digital.srp.sreport.repositories.CarbonFactorRepository;

/**
 * REST web service for accessing carbon factors.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/cfactors")
public class CarbonFactorController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CarbonFactorController.class);

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected  CarbonFactorRepository cfactorRepo;

    @PostConstruct
    protected void init() throws IOException {
        LOGGER.info("init");
        List<CarbonFactor> existingFactors = cfactorRepo.findAll();
        List<CarbonFactor> factors = new CarbonFactorCsvImporter().readCarbonFactors();

        int added = 0;
        for (CarbonFactor factor : factors) {
            // TODO this will not update with new values but only create non-existent factors
            if (existingFactors.contains(factor)) {
                LOGGER.info(String.format(
                        "Skip import of existing factor: %1$s for: %2$s",
                        factor.name(), factor.applicablePeriod()));
            } else {
                factor.createdBy("Installer");
                createInternal(factor);
                added++;
            }
        }
        LOGGER.info("init complete: Carbon factors added {}", added);
    }

    /**
     * @return The specified Carbon factor.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(CarbonFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody CarbonFactor findById(
            @PathVariable("id") Long cfactorId) {
        LOGGER.info(String.format("findById %1$s", cfactorId));

        CarbonFactor cfactor = cfactorRepo.findById(cfactorId)
                .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, cfactorId));

        return addLinks(cfactor);
    }

    /**
     * @return The specified Carbon factor.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @JsonView(CarbonFactorViews.Detailed.class)
    @Transactional
    public @ResponseBody List<CarbonFactor> findByName(
            @PathVariable("name") String name) {
        LOGGER.info("findByName {}", name);

        List<CarbonFactor> cfactors = cfactorRepo.findByName(name);

        LOGGER.info("  found {}", cfactors.size());
        return cfactors;
    }

    /**
     * @return list of Carbon factors, optionally paged.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(CarbonFactorViews.Summary.class)
    public @ResponseBody List<CarbonFactor> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List carbon factors"));

        List<CarbonFactor> list;
        if (limit == null) {
            list = cfactorRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = cfactorRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s carbon factors", list.size()));

        return list;
    }

    /**
     * @return Newly created Carbon factor.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @RequestHeader String Authorization,
            @RequestBody CarbonFactor cfactor) {

        createInternal(cfactor);

        UriComponentsBuilder builder = MvcUriComponentsBuilder
                .fromController(getClass());
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("id", cfactor.id().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/{id}").buildAndExpand(vars).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    protected void createInternal(CarbonFactor cfactor) {
        cfactor.created(new Date());
        cfactor = cfactorRepo.save(cfactor);
    }

    /**
     * Update an existing Carbon factor.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    @Transactional
    public @ResponseBody void update(
            @PathVariable("id") Long cfactorId,
            @RequestBody CarbonFactor updatedCarbonFactor) {
        cfactorRepo.save(updatedCarbonFactor);
    }


    /**
     * Delete an existing Carbon factor.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long cfactorId) {
        cfactorRepo.deleteById(cfactorId);
    }

    private CarbonFactor addLinks(CarbonFactor cfactor) {
        return cfactor.links(Collections.singletonList(Link
                .of(getClass().getAnnotation(RequestMapping.class).value()[0]
                        + "/" + cfactor.id())));
    }
}
