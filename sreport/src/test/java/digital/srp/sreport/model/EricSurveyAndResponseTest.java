package digital.srp.sreport.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.surveys.Eric1516;

public class EricSurveyAndResponseTest {

    private static final File OUT_DIR = new File("target", "test-classes");
    private static final String SUBMITTER = "tim@knowprocess.com";
    private static final String[][] ANSWERS = {
            {"WORCESTERSHIRE HEALTH AND CARE NHS TRUST", "R1A", "COMMUNITY", "MIDLANDS AND EAST OF ENGLAND COMMISSIONING REGION"},
            {"0","0","1","5","1","0","4","1","27","2","46"},
            {"Yes","No","Yes","Yes","2. Target included but not on track to be met","3. Assessed but not approved by the organisation's board","3. Action plan produced but not approved by the organisations board"},
            {"2","864","738","28.33","0","3","106","612","503","156","0","2"},
            {"234","190","1","531","511","0","0","2","408","0"},
            {"8","11","0"},
            {"15","24","0","0","0"}
    };

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeAll
    public static void setUpClass() {
        OUT_DIR.mkdirs();
    }
    
    /**
     * Test that it is possible to build a survey containing all the required 
     * information for the 2015-16 ERIC data.
     */
    @Test
    public void create1516EricReturn() {
        Survey survey = Eric1516.getInstance().getSurvey();
        assertSurvey(survey);
        
        assertQuestionJsonFileOk(survey);
        
        Set<Answer> answers = new HashSet<Answer>();
        for (int i = 0; i < survey.categories().size(); i++) {
            System.out.println(String.format(
                    "  supplying answers for category: %1$s. %2$s", i,
                    survey.categories().get(i).name()));
            for (int j = 0; j < survey.categories().get(i).questionEnums().size(); j++) {
                System.out.println(String.format("    question: %1$d", j,
                        survey.categories().get(i).questionEnums().get(j)));
                answers.add(new Answer()
                        .question(survey.categories().get(i).questionEnums().get(j))
                        .response(ANSWERS[i][j]));
            }
        }
        
        SurveyReturn response = new SurveyReturn()
                .submittedBy(SUBMITTER)
                .applicablePeriod("2015-16")
                .answers(answers);
        
        assertIsComplete(survey);
        
        assertResponseJsonFileOk(response);
    }

    

    private void assertSurvey(Survey survey) {
        assertEquals(7, survey.categories().size());
        assertEquals(4, survey.categories().get(0).questionEnums().size());
        assertEquals(11, survey.categories().get(1).questionEnums().size());
        assertEquals(7, survey.categories().get(2).questionEnums().size());
        assertEquals(2, survey.categories().get(3).questionEnums().size());
        assertEquals(10, survey.categories().get(4).questionEnums().size());
        assertEquals(3, survey.categories().get(5).questionEnums().size());
        assertEquals(5, survey.categories().get(6).questionEnums().size());
    }

    private void assertIsComplete(Survey survey) {
        // TODO fail if incomplete
    }

    private void assertQuestionJsonFileOk(Survey survey) {
        File resultFile = new File(OUT_DIR, "Eric1516.json");
        try {
            objectMapper.writeValue(resultFile , survey);
            assertTrue(resultFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to write JSON representation of ERIC data");
        }
        
        try {
            Survey survey2 = objectMapper.readValue(resultFile, Survey.class);
            assertEquals(survey.categories().size(), survey2.categories().size());
            assertEquals(survey.questionCodes().size(), survey2.questionCodes().size());
            assertEquals(survey.hashCode(), survey2.hashCode());
            assertSurvey(survey2);           
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to re-read ERIC data from JSON");
        }
    }
    private void assertResponseJsonFileOk(SurveyReturn response) {
        File resultFile = new File(OUT_DIR, "Eric1516-Worcester.json");
        try {
            objectMapper.writeValue(resultFile , response);
            assertTrue(resultFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to write JSON representation of ERIC responses");
        }
    }
}
