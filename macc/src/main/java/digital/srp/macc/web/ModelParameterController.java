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

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;

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

    /**
     * Return just the messages for a specific tenant.
     *
     * @return messages for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<digital.srp.macc.model.ModelParameter>> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List messages for tenant {}", tenantId);

        List<ModelParameter> list;
        if (limit == null) {
            list = modelParamRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = modelParamRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info("Found {} messages", list.size());
        return addLinks(tenantId, list);
    }

    /**
     * @return The specified parameter.
     */
    @RequestMapping(value = "/{idOrName}", method = RequestMethod.GET)
    public @ResponseBody EntityModel<digital.srp.macc.model.ModelParameter> findById(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("idOrName") String param) {
        LOGGER.info("findById {}", param);

        ModelParameter parameter;
        try {
            parameter = modelParamRepo.findById(Long.valueOf(param))
                    .orElseThrow(() -> new ObjectNotFoundException((Object) param,
                            ModelParameter.class.getSimpleName()));
        } catch (NumberFormatException e) {
            String msg = String.format("No parameter with name %1$s", param);
            parameter = modelParamRepo.findByName(param, tenantId)
                    .orElseThrow(() -> new IllegalArgumentException(msg));
        }

        return addLinks(tenantId, parameter);
    }

    /**
     * @return Location header for newly created parameter.
     */
    @RolesAllowed("admin")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @PathVariable("tenantId") String tenantId,
            @RequestHeader String Authorization,
            @RequestBody ModelParameter param) {

        EntityModel<ModelParameter> model = addLinks(
                tenantId, modelParamRepo.save(param));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(model.getLink("self").get().toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
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
        LOGGER.info("Export messages for tenant {}", tenantId);

        List<ModelParameter> list;
        if (limit == null) {
            list = modelParamRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = modelParamRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info("Found {} messages", list.size());

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
        ModelParameter param = modelParamRepo.findById(paramId)
                .orElseThrow(() -> new ObjectNotFoundException(paramId,
                        ModelParameter.class.getSimpleName()));

        BeanUtils.copyProperties(updatedParam, param, "id", "valueAsBigDecimal");
        param.setTenantId(tenantId);
        modelParamRepo.save(param);
    }

    protected List<EntityModel<ModelParameter>> addLinks(final String tenantId, List<ModelParameter> params) {
        List<EntityModel<ModelParameter>> entities = new ArrayList<EntityModel<ModelParameter>>();
        for (ModelParameter rtn : params) {
            entities.add(addLinks(tenantId, rtn));
        }
        return entities;
    }

    protected EntityModel<ModelParameter> addLinks(final String tenantId, final ModelParameter param) {
        return EntityModel.of(param,
                linkTo(methodOn(ModelParameterController.class)
                        .findById(tenantId, String.valueOf(param.getId())))
                                .withSelfRel());
    }
}
