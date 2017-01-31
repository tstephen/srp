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
package digital.srp.macc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeries implements Serializable {

    private static final long serialVersionUID = -4408876419570909228L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TimeSeries.class);

    private String values;

    public List<BigDecimal> asList() {
        if (values == null) {
            return null;
        } else {
            List<BigDecimal> bdValues = new ArrayList<BigDecimal>();
            for (String s : values.split("[, ]")) {
                if (s.trim().length() > 0) {
                    s = s.replaceAll("[£$]", "");
                    try {
                        bdValues.add(new BigDecimal(s));
                    } catch (Exception e) {
                        LOGGER.error(String.format(
                                "Unable to parse decimal from %1$s", s));
                    }
                }
            }
            return bdValues;
        }
    }

    public static String asString(List<BigDecimal> list) {
        if (list == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Iterator<BigDecimal> it = list.iterator(); it.hasNext();) {
                BigDecimal bd = it.next();
                sb.append(bd == null ? 0 : bd.toPlainString());
                if (it.hasNext()) {
                    sb.append(',');
                }
            }
            return sb.toString();
        }
    }

}
