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
package digital.srp.sreport.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digital.srp.sreport.importers.QuestionCsvImporter;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.repositories.QuestionRepository;

@Service
public class QuestionService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionService.class);

    @Autowired
    protected QuestionRepository qRepo;

    public void initQuestions() {
        LOGGER.info("initQuestions");
        List<Question> questions;
        try {
            questions = new QuestionCsvImporter().readQuestions();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
        LOGGER.info("questions to import found: {}", questions.size());
        List<Question> existingQuestions = qRepo.findAll();
        LOGGER.info("existing questions found: {}", existingQuestions.size());
        for (Question question : questions) {
            Optional<Question> existingQ = findMatchingQ(question, existingQuestions);
            if (existingQ.isPresent()) {
                LOGGER.info("found question that needs update\n  from: {},\n    to: {}...",
                        existingQ.get(), question);
                BeanUtils.copyProperties(question, existingQ.get(), "id");
                try {
                    qRepo.save(existingQ.get());
                } catch (Exception e) {
                    LOGGER.error("unable to update question: {}\n  ({})",
                            question.name(), e.getMessage(), e);
                }
            } else {
                LOGGER.info("found new question: {}, attempting to save...",
                        question.name());
                try {
                    qRepo.save(question);
                } catch (Exception e) {
                    LOGGER.error("unable to save new question: {}\n  ({})",
                            question.name(), e.getMessage());
                }
            }
        }
    }

    private Optional<Question> findMatchingQ(Question question, List<Question> existingQuestions) {
        for (Question q : existingQuestions) {
            if (q.name().equals(question.name())) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }
}
