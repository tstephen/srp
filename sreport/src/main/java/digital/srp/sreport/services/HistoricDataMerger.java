package digital.srp.sreport.services;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;

/**
 * Import any answers available in the ERIC returns that has not been added to
 * the SDU return yet.
 *
 * @author Tim Stephenson
 */
@Component
public class HistoricDataMerger {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(HistoricDataMerger.class);

    public long merge(Set<Answer> answersToImport, SurveyReturn trgt) {
        long count = 0;
        for (Answer a : answersToImport) {
            if (!trgt.answer(a.applicablePeriod(), a.question().q()).isPresent()) {
                Answer a2 = new Answer()
                        .applicablePeriod(a.applicablePeriod())
                        .revision(trgt.revision())
                        .status(StatusType.Draft.name())
                        .response(a.response())
                        .question(a.question())
                        .addSurveyReturn(trgt);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Copying {}, {} = {} to {} ({})",
                            a2.question().name(), a2.applicablePeriod(),
                            a2.response(), trgt.name(), trgt.id());
                }
                trgt.answers().add(a2);
                count++;
            }
        }
        return count;
    }

}
