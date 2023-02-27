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

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.springframework.stereotype.Component;

import digital.srp.sreport.api.CompletenessValidator;
import digital.srp.sreport.model.SurveyReturn;

@Component
public class DefaultCompletenessValidator implements CompletenessValidator {

    private Validator validator;

    public DefaultCompletenessValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public SurveyReturn validate(SurveyReturn rtn) {

        Set<ConstraintViolation<SurveyReturn>> problems = validator
                .validate(rtn);
        Set<String> issues= new HashSet<String>();
        for (ConstraintViolation<SurveyReturn> constraintViolation : problems) {
            issues.add(constraintViolation.getMessage());
        }
        return rtn.completeness(issues);
    }

}
