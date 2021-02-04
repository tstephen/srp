/*******************************************************************************
 *Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.server;

import java.io.IOException;
import java.io.StringWriter;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.api.exceptions.ResultSetTooLargeException;

@ControllerAdvice
public class SrpExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SrpExceptionHandler.class);
    @Autowired
    private ObjectMapper mapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody String handleConstraintViolationException(
            ConstraintViolationException e) throws IOException {
        LOGGER.error("Constraint violation: " + e.getMessage());
        StringWriter sw = new StringWriter();
        mapper.writeValue(sw,e.getConstraintViolations());
        return sw.toString();
    }

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
