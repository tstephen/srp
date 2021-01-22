package digital.srp.sreport.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.SurveyReturn;

public class ClasspathSurveyReturnHelper {

    private static final String DATA_FILE = "/returns/%1$s.json";
    private final ObjectMapper objectMapper;

    public ClasspathSurveyReturnHelper() {
        objectMapper = new ObjectMapper();
    }

    public Optional<SurveyReturn> readSurveyReturn(String org) {
        String dataFile = String.format(DATA_FILE, org.toLowerCase());
        try (InputStream is = getClass().getResourceAsStream(dataFile)) {
            assertNotNull(is,
                    String.format("Unable to find test data at %1$s", dataFile));
            return Optional.of(objectMapper.readValue(is, SurveyReturn.class));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return Optional.empty();
        }
    }
}
