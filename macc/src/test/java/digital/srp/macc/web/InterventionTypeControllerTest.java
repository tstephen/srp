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

import digital.srp.macc.mocks.MockInterventionTypeRepository;
import digital.srp.macc.model.InterventionType;

public class InterventionTypeControllerTest {
    private static final int INTVN_TYPE_COUNT = 39;
    private static final String TENANT_ID = "sdu";
    private InterventionTypeController svc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new InterventionTypeController();
        svc.interventionTypeRepo = new MockInterventionTypeRepository();
    }

    @Test
    public void testLifecycle() throws IOException {
        // prep
        svc.init();
        assertEquals(INTVN_TYPE_COUNT, ((MockInterventionTypeRepository)
                svc.interventionTypeRepo).intvnTypes.size());

        // finders
        List<EntityModel<InterventionType>> typesFound = svc
                .listForTenant(TENANT_ID, 1, null);
        assertEquals(INTVN_TYPE_COUNT, typesFound.size());
        assertEquals("/" + TENANT_ID + "/intervention-types/1", typesFound
                .get(0).getLinks().getLink("self").get().getHref());
    }
}
