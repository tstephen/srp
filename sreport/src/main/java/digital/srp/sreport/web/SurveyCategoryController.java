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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.repositories.SurveyCategoryRepository;

/**
 * REST web service for accessing category categories.
 */
@Controller
@RequestMapping(value = "/survey-categories")
public class SurveyCategoryController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyCategoryController.class);

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected  SurveyCategoryRepository catRepo;

    /**
     * @return The specified category.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody EntityModel<SurveyCategory> findById(
            @PathVariable("id") Long catId) {
        LOGGER.info(String.format("findById %1$s", catId));

        SurveyCategory cat = catRepo.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(SurveyCategory.class, catId));

        return addLinks(cat);
    }

    /**
     * @return categories.
     */
    @RolesAllowed(SrpRoles.USER)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<SurveyCategory>> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List categories"));

        List<SurveyCategory> list;
        if (limit == null) {
            list = catRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = catRepo.findPage(pageable);
        }
        LOGGER.info(String.format("Found %1$s categories", list.size()));

        return addLinks(list);
    }

    /**
     * Delete an existing survey category.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long catId) {
        catRepo.deleteById(catId);
    }

    protected List<EntityModel<SurveyCategory>> addLinks(List<SurveyCategory> categories) {
        List<EntityModel<SurveyCategory>> entities = new ArrayList<EntityModel<SurveyCategory>>();
        for (SurveyCategory cfactor : categories) {
            entities.add(addLinks(cfactor));
        }
        return entities;
    }

    protected EntityModel<SurveyCategory> addLinks(SurveyCategory cat) {
        return EntityModel.of(cat,
                linkTo(methodOn(SurveyCategoryController.class).findById(cat.id()))
                        .withSelfRel());
    }
}
