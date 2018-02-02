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

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.model.ModelParameter;
import digital.srp.macc.repositories.ModelParameterRepository;
import digital.srp.macc.views.ModelParameterViews;

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

    /**
     * Return just the messages for a specific tenant.
     * 
     * @return messages for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @JsonView(ModelParameterViews.Summary.class)
    public @ResponseBody List<ModelParameter> listForTenant(
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

        return addLinks(list);
    }

    /**
     * @return The specified parameter.
     */
    @RequestMapping(value = "/{idOrName}", method = RequestMethod.GET)
    @JsonView(ModelParameterViews.Detailed.class)
    public @ResponseBody ModelParameter findById(
            @PathVariable("idOrName") String param) {
        LOGGER.info(String.format("findById %1$s", param));
        

        ModelParameter parameter;
        try {
            parameter = modelParamRepo.findOne(Long.parseLong(param));
        } catch (NumberFormatException e) {
            parameter = modelParamRepo.findByName(param);
        }

        return addLinks(parameter);
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

    /**
     * Update an existing parameter.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long paramId,
            @RequestBody ModelParameter updatedParam) {
        ModelParameter param = modelParamRepo.findOne(paramId);

        BeanUtils.copyProperties(updatedParam, param, "id", "valueAsBigDecimal");
        param.setTenantId(tenantId);
        modelParamRepo.save(param);
    }

    private List<ModelParameter> addLinks(List<ModelParameter> params) {
        for (ModelParameter param : params) {
            addLinks(param);
        }
        return params;
    }
    
    private ModelParameter addLinks(ModelParameter modelParameter) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(String.format("/%1$s/parameters/%2$d", modelParameter.getTenantId(), modelParameter.getId())));

        modelParameter.setLinks(links);
        return modelParameter;
    }
}
