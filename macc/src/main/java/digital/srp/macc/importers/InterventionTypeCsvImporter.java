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
package digital.srp.macc.importers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import digital.srp.macc.model.InterventionType;

public class InterventionTypeCsvImporter {

    static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionTypeCsvImporter.class);

    public static final String DATA = "/data/intervention-types.csv";
    public static final String[] HEADERS = { "id", "analysis_status",
            "classification", "client_note", "confidence",
            "cost_per_tonne_co2e", "data_status", "description", "further_info",
            "lifetime", "modelling_year", "name", "note",
            "operational_sub_category", "scaling", "status", "strategic_focus",
            "tactical_driver", "tenant_id", "uptake",
            "overlapping_interventions", "lead_time", "annual_cash_inflows",
            "annual_cash_inflowsts", "annual_cash_outflows",
            "annual_cash_outflowsts", "unit_elec_saved_pa", "unit_gas_saved_pa",
            "tonnes_co2e_saved_pa", "annual_tonnes_co2e_savedts",
            "tonnes_co2e_saved", "existing", "unit",
            "unit_count", "cross_organisation", "cash_outflows_up_front",
            "unit_desc" };

    public List<InterventionType> readInterventionTypes() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readInterventionTypes(isr, HEADERS);
        }
    }

    public List<InterventionType> readInterventionTypes(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readInterventionTypes"));

        List<InterventionType> types = new ArrayList<InterventionType>();

        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            LOGGER.info(String.format("importing record %1$d", record.getRecordNumber()));
            if (record.getRecordNumber() > 1) { // skip headers
                try {
                    types.add(newInterventionType(record));
                } catch (Exception e) {
                    String msg = String.format("Problem with record: %1$s: %2$s", record.getRecordNumber(), e.getMessage());
                    LOGGER.error(msg, e);
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

        return types;
    }

    private InterventionType newInterventionType(CSVRecord record) {
        LOGGER.info("Parsing {}", record.toString());

        InterventionType orgType = new InterventionType()
                .setName(record.get("name"))
                .setAnalysisStatus(record.get("analysis_status"))
                .setClassification(record.get("classification"))
                .setClientNote(record.get("client_note"))
                .setConfidence(Short.parseShort(record.get("confidence")))
                .setCostPerTonneCo2e(Double.parseDouble(record.get("cost_per_tonne_co2e")))
                .setDataStatus(record.get("data_status"))
                .setDescription(record.get("description"))
                .setFurtherInfo(record.get("further_info"))
                .setLifetime(Short.parseShort(record.get("lifetime")))
                .setModellingYear(Integer.parseInt(record.get("modelling_year")))
                .setNote(record.get("note"))
                .setStatus(record.get("status"))
                .setOperationalSubCategory(record.get("operational_sub_category"))
                .setScaling(Float.parseFloat(record.get("scaling")))
                .setStatus(record.get("status"))
                .setStrategicFocus(record.get("strategic_focus"))
                .setTacticalDriver(record.get("tactical_driver"))
                .setTenantId(record.get("tenant_id"))
                .setUptake(Short.parseShort(record.get("uptake")))
                .setOverlappingInterventions(record.get("overlapping_interventions"))
                .setLeadTime(Integer.parseInt(record.get("lead_time")))
                .setAnnualCashInflows(new BigDecimal(record.get("annual_cash_inflows")))
                .setAnnualCashInflowsTS(StringUtils.hasText(record.get("annual_cash_inflowsts")) ? record.get("annual_cash_inflowsts") : null)
                .setAnnualCashOutflows(new BigDecimal(record.get("annual_cash_outflows")))
                .setAnnualCashOutflowsTS(StringUtils.hasText(record.get("annual_cash_outflowsts")) ? record.get("annual_cash_outflowsts") : null)
                .setAnnualElecSaved(StringUtils.hasText(record.get("unit_elec_saved_pa")) ? new BigDecimal(record.get("unit_elec_saved_pa")) : null)
                .setAnnualGasSaved(StringUtils.hasText(record.get("unit_gas_saved_pa")) ? new BigDecimal(record.get("unit_gas_saved_pa")) : null)
                .setAnnualTonnesCo2eSaved(StringUtils.hasText(record.get("tonnes_co2e_saved_pa")) ? new BigDecimal(record.get("tonnes_co2e_saved_pa")) : null)
                .setAnnualTonnesCo2eSavedTS(record.get("annual_tonnes_co2e_savedts"))
                .setTonnesCo2eSaved(StringUtils.hasText(record.get("tonnes_co2e_saved")) ? Integer.parseInt(record.get("tonnes_co2e_saved")) : 0)
                .setExisting(Boolean.parseBoolean(record.get("existing")))
                .setUnit(record.get("unit"))
                .setUnitCount(Integer.parseInt(record.get("unit_count")))
                .setCrossOrganisation(Boolean.parseBoolean(record.get("cross_organisation")))
                .setCashOutflowsUpFront(StringUtils.hasText(record.get("cash_outflows_up_front")) ? new BigDecimal(record.get("cash_outflows_up_front")) : null)
                .setUnitDescription(record.get("unit_desc"));

        LOGGER.info("  resulted in {}", orgType.toString());
        return orgType;
    }

}
