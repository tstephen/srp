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
package digital.srp.macc.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import digital.srp.macc.importers.InterventionTypeCsvImporter;
import digital.srp.macc.importers.OrganisationTypeCsvImporter;
import digital.srp.macc.mocks.MockInterventionRepository;
import digital.srp.macc.mocks.MockInterventionTypeRepository;
import digital.srp.macc.mocks.MockOrganisationTypeRepository;
import digital.srp.macc.model.Intervention;

public class InterventionControllerTest {
    private static final int INTERVENTION_COUNT = 195;
    private static final String TENANT_ID = "sdu";
    private InterventionController svc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new InterventionController();
        MockInterventionTypeRepository intvnTypeRepo = new MockInterventionTypeRepository();
        intvnTypeRepo.saveAll(new InterventionTypeCsvImporter().readInterventionTypes());
        svc.intvnTypeRepo = intvnTypeRepo;
        MockOrganisationTypeRepository orgTypeRepo = new MockOrganisationTypeRepository();
        orgTypeRepo.saveAll(new OrganisationTypeCsvImporter().readOrganisationTypes());
        svc.orgTypeRepo = orgTypeRepo;
        svc.interventionRepo = new MockInterventionRepository();
    }

    @Test
    public void testLifecycle() throws IOException {
        // prep
        svc.init();
        assertEquals(INTERVENTION_COUNT, ((MockInterventionRepository)
                svc.interventionRepo).interventions.size());

        // finders
        List<EntityModel<Intervention>> intvnsFound = svc
                .listForTenant(TENANT_ID, 1, null);
        assertEquals(INTERVENTION_COUNT, intvnsFound.size());
        assertEquals("/" + TENANT_ID + "/interventions/1", intvnsFound
                .get(0).getLinks().getLink("self").get().getHref());
    }
}
