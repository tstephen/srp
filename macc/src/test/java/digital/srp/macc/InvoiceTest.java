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
package digital.srp.macc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import digital.srp.macc.model.Invoice;
import digital.srp.macc.model.ReportingYear;

public class InvoiceTest {

    private ReportingYear year;

    @BeforeEach
    public void setUp() throws Exception {
        year = new ReportingYear("Calendar 2013", new GregorianCalendar(2013,
                0, 1).getTime(), new GregorianCalendar(2013, 11, 31).getTime());
    }

    @Test
    public void testInvoiceDaysInYear() {
        Invoice invoice = new Invoice("elec-q1", new GregorianCalendar(2013, 4,
                10).getTime(), new GregorianCalendar(2013, 0, 1).getTime(),
                new GregorianCalendar(2013, 2, 31).getTime(),
                (31 + 28 + 31) * 10.0f);
        assertEquals(31 + 28 + 31, invoice.getDaysInvoiced());
        assertEquals(invoice.getDaysInvoiced(), invoice.invoiceDaysInYear(year));
        assertEquals(10.0f, invoice.getDailyAverage(), 0.1f);
    }

    @Test
    public void testInvoiceStartsBeforeYear() {
        Invoice invoice = new Invoice("elec-q1",
                new GregorianCalendar(2013, 4, 10).getTime(),
                new GregorianCalendar(2012, 11, 1).getTime(),
                new GregorianCalendar(2013, 1, 28).getTime(), 
                (31 + 31 + 28) * 10.0f);
        assertEquals(31 + 31 + 28, invoice.getDaysInvoiced());
        assertEquals(31 + 28, invoice.invoiceDaysInYear(year));
        assertEquals(10.0f, invoice.getDailyAverage(), 0.1f);
        
    }

    @Test
    public void testInvoiceEndsAfterYear() {
        Invoice invoice = new Invoice("elec-q1",
                new GregorianCalendar(2014, 1, 10).getTime(),
                new GregorianCalendar(2013, 10, 1).getTime(),
                new GregorianCalendar(2014, 0, 31).getTime(), 
                (30 + 31 + 31) * 10.0f);
        assertEquals(30 + 31 + 31, invoice.getDaysInvoiced());
        assertEquals(30 + 31, invoice.invoiceDaysInYear(year));
        assertEquals(10.0f, invoice.getDailyAverage(), 0.1f);
    }

}
