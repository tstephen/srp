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
