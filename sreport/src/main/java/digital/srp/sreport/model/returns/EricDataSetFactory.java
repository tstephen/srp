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
package digital.srp.sreport.model.returns;

public class EricDataSetFactory {

    public static final EricDataSet getInstance(String surveyName) {
        switch (surveyName) {
        case Eric1617.NAME:
            return new Eric1617();
        case Eric1516.NAME:
            return new Eric1516();
        case Eric1415.NAME:
            return new Eric1415();
        case Eric1314.NAME:
            return new Eric1314();
        default:
            throw new IllegalStateException(
                    String.format("No data registered for %1$s", surveyName));
        }
    }
}
