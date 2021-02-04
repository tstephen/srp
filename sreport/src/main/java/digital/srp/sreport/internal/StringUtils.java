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
package digital.srp.sreport.internal;

public class StringUtils {

    public static String toConst(String string) {
        String c = string.toUpperCase().replaceAll("[ /-]", "_")
                .replaceAll("[:;%&()]", "").replaceAll("___", "_")
                .replaceAll("__", "_");
        if (Character.isDigit(c.charAt(0))) {
            return "_" + c;
        } else {
            return c;
        }
    }

    public static String camelCaseToConst(String string) {
        return string.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

    public static String toSentanceCase(String s) {
        return Character.toUpperCase(s.charAt(0))
                + s.substring(1).toLowerCase().replaceAll("_", " ");
    }

}
