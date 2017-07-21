package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.text.Format;
import java.util.Arrays;
import java.util.List;

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
            Format formatter) {
        List<String> hList = Arrays.asList(headers);
        int colCount = headers.length;
        int rowCount = answers.size()/colCount;
        if (answers.size() % colCount != 0) {
            rowCount++;
        }
        TabularDataSet tds = new TabularDataSet(rowCount, colCount)
                .headers(headers);
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            int col = hList.indexOf(answer.question().name());
            int row = i/headers.length;
            try {
                tds.set(row, col, formatter.format(new BigDecimal(answer.response())));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Cannot format {} using {}", answer.response(), formatter.getClass().getName());
                tds.set(row, col, answer.response());
            }
        }
        
        return tds;
    }
}
