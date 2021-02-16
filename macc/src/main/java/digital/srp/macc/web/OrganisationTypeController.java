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
import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.OrganisationTypeRepository;

/**
 * REST endpoint for accessing {@link OrganisationType}
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/organisation-types")
public class OrganisationTypeController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OrganisationTypeController.class);

    @Autowired
    private OrganisationTypeRepository organisationTypeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Imports JSON representation of organistion types.
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
    public @ResponseBody Iterable<OrganisationType> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading org types for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<OrganisationType> list = objectMapper.readValue(content,
                new TypeReference<List<OrganisationType>>() {
                });
        LOGGER.info(String.format("  found %1$d org types", list.size()));
        for (OrganisationType orgType : list) {
            orgType.setTenantId(tenantId);
        }

        Iterable<OrganisationType> result = organisationTypeRepo.saveAll(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Create a new organisation type.
     *
     * @return
     * @throws URISyntaxException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<? extends OrganisationType> create(
            @PathVariable("tenantId") String tenantId,
            @RequestBody OrganisationType orgType) throws Exception {
        orgType.setTenantId(tenantId);

        orgType = organisationTypeRepo.save(orgType);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(methodOn(OrganisationTypeController.class)
                .findById(tenantId, orgType.getId())).withSelfRel().toUri());

        return new ResponseEntity(addLinks(tenantId, orgType), headers, HttpStatus.CREATED);
    }

    /**
     * Return just the organisation types for a specific tenant.
     *
     * @return organisation types for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<OrganisationType>> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List organisation types for tenant {}", tenantId);

        List<OrganisationType> list;
        if ("reportingType".equals(filter)) {
            list = organisationTypeRepo.findAllReportingTypeForTenant(tenantId);
        } else if (limit == null) {
            list = organisationTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = organisationTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s organisation types", list.size()));

        return addLinks(tenantId, list);
    }

    /**
     * @return one organisation type with the provided id.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody EntityModel<OrganisationType> findById(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long orgTypeId) {
        LOGGER.info(String.format(
                "Find organisation types for tenant %1$s with id %2$d",
                tenantId, orgTypeId));

        return addLinks(tenantId, organisationTypeRepo.findById(orgTypeId)
                .orElseThrow(() -> new ObjectNotFoundException(orgTypeId,
                        OrganisationType.class.getSimpleName())));
    }

    /**
     * Return just the matching organisation types for a specific tenant.
     *
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    public @ResponseBody List<EntityModel<OrganisationType>> findByStatusForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status) {
        LOGGER.info(String.format(
                "List organisation types for tenant %1$s with status %2$s",
                tenantId, status));

        List<OrganisationType> list = organisationTypeRepo
                .findByStatusForTenant(tenantId, status);
        LOGGER.info(String.format("Found %1$s organisation types", list.size()));

        return addLinks(tenantId, list);
    }

    /**
     * @return All organisation types for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<OrganisationType> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("Export organisation types for tenant %1$s", tenantId));

        List<OrganisationType> list;
        if (limit == null) {
            list = organisationTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = organisationTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s organisation types", list.size()));

        return list;
    }

    /**
     * Update an existing organisation type.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long orgTypeId,
            @RequestBody OrganisationType updatedOrgType) {
        OrganisationType orgType = organisationTypeRepo.findById(orgTypeId)
                .orElseThrow(() -> new ObjectNotFoundException(orgTypeId,
                        OrganisationType.class.getSimpleName()));

        BeanUtils.copyProperties(updatedOrgType, orgType, "id");
        orgType.setTenantId(tenantId);
        organisationTypeRepo.save(orgType);
    }

    protected List<EntityModel<OrganisationType>> addLinks(final String tenantId, final List<OrganisationType> list) {
        ArrayList<EntityModel<OrganisationType>> entities = new ArrayList<EntityModel<OrganisationType>>();
        for (OrganisationType answer : list) {
            entities.add(addLinks(tenantId, answer));
        }
        return entities;
    }

    protected EntityModel<OrganisationType> addLinks(final String tenantId, OrganisationType survey) {
        return EntityModel.of(survey,
                linkTo(methodOn(OrganisationTypeController.class).findById(tenantId, survey.getId()))
                        .withSelfRel());
    }
}
