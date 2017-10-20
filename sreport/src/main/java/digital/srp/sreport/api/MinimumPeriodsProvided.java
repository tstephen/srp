package digital.srp.sreport.api;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.validators.MinimumPeriodsProvidedValidator;

/**
 * Validates that the {@link SurveyReturn} provides {@code answers} for at least
 * {@code noPeriods} periods.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MinimumPeriodsProvidedValidator.class)
@Documented
public @interface MinimumPeriodsProvided {

    int noPeriods();

    String message() default "Insufficient periods specified";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
