package digital.srp.sreport.model.surveys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import digital.srp.sreport.Application;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.services.SurveyService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class Sdu1819SurveyServiceIntegrationTest {

    @Autowired
    private SurveyService svc;

    @BeforeClass
    public static void setUpClass() {
    }

    @Test
    public void testInitSurveyAndShowStatus() {
        long start = System.currentTimeMillis();
        try {
            Survey survey = SurveyFactory.getInstance(Sdu1819.ID).getSurvey();
            svc.initSurvey(survey);
            assertThat(survey.applicablePeriod()).isEqualTo("2018-19");
            assertThat(survey.categories().size()).isEqualTo(16);
            assertThat(survey.created().getTime()).isGreaterThan(start);
            assertThat(survey.questionCodes().size()).isEqualTo(168);
            assertThat(survey.questions().size()).isEqualTo(survey.questionCodes().size());

            // repeat to test idempotence and pre-existing survey path 
            svc.initSurvey(survey);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
