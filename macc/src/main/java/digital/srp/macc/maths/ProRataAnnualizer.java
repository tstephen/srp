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
package digital.srp.macc.maths;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.macc.model.AnnualizedCost;
import digital.srp.macc.model.Invoice;
import digital.srp.macc.model.ReportingYear;

/**
 * Simply shares costs equally across the period based on a daily rate
 * calculation.
 *
 * @author tstephen
 *
 */
public class ProRataAnnualizer implements Annualizer {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ProRataAnnualizer.class);

    // @Override
    public void annualize() {
        // for each purpose
        // for each reporting year
        // get invoices covering that year and purpose

    }

    @Override
    public AnnualizedCost annualize(ReportingYear year, List<Invoice> invoices) {
        AnnualizedCost cost = new AnnualizedCost();
        cost.setReportingYear(year);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(year.getStartDate());
        int daysInYear = cal.isLeapYear(cal.get(Calendar.YEAR)) ? 366 : 365;

        long invoicedDaysInYear = 0;
        for (Invoice invoice : invoices) {
            invoicedDaysInYear += invoice.invoiceDaysInYear(year);

            float additional = invoice.invoiceDaysInYear(year) * invoice
                    .getDailyAverage();
            float runningTotal = cost.getAmount() == null ? 0f : cost
                    .getAmount();
            cost.setAmount(runningTotal + additional);
        }
        LOGGER.debug(String.format(
                "Counted days actually invoiced in %1$s as %2$s",
                cal.get(Calendar.YEAR), invoicedDaysInYear));

        if (invoicedDaysInYear == daysInYear) {
            cost.setActual(true);
            LOGGER.debug("Whole year has been invoiced.");
        } else if (invoicedDaysInYear > daysInYear) {
            String msg = String.format(
                    "Invoices submitted for %1$s exceed days in year (%2$s).",
                    invoicedDaysInYear, daysInYear);
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        } else {
            cost.setActual(false);
            float dailyRate = cost.getAmount() / invoicedDaysInYear;
            cost.setAmount(dailyRate * daysInYear);
            LOGGER.debug(String.format(
"Extrapolating annual (%1$s days) data from actual day rate of %2$s from %3$s days of data",
                            daysInYear, dailyRate, invoicedDaysInYear));
        }
        return cost;
    }

}
