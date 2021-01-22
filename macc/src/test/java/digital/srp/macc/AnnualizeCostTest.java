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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import digital.srp.macc.maths.ProRataAnnualizer;
import digital.srp.macc.model.AnnualizedCost;
import digital.srp.macc.model.Invoice;
import digital.srp.macc.model.ReportingYear;

public class AnnualizeCostTest {

    private ReportingYear year;
    private List<Invoice> invoices;

    @BeforeEach
    public void setUp() throws Exception {
        year = new ReportingYear("Calendar 2013", new GregorianCalendar(2013,
                0, 1).getTime(), new GregorianCalendar(2013, 11, 31).getTime());
        invoices = new ArrayList<Invoice>();
    }

    @Test
    public void testFourQuartersAlignedToCalendarYear() {
        invoices.clear();
        Invoice invoice = new Invoice("elec-q1", 
                new GregorianCalendar(2013, 4,10).getTime(), 
                new GregorianCalendar(2013, 0, 1).getTime(),
                new GregorianCalendar(2013, 2, 30).getTime(),
                (31 + 28 + 31) * 10.0f);
        invoices.add(invoice);
        invoice = new Invoice("elec-q2",
                new GregorianCalendar(2013, 5, 10).getTime(),
                new GregorianCalendar(2013, 3, 1).getTime(),
                new GregorianCalendar(2013, 5, 31).getTime(),
                (30 + 31 + 30) * 10.0f);
        invoices.add(invoice);
        invoice = new Invoice("elec-q3",
                new GregorianCalendar(2013, 8, 10).getTime(),
                new GregorianCalendar(2013, 6, 1).getTime(),
                new GregorianCalendar(2013, 8, 30).getTime(),
                (31 + 31 + 30) * 10.0f);
        assertEquals(31 + 31 + 30, invoice.getDaysInvoiced());
        invoices.add(invoice);
        invoice = new Invoice("elec-q4",
                new GregorianCalendar(2014, 9, 10).getTime(),
                new GregorianCalendar(2013, 9, 1).getTime(),
                new GregorianCalendar(2013, 11, 31).getTime(),
                (31 + 30 + 31) * 10.0f);
        // invoice.persist();
        invoices.add(invoice);

        ProRataAnnualizer svc = new ProRataAnnualizer();
        AnnualizedCost annualizedCost = svc.annualize(year, invoices);
        assertNotNull(annualizedCost);
        assertEquals(365 * 10.0f, annualizedCost.getAmount(), 0.1d);
    }

    @Test
    public void testFourQuartersOffsetFromCalendarYear() {
        invoices.clear();
        // Invoice period is offset from calendar quarters (Q1 = Feb-Apr inc.)
        Invoice invoice = new Invoice("elec-q1", new GregorianCalendar(2013, 4,
                10).getTime(), new GregorianCalendar(2013, 1, 1).getTime(),
                new GregorianCalendar(2013, 3, 30).getTime(),
                (28 + 31 + 30) * 10.0f);
        invoices.add(invoice);
        invoice = new Invoice("elec-q2",
                new GregorianCalendar(2013, 5, 10).getTime(),
                new GregorianCalendar(2013, 4, 1).getTime(),
                new GregorianCalendar(2013, 6, 31).getTime(),
                (31 + 30 + 31) * 10.0f);
        invoices.add(invoice);
        invoice = new Invoice("elec-q3",
                new GregorianCalendar(2013, 8, 10).getTime(),
                new GregorianCalendar(2013, 7, 1).getTime(),
                new GregorianCalendar(2013, 9, 31).getTime(),
                (31 + 31 + 30) * 10.0f);
        invoices.add(invoice);
        invoice = new Invoice("elec-q4",
                new GregorianCalendar(2014, 1, 10).getTime(),
                new GregorianCalendar(2013, 10, 1).getTime(),
                new GregorianCalendar(2014, 0, 31).getTime(),
                (31 + 30 + 31) * 10.0f);
        // invoice.persist();
        invoices.add(invoice);

        ProRataAnnualizer svc = new ProRataAnnualizer();
        AnnualizedCost annualizedCost = svc.annualize(year, invoices);
        assertNotNull(annualizedCost);
        assertEquals(365 * 10.f, annualizedCost.getAmount(), 0.1d);
    }

}
