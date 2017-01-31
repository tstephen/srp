package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;

public class SduSurveyTest {

    private static final File OUT_DIR = new File("target", "test-classes");
    protected ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeClass
    public static void setUpClass() {
        OUT_DIR.mkdirs();
    }
    
    /**
     * Test that it is possible to build a survey containing all the required 
     * information for the 2016-17 survey.
     */
    @Test
    public void create1617Survey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Name of organisation").required(true),
                        new SurveyQuestion().text("Organisation code e.g. RAA").required(true),
                        new SurveyQuestion().text("Abbreviation or nick name of organisation used").required(false)
                ));
        assertEquals(3, catOrg.questions().size());

        SurveyCategory catPolicy = new SurveyCategory()
                .name("Policy")
                .questions(Arrays.asList(
                        new SurveyQuestion().text("Does your organisation have a current* Board-approved Sustainable Development Management Plan (SDMP) or Carbon Reduction Management Plan (CRMP)?").required(true),
                        new SurveyQuestion().text("Was the SDMP reviewed or approved by the board in the last 12 months?").required(true),
                        new SurveyQuestion().text("If your SDMP has a sustainability mission statement, what is it?").required(false)
                ));
        assertEquals(3, catPolicy.questions().size());
        
        Survey survey = new Survey().applicablePeriod("2016-17").categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey.categories().size());
        assertEquals(3, survey.categories().get(0).questions().size());
        assertEquals(3, survey.categories().get(1).questions().size());
        
        File resultFile = new File(OUT_DIR, "Survey1617.json");
        try {
            objectMapper.writeValue(resultFile , survey);
            assertTrue(resultFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to write JSON representation of survey");
        }
        
        try {
            Survey survey2 = objectMapper.readValue(resultFile, Survey.class);
            assertEquals(survey, survey2);
            assertEquals(2, survey2.categories().size());
            assertEquals(3, survey2.categories().get(0).questions().size());
            assertEquals(3, survey2.categories().get(1).questions().size());            
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to re-read survey from JSON");
        }
    }

}
