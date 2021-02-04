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

import digital.srp.sreport.api.MinimumPeriodsProvided;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.SurveyReturn;

public class MinimumPeriodsProvidedValidator
        implements ConstraintValidator<MinimumPeriodsProvided, SurveyReturn> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MinimumPeriodsProvidedValidator.class);

    private int noPeriods;

    @Override
    public void initialize(MinimumPeriodsProvided annotation) {
        this.noPeriods = annotation.noPeriods();
    }

    @Override
    public boolean isValid(final SurveyReturn bean, final ConstraintValidatorContext ctxt) {
        Set<String> periods = new HashSet<String>();
        for (Answer a : bean.answers()) {
            periods.add(a.applicablePeriod());
        }
        LOGGER.info("Return contains {} periods", periods.size());
        return periods.size() >= noPeriods;
    }
}
