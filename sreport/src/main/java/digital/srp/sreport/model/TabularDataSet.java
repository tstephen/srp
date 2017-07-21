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
    
    public TabularDataSet transpose() {
        // empty or unset array, nothing do to here
        if (rows == null || rows.length == 0) {
            return this;
        }

        int width = rows.length;
        int height = rows[0].length;

        String[][] transposed = new String[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                transposed[y][x] = rows[x][y];
            }
        }
        this.rows = transposed;
        return this;
    }
}
