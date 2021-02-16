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

import digital.srp.macc.mocks.MockOrganisationTypeRepository;
import digital.srp.macc.model.OrganisationType;

public class OrganisationTypeControllerTest {
    private static final int ORG_TYPE_COUNT = 11;
    private static final String TENANT_ID = "sdu";
    private OrganisationTypeController svc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new OrganisationTypeController();
        svc.organisationTypeRepo = new MockOrganisationTypeRepository();
    }

    @Test
    public void testLifecycle() throws IOException {
        // prep
        svc.init();
        assertEquals(ORG_TYPE_COUNT, ((MockOrganisationTypeRepository)
                svc.organisationTypeRepo).orgTypes.size());

        // finders
        List<EntityModel<OrganisationType>> orgTypesFound = svc
                .listForTenant(TENANT_ID, null, 1, null);
        assertEquals(ORG_TYPE_COUNT, orgTypesFound.size());
        assertEquals("/" + TENANT_ID + "/organisation-types/1", orgTypesFound
                .get(0).getLinks().getLink("self").get().getHref());
    }
}
