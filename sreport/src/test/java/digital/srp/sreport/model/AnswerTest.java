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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;


public class AnswerTest {

    @Test
    public void testGetEmptyResponseAsBigDecimal() {
        assertEquals(BigDecimal.ZERO, new Answer().responseAsBigDecimal());
    }

    @Test
    public void testGetResponseAsBigDecimal() {
        assertEquals(new BigDecimal("1.23"), new Answer().response("1.23").responseAsBigDecimal());
    }

    @Test
    public void testGetEmptyResponseAsString() {
        assertNull(new Answer().response());
    }

    @Test
    public void testGetResponseAsString() {
        assertEquals("Foo", new Answer().response("Foo").response());
    }
}
