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
package digital.srp.finance.model.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import digital.srp.finance.model.FinancialTransaction;
import digital.srp.finance.model.converters.FinancialTransactionCsvParser;

public class FinancialTransactionCsvParserOtherTest {

    private InputStream is;

    private FinancialTransactionCsvParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new FinancialTransactionCsvParser();
    }

    @After
    public void tearDown() throws Exception {
        is.close();
    }

    @Test
    public void testFinancialTransaction() throws IOException, Exception {
        parser.setBeanType(FinancialTransaction.class);

        // is = getClass().getResourceAsStream("/data/barts-2013-14.csv");
        is = getClass().getResourceAsStream("/data/sample.csv");
        assertNotNull(is);

        @SuppressWarnings("unchecked")
        List<FinancialTransaction> beans = (List<FinancialTransaction>) parser
                .parseCsv(is);
        assertEquals(2, beans.size());

        FinancialTransaction t0 = beans.get(0);
        assertEquals(t0.getEClass(), "WPD");
        assertEquals(t0.getSupplierName(), "BATES OFFICE SERVICES LTD");
        assertEquals(parser.parseDate("04-Nov-13"), t0.getOrderDate());
    }
}
