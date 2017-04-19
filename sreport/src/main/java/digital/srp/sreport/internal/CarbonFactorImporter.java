package digital.srp.sreport.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.CarbonFactor;

public class CarbonFactorImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CarbonFactorImporter.class);
    
    public static final String DATA_FILE = "/data/CarbonFactors.csv";
    public static final String[] HEADERS = { 
            "category","name","unit","scope","2007-08","2008-09","2009-10","2010-11","2011-12","2012-13","2013-14","2014-15","2015-16","2016-17","comments"};

    public List<CarbonFactor> readCarbonFactors()
            throws IOException {
        return readCarbonFactors(new StringReader(readFile()), HEADERS);
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
                    cfactors.add(newCarbonFactor(record, "2007-08", 4));
                    cfactors.add(newCarbonFactor(record, "2008-09", 5));
                    cfactors.add(newCarbonFactor(record, "2009-10", 6));
                    cfactors.add(newCarbonFactor(record, "2010-11", 7));
                    cfactors.add(newCarbonFactor(record, "2011-12", 8));
                    cfactors.add(newCarbonFactor(record, "2012-13", 9));
                    cfactors.add(newCarbonFactor(record, "2013-14", 10));
                    cfactors.add(newCarbonFactor(record, "2014-15", 11));
                    cfactors.add(newCarbonFactor(record, "2015-16", 12));
                    cfactors.add(newCarbonFactor(record, "2016-17", 13));
                } catch (Exception e) {
                    LOGGER.error(String.format("Problem with record: %1$d: %2$s", record.getRecordNumber(), e.getMessage()));
                    e.printStackTrace();
                }
            }
        }
        parser.close();

        return cfactors;
    }

    @SuppressWarnings("resource")
    private String readFile() {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(CarbonFactorImporter.DATA_FILE);
            LOGGER.error("Unable to find carbon factors at {}",
                    CarbonFactorImporter.DATA_FILE);

            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }
    
    private CarbonFactor newCarbonFactor(CSVRecord record, String period, int idx) {
        String value = record.get(idx);
        if (value.startsWith(".")) { 
            value = "0" + value;
        }
        LOGGER.debug("value: %1$s", value);
        
        CarbonFactor factor = new CarbonFactor().category(record.get(0))
                .name(StringUtils.toConst(record.get(1))).unit(record.get(2))
                .applicablePeriod(period)
                .comments(record.get(HEADERS.length - 1))
                .value(new BigDecimal(value.trim()).setScale(6, RoundingMode.HALF_EVEN));
        if (record.get(3).trim().matches("[123]")) {
            factor.scope(record.get(3).trim());
        }
        return factor;
    }

}
