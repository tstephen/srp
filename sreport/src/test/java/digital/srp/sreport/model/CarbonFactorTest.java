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
package digital.srp.sreport.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CarbonFactorTest {

    @Test
    public void testBusinessDataEquality() {
        CarbonFactor factor1 = new CarbonFactor()
                .id(1l)
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .value(new BigDecimal("0.46673"))
                .applicablePeriod("2007-08")
                .scope("2")
                .created(new Date())
                .createdBy("tstephen")
                .lastUpdated(new Date());

        CarbonFactor factor2 = new CarbonFactor()
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .value(new BigDecimal("0.46673"))
                .applicablePeriod("2007-08")
                .scope("2");

        assertEquals(factor1, factor2);
        List<CarbonFactor> existingFactors = Collections.singletonList(factor1);
        assertTrue(existingFactors.contains(factor2));

        CarbonFactor factor3 = new CarbonFactor()
                .category("Electricity generated")
                .name("Electricity: UK")
                .unit("kWh")
                .applicablePeriod("2019-20")
                .scope("2");
        assertTrue(!existingFactors.contains(factor3));
    }

}
