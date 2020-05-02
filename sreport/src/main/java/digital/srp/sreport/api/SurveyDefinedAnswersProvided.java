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
import digital.srp.sreport.validators.SurveyDefinedAnswersProvidedValidator;

/**
 * Validates that the {@link SurveyReturn} contains all the {@code answers}
 * expected by the referenced {@code survey}, Answers may provide a null
 * response but must be present.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = SurveyDefinedAnswersProvidedValidator.class)
@Documented
public @interface SurveyDefinedAnswersProvided {

    int periods() default 4;

    String message() default "must all be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
