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
package digital.srp.macc.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.importers.InterventionCsvImporter;
import digital.srp.macc.model.Intervention;
import digital.srp.macc.repositories.InterventionRepository;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.repositories.OrganisationTypeRepository;

/**
 * REST endpoint for accessing {@link Interventions}
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/interventions")
@DependsOn({"organisationTypeController", "interventionTypeController"})
public class InterventionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionController.class);

    @Autowired
    protected InterventionRepository interventionRepo;

    @Autowired
    protected OrganisationTypeRepository orgTypeRepo;

    @Autowired
    protected InterventionTypeRepository intvnTypeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    protected void init() throws IOException {
        LOGGER.info("init");
        List<Intervention> intvns = new InterventionCsvImporter(intvnTypeRepo, orgTypeRepo).readInterventions();

        int added = 0;
        // TODO this will not update with new values but only create non-existent records
        for (Intervention intvn : intvns) {
            if (interventionRepo.findByOrganisationTypeAndInterventionTypeNames(
                    intvn.getTenantId(), intvn.getName(), intvn.getOrganisationType().getName()).isPresent()) {
                LOGGER.info("Skip import of existing intervention {} {}",
                        intvn.getOrganisationType().getName(),
                        intvn.getInterventionType().getName());
            } else {
                interventionRepo.save(intvn);
                added++;
            }
        }
        LOGGER.info("init complete: interventions added {}", added);
    }

    /**
     * Imports JSON representation of interventions.
     *
     * <p>
     * This is a handy link: http://shancarter.github.io/mr-data-converter/
     *
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the JSON.
     */
    @RequestMapping(value = "/uploadjson", method = RequestMethod.POST)
    public @ResponseBody Iterable<Intervention> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info("Uploading interventions for: {}", tenantId);
        String content = new String(file.getBytes());

        List<Intervention> list = objectMapper.readValue(content,
                new TypeReference<List<Intervention>>() {
                });
        LOGGER.info("  found {} interventions", list.size());
        ArrayList<Intervention> result = new ArrayList<Intervention>();
        for (Intervention intervention : list) {
            intervention.setTenantId(tenantId);

            try {
                interventionRepo.save(intervention);
            } catch (Exception e) {
                LOGGER.error("Problem saving {}", intervention
                        .getInterventionType().getName());
            }
            result.add(intervention);
        }

        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Update an existing intervention.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long interventionId,
            @RequestBody Intervention updatedIntvn) {
        Intervention intvn = interventionRepo.findById(interventionId)
                .orElseThrow(() -> new ObjectNotFoundException(interventionId, Intervention.class.getSimpleName()));

        BeanUtils.copyProperties(updatedIntvn, intvn, "id");
        intvn.setTenantId(tenantId);
        interventionRepo.save(intvn);
    }

    /**
     * Return just the interventions for a specific tenant.
     *
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<EntityModel<digital.srp.macc.model.Intervention>> listForTenantAsJson(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return listForTenant(tenantId, page, limit);
    }

    /**
     * Export all interventions for the tenant.
     *
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<EntityModel<digital.srp.macc.model.Intervention>> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return listForTenant(tenantId, page, limit);
    }

    protected List<EntityModel<digital.srp.macc.model.Intervention>> listForTenant(String tenantId, Integer page,
            Integer limit) {
        LOGGER.info("List interventions for tenant {}", tenantId);

        List<Intervention> list;
        if (limit == null) {
            list = interventionRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = interventionRepo.findPageForTenant(tenantId, pageable);
        }

        LOGGER.info("Found {} interventions", list.size());
        return addLinks(list);
    }

    /**
     * Return just the matching interventions for a specific tenant and
     * organisation type.
     *
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/status/{status}/{orgTypeName}", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<digital.srp.macc.model.Intervention>> findByStatusForTenantAndOrgType(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status,
            @PathVariable("orgTypeName") String orgTypeName) {
        LOGGER.info(String
                .format("List interventions for tenant %1$s with status %2$s and org type %3$s",
                        tenantId, status, orgTypeName));

        List<Intervention> list = interventionRepo
                .findByStatusForTenantAndOrgType(tenantId, status, orgTypeName);
        LOGGER.info("Found {} interventions", list.size());

        return addLinks(list);
    }

    /**
     * @return The specified intervention.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody EntityModel<digital.srp.macc.model.Intervention> findById(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long interventionId) {
        LOGGER.info("findById {}", interventionId);

        Intervention intvn = interventionRepo.findById(interventionId)
                .orElseThrow(() -> new ObjectNotFoundException(interventionId,
                        Intervention.class.getSimpleName()));

        return addLinks(intvn);
    }

    protected List<EntityModel<Intervention>> addLinks(List<Intervention> intvns) {
        List<EntityModel<Intervention>> entities = new ArrayList<EntityModel<Intervention>>();
        for (Intervention rtn : intvns) {
            entities.add(addLinks(rtn));
        }
        return entities;
    }

    protected EntityModel<Intervention> addLinks(Intervention intvn) {
        return EntityModel.of(intvn,
                linkTo(methodOn(InterventionController.class)
                        .findById(intvn.getTenantId(), intvn.getId()))
                                .withSelfRel());
    }
}
