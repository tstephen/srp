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

    protected static String previous(String period) {
        return previous(startYear(period));
    }
    
    protected static String previous(int startYear) {
        return String.format("%1$d-%2$s", startYear - 1,
                String.valueOf(startYear).substring(2));
    }

    protected static String next(String period) {
        return next(startYear(period));
    }
    
    protected static String next(int startYear) {
        return String.format("%1$d-%2$s", startYear + 1,
                String.valueOf(startYear + 2).substring(2));
    }
}
