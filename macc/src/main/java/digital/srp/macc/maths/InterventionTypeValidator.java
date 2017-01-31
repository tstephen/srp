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
package digital.srp.macc.maths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.macc.model.InterventionType;

public class InterventionTypeValidator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionTypeValidator.class);

    public static boolean isValid(InterventionType intervention) {
        LOGGER.info(String.format("Checking validity of %1$s intervention",
                intervention.getName()));
        boolean valid = true;
        if (intervention.getName() == null) {
            LOGGER.error("  Name is null");
            valid = false;
        }
        if (intervention.getScaling() == null) {
            LOGGER.error("  Scaling is null");
            valid = false;
        }
        if (intervention.getUptake() == null) {
            LOGGER.error("  Uptake is null");
            valid = false;
        }
        if (intervention.getAnnualCashInflowsTS() != null
                && intervention.getAnnualCashInflowsTimeSeries().size() == 0) {
            LOGGER.error("  Did not parse annual cash inflows time series");
            valid = false;
        }
        if (intervention.getAnnualTonnesCo2eSavedTS() == null
                && intervention.getAnnualTonnesCo2eSaved() == null) {
            LOGGER.error("  No CO2e savings, either as time series or single number");
            valid = false;
        }
        if (intervention.getAnnualTonnesCo2eSavedTS() != null
                && intervention.getAnnualTonnesCo2eSavedTimeSeries().size() < 5) {
            LOGGER.error("  CO2e savings are specified by a time series but does not go as far as 2020");
            valid = false;
        }
        // This is likely a calculation bug
        if (valid
                && (intervention.getCostPerTonneCo2e() == null || intervention
                        .getCostPerTonneCo2e().floatValue() == 0.0f)) {
            LOGGER.error("  Could not calculate cost per tonne");
            valid = false;
        }
        if (valid
                && (intervention.getTonnesCo2eSavedTargetYear() == null || intervention
                        .getTonnesCo2eSavedTargetYear().longValue() == 0l)) {
            LOGGER.error("  Could not calculate CO2e saved in target year");
            valid = false;
        }
        if (valid) {
            LOGGER.info("  Valid and complete");
        }
        return valid;
    }
}
