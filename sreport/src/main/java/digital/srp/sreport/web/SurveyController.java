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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.SurveyViews;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;
import digital.srp.sreport.repositories.SurveyRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.SurveyService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

/**
 * REST web service for accessing surveys.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/surveys")
@DependsOn({"questionController"})
public class SurveyController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyController.class);

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected SurveyRepository surveyRepo;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    @Autowired
    protected QuestionRepository questionRepo;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected SurveyService surveySvc;

    @PostConstruct
    protected void init() {
        surveySvc.initSurveys();
    }

    /**
     * @return The specified survey.
     */
    @JsonView(SurveyViews.Detailed.class)
    @GetMapping(value = "/{id}")
    @Transactional
    public @ResponseBody EntityModel<Survey> findById(
            @PathVariable("id") Long surveyId) {
        LOGGER.info("findById {}", surveyId);

        Survey survey = surveyRepo.findById(surveyId)
                .orElseThrow(() -> new ObjectNotFoundException(Survey.class, surveyId));
        // use logger for force child load
        LOGGER.info("Found survey with id {} named {} and with {} categories of questions",
                survey.id(), survey.name(), survey.categories().size());

        return addLinks(survey);
    }

    /**
     * @return The specified survey.
     */
    @JsonView(SurveyViews.Detailed.class)
    @GetMapping(value = "/findByName/{name}")
    public @ResponseBody EntityModel<Survey> findByName(
            @PathVariable("name") String name) {
        LOGGER.info("findByName {}", name);

        Survey survey = surveyRepo.findByName(name);
        // use logger for force child load
        LOGGER.info("Found survey with id {} named {} and with {} categories totalling {} questions",
                survey.id(), survey.name(), survey.categories().size(),
                survey.questionCodes().size());

        // TODO fetch all questions to optimise db access?
//        List<SurveyQuestion> surveyQuestions = qRepo.findBySurvey(survey.name());
        for (SurveyCategory cat : survey.categories()) {
            for (Q q : cat.questionEnums()) {
                cat.questions().add(questionRepo.findByName(q.name()).get());
            }
        }

        return addLinks(survey);
    }

    /**
     * @return list of surveys, optionally paged.
     */
    @JsonView(SurveyViews.Summary.class)
    @RolesAllowed(SrpRoles.ADMIN)
    @GetMapping(value = "/")
    public @ResponseBody List<EntityModel<Survey>> list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List surveys");

        List<Survey> list;
        if (limit == null) {
            list = surveyRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = surveyRepo.findPage(pageable);
        }

        LOGGER.info("Found {} surveys", list.size());
        return addLinks(list);
    }

    /**
     * @return a list of survey returns, optionally paged.
     */
    @JsonView(SurveyViews.Summary.class)
    @RolesAllowed(SrpRoles.ADMIN)
    @GetMapping(value = "/{id}/returns")
    public @ResponseBody List<SurveyReturn> listReturns(
            @PathVariable("id") Long surveyId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info("List returns");

        List<SurveyReturn> list;
        if (limit == null) {
            list = returnRepo.findBySurvey(surveyId);
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = returnRepo.findPageBySurvey(surveyId, pageable);
        }
        LOGGER.info("Found {} returns", list.size());
        return list;
    }

    /**
     * Delete an existing survey.
     */
    @RolesAllowed(SrpRoles.ADMIN)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    @Transactional
    public @ResponseBody void delete(@PathVariable("id") Long surveyId) {
        surveyRepo.deleteById(surveyId);
    }

    protected List<EntityModel<Survey>> addLinks(List<Survey> surveys) {
        List<EntityModel<Survey>> entities = new ArrayList<EntityModel<Survey>>();
        for (Survey rtn : surveys) {
            entities.add(addLinks(rtn));
        }
        return entities;
    }

    protected EntityModel<Survey> addLinks(Survey survey) {
        return EntityModel.of(survey,
                linkTo(methodOn(SurveyController.class).findById(survey.id()))
                        .withSelfRel());
    }
}
