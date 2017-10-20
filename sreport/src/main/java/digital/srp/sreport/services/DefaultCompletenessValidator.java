package digital.srp.sreport.services;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
