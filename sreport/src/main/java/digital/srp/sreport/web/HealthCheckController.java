package digital.srp.sreport.web;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.api.exceptions.FailedHealthCheckException;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.HealthCheck;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.views.HealthCheckViews;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.HealthChecker;

/**
 * REST web service for performing calculations on a report.
 *
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/admin/health")
public class HealthCheckController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HealthCheckController.class);

    @Autowired
    protected HealthChecker healthChecker;

    @Autowired
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected SurveyReturnController surveyReturnController;

    /**
     * Run all calculations based on the return inputs.
     *
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{returnId}", method = RequestMethod.GET, produces = {
            "application/json" })
    @JsonView(HealthCheckViews.Summary.class)
    public @ResponseBody HealthCheck checkHealth(
            @PathVariable("returnId") Long returnId) {
        LOGGER.info(String.format("Checking health for %1$s", returnId));

        SurveyReturn rtn = returnRepo.findOne(returnId);
        checkHealth(rtn,
                PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"),
                false);

        return new HealthCheck(rtn);
    }

    /**
     * Run all calculations based on the return inputs.
     *
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{surveyName}/{org}", method = RequestMethod.GET, produces = {
            "application/json" })
    @JsonView(HealthCheckViews.Summary.class)
    public @ResponseBody HealthCheck checkHealth(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("Checking health for %1$s %2$s", surveyName,
                org));

        SurveyReturn rtn = surveyReturnController
                .findCurrentBySurveyNameAndOrg(surveyName, org);
        checkHealth(rtn,
                PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"),
                false);

        return new HealthCheck(rtn);
    }

    /**
     * Run all validators on the specified return.
     *
     * @return summary return.
     */
    @RequestMapping(value = "/{returnId}", method = RequestMethod.POST, produces = {
            "application/json" })
    @JsonView(HealthCheckViews.Summary.class)
    public @ResponseBody HealthCheck checkHealthAndAttemptFixes(
            @PathVariable("returnId") Long returnId) {
        LOGGER.info(String.format("Checking health for %1$s", returnId));

        SurveyReturn rtn = returnRepo.findOne(returnId);
        checkHealth(rtn,
                PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"),
                true);

        return new HealthCheck(rtn);
    }

    /**
     * Run all validators on the specified return.
     *
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{surveyName}/{org}", method = RequestMethod.POST, produces = {
            "application/json" })
    @JsonView(HealthCheckViews.Summary.class)
    public @ResponseBody HealthCheck checkHealthAndAttemptFixes(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("Checking health for %1$s %2$s", surveyName,
                org));

        SurveyReturn rtn = surveyReturnController
                .findCurrentBySurveyNameAndOrg(surveyName, org);
        checkHealth(rtn,
                PeriodUtil.periodsSinceInc(rtn.applicablePeriod(), "2007-08"),
                true);

        return new HealthCheck(rtn);
    }

    private void checkHealth(SurveyReturn rtn, int yearsToCalc,
            boolean attemptFixes) {
        Set<ConstraintViolation<SurveyReturn>> issues = healthChecker
                .validate(rtn);
        if (issues.size() == 0) {
            ; // Sunny in Sunnyvale
        } else {
            String msg = String.format(
                    "Found %1$d issues, cannot run calculations until they are corrected",
                    issues.size());
            if (attemptFixes) {
                long duplicateViolationCount = 0;
                long duplicateAnswerCount = 0;
                ArrayList<Long> toDelete = new ArrayList<Long>();
                for (ConstraintViolation<SurveyReturn> issue : issues) {
                    switch (issue.getClass().getSimpleName()) {
                    case "DuplicateAnswerConstraintViolation":
                        duplicateViolationCount++;
                        @SuppressWarnings("unchecked")
                        Set<Answer> answers = (Set<Answer>) issue.getLeafBean();
                        Answer[] array = new Answer[answers.size()];
                        answers.toArray(array);
                        for (int i = 1; i < array.length; i++) {
                            long start = System.currentTimeMillis();
                            LOGGER.info("deleting answer with id: {}",
                                    array[i].id());
                            toDelete.add(array[i].id());
                            LOGGER.info("  took {}ms",
                                    (System.currentTimeMillis() - start));
                            duplicateAnswerCount++;
                        }
                        break;
                    case "SurveyDefinedAnswersProvided":
                        long start = System.currentTimeMillis();
                        LOGGER.error("Missing answers detected by validator - Hurrah!");
                        LOGGER.info("  took {}ms",
                                (System.currentTimeMillis() - start));
                        break;
                    }
                }
                LOGGER.info(
                        "found {} answers with duplicates and {} records to delete",
                        duplicateViolationCount, duplicateAnswerCount);
                Long[] ids = new Long[toDelete.size()];
                toDelete.toArray(ids);
                answerRepo.deleteAnswers(ids);
                LOGGER.info("  the deed is done");
            } else {
                throw new FailedHealthCheckException(msg, rtn, issues);
            }
        }
    }

}
