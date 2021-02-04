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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.internal.CsvImporter;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.views.InterventionTypeViews;

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
    private InterventionTypeRepository interventionTypeRepo;

    @Autowired
    private ObjectMapper objectMapper;

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
        LOGGER.info(String.format("  found %1$d interventions", list.size()));
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
        LOGGER.info(String.format("Uploading CSV interventionTypes for: %1$s",
                tenantId));
        String content = new String(file.getBytes());
        List<InterventionType> list = new CsvImporter().readInterventionTypes(
                new StringReader(
                content), content.substring(0, content.indexOf('\n'))
                .split(","));
        LOGGER.info(String.format("  found %1$d interventionTypes", list.size()));
        for (InterventionType interventionType : list) {
            interventionType.setTenantId(tenantId);
        }

        Iterable<InterventionType> result = interventionTypeRepo.saveAll(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Return just the interventions for a specific tenant.
     *
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<InterventionType> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List interventions for tenant %1$s",
                tenantId));

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s intervention types", list.size()));

        return addLinks(list);
    }

    /**
     * @return The specified intervention type.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(InterventionTypeViews.Detailed.class)
    public @ResponseBody InterventionType findById(
            @PathVariable("id") Long interventionTypeId) {
        LOGGER.info(String.format("findById %1$s", interventionTypeId));

        InterventionType intvnType = interventionTypeRepo
                .findById(interventionTypeId).orElseThrow(
                        () -> new ObjectNotFoundException(interventionTypeId,
                                InterventionType.class.getSimpleName()));

        return addLinks(intvnType);
    }

    /**
     * Export all interventionTypes for the tenant.
     *
     * @return interventionTypes for that tenant.
     */
    @JsonView(InterventionTypeViews.Detailed.class)
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<InterventionType> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("Export interventions for tenant %1$s",
                tenantId));

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return list;
    }

    /**
     * Return just the matching interventions for a specific tenant.
     *
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @JsonView(InterventionTypeViews.Summary.class)
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    public @ResponseBody List<InterventionType> findByStatusForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status) {
        LOGGER.info(String.format(
                "List interventions for tenant %1$s with status %2$s",
                tenantId, status));

        List<InterventionType> list = interventionTypeRepo
                .findByStatusForTenant(tenantId, status);
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return addLinks(list);
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

    private List<InterventionType> addLinks(
            final List<InterventionType> interventionTypes) {
        for (InterventionType intType : interventionTypes) {
            addLinks(intType);
        }
        return interventionTypes;
    }

    private InterventionType addLinks(final InterventionType type) {
        return type.links(Collections
                .singletonList(Link.of(String.format("/%1$s/intervention-types/%2$d",
                        type.getTenantId(), type.getId()))));
    }
}
