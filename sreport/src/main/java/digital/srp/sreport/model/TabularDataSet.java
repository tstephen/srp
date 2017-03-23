package digital.srp.sreport.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
@Data
public class TabularDataSet {

    @JsonProperty
    private String[] headers;

    @JsonProperty
    private String[][] rows;

    public TabularDataSet(int rowCount, int colCount) {
        rows = new String[rowCount][colCount];
    }

    public void row(int row, String[] cols) {
        rows[row] = Arrays.copyOf(cols, cols.length);
    }

    public void set(int row, int col, String value) {
        rows[row][col] = value;
    }
}
