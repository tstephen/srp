package digital.srp.cucumber.sreport;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;

import com.knowprocess.cucumber.IntegrationTestSupport;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Eric1516;
import digital.srp.sreport.model.surveys.Sdu1617;

public class SustainabilityReturnStepDefs extends IntegrationTestSupport {

    private SurveyReturn rtn;

    private String getSduSurveyName(int startYear, int endYear) {
        return "SDU-"+startYear+"-"+endYear;
    }
    
    @When("^the data status is requested$")
    public void the_data_status_is_requested() throws Throwable {
        executeGet("/admin/data-mgmt/");
    }
    
    @Then("^there should be at least (\\d+) questions, (\\d+) surveys, (\\d+) answers, (\\d+) returns and (\\d+) orgs$")
    public void there_should_be_at_least_questions_surveys_answers_returns_and_orgs(int qCount, int surveyCount, int answerCount, int returnCount, int orgCount) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        // for example: { "questions": 250, "surveys": 2, "answers": 71,220, "returns": 238, "orgs": 3 }
        String json = latestResponse.getBody();
        int start = json.indexOf("\"questions\":")+"\"questions\":".length();
        assertEquals(qCount, Integer.parseInt(json.substring(start, json.indexOf(',', start)).trim()));
        start = json.indexOf("\"answers\":")+"\"answers\":".length();
        assertEquals(answerCount, Integer.parseInt(json.substring(start, json.indexOf(',', start)).trim()));
        start = json.indexOf("\"surveys\":")+"\"surveys\":".length();
        assertEquals(surveyCount, Integer.parseInt(json.substring(start, json.indexOf(',', start)).trim()));
        start = json.indexOf("\"returns\":")+"\"returns\":".length();
        assertEquals(returnCount, Integer.parseInt(json.substring(start, json.indexOf(',', start)).trim()));
    }
    
    @When("^a list of surveys is requested$")
    public void a_list_of_surveys_is_requested() throws Throwable {
        executeGet("/surveys/");
    }

    @Then("^the list of (\\d+) available surveys is returned$")
    public void the_list_of_available_surveys_is_returned(int surveyCount) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        Survey[] surveys = (Survey[]) latestResponse.parseArray(Survey.class);
        assertEquals(surveyCount, surveys.length);
    }
    
    @When("^the SDU survey for (\\d+)-(\\d+) is uploaded$")
    public void the_SDU_survey_for_period_is_uploaded(int startYear, int endYear) throws Throwable {
        String surveyName = getSduSurveyName(startYear, endYear);
        Survey survey = null;
        switch (surveyName) {
        case Eric1516.ID:
            survey = Eric1516.getSurvey();
            break;
        case Sdu1617.ID:
            survey = Sdu1617.getSurvey();
            break;
        default: 
            fail("No survey definition for " + surveyName);
        }
        for (SurveyCategory cat : survey.categories()) {
            assertNotNull(cat);
            assertNotNull(cat.name());
        }
        executePut("/surveys/"+surveyName, survey);
    }
    
    @When("^the survey ([-\\w]+) is requested$")
    public void the_survey_is_requested(String surveyName) throws Throwable {
        executeGet("/surveys/findByName/"+surveyName);
    }
    
    @Then("^the full survey for (\\d+)-(\\d+) comprising (\\d+) questions is returned$")
    public void the_full_survey_comprising_questions_is_returned(int startYear, int endYear, int qCount) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        Survey survey = (Survey) latestResponse.parseObject(Survey.class);
        assertNotNull(survey.id());
        assertEquals(getSduSurveyName(startYear, endYear), survey.name());
        assertEquals(startYear+"-"+endYear, survey.applicablePeriod());
        assertEquals(qCount, survey.questionCodes().size());
    }
    
    @Then("^all questions have a label$")
    public void all_questions_have_a_label() throws Throwable {
        Survey survey = (Survey) latestResponse.parseObject(Survey.class);
        for (Question q : survey.questions()) {
            System.err.println("  does q have label?" + q);
            assertNotNull(String.format("Question $1%s has no label", q.name()), q.label());
        }
    }

    @When("^historic ERIC data is imported for (\\w+) (\\d+)-(\\d+)$")
    public void the_ERIC_data_for_is_imported(String org, int startYear, int endYear) throws Throwable {
        executeGet("/returns/importEric/"+getSduSurveyName(startYear, endYear)+"/"+org+"?import=ERIC-"+startYear+"-"+endYear);
    }

    @Then("^(\\d+) years answers exist for (\\w+), (\\w+) and (\\w+)$")
    public void answers_exist_for_x_y_and_z(int yearCount, String q1, String q2,
            String q3) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        SurveyReturn rtn = (SurveyReturn) latestResponse
                .parseObject(SurveyReturn.class);
        List<String> periods = new ArrayList<String>();
        for (Answer answer : rtn.answers()) {
            if (!periods.contains(answer.applicablePeriod())) {
                periods.add(answer.applicablePeriod());
            }
        }
        for (String period : periods) {
            assertNotNull(
                    String.format("No answer for %1$s in %2$s", q1, period),
                    rtn.answer(Q.valueOf(q1), period));
            assertNotNull(
                    String.format("No answer for %1$s in %2$s", q2, period),
                    rtn.answer(Q.valueOf(q2), period));
            assertNotNull(
                    String.format("No answer for %1$s in %2$s", q3, period),
                    rtn.answer(Q.valueOf(q3), period));
        }
        assertTrue(periods.size() >= yearCount);
    }
    
    @When("^a list of returns is requested for organisation (\\w+)$")
    public void a_list_of_returns_is_requested_for_organisation(String org) throws Throwable {
        executeGet("/returns/findByOrg/"+org);
    }

    @Then("^the list of available survey returns provided includes ([-\\w]+) (\\w+) and ([-\\w]+) (\\w+)$")
    public void the_list_of_available_survey_returns_is_provided_including(String survey1, String survey1Status, String survey2, String survey2Status) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        SurveyReturn[] returns = (SurveyReturn[]) latestResponse.parseArray(SurveyReturn.class);
        assertTrue(returns.length >= 2);
        for (SurveyReturn surveyReturn : returns) {
            if (surveyReturn.name().equals(survey1)) {
                assertEquals(survey1, surveyReturn.name());
                assertEquals("2015-16", surveyReturn.applicablePeriod());
                assertEquals(survey1Status, surveyReturn.status());
            } else if (surveyReturn.name().equals(survey1)) {
                assertEquals(survey2, surveyReturn.name());
                assertEquals("2016-17", surveyReturn.applicablePeriod());
                assertEquals(survey2Status, surveyReturn.status());
            }
        }
    }

    @When("^the SDU (\\d+)-(\\d+) return of (\\w+) is requested$")
    public void the_sdu_return_for_the_current_year_is_requested(int startYear, int endYear, String org) throws Throwable {
        executeGet("/returns/findCurrentBySurveyNameAndOrg/"+getSduSurveyName(startYear, endYear)+"/"+org);
        latestResponse.statusCodeIs(HttpStatus.OK);
        rtn = (SurveyReturn) latestResponse.parseObject(SurveyReturn.class);
    }

    @Then("^an SDU (\\d+)-(\\d+) return for (\\w+) with correct header information is returned$")
    public void an_SDU_return_for_the_organisation_is_returned(int startYear, 
            int endYear, String org) throws Throwable {
        assertNotNull(rtn);
        assertEquals(startYear+"-"+endYear, rtn.applicablePeriod());
        assertEquals(org, rtn.org());
        assertEquals(org, rtn.answer(Q.ORG_CODE, rtn.applicablePeriod()).response());
    }
    

    @Then("^the return contains pro-forma answers for (\\d+) years \\(at least\\)$")
    public void the_return_contains_header_information_and_pro_forma_answers_for_the_expected_years(int minNoOfYears) throws Throwable {
        Set<String> years = new HashSet<String>();
        ArrayList<Q> missingAnswers = new ArrayList<Q>();
        for (Q q : Sdu1617.getSurvey().questionCodes()) {
            if (rtn.answer(q, rtn.applicablePeriod())==null) {
                missingAnswers.add(q);
            }
        }
        System.out.println("Missing answers: "+ missingAnswers);
        assertEquals(0, missingAnswers.size());
        ArrayList<Q> calculatedAnswers = new ArrayList<Q>();
        for (Answer a : rtn.answers()) {
            if (!Sdu1617.getSurvey().questionCodes().contains(a.question().q())) {
                calculatedAnswers.add(a.question().q());
            }
            years.add(a.applicablePeriod());
        }
        System.out.println("Calculated answers: "+ calculatedAnswers);
        assertTrue(years.size() >= minNoOfYears);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(), minNoOfYears);
        for (String p : periods) {
            assertTrue(years.contains(p));
        }
    }

    @When("^the (\\w+) return is updated with (\\w+), (\\w+) and (\\w+) and uploaded$")
    public void the_return_is_updated_with_x_y_and_z_and_submitted(String org, String q1, String q2, String q3) throws Throwable {
        rtn.answer(Q.valueOf(q1), Sdu1617.PERIOD).response(getTestDataNumber().toString());
        rtn.answer(Q.valueOf(q2), Sdu1617.PERIOD).response(getTestDataNumber().toString());
        rtn.answer(Q.valueOf(q3), Sdu1617.PERIOD).response(getTestDataNumber().toString());
        executePut("/returns/"+rtn.id(), rtn);
    }

    private Object getTestDataNumber() {
        return new Date().getTime();
    }

    @Then("^the (\\w+) return for (\\d+)-(\\d+) is available with status '(\\w+)' and audit data is set$")
    public void the_return_for_is_available_with_expected_status(String org, int startYear, int endYear, String status) throws Throwable {
        executeGet("/returns/findCurrentBySurveyNameAndOrg/"+getSduSurveyName(startYear, endYear)+"/"+org);
        latestResponse.statusCodeIs(HttpStatus.OK);
        rtn = (SurveyReturn) latestResponse.parseObject(SurveyReturn.class);
//        executeGet("/returns/"+rtn.id());
        System.out.println("Found return with id:"+rtn.id());
        assertEquals(startYear+"-"+endYear, rtn.applicablePeriod());
        assertEquals(status, rtn.status());
        for (Answer answer : rtn.answers()) {
            System.out.println("Checking status of "+answer.id());
            switch (StatusType.valueOf(rtn.status())) {
            case Draft:
                assertThat(answer.status(), anyOf(equalTo(StatusType.Draft.name()), equalTo(StatusType.Published.name())));
                break;
            case Published:
            case Submitted:
                // TODO Calculations always appear to be draft, that may need to be fixed
                assertThat(answer.status(), anyOf(equalTo(StatusType.Draft.name()), equalTo(StatusType.Published.name()), equalTo(StatusType.Submitted.name())));
                break;
            case Superceded:
                assertEquals(StatusType.Superceded.name(), answer.status());
                break;
            default:
                // fail?
                break;
            }
            switch(answer.question().name()) {
            case "orgCode": 
                assertEquals(org, answer.response());
            }
        }
        assertTrue("Last updated date is not set to today", DateUtils.isToday(rtn.lastUpdated()));
        assertNotNull("Last updated by is not set", rtn.updatedBy());
    }
    
    @When("^the SDU (\\d+)-(\\d+) return of (\\w+) for period (\\d+)-(\\d+) is requested$")
    public void the_SDU_return_of_for_period_is_requested(int qStartYear, int qEndYear, String org, int aStartYear, int aEndYear) throws Throwable {
        executeGet("/returns/findCurrentBySurveyOrgAndPeriod/"+getSduSurveyName(qStartYear, qEndYear)+"/"+org+"/"+aStartYear+"-"+(aEndYear-2000));
    }

    @Then("^the (\\w+) (\\d+)-(\\d+) data for (\\d+)-(\\d+) questions is returned$")
    public void the_RDR_data_for_questions_is_returned(String org, int qStartYear, int qEndYear, int aStartYear, int aEndYear) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.OK);
        rtn = (SurveyReturn) latestResponse.parseObject(SurveyReturn.class);
        assertEquals(qStartYear+"-"+qEndYear, rtn.applicablePeriod());
        assertEquals(org, rtn.org());
        assertEquals(org, rtn.answer(Q.ORG_CODE, rtn.applicablePeriod()).response());
    }

    @Given("^the SDU (\\d+)-(\\d+) return of (\\w+) is complete$")
    public void the_SDU_return_is_complete(int startYear, int endYear, String org) throws Throwable {
        executeGet("/returns/findCurrentBySurveyNameAndOrg/"+Sdu1617.ID+"/"+org);
        latestResponse.statusCodeIs(HttpStatus.OK);
        rtn = (SurveyReturn) latestResponse.parseObject(SurveyReturn.class);
        
        assertTrue(rtn.isComplete());
        assertEquals(StatusType.Draft.name(), rtn.status());
    }
    
    @When("^the user submits the return$")
    public void the_user_submits_the_return() throws Throwable {
        executePost("/returns/"+rtn.id()+"/status", "Submitted");
        latestResponse.statusCodeIs(HttpStatus.OK);
    }

    @Then("^it is no longer available for edit$")
    public void it_is_no_longer_available_for_edit() throws Throwable {
        try {
            executePut("/returns/"+rtn.id(), rtn);
        } catch (IllegalStateException e) {
            ; // expected
        }
    }
    
    @When("^the user restates the return$")
    public void the_user_restates_the_return() throws Throwable {
        executePut("/returns/"+rtn.id()+"/restate", rtn);
    }

}
