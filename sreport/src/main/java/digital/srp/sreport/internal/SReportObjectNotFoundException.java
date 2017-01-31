package digital.srp.sreport.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SReportObjectNotFoundException extends SReportException {

    private static final long serialVersionUID = -5639258797169164350L;

    private Class<?> type;

    private Object id;

    public SReportObjectNotFoundException() {
        super();
    }

    public SReportObjectNotFoundException(String msg) {
        super(msg);
    }
    
    public SReportObjectNotFoundException(String message, Class<?> type,
            Object id) {
        super(message);
        this.type = type;
        this.id = id;
    }

}
