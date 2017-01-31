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
package digital.srp.disclosure;

import org.activiti.bdd.ActivitiSpec;
import org.activiti.bdd.ext.DumpAuditTrail;
import org.activiti.bdd.test.activiti.ExtendedRule;
import org.activiti.bdd.test.mailserver.TestMailServer;
import org.activiti.engine.IdentityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import digital.srp.TestCredentials;

public class DisclosureIT {

    private static final String TENANT_ID = "srp";

    private static final String MSG_DISCLOSURE = TENANT_ID
            + ".sustainabilityDisclosure";

    @Rule
    public ExtendedRule activitiRule = new ExtendedRule("test-activiti.cfg.xml");

    @Rule
    public TestMailServer mailServer = new TestMailServer();

    @Before
    public void setUp() {
        IdentityService idSvc = activitiRule.getIdentityService();
        TestCredentials.initBot(idSvc, TENANT_ID);
    }

    @After
    public void tearDown() {
        IdentityService idSvc = activitiRule.getIdentityService();
        TestCredentials.removeBot(idSvc, TENANT_ID);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = {
            "processes/digital/srp/disclosure/RecordDisclosure.bpmn",
            "processes/digital/srp/disclosure/ConfirmEmail.bpmn",
            "processes/link/omny/mail/SendMemo.bpmn",
            "processes/link/omny/custmgmt/CreateContactAndAccount.bpmn",
            "processes/link/omny/custmgmt/AddActivityToContact.bpmn",
            "processes/link/omny/wordpress/CreateWordPressAccount.bpmn" }, tenantId = TENANT_ID)
    public void testDisclosureSubmission() throws Exception {
        new ActivitiSpec(activitiRule, "testDisclosureSubmission")
                .given("No particular pre-conditions")
                .whenMsgReceived(
                        "New disclosure submission", MSG_DISCLOSURE,
                        "/data/" + TENANT_ID + "/" + MSG_DISCLOSURE
                        + ".json", TENANT_ID)
                .whenExecuteJobsForTime(2000)
                .thenSubProcessCalled("CreateContactAndAccount")
                .thenSubProcessCalled("ConfirmEmail")
                .thenSubProcessCalled("AddActivityToContact")
                .thenSubProcessCalled("CreateWordPressAccount")
                .thenSubProcessCalled("SendMemo")
                .thenProcessIsComplete()
                .thenExtension(new DumpAuditTrail(activitiRule));
    }
}