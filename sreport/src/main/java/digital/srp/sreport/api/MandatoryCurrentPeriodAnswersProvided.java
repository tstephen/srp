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
package digital.srp.sreport.api;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.validators.MandatoryCurrentPeriodAnswersProvidedValidator;

/**
 * Validates that the {@link SurveyReturn} provides at least the mandatory
 * {@code answers} in the current period.
 *
 * <p>
 * Some answers may be mandatory in all periods, but this is 'header'-type
 * answers that are not worth repeating in multiple years.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MandatoryCurrentPeriodAnswersProvidedValidator.class)
@Documented
public @interface MandatoryCurrentPeriodAnswersProvided {

    Q[] requiredAnswers();

    String message() default "must all be specified for the current period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
