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
public class Sdu1920SurveyServiceIntegrationTest {

    @Autowired
    private SurveyService svc;

    @Test
    public void testInitSurveyAndShowStatus() {
        try {
            Survey survey = SurveyFactory.getInstance(Sdu1920.ID).getSurvey();
            svc.initSurvey(survey);
            assertThat(survey.applicablePeriod()).isEqualTo("2019-20");
            assertThat(survey.categories().size()).isEqualTo(18);
            // h2 database used for tests does not default create time
            assertThat(survey.questionCodes().size()).isEqualTo(183);
            assertThat(survey.questions().size()).isEqualTo(survey.questionCodes().size());

            // repeat to test idempotence and pre-existing survey path 
            svc.initSurvey(survey);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

