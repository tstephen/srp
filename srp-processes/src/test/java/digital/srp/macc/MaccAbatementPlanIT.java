/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bdd.ActivitiSpec;
import org.activiti.bdd.ext.DumpAuditTrail;
import org.activiti.bdd.test.activiti.ExtendedRule;
import org.activiti.engine.IdentityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.knowprocess.resource.spi.RestDelete;
import com.knowprocess.resource.spi.RestPost;

import digital.srp.TestCredentials;

public class MaccAbatementPlanIT {

    private static final String TENANT_ID = "srp";

    private static final String MSG_ABATEMENT_PLAN = TENANT_ID
            + ".abatementplan";

    @Rule
    public ExtendedRule activitiRule = new ExtendedRule("test-activiti.cfg.xml");

    private String contactId;

    private String baseJson;

    private String shortId;

    @Before
    public void setUp() {
        IdentityService idSvc = activitiRule.getIdentityService();
        TestCredentials.initBot(idSvc, TENANT_ID);
        
        createContact();
    }

    @After
    public void tearDown() {
        IdentityService idSvc = activitiRule.getIdentityService();
        TestCredentials.removeBot(idSvc, TENANT_ID);
        
        deleteContact(contactId);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = {
            "processes/digital/srp/macc/RecordAbatementPlan.bpmn",
            "processes/link/omny/custmgmt/UpdateContact.bpmn",
            "processes/link/omny/custmgmt/AddActivityToContact.bpmn" }, tenantId = TENANT_ID)
    public void testRecordAbatementPlan() throws Exception {
        new ActivitiSpec(activitiRule, "testRecordAbatementPlan")
                .given("No particular pre-conditions")
                .whenMsgReceived("New plan submission", MSG_ABATEMENT_PLAN,
                        getPlanJson(), TENANT_ID)
                .whenExecuteJobsForTime(2000)
                .thenSubProcessCalled("UpdateContact")
                .thenSubProcessCalled("AddActivityToContact")
                .thenProcessIsComplete()
                .thenExtension(new DumpAuditTrail(activitiRule));
    }
    
    private String getPlanJson() {
       String json = String.format("{"
              +"\"organisationType\":\"/organisation-types/22\","
              +"\"existingInterventions\":[],"
              +"\"characteristics\":["
              +"  {\"unit\":\"No. of staff\",\"unitCount\":5740},"
              +"  {\"unit\":\"Floor area (m2)\",\"unitCount\":126001}"
              +"],"
              +"\"shortId\":\"%1$s\","
              +"%2$s"
              +"}", shortId, baseJson.substring(1, baseJson.length()-1));
        return json;
    }

    private void createContact() {
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            baseJson = "{\"firstName\":\"Julian\",\"lastName\":\"Hibbert\","
                    + "\"email\":\"drhibbert@springfieldhealth.com\","
                    + "\"phone1\":\"+447788967607\","
                    + "\"licenseAgreed\":true,\"tenantId\":\"sdu\"}";
            Map<String, Object> response = new RestPost().post(TestCredentials.BOT_USERNAME,
                            TestCredentials.BOT_PWD,
                            TestCredentials.CUST_MGMT_URL + "/" + TENANT_ID
                                    + "/contacts/",
                            headers,
                            new String[] { "Location" },
                            baseJson);
            System.out.println(" created contact: " + response);
            String location = (String) response.get("Location");
            contactId = TestCredentials.CUST_MGMT_URL+ location;
            shortId = location.substring(location.lastIndexOf('/') + 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    private void deleteContact(String contactId) {
        try {
            new RestDelete().delete(contactId, TestCredentials.BOT_USERNAME,
                    TestCredentials.BOT_PWD, "Content-Type:application/json",
                    null);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ":" + e.getMessage());
        }
    }
}