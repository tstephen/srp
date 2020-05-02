package digital.srp.sreport.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.api.SurveyDefinedAnswersProvided;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.SurveyFactory;

public class SurveyDefinedAnswersProvidedValidator
        implements ConstraintValidator<SurveyDefinedAnswersProvided, SurveyReturn> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyDefinedAnswersProvidedValidator.class);

    private static int periodCount = 4;

    @Override
    public void initialize(SurveyDefinedAnswersProvided annotation) {
        annotation.periods();
    }

    @Override
    public boolean isValid(final SurveyReturn rtn, final ConstraintValidatorContext ctxt) {
        List<Q> missingQs = new ArrayList<Q>();
        try {
            SurveyFactory fact = SurveyFactory.getInstance(rtn.survey().name());
            for (String period : PeriodUtil.fillBackwards(rtn.applicablePeriod(), periodCount)) {
                for (Q q : fact.getQs()) {
                    Optional<Answer> optional = rtn.answer(period, q);
                    if (!optional.isPresent()) {
                        missingQs.add(q);
                    }
                }
            }
            if (LOGGER.isInfoEnabled() && missingQs.size() > 0) {
                LOGGER.info(
                        "Return missing these questions in the current period: {}",
                        missingQs);
                ctxt.disableDefaultConstraintViolation();
                ctxt.buildConstraintViolationWithTemplate(
                        ctxt.getDefaultConstraintMessageTemplate())

                        .addPropertyNode(missingQs.toString())
                        .addBeanNode().addConstraintViolation();
                return false;
            } else {
                return true;
            }
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Return {} ({}) is for a survey that has no definition", rtn.name(), rtn.id());
            return true; // We'll take on trust that the right answers exist in the return
        }
    }
}
