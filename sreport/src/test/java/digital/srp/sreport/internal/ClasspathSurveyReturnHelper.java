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
