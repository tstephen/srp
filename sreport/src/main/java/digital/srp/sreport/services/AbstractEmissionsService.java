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
package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digital.srp.sreport.model.CarbonFactor;

public abstract class AbstractEmissionsService {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractEmissionsService.class);

    protected static final BigDecimal ONE_THOUSAND = new BigDecimal(
                "1000.000000");
    protected static final BigDecimal ONE_HUNDRED = new BigDecimal(
                "100.000000");
    protected static final BigDecimal m2km = new BigDecimal("1.60934");

    protected int divisionScale = 2;

    public Optional<BigDecimal> calculate(BigDecimal use, CarbonFactor cFactor) {
        try {
            if (cFactor.value().floatValue() < 0) {
                LOGGER.warn("No Carbon factor for {}", cFactor.name());
                throw new IllegalStateException();
            }
            return Optional.of(use
                    .multiply(cFactor.value())
                    .divide(Cruncher.ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate {}", cFactor.name());
            return Optional.empty();
        }
    }

}
