/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import digital.srp.sreport.api.exceptions.FailedHealthCheckException;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.HealthCheck;

@ControllerAdvice
public class SReportExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SReportExceptionHandler.class);

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
    @ExceptionHandler(FailedHealthCheckException.class)
    public @ResponseBody HealthCheck handleFailedHealthException(
            FailedHealthCheckException e) {
        LOGGER.error(e.getMessage(), e);
        return new HealthCheck(e.getBean(), e.getIssues());
    }
}
