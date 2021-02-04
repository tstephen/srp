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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;

@Service
public class EnergyEmissionsService extends AbstractEmissionsService {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(EnergyEmissionsService.class);

    public void calcGridElecCo2e(BigDecimal gridElecUse, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(gridElecUse, cFactor).orElse(BigDecimal.ZERO));
    }

    public void calcGridElecWttCo2e(BigDecimal gridElecUse, CarbonFactor cFactor, Answer co2e) {
        co2e.response(calculate(gridElecUse, cFactor).orElse(BigDecimal.ZERO));
    }
}
