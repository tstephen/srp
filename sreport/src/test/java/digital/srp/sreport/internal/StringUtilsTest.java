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
package digital.srp.sreport.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testNaturalToConst() {
        assertEquals("COAL_INDUSTRIAL", StringUtils.toConst("Coal (industrial)"));
        assertEquals("ELECTRICITY_UK", StringUtils.toConst("Electricity: UK"));
        assertEquals("GAS_FIRED_CHP", StringUtils.toConst("Gas-fired CHP"));
        assertEquals("NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT", StringUtils.toConst("Nitrous oxide with oxygen 50/50 split"));
        assertEquals("WEEE_LARGE", StringUtils.toConst("WEEE - large"));
        assertEquals("_5_LOSS", StringUtils.toConst("5% loss"));
        assertEquals("HIGH_TEMPERATURE_DISPOSAL_WASTE", StringUtils.toConst("High Temperature Disposal Waste"));
    }

    @Test
    public void testCamelCaseToConst() {
        assertEquals("FLOOR_AREA", StringUtils.camelCaseToConst("floorArea"));
    }

    @Test
    public void testToSentenceCase() {
        assertEquals("Wood pellets used", StringUtils.toSentanceCase("WOOD_PELLETS_USED"));
    }


}
