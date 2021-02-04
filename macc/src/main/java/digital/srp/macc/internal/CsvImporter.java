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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationType;

public class CsvImporter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CsvImporter.class);

    public List<InterventionType> readInterventions(Reader in, String[] headers)
            throws IOException {
        List<InterventionType> interventionTypes = new ArrayList<InterventionType>();
        PropertyDescriptor[] interventionTypeDescriptors = BeanUtils
                .getPropertyDescriptors(InterventionType.class);
        // This JavaDoc is not (currently) true:
        // If your source contains a header record, you can simplify your
        // code and safely reference columns, by using withHeader(String...)
        // with no arguments:
        // CSVFormat.EXCEL.withHeader();

        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        Iterable<CSVRecord> records = parser.getRecords();
        // Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader(headers)
        // .parse(in);

        for (CSVRecord record : records) {
            // skip header
            if (record.getRecordNumber() > 1) {
                InterventionType interventionType = new InterventionType();
                for (PropertyDescriptor pd : interventionTypeDescriptors) {
                    if (record.isMapped(pd.getName())) {
                        setField(interventionType, pd, record.get(pd.getName())
                                .trim());
                    } else if ("annualCashOutflowsTS".equals(pd.getName())
                            && (record.get("annualCashOutflows").contains(",") || record
                                    .get("annualCashOutflows").contains(" "))) {
                        setTimeSeriesField(interventionType, pd, record,
                                "annualCashOutflows");
                    } else if ("annualCashInflowsTS".equals(pd.getName())
                            && (record.get("annualCashInflows").contains(",") || record
                                    .get("annualCashInflows").contains(" "))) {
                        setTimeSeriesField(interventionType, pd, record,
                                "annualCashInflows");
                    } else if ("annualTonnesCo2eSavedTS".equals(pd.getName())
                            && (record.get("annualTonnesCo2eSaved").contains(
                                    ",") || record.get("annualTonnesCo2eSaved")
                                    .contains(" "))) {
                        setTimeSeriesField(interventionType, pd, record,
                                "annualTonnesCo2eSaved");
                    } else {
                        LOGGER.warn(String.format(
                                "No value for %1$s of record %2$d",
                                pd.getName(), record.getRecordNumber()));
                    }
                }
                interventionTypes.add(interventionType);
            }
        }
        parser.close();
        return interventionTypes;
    }

    // Time series are a special case
    private void setTimeSeriesField(InterventionType intervention,
            PropertyDescriptor pd, CSVRecord record, String key) {
        String val = record.get(key).trim();
        if (val.endsWith(",")) {
            val = val.substring(0, val.length() - 1);
        }
        setField(intervention, pd, val);
    }

    private void setField(Object bean, PropertyDescriptor propertyDescriptor,
            Object value) {
        if (value == null || value.toString().length() == 0) {
            return;
        }
        try {
            // Convert %, cannot do it in LibreOffice as converts to fraction
            if (value.toString().endsWith("%")) {
                value = value.toString().substring(0,
                        value.toString().length() - 1);
            }
            // Some funny formatting of money as strings
            if (value.toString().startsWith("£")) {
                value = value.toString().substring(1).replace(",", "");
            }
            // Change 8 spaces (originally a tab?) to comma
            if (value.toString().contains("        ")) {
                value = value.toString().replaceAll("        ", ",");
            }
            Method method = propertyDescriptor.getWriteMethod();
            switch (method.getParameterTypes()[0].getName()) {
            case "boolean":
                method.invoke(bean, Boolean.parseBoolean(value.toString()));
                break;
            case "double":
                method.invoke(bean, Double.valueOf(value.toString()));
                break;
            case "java.lang.Float":
                method.invoke(bean, new Float(value.toString()));
                break;
            case "java.lang.Integer":
                method.invoke(bean, new Integer(value.toString()));
                break;
            case "java.lang.Short":
                method.invoke(bean, new Short(value.toString()));
                break;
            case "java.lang.String":
                if (value != null && value.toString().trim().length() > 0) {
                    method.invoke(bean, value.toString());
                }
                break;
            case "java.math.BigDecimal":
                method.invoke(bean, new BigDecimal(value.toString()));
                break;
            case "digital.srp.macc.model.OrganisationType":
                OrganisationType organisationType = new OrganisationType();
                ((Intervention) bean).setOrganisationType(organisationType);
                organisationType.setName(value.toString());
                break;
            default:
                LOGGER.warn(String.format("  Treating %1$s as String",
                        method.getParameterTypes()[0].getName()));
                method.invoke(bean, value.toString());
            }

        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error(String.format("Error parsing %1$s.%2$s from %3$s",
                    bean.getClass().getName(), propertyDescriptor.getName(),
                    value.toString()));
        }
    }

    public List<InterventionType> readInterventionTypes(StringReader in,
            String[] headers) throws IOException {
        List<InterventionType> types = new ArrayList<InterventionType>();
        PropertyDescriptor[] propertyDescriptors = BeanUtils
                .getPropertyDescriptors(InterventionType.class);

        final CSVParser parser = new CSVParser(in,
                CSVFormat.EXCEL.withHeader(headers));
        Iterable<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            // skip header
            if (record.getRecordNumber() > 1) {
                InterventionType type = new InterventionType();
                for (PropertyDescriptor pd : propertyDescriptors) {
                    if (record.isMapped(pd.getName())) {
                        setField(type, pd, record.get(pd.getName()).trim());
                    }
                }

                types.add(type);
            }
        }
        parser.close();
        return types;
    }

}
