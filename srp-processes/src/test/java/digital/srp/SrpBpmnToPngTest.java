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
 ******************************************************************************/
package digital.srp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.knowprocess.bpm.impl.Bpmn2Image;

@RunWith(Parameterized.class)
public class SrpBpmnToPngTest {

    private static Bpmn2Image svc;

    private String bpmnResource;
    private String imageFileName;

    @Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
            { "/processes/digital/srp/acctmgmt/ChangeOrg.bpmn", "ChangeOrg.png" },
            { "/processes/digital/srp/acctmgmt/FulfilUserAccount.bpmn", "FulfilUserAccount.png" },
            { "/processes/digital/srp/disclosure/ConfirmEmail.bpmn", "ConfirmEmail.png" },
            { "/processes/digital/srp/disclosure/RecordDisclosure.bpmn", "RecordDisclosure.png" },
            { "/processes/digital/srp/disclosure/RunScorecardDecisionLogicOnContact.bpmn", "RunScorecardDecisionLogicOnContact.png" },
            { "/processes/digital/srp/macc/RecordAbatementPlan.bpmn", "RecordAbatementPlan.png" },
            { "/processes/digital/srp/macc/RespondToQuestionnaire.bpmn", "RespondToQuestionnaire.png" },
            { "/processes/digital/srp/returns/RestateSustainabilityReturn.bpmn", "RestateSustainabilityReturn.png" },
            { "/processes/digital/srp/returns/SubmitSustainabilityReturn.bpmn", "SubmitSustainabilityReturn.png" },
            { "/processes/digital/srp/support/CatchAllProcess.bpmn", "CatchAllProcess.png" },

            { "/processes/uk/org/sduhealth/website/RecordCaseStudy.bpmn", "RecordCaseStudy.png" },
            { "/processes/uk/org/sduhealth/website/RecordPowerfulPoint.bpmn", "RecordPowerfulPoint.png" }
        });
    }

    @BeforeClass
    public static void setUp() {
        svc = new Bpmn2Image();
    }

    public SrpBpmnToPngTest(String bpmnResource, String imageFileName) {
        this.bpmnResource = bpmnResource;
        this.imageFileName = imageFileName;
    }

    @Test
    public void testBpmnImageGeneration() throws IOException {
        testClasspathBpmnResourceImageGeneration(bpmnResource, imageFileName);
    }

    protected void testClasspathBpmnResourceImageGeneration(
            String bpmnResource, String imageResource) throws IOException {
        String bpmn = svc.readFromClasspath(bpmnResource);
        assertNotNull("Unable to read input from " + bpmnResource, bpmn);
        String svg = svc.bpmn2Svg(bpmn);
        assertNotNull("Unable to create svg from " + bpmnResource, svg);
        byte[] pngBytes = svc.svgToPng(svg);
        assertNotNull(pngBytes);
        assertTrue(svc.writeToFile(pngBytes, new File("target", "processes"), imageResource).exists());
    }

}
