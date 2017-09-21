package digital.srp.sreport.services.tds;

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.TabularDataSet;

public class Totaller implements Aggregator {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Totaller.class);
    
    @Override
    public List<String> addAggregatedHeaders(String[] headers) {
        List<String> tmp = new ArrayList<String>();
        tmp.addAll(Arrays.asList(headers));
        tmp.add("TOTAL");
        return tmp;
    }

    @Override
    public void addAggregateData(List<Answer> answers, Format formatter,
            int colCount, int rowCount, TabularDataSet tds) {
        int col = 0;
        for (int row = 0; row < rowCount; row++) {
            tds.set(row, colCount-1, formatter.format(sum(answers.subList(col, (col+colCount-1 > answers.size() ? answers.size() : col+colCount-1)))));
            col = col+colCount-1;
        }
    }

    private BigDecimal sum(List<Answer> subList) {
        BigDecimal sum = new BigDecimal("0.0000");
        for (Answer answer : subList) {
            LOGGER.debug("...adding {}", answer.response());
            sum = sum.add(answer.responseAsBigDecimal());
        }
        LOGGER.debug("Resulting sum is {}", sum);
        return sum;
    }

}
