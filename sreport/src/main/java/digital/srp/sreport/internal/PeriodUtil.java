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

import java.util.ArrayList;
import java.util.List;

public class PeriodUtil {

    /**
     * @param period
     *            tax year in the form 20xx-yy, e.g. 2015-16.
     * @return A list of periods starting with the specified one and moving back
     *         in time until size = <code>count</code>.
     */
    public static List<String> fillBackwards(String period, int count) {
        ArrayList<String> periods = new ArrayList<String>();

        int startYear = startYear(period)+1;
        for (int i = 0; i < count; i++, startYear--) {
            periods.add(previous(startYear));
        }

        return periods;
    }

    protected static int startYear(String period) {
        int startYear = Integer
                .parseInt(period.substring(0, period.indexOf('-')));
        return startYear;
    }

    public static String previous(String period) {
        return previous(startYear(period));
    }

    protected static String previous(int startYear) {
        return String.format("%1$d-%2$s", startYear - 1,
                String.valueOf(startYear).substring(2));
    }

    protected static String next(String period) {
        return next(startYear(period));
    }

    public static String next(int startYear) {
        return String.format("%1$d-%2$s", startYear + 1,
                String.valueOf(startYear + 2).substring(2));
    }

    public static int periodsSinceInc(String now, String since) {
        return startYear(now) - startYear(since) + 1;
    }

    public static boolean after(String periodToTest, String period) {
        return startYear(periodToTest) > startYear(period) ;
    }

    public static boolean before(String periodToTest, String period) {
        return startYear(periodToTest) < startYear(period) ;
    }

    public static boolean equals(String periodToTest, String period) {
        return startYear(periodToTest) == startYear(period) ;
    }
}
