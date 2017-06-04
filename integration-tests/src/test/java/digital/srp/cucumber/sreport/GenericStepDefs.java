package digital.srp.cucumber.sreport;

import org.springframework.http.HttpStatus;

import com.knowprocess.cucumber.IntegrationTestSupport;

import cucumber.api.java.en.Then;

public class GenericStepDefs extends IntegrationTestSupport {
    
    @Then("^the response is success$")
    public void the_response_is_success() throws Throwable {
        latestResponse.statusCodeIsSuccess();
    }

    @Then("^the response code is (\\d+)$")
    public void the_response_code_is(int statusCode) throws Throwable {
        latestResponse.statusCodeIs(HttpStatus.valueOf(statusCode));
    }
    
}
