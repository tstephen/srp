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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class SignificantFiguresFormat {

    private static final int NO_SIGNIFICANT_FIGURES = 3;
    
    private static final NumberFormat NUMBER_FORMAT = NumberFormat
            .getInstance(Locale.UK);


    public static String format(BigDecimal bd) {
        if (bd == null) {
            return null;
        }
        return NUMBER_FORMAT.format(asDouble(bd));
    }

    public static Double asDouble(BigDecimal bd) {
        if (bd == null) {
            return null;
        }
        return Double.valueOf(String.format(
                "%." + NO_SIGNIFICANT_FIGURES
                + "G", bd));
    }

    public static BigDecimal round(BigDecimal bd) {
        try {
            return bd.round(new MathContext(NO_SIGNIFICANT_FIGURES,
                    RoundingMode.HALF_UP));
        } catch (NullPointerException e) {
            return new BigDecimal(0.00);
        }
    }

}
