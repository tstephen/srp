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
package digital.srp.macc.maths;

import java.math.BigDecimal;

public class Finance {

    public static double presentValue(double futureValue, int years, double d) {
        return Math.round(futureValue / Math.pow(1 + d, years));
    }

    public static BigDecimal presentValue(BigDecimal futureValue, int years,
            double d) {
        BigDecimal divisor = new BigDecimal(Math.pow(1 + d, years));
        // divisor.round(MathContext.DECIMAL32);
        // divisor.setScale(4, RoundingMode.HALF_DOWN);
        return futureValue.divideToIntegralValue(divisor);
    }

    public static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal("0.00");
    public static final int ROUND_MODE = BigDecimal.ROUND_HALF_UP;
}
