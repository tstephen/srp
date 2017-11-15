package digital.srp.sreport.api.exceptions;

public class SReportException extends RuntimeException {

    private static final long serialVersionUID = 8596935185538284707L;

    public SReportException() {
        super();
    }

    public SReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public SReportException(String message) {
        super(message);
    }

    public SReportException(Throwable cause) {
        super(cause);
    }

}
