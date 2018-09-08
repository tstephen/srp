package digital.srp.sreport.api;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.exceptions.FailedHealthCheckException;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.HealthCheck;
import digital.srp.sreport.model.views.HealthCheckViews;

@ControllerAdvice
public class SReportExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SReportExceptionHandler.class);
    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper mapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody String handleConstraintViolationException(
            ConstraintViolationException e) {
        LOGGER.error("Constraint violation: " + e.getMessage());
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
            sb.append(String.format(
                    "  %1$s %2$s but was %3$s, bean affected is %4$s",
                    cv.getPropertyPath(), cv.getMessage(),
                    cv.getInvalidValue(), cv.getLeafBean()));
        }
        LOGGER.error(sb.toString());
        return sb.toString();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody String handleIllegalStateException(
            IllegalStateException e) {
        LOGGER.error(e.getMessage(), e);
        return String.format("{\"message\":\"%1$s\"}", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public @ResponseBody String handleEntityNotFoundException(
            ObjectNotFoundException e) {
        LOGGER.warn(e.getMessage(), e);
        return String.format("{\"message\":\"%1$s\"}", e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(HealthCheckViews.Detailed.class)
    @ExceptionHandler(FailedHealthCheckException.class)
    public @ResponseBody HealthCheck handleFailedHealthException(
            FailedHealthCheckException e) {
        LOGGER.error(e.getMessage(), e);
        return new HealthCheck(e.getBean(), e.getIssues());
    }
}
