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


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.StringUtils;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.surveys.Sdu2021;

public class CarbonFactorCsvImporter {

    static final Logger LOGGER = LoggerFactory
            .getLogger(CarbonFactorCsvImporter.class);

    public static final String DATA = "/data/CarbonFactors.csv";
    public static final String[] HEADERS = {
            "category","name","unit","scope","2007-08","2008-09","2009-10",
            "2010-11","2011-12","2012-13","2013-14","2014-15","2015-16",
            "2016-17","2017-18","2018-19","2019-20","2020-21","2021-22","comments"};

    public List<CarbonFactor> readCarbonFactors() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readCarbonFactors(isr, HEADERS);
        }
    }

    public List<CarbonFactor> readCarbonFactors(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readCarbonFactors"));

        List<CarbonFactor> cfactors = new ArrayList<CarbonFactor>();

        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            LOGGER.info(String.format("importing record %1$d", record.getRecordNumber()));
            if (record.getRecordNumber() > 1) { // skip headers
                try {
                    cfactors.add(newCarbonFactor(record, HEADERS[4], 4));
                    cfactors.add(newCarbonFactor(record, HEADERS[5], 5));
                    cfactors.add(newCarbonFactor(record, HEADERS[6], 6));
                    cfactors.add(newCarbonFactor(record, HEADERS[7], 7));
                    cfactors.add(newCarbonFactor(record, HEADERS[8], 8));
                    cfactors.add(newCarbonFactor(record, HEADERS[9], 9));
                    cfactors.add(newCarbonFactor(record, HEADERS[10], 10));
                    cfactors.add(newCarbonFactor(record, HEADERS[11], 11));
                    cfactors.add(newCarbonFactor(record, HEADERS[12], 12));
                    cfactors.add(newCarbonFactor(record, HEADERS[13], 13));
                    cfactors.add(newCarbonFactor(record, HEADERS[14], 14));
                    cfactors.add(newCarbonFactor(record, HEADERS[15], 15));
                    cfactors.add(newCarbonFactor(record, HEADERS[16], 16));
                    cfactors.add(newCarbonFactor(record, HEADERS[17], 17));
                    cfactors.add(newCarbonFactor(record, HEADERS[18], 18));
                } catch (Exception e) {
                    String msg = String.format("Problem with record: %1$s: %2$s", record.getRecordNumber(), e.getMessage());
                    LOGGER.error(msg);
                    if (e instanceof IOException) {
                        throw e;
                    } else {
                        throw new IOException(msg);
                    }
                } finally {
                    parser.close();
                }
            }
        }

        for (CarbonFactors cFactor : CarbonFactors.values()) {
            final String latestPeriod = Sdu2021.PERIOD;
            cfactors.stream()
                    .filter(e -> cFactor.name().equals(e.getName()) && latestPeriod.equals(e.getApplicablePeriod())).findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException(CarbonFactor.class, cFactor.name() + "_" + latestPeriod));

        }
        return cfactors;
    }

    private CarbonFactor newCarbonFactor(CSVRecord record, String period, int idx) {
        LOGGER.info("Parsing {}", record.toString());
        String value = record.get(idx);
        if (value.startsWith(".")) {
            value = "0" + value;
        }
        LOGGER.debug("value: {}", value);

        CarbonFactor factor = new CarbonFactor().category(record.get(0))
                .name(StringUtils.toConst(record.get(1)))
                .unit(record.get(2))
                .applicablePeriod(period)
                .comments(record.get(HEADERS.length - 1))
                .value(new BigDecimal(value.trim()).setScale(6, RoundingMode.HALF_EVEN));
        if (record.get(3).trim().matches("[123]")) {
            factor.scope(record.get(3).trim());
        }
        LOGGER.info("  resulted in {}", factor.toString());
        return factor;
    }

}
