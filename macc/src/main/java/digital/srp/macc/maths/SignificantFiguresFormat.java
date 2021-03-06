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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class SignificantFiguresFormat extends Format {

    private static final long serialVersionUID = -1964292745833277765L;

    private static final int NO_SIGNIFICANT_FIGURES = 3;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat
            .getInstance(Locale.UK);

//    private static final SignificantFiguresFormat me = new SignificantFiguresFormat();

    private final int noSigFigs;


    public static SignificantFiguresFormat getInstance() {
        return new SignificantFiguresFormat();
    }

    public SignificantFiguresFormat() {
        this(NO_SIGNIFICANT_FIGURES);
    }

    public SignificantFiguresFormat(int noSigFigs) {
        this.noSigFigs = noSigFigs;
    }

    public Double asDouble(BigDecimal bd) {
        if (bd == null) {
            return null;
        }
        return Double.valueOf(String.format("%." + noSigFigs + "G", bd));
    }

    public BigDecimal round(BigDecimal bd) {
        try {
            return bd.round(new MathContext(noSigFigs,
                    RoundingMode.HALF_UP));
        } catch (NullPointerException e) {
            return new BigDecimal(0.00);
        }
    }

    public String format(BigDecimal bd) {
        if (bd == null) {
            return null;
        }
        return NUMBER_FORMAT.format(asDouble(bd));
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        if (obj instanceof BigDecimal) {
            return toAppendTo.append(format((BigDecimal) obj));
        } else if (obj instanceof Long) {
            return toAppendTo.append(format(new BigDecimal((Long) obj)));
        } else if (obj instanceof Double) {
            return toAppendTo.append(format(new BigDecimal((Double) obj)));
        } else if (obj instanceof String){
            return toAppendTo.append(format(new BigDecimal((String) obj)));
        } else {
            throw new NumberFormatException();
        }
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return NUMBER_FORMAT.parseObject(source, pos);
    }

}
