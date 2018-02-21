package digital.srp.sreport.services.tds;

import java.math.BigDecimal;
import java.text.Format;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.TabularDataSet;

@Component
public class TabularDataSetHelper {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TabularDataSetHelper.class);
    
    public synchronized TabularDataSet tabulate(final String[] cHeaders, final String[] rHeaders,
            final List<Answer> answers, final Format formatter, final Optional<Aggregator> aggregator) {
        List<String> rhList = Arrays.asList(rHeaders);
        List<String> chList;
        if (aggregator.isPresent()) {
            chList = aggregator.get().addAggregatedHeaders(cHeaders);
        } else {
            chList = Arrays.asList(cHeaders);
        }
        LOGGER.debug("Row headers for table: {}", rhList);
        LOGGER.debug("Column headers for table: {}", chList);
        int colCount = chList.size();
        int rowCount = rHeaders.length;
        TabularDataSet tds = new TabularDataSet(rowCount, colCount)
                .headers(chList.toArray(new String[colCount]));
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            int col = chList.indexOf(answer.question().name());
            int row = rhList.indexOf(answer.applicablePeriod());
            try {
                LOGGER.debug("Setting {} in {} ({},{}) to {}", answer.question().name(), answer.applicablePeriod(), row, col, answer.response());
                tds.set(row, col, formatter.format((BigDecimal) answer.responseAsBigDecimal()));
            } catch (NullPointerException e) {
                tds.set(row, col, "0");
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Cannot format {} using {}", answer.response(), formatter.getClass().getName());
                tds.set(row, col, answer.response());
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                LOGGER.warn("Attempt to set row {}, col {} to {}", row, col, answer.response());
            }
        }
        
        if (aggregator.isPresent()) {
            aggregator.get().addAggregateData(tds, formatter, colCount, rowCount);
        }
        return tds;
    }
}
