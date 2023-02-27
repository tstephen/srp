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

import java.util.Optional;

import jakarta.persistence.NonUniqueResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.repositories.QuestionRepository;

@Service
public class PersistentAnswerFactory implements AnswerFactory {

    @Autowired
    protected QuestionRepository qRepo;

    private static final String TENANT_ID = "sdu";

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PersistentAnswerFactory.class);

    @Override
    public Answer addAnswer(SurveyReturn rtn, String period, Q q) {
        LOGGER.info("Creating new derived answer '{}' for '{}' in '{}'",
               q.name(), rtn.org(), period);
        try {
            Optional<Question> existingQ = qRepo.findByName(q.name());
            if (!existingQ.isPresent()) {
                LOGGER.info("Creating new question {} ", q.name());
                // TODO tenant needs to be injected, e.g. via rtn.tenantId()
                existingQ = Optional.of(qRepo.save(new Question().q(q).tenantId(TENANT_ID)));
            }
            Answer answer = rtn.initAnswer(rtn, null, existingQ.get())
                    .applicablePeriod(period)
                    .derived(true);
            return answer;
        } catch (NonUniqueResultException e) {
            LOGGER.error("looking for answer '{}' for '{}' in '{}'",
                    q.name(), rtn.org(), period);
            throw e;
        }
    }

}
