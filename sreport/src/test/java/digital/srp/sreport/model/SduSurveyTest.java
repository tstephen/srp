package digital.srp.sreport.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SduSurveyTest {

    private static final File OUT_DIR = new File("target", "test-classes");
    protected ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeAll
    public static void setUpClass() {
        OUT_DIR.mkdirs();
    }
    
    /**
     * Test that it is possible to build a survey containing all the required 
     * information for the 2016-17 survey.
     */
    @Test
    public void create1617Survey() {
        SurveyCategory catOrg = new SurveyCategory().name("Organisation")
                .questionEnums(Q.ORG_NAME, Q.ORG_CODE, Q.ORG_NICKNAME);
        assertEquals(3, catOrg.questionEnums().size());

        SurveyCategory catPolicy = new SurveyCategory().name("Policy")
                .questionEnums(Q.SDMP_CRMP, Q.SDMP_BOARD_REVIEW_WITHIN_12_MONTHS,
                        Q.SDMP_MISSION_STATEMENT);
        assertEquals(3, catPolicy.questionEnums().size());
 
        Survey survey = new Survey().applicablePeriod("2016-17").categories(Arrays.asList(catOrg, catPolicy));
        assertEquals(2, survey.categories().size());
        assertEquals(3, survey.categories().get(0).questionEnums().size());
        assertEquals(3, survey.categories().get(1).questionEnums().size());
        
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
            assertEquals(3, survey2.categories().get(0).questionEnums().size());
            assertEquals(3, survey2.categories().get(1).questionEnums().size());            
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unable to re-read survey from JSON");
        }
    }

}
