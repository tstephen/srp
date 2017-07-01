package digital.srp.sreport;

import java.util.List;

import digital.srp.sreport.model.Criterion;

public class ResultSetTooLargeException extends Exception {

    private static final long serialVersionUID = 5521259956373719629L;
    
    public ResultSetTooLargeException(List<Criterion> criteria, int size) {
        super(buildMessage(criteria, size));
        
    }

    private static String buildMessage(List<Criterion> criteria, int size) {
        StringBuilder sb = new StringBuilder();
        for (Criterion criterion : criteria) {
            sb.append(criterion.getField()).append(criterion.getOperator()).append(criterion.getValue());
        }
        return String.format("{ \"message\": \"Search resulted in %1$d records for criteria: %2$s\", \"size\": %1$d, \"criteria\":["+""+"] }", size, sb.toString());
    }

}
