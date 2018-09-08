package digital.srp.sreport.api.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import digital.srp.sreport.model.SurveyReturn;

public class FailedHealthCheckException extends SReportException {

    private static final long serialVersionUID = 1253790716205799541L;
    private final Set<ConstraintViolation<SurveyReturn>> issues;
    private final SurveyReturn rtn;

    public FailedHealthCheckException(String msg,
            SurveyReturn rtn,
            Set<ConstraintViolation<SurveyReturn>> issues) {
        super(msg);
        this.rtn = rtn;
        this.issues = issues;
    }

    public Set<ConstraintViolation<SurveyReturn>> getIssues() {
        return issues;
    }

    public SurveyReturn getBean() {
        return rtn;
    }
}
