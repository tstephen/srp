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
package digital.srp.macc.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.junit.jupiter.api.Test;

import digital.srp.macc.maths.InterventionTypeValidator;
import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.macc.model.InterventionType;

public class CsvInterventionTypeImporterTest {

    private CsvImporter importer = new CsvImporter();

    @Test
    public void testImportInterventions() {
        List<InterventionType> interventions = read("/data/national-sector-wide.csv");
        assertEquals(2, interventions.size());

        assertRecyclingValues(interventions.get(0));
        assertType2DiabetesValues(interventions.get(1));
    }

    @Test
    public void testImportSmokingIntervention() {
        List<InterventionType> interventions = read("/data/smoking.csv");
        assertEquals(1, interventions.size());
        assertSmokingValues(interventions.get(0));
    }

    private void assertSmokingValues(InterventionType intervention) {
        assertTrue(InterventionTypeValidator.isValid(intervention));
    }

    @Test
    public void testImportInterventions20160225() {
        analyse("/data/national-sector-wide-20160225-ij.csv");
    }

    @Test
    public void testImportInterventions20160226() {
        analyse("/data/national-sector-wide-20160226.csv");
    }

    private void analyse(String resourceName) {
        List<InterventionType> interventions = read(resourceName);
        List<InterventionType> validInterventions = new ArrayList<InterventionType>();
        int intendedForPublication = 0;
        int amberAnalysis = 0;
        for (InterventionType intervention : interventions) {
            if (intervention.getStatus()
                    .equalsIgnoreCase("GREEN")) {
                intendedForPublication++;
                if (intervention.getAnalysisStatus()
                        .contains("AMBER")) {
                    amberAnalysis++;
                }
                if (InterventionTypeValidator.isValid(intervention)) {
                    validInterventions.add(intervention);
                } else if (intervention
                        .getAnalysisStatus().equalsIgnoreCase("GREEN")) {
                    System.err.println("Problem: "
 + intervention.getName());
                }
            }
        }
        System.out.println(String.format("Received %1$d interventions",
                interventions.size()));
        System.out.println(String.format(
                "  of which %1$d are intended for publication",
                intendedForPublication));
        System.out
                .println(String
                        .format("  and %1$d are intended for publication but currently analysis is incomplete",
                                amberAnalysis));
        System.out.println(String.format("  and %1$d are complete",
                validInterventions.size()));

        for (InterventionType intervention : validInterventions) {
            System.out.println(String.format("%1$s,%2$s,%3$d,%4$s",
                    intervention.getName(), intervention.getClassification(),
                    intervention.getCostPerTonneCo2e().longValue(),
                    SignificantFiguresFormat.getInstance().format(intervention
                            .getTonnesCo2eSavedTargetYear())));
        }
    }

    protected List<InterventionType> read(String resourceName) {
        String[] headers = { "status", "name", "classification", "description",
                "existing", "strategicFocus", "tacticalDriver",
                "operationalSubCategory", "dataStatus", "analysisStatus",
                "note", "clientNote", "lifetime", "uptake", "scaling",
                "confidence", "cashOutflowsUpFront",
                "annualCashOutflows", "annualCashInflows",
                "annualTonnesCo2eSaved", "organisationType" };
        List<InterventionType> interventions = null;
        try (InputStream is = getClass().getResourceAsStream(resourceName);
                final Reader in = new InputStreamReader(new BOMInputStream(is), "UTF-8")) {
            interventions = importer.readInterventions(in, headers);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception: " + e.getMessage());
        }
        return interventions;
    }

