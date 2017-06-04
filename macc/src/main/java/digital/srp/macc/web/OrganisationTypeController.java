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
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.OrganisationTypeRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * Imports JSON representation of messages.
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
        LOGGER.info(String.format("Uploading messages for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<OrganisationType> list = objectMapper.readValue(content,
                new TypeReference<List<OrganisationType>>() {
                });
        LOGGER.info(String.format("  found %1$d messages", list.size()));
        for (OrganisationType message : list) {
            message.setTenantId(tenantId);
        }

        Iterable<OrganisationType> result = organisationTypeRepo.save(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Return just the messages for a specific tenant.
     * 
     * @return messages for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<ShortOrganisationType> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List messages for tenant %1$s", tenantId));

        List<OrganisationType> list;
        if ("reportingType=true".equals(filter)) {
            list = organisationTypeRepo.findAllReportingTypeForTenant(tenantId);
        } else if (limit == null) {
            list = organisationTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = organisationTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s messages", list.size()));

        return wrap(list);
    }

    /**
     * Return just the matching organisation types for a specific tenant.
     * 
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    public @ResponseBody List<ShortOrganisationType> findByStatusForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status) {
        LOGGER.info(String.format(
                "List organisation types for tenant %1$s with status %2$s",
                tenantId, status));

        List<OrganisationType> list = organisationTypeRepo
                .findByStatusForTenant(tenantId, status);
        LOGGER.info(String.format("Found %1$s organisation types", list.size()));

        return wrap(list);
    }

    /**
     * Export all contacts for the tenant.
     * 
     * @return contacts for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<OrganisationType> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("Export messages for tenant %1$s", tenantId));

        List<OrganisationType> list;
        if (limit == null) {
            list = organisationTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = organisationTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s messages", list.size()));

        return list;
    }

    private List<ShortOrganisationType> wrap(List<OrganisationType> list) {
        List<ShortOrganisationType> resources = new ArrayList<ShortOrganisationType>(
                list.size());
        for (OrganisationType message : list) {
            resources.add(wrap(message));
        }
        return resources;
    }

    private ShortOrganisationType wrap(OrganisationType message) {
        ShortOrganisationType resource = new ShortOrganisationType();
        BeanUtils.copyProperties(message, resource);
        Link detail = linkTo(OrganisationTypeRepository.class, message.getId())
                .withSelfRel();
        resource.add(detail);
        resource.setSelfRef(detail.getHref());
        return resource;
    }
    
    
    private Link linkTo(
            @SuppressWarnings("rawtypes") Class<? extends CrudRepository> clazz,
            Long id) {
        return new Link(clazz.getAnnotation(RepositoryRestResource.class)
                .path() + "/" + id);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ShortOrganisationType extends ResourceSupport {
        private String selfRef;
        private String name;
        private Integer count;
        private String sector;
        private String icon;
        private boolean commissioner;
      }
}
