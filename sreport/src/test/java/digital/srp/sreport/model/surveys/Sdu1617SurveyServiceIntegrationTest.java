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


import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.Survey;

public class Sdu1617SurveyServiceIntegrationTest {

    @Autowired
    private static ObjectMapper objectMapper;
    private static File outDir = new File("target", "test-classes");

    @BeforeAll
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
