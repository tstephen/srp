package digital.srp.cucumber.sreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.springframework.http.HttpStatus;

import com.knowprocess.cucumber.IntegrationTestSupport;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import digital.srp.sreport.model.Question;

public class QuestionStepDefs extends IntegrationTestSupport {
    private String qUri;
    private Question q;

    @When("^a list of questions is requested$")
    public void a_list_of_questions_is_requested() throws Throwable {
        executeGet(String.format("/%1$s/questions/", tenantId));
    }

    @Then("^the list of (\\d+) available questions is returned$")
    public void the_list_of_available_questions_is_returned(int qCount) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        Question[] questions = (Question[]) latestResponse.parseArray(Question.class);
        assertEquals(qCount, questions.length);
    }

    @When("^a new question named \"([^\"]*)\" is added$")
    public void a_new_question_named_is_added(String qName) throws Throwable {
        q = new Question().name(qName);
        executePost(String.format("/%1$s/questions/", tenantId), q);
    }
    
    @When("^the question's type is set to \"([^\"]*)\", placeholder to \"([^\"]*)\" and hint to \"([^\"]*)\"$")
    public void the_question_fields_are_set(String type, String placeholder, String hint) throws Throwable {
        executePut(qUri, q.type(type).placeholder(placeholder).hint(hint));
    }

    @Then("^the new question's identifier is returned$")
    public void the_new_question_identifier_is_returned() throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.CREATED);
        qUri = latestResponse.location();
        assertNotNull(qUri);
    }

    @When("^the question's identifier is requested$")
    public void the_question_identifier_is_requested() throws Throwable {
        executeGet(qUri);
    }
    @Then("^the question with name \"([^\"]*)\" is returned with type = \"([^\"]*)\", placeholder = \"([^\"]*)\" and hint = \"([^\"]*)\"$")
    public void the_question_is_returned(String qName, String type, String placeholder, String hint) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        Question question = (Question) latestResponse.parseObject(Question.class);
        assertEquals(qName, question.name());
        assertEquals(type.equals("null") ? null : type, question.type());
        assertEquals(placeholder.equals("null") ? null : placeholder, question.placeholder());
        assertEquals(hint.equals("null") ? null : hint, question.hint());
    }

    @When("^question is deleted$")
    public void question_is_deleted() throws Throwable {
        executeDelete(qUri);
    }
}
