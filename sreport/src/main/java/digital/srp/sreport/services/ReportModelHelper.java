package digital.srp.sreport.services;

import java.text.Format;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.TabularDataSet;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.services.tds.Aggregator;
import digital.srp.sreport.services.tds.TabularDataSetHelper;

public class ReportModelHelper {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReportModelHelper.class);

    private final AnswerRepository answerRepo;

    private final TabularDataSetHelper tdsHelper;

    public ReportModelHelper(final AnswerRepository answerRepo) {
        this.answerRepo = answerRepo;
        this.tdsHelper = new TabularDataSetHelper();
    }

    public synchronized void fillModel(String org, String period, Q[] headers, final Model model,
            boolean periodAsCol, Format decimalFormat, Integer maxPeriods,
            boolean ascending, Optional<Aggregator> aggregator) {
        String[] headerNames = new String[headers.length];
        for (int i = 0 ; i < headers.length ; i++) {
            headerNames[i] = headers[i].name();
        }

        List<Answer> answers;
        if (ascending) {
            answers = answerRepo.findByOrgAndQuestionAsc(org, headerNames);
        } else {
            answers = answerRepo.findByOrgAndQuestion(org, headerNames);
        }
        LOGGER.info(
                String.format("Found %1$s answers about organisation for %2$s",
                        answers.size(), org));
        if (LOGGER.isDebugEnabled()) {
            for (Answer answer : answers) {
                LOGGER.debug(answer.toString());
            }
        }

        TabularDataSet table = tdsHelper.tabulate(headerNames, answers, decimalFormat, aggregator);
        List<String> periods = PeriodUtil.fillBackwards(period,
                table.rows().length < maxPeriods ? table.rows().length : maxPeriods);
                model.addAttribute("periods", periods);
        if (ascending) {
            Collections.reverse(periods);
        }
        if (periodAsCol) {
            table = table.transpose();
        }

        model.addAttribute("table", table);
        model.addAttribute("messages",
                ResourceBundle.getBundle("digital.srp.sreport.Messages"));
    }
}