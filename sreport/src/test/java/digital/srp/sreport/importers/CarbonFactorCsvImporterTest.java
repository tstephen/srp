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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;

public class CarbonFactorCsvImporterTest {

    private static final int NO_OF_YEARS_INCLUDED = 14;
    private static final int NO_OF_FACTORS = 172;

    @Test
    public void testImportCoversExpectedPeriods() {
        try {
            List<CarbonFactor> factors = new CarbonFactorCsvImporter()
                    .readCarbonFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(NO_OF_FACTORS * NO_OF_YEARS_INCLUDED, factors.size());

            // assert expected values of first record
            // Electricity generated,Electricity: UK,kWh,2,0.46673,0.49608,0.49381,0.48531,0.45205,0.46002,0.44548,0.49426,0.46219,0.41205,0.35156,0.28307
            CarbonFactor elecFactor0708 = factors.get(0);
            assertEquals("Electricity generated", elecFactor0708.category());
            // name is run thru StringUtils.toConst
            assertEquals("ELECTRICITY_UK", elecFactor0708.name());
            assertEquals("kWh", elecFactor0708.unit());
            assertEquals("2", elecFactor0708.scope());
            assertEquals("2007-08", elecFactor0708.applicablePeriod());
            assertTrue(new BigDecimal("0.46673").compareTo(elecFactor0708.value()) == 0);
            assertEquals("", elecFactor0708.comments());

            CarbonFactor elecFactor0809 = factors.get(1);
            assertEquals("Electricity generated", elecFactor0809.category());
            assertEquals("ELECTRICITY_UK", elecFactor0809.name());
            assertEquals("kWh", elecFactor0809.unit());
            assertEquals("2", elecFactor0809.scope());
            assertEquals("2008-09", elecFactor0809.applicablePeriod());
            assertTrue(new BigDecimal("0.49608").compareTo(elecFactor0809.value()) == 0);
            assertEquals("", elecFactor0809.comments());

            CarbonFactor elecFactor1819 = factors.get(11);
            assertEquals("Electricity generated", elecFactor1819.category());
            assertEquals("ELECTRICITY_UK", elecFactor1819.name());
            assertEquals("kWh", elecFactor1819.unit());
            assertEquals("2", elecFactor1819.scope());
            assertEquals("2018-19", elecFactor1819.applicablePeriod());
            assertTrue(new BigDecimal("0.28307").compareTo(elecFactor1819.value()) == 0);
            assertEquals("", elecFactor1819.comments());

            CarbonFactor elecFactor2021 = factors.get(13);
            assertEquals("Electricity generated", elecFactor2021.category());
            assertEquals("ELECTRICITY_UK", elecFactor2021.name());
            assertEquals("kWh", elecFactor2021.unit());
            assertEquals("2", elecFactor2021.scope());
            assertEquals("2020-21", elecFactor2021.applicablePeriod());
            assertTrue(new BigDecimal("0.23314").compareTo(elecFactor2021.value()) == 0);
            assertEquals("", elecFactor2021.comments());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }

    }
    @Test
    public void testImportCoversExpectedFactors() {
        try {
            List<CarbonFactor> factors = new CarbonFactorCsvImporter()
                    .readCarbonFactors();
            System.out.println(
                    String.format(" found %1$d factors", factors.size()));
            assertEquals(NO_OF_FACTORS * NO_OF_YEARS_INCLUDED, factors.size());

            HashMap<String, Boolean> expectedFactors = new HashMap<String, Boolean>();
            expectedFactors.put(CarbonFactors.DIESEL_TOTAL.name(), false);
            expectedFactors.put(CarbonFactors.DOMESTIC_WASTE_INCINERATION.name(), false);
            expectedFactors.put(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE.name(), false);
            expectedFactors.put(CarbonFactors.GAS_TOTAL.name(), false);
            expectedFactors.put(CarbonFactors.PAPER.name(), false);
            for (Entry<String, Boolean> entry : expectedFactors.entrySet()) {
                for (CarbonFactor factor : factors) {
                    if (entry.getKey().equals(factor.name())) {
                        entry.setValue(true);
                        break;
                    }
                }
                assertTrue(entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected IO exception: " + e.getMessage());
        }
    }
}
