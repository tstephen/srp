package digital.srp.sreport.internal;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.SurveyAnswer;
import digital.srp.sreport.model.SurveyQuestion;
import digital.srp.sreport.model.SurveyReturn;

public class EricCsvImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EricCsvImporter.class);
    
    private List<SurveyQuestion> questions;

    public List<SurveyReturn> readEricReturns(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readEricReturns"));

        questions = new ArrayList<SurveyQuestion>();
        for (String hdr : headers) {
            questions.add(new SurveyQuestion().text(hdr));
        }
        LOGGER.debug(String.format("Found %1$d questions", questions.size())); 

        List<SurveyReturn> surveyResponses = new ArrayList<SurveyReturn>();
        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            if (record.getRecordNumber() > 2) { // skip headers
                List<SurveyAnswer> surveyAnswers = new ArrayList<SurveyAnswer>();
                String org = record.get(0);
                for (int i = 0; i < record.size(); i++) {
                    surveyAnswers.add(new SurveyAnswer().question(getQuestion(i))
                            .response(record.get(i)));
                }

                surveyResponses
                        .add(new SurveyReturn().org(org).answers(surveyAnswers));
            }
        }
        parser.close();

        return surveyResponses;
    }

    private SurveyQuestion getQuestion(int i) {
        LOGGER.debug(String.format("Looking for question %1$d", i)); 
        return questions.get(i);
    }

}
