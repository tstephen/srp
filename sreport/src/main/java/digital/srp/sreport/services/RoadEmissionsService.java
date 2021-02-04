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
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.CarbonFactor;

@Service
public class RoadEmissionsService extends AbstractEmissionsService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RoadEmissionsService.class);

    public Optional<BigDecimal> calculate(CarbonFactor fuelFactor,
            Optional<BigDecimal> fuelUsed, CarbonFactor mileageFactor,
            Optional<BigDecimal> mileage) {
        LOGGER.info("calculate({},{},{},{})", fuelFactor, fuelUsed,
                mileageFactor, mileage);
        if (fuelUsed.isPresent()) {
            return Optional.of(fuelUsed.get().multiply(fuelFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } else if (mileage.isPresent()) {
            return Optional.of(mileage.get()
                    .multiply(mileageFactor.value()).multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP));
        } else {
            return Optional.empty();
        }
    }
}
