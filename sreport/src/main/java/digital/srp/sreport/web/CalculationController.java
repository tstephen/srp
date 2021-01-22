package digital.srp.sreport.web;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.HealthChecker;

/**
 * REST web service for performing calculations on a return.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/calculations")
public class CalculationController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CalculationController.class);

    @Autowired
    protected Calculator cruncher;

    @Autowired
    protected HealthChecker healthCheck;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected SurveyReturnController surveyReturnController;

    /**
     * Run all calculations based on the return inputs.
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{returnId}", method = RequestMethod.POST)
    @JsonView(SurveyReturnViews.Detailed.class)
    public @ResponseBody SurveyReturn calculate(
            @PathVariable("returnId") Long returnId) {
        LOGGER.info(String.format("Running calculations for %1$s", returnId));

        SurveyReturn rtn = returnRepo.findById(returnId)
                .orElseThrow(() -> new ObjectNotFoundException(SurveyReturn.class, returnId));
        calculate(rtn, PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"));

        return rtn;
    }

    /**
     * Run all calculations based on the return inputs.
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{surveyName}/{org}", method = RequestMethod.POST)
    @JsonView(SurveyReturnViews.Detailed.class)
    public @ResponseBody SurveyReturn calculate(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("Running calculations for %1$s %2$s", surveyName, org));

        SurveyReturn rtn = surveyReturnController.findCurrentBySurveyNameAndOrg(surveyName, org);
        calculate(rtn, PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"));

        return rtn;
    }

    private void calculate(SurveyReturn rtn, int yearsToCalc) {
        if (isUpToDate(rtn)) {
            LOGGER.info("Skipping calculations for {} in {}", rtn.org(),
                    rtn.applicablePeriod());
        } else {
            Set<ConstraintViolation<SurveyReturn>> issues = healthCheck
                    .validate(rtn);
            if (issues.size() == 0) {
                rtn = cruncher.calculate(rtn, yearsToCalc);
            } else {
                String msg = String.format(
                        "Found %1$d issues, cannot run calculations until they are corrected",
                        issues.size());
                throw new ConstraintViolationException(msg, issues);
            }
            try {
                returnRepo.save(rtn);
            } catch (RuntimeException e) {
                unwrapException(e);
            }
        }
    }

    protected void unwrapException(Throwable e) {
        LOGGER.error(e.getMessage()+":" + e.getCause()==null ? "" : e.getCause().getMessage());
        if (e.getCause() instanceof ConstraintViolationException) {
            throw (ConstraintViolationException) e.getCause();
        } else if (e.getCause() == null) {
            throw new RuntimeException(e);
        } else {
            unwrapException(e.getCause());
        }
    }

//    /**
//     * Remove all calculations (i.e. those with derived = true).
//     */
//    @RequestMapping(value = "/{surveyName}/{org}", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @JsonView(SurveyReturnViews.Detailed.class)
//    public @ResponseBody void clean(
//            @PathVariable("surveyName") String surveyName,
//            @PathVariable("org") String org) {
//        LOGGER.info(String.format("Clean calculations for %1$s %2$s", surveyName, org));
//
//        SurveyReturn rtn = findLatestReturn(surveyName, org);
//        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
//                PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"));
//        answerRepo.deleteDerivedAnswers(rtn.getId(), periods.toArray(new String[periods.size()]));
//    }

        private boolean isUpToDate(SurveyReturn rtn) {
            return false;
//        try {
//            Set<Answer> underivedAnswers = rtn.underivedAnswers();
//            if (underivedAnswers.size() == 0) {
//                return true;
//            }
//            Date lastManualUpdate = underivedAnswers.stream().max(new Comparator<Answer>() {
//                @Override
//                public int compare(Answer a1, Answer a2) {
//                    if (a1.lastUpdated() == null) return -1;
//                    if (a2.lastUpdated() == null) return 1;
//                    return a1.lastUpdated().compareTo(a2.lastUpdated());
//                }
//            }).get().lastUpdated();
//            LOGGER.debug("lastManualUpdate: {}", lastManualUpdate);
//            Set<Answer> derivedAnswers = rtn.derivedAnswers();
//            if (derivedAnswers.size() == 0) {
//                return false;
//            }
//            Optional<Answer> lastCalculatedAnswer = derivedAnswers.stream().min(new Comparator<Answer>() {
//                @Override
//                public int compare(Answer a1, Answer a2) {
//                    if (a1.lastUpdated() == null) return 0;
//                    if (a2.lastUpdated() == null) return 0;
//                    return a1.lastUpdated().compareTo(a2.lastUpdated());
//                }
//            });
//            return lastCalculatedAnswer.isPresent() && (
//                    lastCalculatedAnswer.get().created().after(lastManualUpdate)
//                    || lastCalculatedAnswer.get().lastUpdated().after(lastManualUpdate));
//        } catch (Exception e) {
//            return false; // safety net
//        }
    }

}
