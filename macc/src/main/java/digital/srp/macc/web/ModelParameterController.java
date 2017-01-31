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

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.model.ModelParameter;
import digital.srp.macc.repositories.ModelParameterRepository;

/**
 * REST endpoint for accessing {@link ModelParameter}
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/parameters")
public class ModelParameterController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ModelParameterController.class);

    @Autowired
    private ModelParameterRepository modelParamRepo;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Return just the messages for a specific tenant.
     * 
     * @return messages for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<ShortModelParameters> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List messages for tenant %1$s", tenantId));

        List<ModelParameter> list;
        if (limit == null) {
            list = modelParamRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = modelParamRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s messages", list.size()));

        return wrap(list);
    }

    /**
     * Export all contacts for the tenant.
     * 
     * @return contacts for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<ModelParameter> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("Export messages for tenant %1$s", tenantId));

        List<ModelParameter> list;
        if (limit == null) {
            list = modelParamRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = modelParamRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s messages", list.size()));

        return list;
    }

    private List<ShortModelParameters> wrap(List<ModelParameter> list) {
        List<ShortModelParameters> resources = new ArrayList<ShortModelParameters>(
                list.size());
        for (ModelParameter message : list) {
            resources.add(wrap(message));
        }
        return resources;
    }

    private ShortModelParameters wrap(ModelParameter message) {
        ShortModelParameters resource = new ShortModelParameters();
        BeanUtils.copyProperties(message, resource);
        Link detail = linkTo(ModelParameterRepository.class, message.getId())
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
    public static class ShortModelParameters extends ResourceSupport {
        private String selfRef;
        private String name;
        private Number value;
      }
}
