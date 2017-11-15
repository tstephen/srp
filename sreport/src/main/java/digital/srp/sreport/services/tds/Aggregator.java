package digital.srp.sreport.services.tds;

import java.text.Format;
import java.util.List;

import digital.srp.sreport.model.TabularDataSet;

public interface Aggregator {
    List<String> addAggregatedHeaders(String[] headers);
    
    void addAggregateData(TabularDataSet tds, Format formatter,
            int colCount, int rowCount);
}
