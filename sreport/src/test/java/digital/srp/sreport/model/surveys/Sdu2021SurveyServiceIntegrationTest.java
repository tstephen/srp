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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import digital.srp.sreport.Application;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.services.SurveyService;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class Sdu2021SurveyServiceIntegrationTest {

    @Autowired
    private SurveyService svc;

    @Test
    public void testInitSurveyAndShowStatus() {
        try {
            Survey survey = SurveyFactory.getInstance(Sdu2021.ID).getSurvey();
            svc.initSurvey(survey);
            assertThat(survey.applicablePeriod()).isEqualTo(Sdu2021.PERIOD);
            assertThat(survey.categories().size()).isEqualTo(20);
            // h2 database used for tests does not default create time
            assertThat(survey.questionCodes().size()).isEqualTo(196);
            assertThat(survey.questions().size()).isEqualTo(survey.questionCodes().size());

            // repeat to test idempotence and pre-existing survey path
            svc.initSurvey(survey);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
