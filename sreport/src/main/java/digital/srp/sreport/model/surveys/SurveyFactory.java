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
package digital.srp.sreport.model.surveys;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;

/**
 * Marker interface for surveys that provides a factory for (@code Survey)s.
 *
 * @author Tim Stephenson
 */
public interface SurveyFactory {

    Survey getSurvey();
    Q[] getQs();

    static Survey[] getExpectedSurveys() {
        Survey[] expectedSurveys = {
                Eric1516.getInstance().getSurvey(),
                Sdu1617.getInstance().getSurvey(),
                Sdu1718.getInstance().getSurvey(),
                Sdu1819.getInstance().getSurvey(),
                Sdu1920.getInstance().getSurvey(),
                Sdu2021.getInstance().getSurvey()
        };
        return expectedSurveys;
    }

    static SurveyFactory getInstance(String name) {
        switch (name) {
        case Eric1516.ID:
            return Eric1516.getInstance();
        case Sdu1516.ID:
            return Sdu1516.getInstance();
        case Sdu1617.ID:
            return Sdu1617.getInstance();
        case Sdu1718.ID:
            return Sdu1718.getInstance();
        case Sdu1819.ID:
            return Sdu1819.getInstance();
        case Sdu1920.ID:
            return Sdu1920.getInstance();
        case Sdu2021.ID:
            return Sdu2021.getInstance();
        default:
            throw new ObjectNotFoundException(Survey.class,
                    String.format("No survey named %1$s is known", name));
        }
    }

}
