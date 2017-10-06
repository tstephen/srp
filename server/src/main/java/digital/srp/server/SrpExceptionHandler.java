package digital.srp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.api.exceptions.ResultSetTooLargeException;

@ControllerAdvice
public class SrpExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SrpExceptionHandler.class);

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(ResultSetTooLargeException.class)
    public @ResponseBody String handleLargeResultSetException(
            ResultSetTooLargeException e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public @ResponseBody String handleObjectNotFoundException(
            ObjectNotFoundException e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
    }
}
