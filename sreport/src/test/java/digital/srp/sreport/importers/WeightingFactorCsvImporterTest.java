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
package digital.srp.sreport.importers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import digital.srp.sreport.model.WeightingFactor;

public class WeightingFactorCsvImporterTest {

    @Test
    public void testImport() {
        try {
            List<WeightingFactor> factors = new WeightingFactorCsvImporter()
                    .readWeightingFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(26*WeightingFactorCsvImporter.NO_ORG_TYPES, factors.size());

            // assert expected values of 5th record
            // Business services,898385085,36484424,430605458,106399,63999801,153255016,63999801,5913115,240138,7892636,700,421243,1008715,421243
            int fifthAcuteIdx = 5*WeightingFactorCsvImporter.NO_ORG_TYPES;
            assertEquals("Acute", factors.get(fifthAcuteIdx).orgType());
            assertEquals("BIZ_SVCS", factors.get(fifthAcuteIdx).category());
            assertEquals(new BigDecimal("898385085"), factors.get(fifthAcuteIdx).carbonValue());
            assertEquals(new BigDecimal("5913115000"), factors.get(fifthAcuteIdx).moneyValue());
            assertEquals(new BigDecimal("0.152"), factors.get(fifthAcuteIdx).intensityValue());
            assertEquals(new BigDecimal("0.267"), factors.get(fifthAcuteIdx).proportionOfTotal());

            assertEquals("Mental health learning disability", factors.get(fifthAcuteIdx+5).orgType());
            assertEquals("BIZ_SVCS", factors.get(fifthAcuteIdx+5).category());
            assertEquals(new BigDecimal("153255016"), factors.get(fifthAcuteIdx+5).carbonValue());
            assertEquals(new BigDecimal("1008715000"), factors.get(fifthAcuteIdx+5).moneyValue());
            assertEquals(new BigDecimal("0.152"), factors.get(fifthAcuteIdx+5).intensityValue());
            assertEquals(new BigDecimal("0.343"), factors.get(fifthAcuteIdx+5).proportionOfTotal());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }

}
