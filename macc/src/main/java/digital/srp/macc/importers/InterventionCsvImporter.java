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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.repositories.OrganisationTypeRepository;

public class InterventionCsvImporter {

    static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionCsvImporter.class);

    public static final String DATA = "/data/interventions.csv";
    public static final String[] HEADERS = { "id", "share", "tenant_id",
            "intervention_type_name", "org_type_name" };

    @Autowired
    protected final InterventionTypeRepository intvnTypeRepo;

    @Autowired
    protected final OrganisationTypeRepository orgTypeRepo;

    public InterventionCsvImporter(
            final InterventionTypeRepository intvnTypeRepo,
            final OrganisationTypeRepository orgTypeRepo) {
        this.intvnTypeRepo = intvnTypeRepo;
        this.orgTypeRepo = orgTypeRepo;
    }

    public List<Intervention> readInterventions() throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                getClass().getResourceAsStream(DATA))) {
            return readInterventions(isr, HEADERS);
        }
    }

    public List<Intervention> readInterventions(Reader in, String[] headers)
            throws IOException {
        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        LOGGER.info(String.format("readInterventions"));

        List<Intervention> orgTypes = new ArrayList<Intervention>();

        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            LOGGER.info(String.format("importing record %1$d", record.getRecordNumber()));
            if (record.getRecordNumber() > 1) { // skip headers
                try {
                    orgTypes.add(newIntervention(record));
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

    private Intervention newIntervention(CSVRecord record) {
        LOGGER.info("Parsing {}", record.toString());

        String tenantId = record.get("tenant_id");
        Intervention orgType = new Intervention()
                .setShareOfTotal(StringUtils.hasText(record.get("share")) ? Float.parseFloat(record.get("share")) : 0.0f)
                .setOrganisationType(orgTypeRepo.findByName(tenantId, record.get("org_type_name"))
                        .orElseThrow(() -> new IllegalStateException(String.format("Cannot find org type named %1$s", record.get("org_type_name")))))
                .setInterventionType(intvnTypeRepo.findByName(tenantId, record.get("intervention_type_name"))
                        .orElseThrow(() -> new IllegalStateException(String.format("Cannot find intervention type named %1$s", record.get("intervention_type_name")))))
                .setTenantId(record.get("tenant_id"));

        LOGGER.info("  resulted in {}", orgType.toString());
        return orgType;
    }

}
