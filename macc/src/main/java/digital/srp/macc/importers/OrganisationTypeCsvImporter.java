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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.macc.model.OrganisationType;

public class OrganisationTypeCsvImporter {

    static final Logger LOGGER = LoggerFactory
            .getLogger(OrganisationTypeCsvImporter.class);

    public static final String DATA = "/data/org-types.csv";
    public static final String[] HEADERS = { "id", "name", "annual_energy_use",
            "annual_mileage", "annual_turnover", "floor_area",
            "no_inpatients", "no_patient_interactions", "no_staff",
            "typical_journey_distance", "sector", "tenant_id", "count", "icon",
            "no_inhalers_prescribed", "commissioner", "status",
            "reporting_type" };

    public List<OrganisationType> readOrganisationTypes() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readOrganisationTypes(isr, HEADERS);
        }
    }

    public List<OrganisationType> readOrganisationTypes(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readOrganisationTypes"));

        List<OrganisationType> orgTypes = new ArrayList<OrganisationType>();

        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            LOGGER.info(String.format("importing record %1$d", record.getRecordNumber()));
            if (record.getRecordNumber() > 1) { // skip headers
                try {
                    orgTypes.add(newOrganisationType(record));
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

        return orgTypes;
    }

    private OrganisationType newOrganisationType(CSVRecord record) {
        LOGGER.info("Parsing {}", record.toString());

        OrganisationType orgType = new OrganisationType()
                .setName(record.get("name"))
                .setSector(record.get("sector"))
                .setCount(Integer.parseInt(record.get("count")))
                .setAnnualTurnover(Integer.parseInt(record.get("annual_turnover")))
                .setAnnualEnergyUse(Integer.parseInt(record.get("annual_energy_use")))
                .setNoOfStaff(Double.parseDouble(record.get("no_staff")))
                .setNoOfPatientInteractions(Integer.parseInt(record.get("no_patient_interactions")))
                .setFloorArea(Integer.parseInt(record.get("floor_area")))
                .setAnnualMileage(Integer.parseInt(record.get("annual_mileage")))
                .setTypicalJourneyDistance(Integer.parseInt(record.get("typical_journey_distance")))
                .setNoInhalersPrescribed(Integer.parseInt(record.get("no_inhalers_prescribed")))
                .setIcon(record.get("icon"))
                .setStatus(record.get("status"))
                .setCommissioner(Boolean.getBoolean(record.get("commissioner")))
                .setReportingType(Boolean.getBoolean(record.get("reporting_type")))
                .setTenantId(record.get("tenant_id"));

        LOGGER.info("  resulted in {}", orgType.toString());
        return orgType;
    }

}
