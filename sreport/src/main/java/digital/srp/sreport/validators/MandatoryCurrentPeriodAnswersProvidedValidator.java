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
package digital.srp.sreport.validators;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.api.MandatoryCurrentPeriodAnswersProvided;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;

public class MandatoryCurrentPeriodAnswersProvidedValidator implements
        ConstraintValidator<MandatoryCurrentPeriodAnswersProvided, SurveyReturn> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MandatoryCurrentPeriodAnswersProvidedValidator.class);

    private Q[] requiredAnswers;

    @Override
    public void initialize(MandatoryCurrentPeriodAnswersProvided annotation) {
        this.requiredAnswers = annotation.requiredAnswers();
    }

    @Override
    public boolean isValid(final SurveyReturn bean,
            final ConstraintValidatorContext ctx) {
        Set<Q> qs = new HashSet<Q>();
        for (Answer a : bean.answers()) {
            if (a.applicablePeriod().equals(bean.applicablePeriod())) {
                qs.add(a.question().q());
            }
        }
        LOGGER.debug("Return contains {} questions in the current period", qs.size());
        Set<Q> missingAnswers = new HashSet<Q>();
        for (Q q : requiredAnswers) {
            if (!qs.contains(q)) {
                missingAnswers.add(q);
            }
        }
        if (LOGGER.isInfoEnabled() && missingAnswers.size() > 0) {
            LOGGER.info("Return missing these mandatory questions in the current period: {}", missingAnswers);
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                            ctx.getDefaultConstraintMessageTemplate())

                    .addPropertyNode(missingAnswers.toString())
                    .addBeanNode()
                    .addConstraintViolation();
            return false;
        } else {
            return true;
        }
    }

}
