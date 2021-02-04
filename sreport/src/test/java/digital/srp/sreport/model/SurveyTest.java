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
package digital.srp.sreport.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import digital.srp.sreport.model.returns.EricQuestions;

public class SurveyTest implements EricQuestions{

    @Test
    public void testBusinessDataEquality() {
        SurveyCategory catOrg = new SurveyCategory()
                .id(1l)
                .name("Organisation")
                .questionEnums(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg.questionEnums().size());

        SurveyCategory catPolicy = new SurveyCategory()
                .id(2l)
                .name("Policy")
                .questionEnums(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy.questionEnums().size());

        Survey survey1 = new Survey().id(1l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey1.categories().size());
        assertEquals(2, survey1.categories().get(0).questionEnums().size());
        assertEquals(2, survey1.categories().get(1).questionEnums().size());

        SurveyCategory catOrg2 = new SurveyCategory()
                .id(2l)
                .name("Organisation")
                .questionEnums(Q.ORG_NAME,Q.ORG_CODE);
        assertEquals(2, catOrg2.questionEnums().size());

        SurveyCategory catPolicy2 = new SurveyCategory()
                .id(3l)
                .name("Policy")
                .questionEnums(Q.SDMP_CRMP,Q.ADAPTATION_PLAN_INC);
        assertEquals(2, catPolicy2.questionEnums().size());

        Survey survey2 = new Survey().id(2l).applicablePeriod("2016-17").categories(Arrays.asList(catOrg2, catPolicy2));
        assertEquals(2, survey2.categories().size());
        assertEquals(2, survey2.categories().get(0).questionEnums().size());
        assertEquals(2, survey2.categories().get(1).questionEnums().size());

        assertEquals(survey1, survey2);
    }

}
