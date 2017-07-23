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
package digital.srp.macc.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class Invoice implements Serializable {

    private static final long serialVersionUID = 3710321395342737285L;

    public static final List<String> fieldNames4WhereClause = java.util.Arrays.asList("id", "invoiceRef", "invoiceDate", "invoicePeriodStart", "invoicePeriodEnd", "amount", "supplier");

    public static final List<String> fieldNames4OrderByClause = java.util.Arrays.asList("id", "invoiceRef", "invoiceDate", "invoicePeriodStart", "invoicePeriodEnd", "amount", "supplier");

    private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class);

    private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     */
    private String invoiceRef;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceDate;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoicePeriodStart;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoicePeriodEnd;

    /**
     */
    private Float amount;

    public Invoice(String invoiceRef, Date invoiceDate, Date invoicePeriodStart, Date invoicePeriodEnd, Float amount) {
        super();
        this.invoiceRef = invoiceRef;
        this.invoiceDate = invoiceDate;
        setInvoicePeriodStart(invoicePeriodStart);
        setInvoicePeriodEnd(invoicePeriodEnd);
        this.amount = amount;
    }

    public void setInvoicePeriodStart(Date invoicePeriodStart) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(invoicePeriodStart);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        this.invoicePeriodStart = cal.getTime();
        LOGGER.debug(String.format("set invoice period to start %1$s", getInvoicePeriodStart()));
    }

    public void setInvoicePeriodEnd(Date invoicePeriodEnd) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(invoicePeriodEnd);
        cal.set(Calendar.MILLISECOND, 999);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.HOUR, 23);
        this.invoicePeriodEnd = cal.getTime();
        LOGGER.debug(String.format("set invoice period to end %1$s", getInvoicePeriodEnd()));
    }

    public long getDaysInvoiced() {
        // Chose to not use Joda Time as nearly half a meg of disk space
        // Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays()
        Calendar startCal = new GregorianCalendar();
        startCal.setTime(getInvoicePeriodStart());
        Calendar endCal = new GregorianCalendar();
        endCal.setTime(getInvoicePeriodEnd());
        long dstAdjustment = 1;
        if (!startCal.getTimeZone().inDaylightTime(startCal.getTime()) && endCal.getTimeZone().inDaylightTime(getInvoicePeriodEnd())) {
            dstAdjustment += endCal.getTimeZone().getDSTSavings();
        } else if (startCal.getTimeZone().inDaylightTime(getInvoicePeriodStart()) && !endCal.getTimeZone().inDaylightTime(endCal.getTime())) {
            dstAdjustment -= endCal.getTimeZone().getDSTSavings();
        }
        double daysInvoiced = (getInvoicePeriodEnd().getTime() - getInvoicePeriodStart().getTime() + dstAdjustment) / MILLIS_IN_DAY;
        LOGGER.debug("days invoiced: " + daysInvoiced);
        return (long) daysInvoiced;
    }

    public float getDailyAverage() {
        float dailyRate = getAmount() / getDaysInvoiced();
        LOGGER.debug(String.format("Daily average of %1$s for %2$s days is %3$s", getAmount(), getDaysInvoiced(), dailyRate));
        return dailyRate;
    }

    public long invoiceDaysInYear(ReportingYear year) {
        long daysInYear = getDaysInvoiced();
        if (getInvoicePeriodStart().before(year.getStartDate())) {
            daysInYear -= ((year.getStartDate().getTime() - getInvoicePeriodStart().getTime()) / MILLIS_IN_DAY);
        }
        if (getInvoicePeriodEnd().after(year.getEndDate())) {
            daysInYear -= ((getInvoicePeriodEnd().getTime() - year.getEndDate().getTime()) / MILLIS_IN_DAY);
        }
        return daysInYear;
    }

    public static List<Invoice> findInvoicesByExample(Invoice example) {
        throw new RuntimeException("Not yet implemented");
        //return (List<Invoice>) FindByExampleHelper.findByExample(example, "invoiceDate", "DESC", fieldNames4OrderByClause);
    }

    public static List<Invoice> findInvoicesByPurposeAndYear(String purpose2, String year) {
        throw new RuntimeException("Not yet implemented");
        // String jpaQuery = "SELECT o FROM Invoice o " +
        // "WHERE o.purpose.name = :name " +
        // "AND o.invoicePeriodStart BETWEEN :yearStart AND :yearEnd";
        // Query q = entityManager().createQuery(jpaQuery, Invoice.class);
        // q.setParameter("name", purpose2);
        // GregorianCalendar yearStart = new
        // GregorianCalendar(Integer.parseInt(year), Calendar.JANUARY, 0);
        // q.setParameter("yearStart", yearStart.getTime());
        // GregorianCalendar yearEnd = new
        // GregorianCalendar(Integer.parseInt(year), Calendar.DECEMBER, 31);
        // q.setParameter("yearEnd", yearEnd.getTime());
        // return q.getResultList();
    }

}
