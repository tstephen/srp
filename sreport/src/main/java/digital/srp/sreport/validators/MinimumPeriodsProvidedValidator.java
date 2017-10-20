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
