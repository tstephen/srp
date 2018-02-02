package digital.srp.sreport.services.tds;

import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void addAggregateData(TabularDataSet tds, Format formatter,
            int colCount, int rowCount) {
        for (int row = 0; row < rowCount; row++) {
            BigDecimal sum = new BigDecimal("0.0000");
            for (int col = 0; col < colCount; col++) {
                LOGGER.debug("...adding {}", tds.rows()[row][col]);
                try {
                    sum = sum.add(new BigDecimal(formatter.parseObject(tds.rows()[row][col]).toString()));
                } catch (ParseException | NumberFormatException e) {
                    LOGGER.warn("... cannot parse number from {}", tds.rows()[row][col]);
                } catch (NullPointerException e) {
                    LOGGER.debug("... cannot add null to total");
                }
            }
            LOGGER.debug("total is {}", sum);
            tds.set(row, colCount-1, formatter.format(sum));
        }
    }

}
