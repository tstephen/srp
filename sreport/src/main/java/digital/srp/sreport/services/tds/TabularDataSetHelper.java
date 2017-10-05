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
    
    public TabularDataSet tabulate(String[] headers, List<Answer> answers, 
            Format formatter, Optional<Aggregator> aggregator) {
        List<String> hList;
        if (aggregator.isPresent()) {
            hList = aggregator.get().addAggregatedHeaders(headers);
        } else {
            hList = Arrays.asList(headers);
        }
        int colCount = hList.size();
        int rowCount = answers.size()/headers.length;
        if (answers.size() % headers.length != 0) {
            rowCount++;
        }
        TabularDataSet tds = new TabularDataSet(rowCount, colCount)
                .headers(hList.toArray(new String[colCount]));
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            int col = hList.indexOf(answer.question().name());
            int row = i/headers.length;
            try {
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
            aggregator.get().addAggregateData(answers, formatter, colCount, rowCount, tds);
        }
        return tds;
    }
}
