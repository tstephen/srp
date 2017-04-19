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

import digital.srp.sreport.model.WeightingFactor;

public class WeightingFactorImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(WeightingFactorImporter.class);
    
    public static final String DATA_FILE = "/data/WeightingFactors.csv";
    public static final String[] HEADERS = { "category",
            "acute_c", "ambulance_c", "care_c", "ccg_c", "community_c",
            "mental_health_learning_disability_c", "social_enterprise_c", 
            "acute_m", "ambulance_m", "care_m", "ccg_m", "community_m",
            "mental_health_learning_disability_m", "social_enterprise_m" };

    public static final int NO_ORG_TYPES = 7;

    public List<WeightingFactor> readWeightingFactors()
            throws IOException {
        return readWeightingFactors(new StringReader(readFile()), HEADERS);
    }
    
    public List<WeightingFactor> readWeightingFactors(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readWeightingFactors"));

        List<WeightingFactor> wfactors = new ArrayList<WeightingFactor>();

        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            LOGGER.info(String.format("importing record %1$d", record.getRecordNumber()));
            if (record.getRecordNumber() > 2) { // skip headers
                try {
                    wfactors.add(newWeightingFactor(record, 1));
                    wfactors.add(newWeightingFactor(record, 2));
                    wfactors.add(newWeightingFactor(record, 3));
                    wfactors.add(newWeightingFactor(record, 4));
                    wfactors.add(newWeightingFactor(record, 5));
                    wfactors.add(newWeightingFactor(record, 6));
                    wfactors.add(newWeightingFactor(record, 7));
                } catch (Exception e) {
                    System.err.println(String.format("Problem with record: %1$d", record.getRecordNumber()));
                    LOGGER.error(String.format("Problem with record: %1$d: %2$s", record.getRecordNumber(), e.getMessage()));
                    e.printStackTrace();
                }
            }
        }
        parser.close();

        return wfactors;
    }

    @SuppressWarnings("resource")
    private String readFile() {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(WeightingFactorImporter.DATA_FILE);
            LOGGER.error("Unable to find weighting factors at {}",
                    WeightingFactorImporter.DATA_FILE);
            
            return new Scanner(is).useDelimiter("\\A").next();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }
    
    private WeightingFactor newWeightingFactor(CSVRecord record, int idx) {
        String value = record.get(idx);
        if (value.startsWith(".")) { 
            value = "0" + value;
        }
        LOGGER.debug("value: %1$s", value);
        
        WeightingFactor factor = new WeightingFactor().category(record.get(0))
                .applicablePeriod("2014-15")
                .orgType(HEADERS[idx].substring(0, 1).toUpperCase()+HEADERS[idx].substring(1, HEADERS[idx].length()-2).replace('_', ' '))
                .carbonValue(new BigDecimal(record.get(idx).trim()).setScale(0, RoundingMode.HALF_UP))
                .moneyValue(new BigDecimal(record.get(idx+NO_ORG_TYPES).trim()).multiply(new BigDecimal("1000")).setScale(0, RoundingMode.HALF_UP));
        if (record.get(3).trim().matches("[123]")) {
            factor.scope(record.get(3).trim());
        }
        return factor;
    }

}
