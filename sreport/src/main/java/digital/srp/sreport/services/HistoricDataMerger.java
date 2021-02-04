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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;

/**
 * Import any answers available in the ERIC returns that has not been added to
 * the SDU return yet.
 *
 * @author Tim Stephenson
 */
@Component
public class HistoricDataMerger {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(HistoricDataMerger.class);

    public long merge(Set<Answer> answersToImport, SurveyReturn trgt) {
        long count = 0;
        for (Answer a : answersToImport) {
            Optional<Answer> trgtA = trgt.answer(a.applicablePeriod(),
                    a.question().q());
            if (!trgtA.isPresent()) {
                Answer a2 = new Answer()
                        .applicablePeriod(a.applicablePeriod())
                        .revision(trgt.revision())
                        .status(StatusType.Draft.name())
                        .response(a.response())
                        .question(a.question())
                        .addSurveyReturn(trgt);
                LOGGER.info("Copying {}, {} = {} to {} ({})",
                        a2.question().name(), a2.applicablePeriod(),
                        a2.response(), trgt.name(), trgt.id());
                trgt.answers().add(a2);
                count++;
            } else if (trgtA.get().response() == null) {
                trgtA.get().response(a.response());
                LOGGER.info("Copying {}, {} = {} to {} ({})",
                        a.question().name(), a.applicablePeriod(), a.response(),
                        trgt.name(), trgt.id());
                count++;
            } else {
                LOGGER.debug(
                        "{}({}) already has answer of {} to {} for {}, ignoring",
                        trgt.name(), trgt.id(), a.response(),
                        a.question().name(), a.applicablePeriod());
            }
        }
        return count;
    }

}
