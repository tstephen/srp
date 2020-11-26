package digital.srp.sreport.model.surveys;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.Survey;

public class Sdu1617SurveyServiceIntegrationTest {

    private static ObjectMapper objectMapper;
    private static File outDir = new File("target", "test-classes");
    
    @BeforeClass
    public static void setUp() {
        objectMapper = new ObjectMapper();
        outDir.mkdirs();
    }
    
    @Test
    public void test() throws IOException {
        Survey survey = Sdu1617.getInstance().getSurvey();
        
        Writer out = null; 
        try {
            out = new FileWriter(new File(outDir, "Sdu1617.json"));
            objectMapper.writeValue(out, survey);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Cannot write survey JSON");
        }
    }

}
