package digital.srp.sreport.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.TabularDataSet;

@Component
public class TabularDataSetHelper {

    public TabularDataSet tabulate(String[] headers, List<Answer> answers) {
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
            tds.set(row,col,answer.response());
        }
        
        return tds;
    }
}
