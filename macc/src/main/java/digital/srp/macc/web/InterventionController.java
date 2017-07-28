/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import digital.srp.macc.model.Intervention;
import digital.srp.macc.repositories.InterventionRepository;
import digital.srp.macc.views.InterventionViews;

/**
 * REST endpoint for accessing {@link Interventions}
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/interventions")
public class InterventionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionController.class);

    @Autowired
    private InterventionRepository interventionRepo;

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
    @RequestMapping(value = "/uploadjson", method = RequestMethod.POST)
    public @ResponseBody Iterable<Intervention> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String
                .format("Uploading interventions for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<Intervention> list = objectMapper.readValue(content,
                new TypeReference<List<Intervention>>() {
                });
        LOGGER.info(String.format("  found %1$d interventions", list.size()));
        ArrayList<Intervention> result = new ArrayList<Intervention>();
        for (Intervention intervention : list) {
            intervention.setTenantId(tenantId);

            try {
                interventionRepo.save(intervention);
            } catch (Exception e) {
                LOGGER.error(String.format("Problem saving %1$s", intervention
                        .getInterventionType().getName()));
            }
            result.add(intervention);
        }

        // Iterable<Intervention> result = interventionRepo.save(list);
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
        Intervention intvn = interventionRepo.findOne(interventionId);

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
    @JsonView(InterventionViews.Detailed.class)
    public @ResponseBody List<Intervention> listForTenantAsJson(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        List<Intervention> interventions = listForTenant(tenantId, page, limit);
        return addLinks(interventions);
    }

    /**
     * Export all interventions for the tenant.
     * 
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<Intervention> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return listForTenant(tenantId, page, limit);
    }

    protected List<Intervention> listForTenant(String tenantId, Integer page,
            Integer limit) {
        LOGGER.info(String.format("List interventions for tenant %1$s",
                tenantId));

        List<Intervention> list;
        if (limit == null) {
            list = interventionRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = interventionRepo.findPageForTenant(tenantId, pageable);
        }

        LOGGER.info(String.format("Found %1$s interventions", list.size()));
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
    @JsonView(InterventionViews.Detailed.class)
    public @ResponseBody List<Intervention> findByStatusForTenantAndOrgType(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status,
            @PathVariable("orgTypeName") String orgTypeName) {
        LOGGER.info(String
                .format("List interventions for tenant %1$s with status %2$s and org type %3$s",
                        tenantId, status, orgTypeName));

        List<Intervention> list = interventionRepo
                .findByStatusForTenantAndOrgType(tenantId, status, orgTypeName);
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return addLinks(list);
    }

    /**
     * @return The specified intervention.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @JsonView(InterventionViews.Detailed.class)
    public @ResponseBody Intervention findById(
            @PathVariable("id") Long interventionId) {
        LOGGER.info(String.format("findById %1$s", interventionId));

        Intervention intervention = interventionRepo.findOne(interventionId);

        return addLinks(intervention);
    }

    private List<Intervention> addLinks(final List<Intervention> interventions) {
        for (Intervention i : interventions) {
            addLinks(i);
        }
        return interventions;
    }

    private Intervention addLinks(final Intervention intervention) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(String.format("/%1$s/interventions/%2$d", intervention.getTenantId(), intervention.getId())));

        intervention.setLinks(links);
        return intervention;
    }
}
