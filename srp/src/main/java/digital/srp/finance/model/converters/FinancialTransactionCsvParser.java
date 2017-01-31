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
package digital.srp.finance.model.converters;

import java.beans.PropertyDescriptor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.init.ResourceReader;

import digital.srp.finance.model.ClinicalGroup;
import digital.srp.finance.model.FinancialTransaction;
import digital.srp.finance.model.LineType;
import digital.srp.finance.model.OrderType;
import digital.srp.finance.model.ServiceLine;
import digital.srp.finance.model.StatusType;
import digital.srp.finance.model.UnitType;

public class FinancialTransactionCsvParser extends AbstractCsvParser implements
        ResourceReader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FinancialTransactionCsvParser.class);

    private DateFormat dateParser = new SimpleDateFormat("dd-MMM-yy");

    public FinancialTransactionCsvParser() {
        super();
        setBeanType(FinancialTransaction.class);
    }

    protected Object coerce(PropertyDescriptor descriptor, Object val) {
        LOGGER.info("coerce, expecting " + descriptor.getPropertyType());
        switch (descriptor.getPropertyType().getName()) {
        case "digital.srp.finance.model.ClinicalGroup":
            switch ((String) val) {
            case "EMERGENCY CARE & ACUTE MEDICINE":
                val = ClinicalGroup.EMERGENCY_CARE_AND_ACUTE_MEDICINE;
                break;
            case "CLINICAL SUPPORT SERVICES":
                val = ClinicalGroup.CLINICAL_SUPPORT_SERVICES;
                break;
            case "SURGERY & CANCER":
                val = ClinicalGroup.SURGERY_AND_CANCER;
                break;
            default:
                val = ClinicalGroup.OTHER;
            }
            break;
        case "digital.srp.finance.model.LineType":
            if ("Goods".equals(val)) {
                val = LineType.GOODS;
            } else if ("Service".equals(val)) {
                val = LineType.SERVICE;
            } else {
                val = LineType.OTHER;
            }
            break;
        case "digital.srp.finance.model.OrderType":
            if ("Standard Purchase Order".equals(val)) {
                val = OrderType.STANDARD;
            } else {
                val = OrderType.OTHER;
            }
            break;
        case "digital.srp.finance.model.ServiceLine":
            switch ((String) val) {
            case "GASTROENTEROLOGY & RESPIRATORY MEDICINE":
                val = ServiceLine.GASTROENTEROLOGY_AND_RESPIRATORY_MEDICINE;
                break;
            case "IMAGING INC RADIOLOGY AND NUCLEAR MEDICINE":
                val = ServiceLine.IMAGING_RADIOLOGY_AND_NUCLEAR_MEDICINE;
                break;
            case "PERI-OPERATIVE CARE, THEATRES AND PAIN MANAGEMENT":
                val = ServiceLine.PERI_OPERATIVE_CARE_THEATRES_AND_PAIN_MANAGEMENT;
                break;
            case "CRITICAL CARE":
                val = ServiceLine.CRITICAL_CARE;
                break;
            case "PHARMACY":
                val = ServiceLine.PHARMACY;
                break;
            default:
                val = ServiceLine.OTHER;
            }
            break;
        case "digital.srp.finance.model.StatusType":
            if ("Approved".equals(val)) {
                val = StatusType.APPROVED;
            } else {
                val = StatusType.OTHER;
            }
            break;
        case "digital.srp.finance.model.UnitType":
            val = UnitType.valueOf(((String) val).toUpperCase());
            break;
        case "java.util.Date":
            try {
                val = dateParser.parse(((String) val).trim());
            } catch (ParseException e) {
                LOGGER.warn(String.format("  %1$s", e.getMessage()), e);
            }
            break;
        case "java.lang.Float":
            val = Float.parseFloat(stripCommas(((String) val).trim()));
            break;
        case "java.lang.Integer":
            val = Integer.parseInt(stripCommas(((String) val).trim()));
            break;
        case "java.lang.Long":
            val = Long.parseLong(stripCommas(((String) val).trim()));
            break;
        case "java.lang.Short":
            val = ((String) val).trim();
            if ("-".equals(val)) {
                LOGGER.info("  setting val to 0");
                val = new Short((short) 0);
            } else {
                val = Short.parseShort(stripCommas(((String) val).trim()));
            }
            break;
        case "java.lang.String":
            val = ((String) val).trim();
            break;
        default:
            LOGGER.warn(String.format(
                    "  Don't know how to handle: %1$s, treating as String",
                    descriptor.getPropertyType().getName()));
            val = ((String) val).trim();
        }
        return val;
    }

    private String stripCommas(String val) {
        return val.replace(",", "");
    }
}
