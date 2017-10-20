package digital.srp.sreport.web;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;
import digital.srp.sreport.services.PersistentAnswerFactory;

/**
 * REST web service for performing calculations on a report.
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
    protected SurveyReturnRepository returnRepo;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    @Autowired
    protected PersistentAnswerFactory answerFactory;

    /**
     * Run all calculations based on the return inputs.
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{returnId}", method = RequestMethod.POST)
    public @ResponseBody SurveyReturn calculate(
            @PathVariable("returnId") Long returnId) {
        LOGGER.info(String.format("Running calculations for %1$s", returnId));

        SurveyReturn rtn = returnRepo.findOne(returnId);
        calculate(rtn, 4);

        return rtn;
    }

    /**
     * Run all calculations based on the return inputs.
     * @return return including latest calculations.
     */
    @RequestMapping(value = "/{surveyName}/{org}", method = RequestMethod.POST)
    public @ResponseBody SurveyReturn calculate(
            @PathVariable("surveyName") String surveyName,
            @PathVariable("org") String org) {
        LOGGER.info(String.format("Running calculations for %1$s %2$s", surveyName, org));

        List<SurveyReturn> returns = returnRepo.findBySurveyAndOrg(surveyName, org);
        returns.sort((r1,r2) -> r1.revision().compareTo(r2.revision()));
        SurveyReturn rtn = returns.get(returns.size()-1);
        calculate(rtn, 4);

        return rtn;
    }

    private void calculate(SurveyReturn rtn, int yearsToCalc) {
     // remove derived?
//      List<Answer> answers = answerRepo.findByOrg(rtn.org());
//      if (answers.size() == 0) {
//          return;
//      }
//      Long[] ids = new Long[answers.size()];
//      for (int i = 0; i < answers.size(); i++) {
//          Answer a = answers.get(i);
//          ids[i] = a.id();
//      }
//      answerRepo.deleteDerivedAnswers(ids);
//        returnRepo.save(rtn);
        if (false /*isUpToDate(rtn)*/) {
            LOGGER.info("Skipping calculations for {} in {}", rtn.org(), rtn.applicablePeriod());
        } else {
//          deleteDerivedForOrg(rtn);
            rtn = cruncher.calculate(rtn, 4, answerFactory);
        }
        try {
            returnRepo.save(rtn);
        } catch (RuntimeException e) {
            unwrapException(e);
        }
    }

    protected void unwrapException(Throwable e) {
        LOGGER.error(e.getMessage()+":" + e.getCause()==null ? "" : e.getCause().getMessage());
        if (e.getCause() instanceof ConstraintViolationException) {
            throw (ConstraintViolationException) e.getCause();
        } else if (e.getCause() == null) {
            throw new  RuntimeException(e);
        } else {
            unwrapException(e.getCause());
        }
    }

//    private void deleteDerivedForOrg(SurveyReturn rtn) {
//        for (Answer answer : rtn.answers()) {
//            if (answer.derived() && answer.response() != null ) {
//                rtn.answers().remove(answer);
//            }
//        }
//        return;
//    }

//    private boolean isUpToDate(SurveyReturn rtn) {
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
//    }

}