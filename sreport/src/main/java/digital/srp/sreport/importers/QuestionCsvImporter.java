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
package digital.srp.sreport.importers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.Question;

public class QuestionCsvImporter {

    private static final String NAME = "name";

    private static final String LABEL = "label";

    private static final String UNIT = "unit";

    private static final String SOURCE = "source";

    private static final String HINT = "hint";

    private static final String TYPE = "type";

    private static final String PLACEHOLDER = "placeholder";

    private static final String REQD = "required";

    private static final String VALIDATION = "validation";

    public static final String[] HEADERS = { NAME, LABEL, UNIT, SOURCE, HINT, TYPE, PLACEHOLDER, REQD, VALIDATION };

    public static final String DATA = "/data/Questions.csv";

    public static final String TENANT_ID = "sdu";

    private static final Logger LOGGER = LoggerFactory
            .getLogger(QuestionCsvImporter.class);

    public List<Question> readQuestions() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readQuestions(isr, HEADERS);
        }
    }

    public List<Question> readQuestions(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers).withDelimiter('$'));
        LOGGER.info(String.format("readQuestions"));

        List<Question> questions = new ArrayList<Question>();
        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            if (record.getRecordNumber() > 1) { // skip header row
                questions.add(new Question()
                        .name(record.get(NAME).trim())
                        .label(record.get(LABEL).trim())
                        .unit(record.get(UNIT).trim())
                        .source(record.get(SOURCE).trim())
                        .hint(record.get(HINT).trim())
                        .type(record.get(TYPE).trim())
                        .required(Boolean.valueOf(record.get(REQD).trim()))
                        .placeholder(record.get(PLACEHOLDER).trim())
                        .validation(record.get(VALIDATION).trim())
                        .tenantId(TENANT_ID)
                );
            }
        }
        parser.close();
        LOGGER.debug("Found {} questions", questions.size());

        return questions;
    }

}