    private void assertRecyclingValues(InterventionType intervention) {
        System.out.println("  intervention: " + intervention);

        assertNotNull(intervention);
        assertEquals("Dry Recycling".toLowerCase(),
                intervention.getName().toLowerCase());
        assertEquals("Dry Recycling of General Waste".toLowerCase(),
                intervention.getClassification()
                        .toLowerCase());
        assertTrue(intervention.getDescription() != null);
        assertEquals(Boolean.FALSE, intervention
                .isExisting());
        assertEquals("Buildings & Facilities".toLowerCase(), intervention
                .getStrategicFocus().toLowerCase());
        assertEquals("Waste Management".toLowerCase(), intervention
                .getTacticalDriver().toLowerCase());
        assertEquals("Waste and recycling".toLowerCase(), intervention
                .getOperationalSubCategory()
                .toLowerCase());
        assertEquals("GREEN", intervention
                .getDataStatus());
        assertEquals("GREEN", intervention
                .getAnalysisStatus());
        assertNotNull(intervention.getNote());
        assertTrue(intervention.getNote().toLowerCase()
                .contains("to be confirmed"));
        assertTrue(intervention.getClientNote()
                .contains("ok"));
        assertEquals(Short.valueOf("7"), intervention
                .getLifetime());
        assertEquals(Short.valueOf("75"), intervention
                .getUptake());
        assertEquals(Float.valueOf("70"), intervention
                .getScaling());
        assertEquals(Long.valueOf("1842800").longValue(),
                intervention.getCashOutflowsUpFront().longValue());
        assertEquals(Long.valueOf("0").longValue(),
                intervention.getAnnualCashOutflows().longValue());
        assertEquals(Long.valueOf("465360").longValue(), intervention
                .getAnnualCashInflows().toBigInteger().longValue());
        assertEquals(Long.valueOf("1525007").longValue(),
                intervention.getAnnualTonnesCo2eSaved().longValue());
    }

    private void assertType2DiabetesValues(InterventionType intervention) {
        System.out.println("  intervention: " + intervention);

        assertNotNull(intervention);
        assertEquals("Effective Management of Type 2 Diabetes".toLowerCase(),
                intervention.getName().toLowerCase());
        assertEquals("Effective Management of Type 2 Diabetes".toLowerCase(),
                intervention.getClassification()
                        .toLowerCase());
        assertNull(intervention.getDescription());
        assertEquals(Boolean.FALSE, intervention
                .isExisting());
        assertEquals("Healthcare Delivery".toLowerCase(), intervention
                .getStrategicFocus().toLowerCase());
        assertEquals("New Models of Care".toLowerCase(), intervention
                .getTacticalDriver().toLowerCase());
        // assertEquals("Waste and recycling".toLowerCase(), intervention
        // .getOperationalSubCategory()
        // .toLowerCase());
        assertEquals("GREEN", intervention
                .getDataStatus());
        assertEquals("AMBER", intervention
                .getAnalysisStatus());
        assertNotNull(intervention.getNote());
        assertTrue(intervention.getNote().toLowerCase()
                .contains("to be confirmed"));
        assertNull(intervention.getClientNote());
        assertEquals(Short.valueOf("10"), intervention
                .getLifetime());
        assertEquals(Short.valueOf("80"), intervention
                .getUptake());
        assertEquals(Float.valueOf("100"), intervention
                .getScaling());
        assertEquals(Long.valueOf("100000").longValue(),
                intervention.getCashOutflowsUpFront().longValue());
        assertEquals("500000,500000,500000,500000,500000",
                intervention.getAnnualCashOutflowsTS());
        BigDecimal[] annualCashOutflowsTimeSeries = { new BigDecimal("500000"),
                new BigDecimal("500000"), new BigDecimal("500000"),
                new BigDecimal("500000"), new BigDecimal("500000") };
        assertEquals(Arrays.asList(annualCashOutflowsTimeSeries),
                intervention.getAnnualCashOutflowsTimeSeries());
        assertEquals(
                "203695081 212820661 221946240 231071820 240197399 249322979 258448559 267574138 276699718 285825297",
                intervention.getAnnualCashInflowsTS());
        BigDecimal[] annualCashInflowsTimeSeries = {
                new BigDecimal("203695081"), new BigDecimal("212820661"),
                new BigDecimal("221946240"), new BigDecimal("231071820"),
                new BigDecimal("240197399"), new BigDecimal("249322979"),
                new BigDecimal("258448559"), new BigDecimal("267574138"),
                new BigDecimal("276699718"), new BigDecimal("285825297") };
        assertEquals(Arrays.asList(annualCashInflowsTimeSeries),
                intervention.getAnnualCashInflowsTimeSeries());
        assertEquals(
                "24262, 25349, 26436, 27523, 28610, 29697, 30784, 31871, 32958, 34045",
                intervention.getAnnualTonnesCo2eSavedTS());
        BigDecimal[] annualTonnesCo2eSaved = { new BigDecimal("24262"),
                new BigDecimal("25349"), new BigDecimal("26436"),
                new BigDecimal("27523"), new BigDecimal("28610"),
                new BigDecimal("29697"), new BigDecimal("30784"),
                new BigDecimal("31871"), new BigDecimal("32958"),
                new BigDecimal("34045") };
        assertEquals(Arrays.asList(annualTonnesCo2eSaved),
                intervention.getAnnualTonnesCo2eSavedTimeSeries());
    }
}
