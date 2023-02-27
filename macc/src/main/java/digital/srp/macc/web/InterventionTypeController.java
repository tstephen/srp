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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.importers.InterventionTypeCsvImporter;
import digital.srp.macc.internal.CsvImporter;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.repositories.InterventionTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;

/**
 * REST endpoint for accessing {@link InterventionType}
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/intervention-types")
public class InterventionTypeController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionTypeController.class);

    @Autowired
    protected InterventionTypeRepository interventionTypeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    protected void init() throws IOException {
        LOGGER.info("init");
        List<InterventionType> orgTypes = new InterventionTypeCsvImporter().readInterventionTypes();

        int added = 0;
        // TODO this will not update with new values but only create non-existent records
        for (InterventionType orgType : orgTypes) {
            if (interventionTypeRepo.findByName(orgType.getTenantId(), orgType.getName()).isPresent()) {
                LOGGER.info("Skip import of existing org type: {}",
                        orgType.getName());
            } else {
                interventionTypeRepo.save(orgType);
                added++;
            }
        }
        LOGGER.info("init complete: organisation types added {}", added);
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
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Iterable<InterventionType> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String
                .format("Uploading interventions for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<InterventionType> list = objectMapper.readValue(content,
                new TypeReference<List<InterventionType>>() {
                });
        LOGGER.info("  found {} interventions", list.size());
        for (InterventionType message : list) {
            message.setTenantId(tenantId);
        }

        Iterable<InterventionType> result = interventionTypeRepo.saveAll(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Imports CSV representation of intervention types.
     *
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the CSV.
     */
    @RequestMapping(value = "/uploadcsv", method = RequestMethod.POST)
    public @ResponseBody Iterable<InterventionType> handleCsvFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info("Uploading CSV interventionTypes for: {}", tenantId);
        String content = new String(file.getBytes());
        List<InterventionType> list = new CsvImporter().readInterventionTypes(
                new StringReader(
                content), content.substring(0, content.indexOf('\n'))
                .split(","));
        LOGGER.info("  found {} interventionTypes", list.size());
        for (InterventionType interventionType : list) {
            interventionType.setTenantId(tenantId);
        }

        Iterable<InterventionType> result = interventionTypeRepo.saveAll(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * @return Location header for newly created intervention type.
     */
    @RolesAllowed("admin")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @PathVariable("tenantId") String tenantId,
            @RequestHeader String Authorization,
            @RequestBody InterventionType param) {

        EntityModel<InterventionType> model = addLinks(
                tenantId, interventionTypeRepo.save(param));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(model.getLink("self").get().toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Return just the interventions for a specific tenant.
     *
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<EntityModel<InterventionType>> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List interventions for tenant {}", tenantId);

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info("Found {} intervention types", list.size());

        return addLinks(tenantId, list);
    }

    /**
     * @param idOrName database id:Long or name:String
     * @return The specified intervention type.
     * @throws UnsupportedEncodingException
     * @throws ObjectNotFoundException
     */
    @RequestMapping(value = "/{idOrName}", method = RequestMethod.GET)
    public @ResponseBody EntityModel<InterventionType> findByIdOrName(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("idOrName") String idOrName) {
        LOGGER.info("findByIdOrName {}", idOrName);

        try {
            return addLinks(tenantId, interventionTypeRepo
                    .findById(Long.parseLong(idOrName)).orElseThrow(
                            () -> new ObjectNotFoundException((Object) idOrName,
                                    InterventionType.class.getSimpleName())));
        } catch (NumberFormatException e) {
            return addLinks(tenantId, interventionTypeRepo
                    .findByName(tenantId, idOrName)
                    .orElseThrow(() -> new ObjectNotFoundException((Object) idOrName,
                                    InterventionType.class.getSimpleName())));
        }
    }

    /**
     * Export all interventionTypes for the tenant.
     *
     * @return interventionTypes for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<InterventionType> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("Export interventions for tenant {}", tenantId);

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }

        LOGGER.info("Found {} interventions", list.size());
        return list;
    }

    /**
     * Return just the matching interventions for a specific tenant.
     *
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<InterventionType>> findByStatusForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status) {
        LOGGER.info("List interventions for tenant {} with status {}", tenantId, status);

        List<InterventionType> list = interventionTypeRepo
                .findByStatusForTenant(tenantId, status);
        LOGGER.info("Found {} interventions", list.size());

        return addLinks(tenantId, list);
    }

    /**
     * Update an existing intervention type.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long interventionTypeId,
            @RequestBody InterventionType updatedIntvnType) {
        InterventionType intvnType = interventionTypeRepo
                .findById(interventionTypeId).orElseThrow(
                        () -> new ObjectNotFoundException(interventionTypeId,
                                InterventionType.class.getSimpleName()));

        BeanUtils.copyProperties(updatedIntvnType, intvnType, "id");
        intvnType.setTenantId(tenantId);
        interventionTypeRepo.save(intvnType);
    }

    protected List<EntityModel<InterventionType>> addLinks(final String tenantId, final List<InterventionType> intvnTypes) {
        List<EntityModel<InterventionType>> entities = new ArrayList<EntityModel<InterventionType>>();
        for (InterventionType rtn : intvnTypes) {
            entities.add(addLinks(tenantId, rtn));
        }
        return entities;
    }

    protected EntityModel<InterventionType> addLinks(final String tenantId,
            final InterventionType interventionType) {
        return EntityModel.of(interventionType, linkTo(
                methodOn(InterventionTypeController.class)
                        .findByIdOrName(tenantId, interventionType.getId().toString()))
                                .withSelfRel());
    }
}
