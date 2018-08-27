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
package digital.srp.macc.internal;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.macc.maths.SignificantFiguresFormat;

public class SignificantFiguresFormatTest {

    private static SignificantFiguresFormat sigFigFormatter;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        sigFigFormatter = new SignificantFiguresFormat();
    }

    @Test
    public void testNull() {
        assertEquals(null, sigFigFormatter.format(null));
    }

    @Test
    public void testNegativeAsString() {
        assertEquals("-20,100",
                sigFigFormatter.format(new BigDecimal("-20133")));
    }

    @Test
    public void testNegative() {
        assertEquals(new Double(-20100),
                sigFigFormatter.asDouble(new BigDecimal("-20133")));
    }

    @Test
    public void testFraction() {
        assertEquals("0.123",
                sigFigFormatter.format(new BigDecimal("0.123456")));
    }

    @Test
    public void testFormatTenThousands() {
        assertEquals("16,700",
                sigFigFormatter.format(new BigDecimal("16736")));
    }

    @Test
    public void testFormatMillions() {
        assertEquals("1,230,000",
                sigFigFormatter.format(new BigDecimal("1234567")));
    }
    
    @Test
    public void testFormatHundredMillions() {
        assertEquals("416,000,000",
                sigFigFormatter.format(new BigDecimal("416012596")));
    }    

}
