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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.SrpRoles;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.NullAwareBeanUtils;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.services.QuestionService;

/**
 * REST web service for accessing questions.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/questions")
public class QuestionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionController.class);

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected QuestionRepository questionRepo;

    @Autowired
    protected QuestionService questionSvc;

    @PostConstruct
    protected void init() throws IOException {
        questionSvc.initQuestions();
    }

    /**
     * @return the created question.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @PathVariable("tenantId") String tenantId,
            @RequestBody Question question) {
        question.tenantId(tenantId);

        questionRepo.save(question);

        Map<String, String> vars = new HashMap<String, String>();
        vars.put("id", question.id().toString());
        vars.put("tenantId", tenantId);
        return getCreatedResponseEntity("/{id}", vars );
    }

    protected ResponseEntity<Object> getCreatedResponseEntity(String path, Map<String, String> vars) {
        URI location = MvcUriComponentsBuilder.fromController(getClass()).path(path).buildAndExpand(vars).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<Object>(headers, HttpStatus.CREATED);
    }

    /**
     * @return The specified question.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody EntityModel<Question> findById(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long questionId) {
        LOGGER.info(String.format("findById %1$s", questionId));

        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ObjectNotFoundException(Question.class, questionId));

        return addLinks(tenantId, question);
    }

    /**
     * @return The specified question.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody EntityModel<Question> findByName(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("name") String name) {
        LOGGER.info(String.format("findByName %1$s", name));

        Question question = questionRepo.findByName(name).orElseThrow(
                () -> new ObjectNotFoundException(Question.class, name));
        // use logger for force child load
        LOGGER.info(String.format("Found question with id %1$d named %2$s",
                question.id(), question.name()));

        return addLinks(tenantId, question);
    }

    /**
     * @return a list of questions, optionally paged.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<EntityModel<Question>> list(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List questions"));

        List<Question> list;
        if (limit == null) {
            list = questionRepo.findAll();
        } else {
            Pageable pageable = PageRequest.of(page == null ? 0 : page, limit);
            list = questionRepo.findPage(pageable);
        }

        LOGGER.info("Found {} questions", list.size());
        return addLinks(tenantId, list);
    }

    /**
     * Update an existing question.
     */
    @RolesAllowed(SrpRoles.ANALYST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long questionId,
            @RequestBody Question updatedQuestion) {
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ObjectNotFoundException(Question.class, questionId));

        NullAwareBeanUtils.copyNonNullProperties(updatedQuestion, question, "id");

        questionRepo.save(question);
    }

    /**
     * Delete an existing question.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void delete(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long questionId) {
        questionRepo.deleteById(questionId);
    }

    protected List<EntityModel<Question>> addLinks(final String tenantId, final List<Question> list) {
        ArrayList<EntityModel<Question>> entities = new ArrayList<EntityModel<Question>>();
        for (Question question : list) {
            entities.add(addLinks(tenantId, question));
        }
        return entities;
    }

    protected EntityModel<Question> addLinks(final String tenantId, Question question) {
        return EntityModel.of(question,
                linkTo(methodOn(QuestionController.class).findById(tenantId, question.id()))
                        .withSelfRel());
    }
}
