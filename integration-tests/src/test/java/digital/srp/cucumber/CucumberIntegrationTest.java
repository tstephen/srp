package digital.srp.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources", 
        glue = { "com.knowprocess.cucumber", "digital.srp.cucumber" },
        plugin = {
        /*
         * "junit:target/surefire-reports/cucumber.xml",
         * "json:target/surefire-reports/cucumber.json",
         */
        "html:target/site/cucumber" })
public class CucumberIntegrationTest {

}