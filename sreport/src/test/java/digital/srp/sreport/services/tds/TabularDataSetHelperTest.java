package digital.srp.sreport.services.tds;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.TabularDataSet;

public class TabularDataSetHelperTest {

    private DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");

    private Format prettyPrintDecimalFormat = new SignificantFiguresFormat();

    private Aggregator totaller = new Totaller();

    @Test
    public void test() throws Exception {
        TabularDataSetHelper tdsHelper = new TabularDataSetHelper();
        String[] headers  = { "OWNED_FLEET_TRAVEL_CO2E","BIZ_MILEAGE_ROAD_CO2E","BIZ_MILEAGE_RAIL_CO2E","BIZ_MILEAGE_AIR_CO2E","FUEL_WTT","PATIENT_AND_VISITOR_MILEAGE_CO2E", "STAFF_COMMUTE_MILES_CO2E" };
        List<Answer> answers = new ArrayList<Answer>();
        answers.add(new Answer(101996, "15147", new Question().name(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-02 18:04:17.0"), null));
        answers.add(new Answer(102056, "816", new Question().name(Q.BIZ_MILEAGE_ROAD_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:41.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101998, "1638", new Question().name(Q.STAFF_COMMUTE_MILES_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-12 12:01:03.0"), null));
        answers.add(new Answer(102058, "0", new Question().name(Q.BIZ_MILEAGE_AIR_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:41.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(102059, "0", new Question().name(Q.OWNED_FLEET_TRAVEL_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:41.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(102057, "0", new Question().name(Q.BIZ_MILEAGE_RAIL_CO2E.name()), StatusType.Draft, "2013-14", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:41.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101861, "1705", new Question().name(Q.STAFF_COMMUTE_MILES_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-12 12:01:03.0"), null));
        answers.add(new Answer(101921, "0", new Question().name(Q.BIZ_MILEAGE_AIR_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101922, "0", new Question().name(Q.OWNED_FLEET_TRAVEL_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101920, "0", new Question().name(Q.BIZ_MILEAGE_RAIL_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101859, "20066", new Question().name(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-02 18:04:17.0"), null));
        answers.add(new Answer(101919, "691", new Question().name(Q.BIZ_MILEAGE_ROAD_CO2E.name()), StatusType.Draft, "2014-15", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:40.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101783, "0", new Question().name(Q.BIZ_MILEAGE_RAIL_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101722, "17566", new Question().name(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:38.0"), null, isoFormat.parse("2017-11-02 18:04:17.0"), null));
        answers.add(new Answer(101782, "634", new Question().name(Q.BIZ_MILEAGE_ROAD_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101724, "1679", new Question().name(Q.STAFF_COMMUTE_MILES_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:38.0"), null, isoFormat.parse("2017-11-12 12:01:02.0"), null));
        answers.add(new Answer(101784, "0", new Question().name(Q.BIZ_MILEAGE_AIR_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101785, "0", new Question().name(Q.OWNED_FLEET_TRAVEL_CO2E.name()), StatusType.Draft, "2015-16", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:39.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101585, "22889", new Question().name(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-02 18:04:17.0"), null));
        answers.add(new Answer(101645, "350", new Question().name(Q.BIZ_MILEAGE_ROAD_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101587, "1678", new Question().name(Q.STAFF_COMMUTE_MILES_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-02 18:10:10.0"), null));
        answers.add(new Answer(101647, "0", new Question().name(Q.BIZ_MILEAGE_AIR_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101648, "0", new Question().name(Q.OWNED_FLEET_TRAVEL_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        answers.add(new Answer(101646, "0", new Question().name(Q.BIZ_MILEAGE_RAIL_CO2E.name()), StatusType.Draft, "2016-17", (short) 1, true, null, null, isoFormat.parse("2017-10-06 16:28:37.0"), null, isoFormat.parse("2017-11-03 14:35:54.0"), null));
        assertEquals(24, answers.size());
        
        String[] periods = new String[] { "2013-14", "2014-15", "2015-16", "2016-17" };
        TabularDataSet table = tdsHelper.tabulate(headers, periods, answers, prettyPrintDecimalFormat, Optional.of(totaller));
        table = table.transpose();
        assertEquals(8, table.rows().length);
        assertEquals(4, table.rows()[0].length);
        // Business Mileage - Road
        assertEquals("816", table.rows()[1][0]);
        assertEquals("691", table.rows()[1][1]);
        assertEquals("634", table.rows()[1][2]);
        assertEquals("350", table.rows()[1][3]);
        // Staff commute
        assertEquals("1,640", table.rows()[6][0]);
        assertEquals("1,710", table.rows()[6][1]);
        assertEquals("1,680", table.rows()[6][2]);
        assertEquals("1,680", table.rows()[6][3]);
        // Totals
        assertEquals("17,600", table.rows()[7][0]);
        assertEquals("22,500", table.rows()[7][1]);
        assertEquals("19,900", table.rows()[7][2]);
        assertEquals("24,900", table.rows()[7][3]);
    }

}
