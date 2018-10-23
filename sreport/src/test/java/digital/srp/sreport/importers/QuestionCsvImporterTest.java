package digital.srp.sreport.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import digital.srp.sreport.model.Question;

public class QuestionCsvImporterTest {

    @Test
    public void testImport() {
        try {
            List<Question> questions = new QuestionCsvImporter().readQuestions();
            System.out.println(
                    String.format(" found %1$d returns", questions.size()));
            assertEquals(330, questions.size());

            // assert expected values of record (first currently complete)
            //TOTAL_ENERGY_COST$Total Energy Cost (all energy supplies)$£$ERIC S08_01$The total cost of electricity, gas, oil, and coal from all sources (including utility supplier, local source, renewable source), net of any costs that are charged to other organisations for which the trust provides energy (see apportionment rules). $
            Question question = getQuestion(questions, "TOTAL_ENERGY_COST");
            assertEquals("TOTAL_ENERGY_COST", question.name());
            assertEquals("Total Energy Cost (all energy supplies)", question.label());
            assertEquals("£", question.unit());
            assertEquals("ERIC S08_01", question.source());
            assertEquals("The total cost of electricity, gas, oil, and coal from all sources (including utility supplier, local source, renewable source), net of any costs that are charged to other organisations for which the trust provides energy (see apportionment rules).", question.hint());
            assertEquals("number", question.type());

            //CARBON_REDUCTION_BASE_YEAR$Which year is it?$$SDU$$number$yyyy$$[\\d]{4}
            question = getQuestion(questions, "CARBON_REDUCTION_BASE_YEAR");
            assertEquals("CARBON_REDUCTION_BASE_YEAR", question.name());
            assertEquals("What is your organisation's baseline year?", question.label());
            assertEquals("SDU", question.source());
            assertEquals("number", question.type());
            assertEquals("yyyy", question.placeholder());
            assertEquals("[\\\\d]{4}", question.validation());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

    private Question getQuestion(List<Question> questions, String qName) {
        for (Question q : questions) {
            if (qName.equals(q.name())) {
                return q;
            }
        }
        fail("unable to find question named "+qName);
        return null;
    }

}
