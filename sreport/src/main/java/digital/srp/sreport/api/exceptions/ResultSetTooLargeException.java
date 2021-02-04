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
