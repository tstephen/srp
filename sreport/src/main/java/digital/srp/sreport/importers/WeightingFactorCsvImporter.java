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

import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.WeightingFactors;

public class WeightingFactorCsvImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(WeightingFactorCsvImporter.class);

    public static final String DATA = "/data/WeightingFactors.csv";
    public static final String[] HEADERS = { "category",
            "acute_c", "ambulance_c", "care_c", "clinical_commissioning_group_c",
            "community_c", "mental_health_learning_disability_c", "social_enterprise_c",
            "acute_m", "ambulance_m", "care_m", "clinical_commissioning_group_m",
            "community_m", "mental_health_learning_disability_m", "social_enterprise_m",
            "acute_p", "ambulance_p", "care_p", "clinical_commissioning_group_p",
            "community_p", "mental_health_learning_disability_p", "social_enterprise_p"};

    public static final int NO_ORG_TYPES = 7;

    public List<WeightingFactor> readWeightingFactors()
            throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readWeightingFactors(isr, HEADERS);
        }
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
                    LOGGER.error(String.format("Problem with record: %1$d: %2$s", record.getRecordNumber(), e.getMessage()));
                }
            }
        }
        parser.close();

        return wfactors;
    }

    private WeightingFactor newWeightingFactor(CSVRecord record, int idx) {
        String value = record.get(idx);
        if (value.startsWith(".")) {
            value = "0" + value;
        }
        LOGGER.debug("value: {}", value);

        String category = record.get(0);
        switch(category) {
        case "Energy":
            category = WeightingFactors.ENERGY.name();
            break;
        case "Gas":
            category = WeightingFactors.GAS.name();
            break;
        case "Electricity":
            category = WeightingFactors.ELEC.name();
            break;
        case "Oil":
            category = WeightingFactors.OIL.name();
            break;
        case "Coal":
            category = WeightingFactors.COAL.name();
            break;
        case "Business services":
            category = WeightingFactors.BIZ_SVCS.name();
            break;
        case "Capital spend":
            category = WeightingFactors.CAPITAL.name();
            break;
        case "Construction":
            category = WeightingFactors.CONSTRUCTION.name();
            break;
        case "Food and catering":
            category = WeightingFactors.CATERING.name();
            break;
        case "Freight transport":
            category = WeightingFactors.FREIGHT.name();
            break;
        case "Information and communication technologies":
            category = WeightingFactors.ICT.name();
            break;
        case "Manufactured fuels chemicals and gases":
            category = WeightingFactors.FUEL_CHEM_AND_GASES.name();
            break;
        case "Medical Instruments /equipment":
            category = WeightingFactors.MED_INSTRUMENTS.name();
            break;
        case "Other manufactured products":
            category = WeightingFactors.OTHER_MANUFACTURING.name();
            break;
        case "Other procurement":
            category = WeightingFactors.OTHER_PROCURMENT.name();
            break;
        case "Paper products":
            category = WeightingFactors.PAPER.name();
            break;
        case "Pharmaceuticals":
            category = WeightingFactors.PHARMA.name();
            break;
        case "Waste products and recycling":
            category = WeightingFactors.WASTE.name();
            break;
        case "Water and sanitation":
            category = WeightingFactors.WATER.name();
            break;
        case "Anaesthetic gases":
            category = WeightingFactors.ANAESTHETIC_GASES.name();
            break;
        case "Patient Travel":
            category = WeightingFactors.PATIENT_TRAVEL.name();
            break;
        case "Visitor Travel":
            category = WeightingFactors.VISITOR_TRAVEL.name();
            break;
        case "Staff Travel":
            category = WeightingFactors.STAFF_TRAVEL.name();
            break;
        case "Business Travel and fleet":
            category = WeightingFactors.BIZ_TRAVEL.name();
            break;
        case "Commissioned health and social care services":
            category = WeightingFactors.COMMISSIONING.name();
            break;
        }

        WeightingFactor factor = new WeightingFactor()
                .category(category)
                .applicablePeriod("2014-15")
                .orgType(HEADERS[idx].substring(0, 1).toUpperCase()+HEADERS[idx].substring(1, HEADERS[idx].length()-2).replace('_', ' '))
                .carbonValue(new BigDecimal(record.get(idx).trim()).setScale(0, RoundingMode.HALF_UP))
                .moneyValue(new BigDecimal(record.get(idx+NO_ORG_TYPES).trim()).multiply(new BigDecimal("1000")).setScale(0, RoundingMode.HALF_UP))
                .proportionOfTotal(new BigDecimal(record.get(idx+(NO_ORG_TYPES*2)).trim()).setScale(3, RoundingMode.HALF_UP));

        return factor;
    }

}
