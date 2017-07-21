package digital.srp.sreport.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import digital.srp.sreport.model.returns.EricQuestions;
import digital.srp.sreport.services.TabularDataSetHelper;

public class MultiYearReturnTest implements EricQuestions{
    
    private static final String FLOOR_AREA = "floorArea";
    private static final String NO_STAFF = "noStaff";

    @Test
    public void testTabulate() {
        List<Answer> answers = new ArrayList<Answer>();
        SurveyReturn rtn1617 = new SurveyReturn().applicablePeriod("2016-17");
        SurveyReturn rtn1516 = new SurveyReturn().applicablePeriod("2015-16");
        Question floorAreaQ = new Question().name(FLOOR_AREA);
        Question noStaffQ = new Question().name(NO_STAFF);
        answers.add(new Answer().response("1000").question(floorAreaQ).addSurveyReturn(rtn1617));
        answers.add(new Answer().response("500").question(noStaffQ).addSurveyReturn(rtn1617));
        answers.add(new Answer().response("1100").question(floorAreaQ).addSurveyReturn(rtn1516));
        answers.add(new Answer().response("550").question(noStaffQ).addSurveyReturn(rtn1516));
        
        TabularDataSet tds = new TabularDataSetHelper()
                .tabulate(new String[] { FLOOR_AREA, NO_STAFF }, answers, new DecimalFormat("#,###"));
        
        assertEquals(2, tds.rows().length);
        assertEquals("1,000", tds.rows()[0][0]);
        assertEquals("500", tds.rows()[0][1]);
        assertEquals("1,100", tds.rows()[1][0]);
        assertEquals("550", tds.rows()[1][1]);
        System.out.println(tds.toString());
    }

    @Test
    public void testTabulateMissingData() {
        List<Answer> answers = new ArrayList<Answer>();
        SurveyReturn rtn1617 = new SurveyReturn().applicablePeriod("2016-17");
        SurveyReturn rtn1516 = new SurveyReturn().applicablePeriod("2015-16");
        Question floorAreaQ = new Question().name(FLOOR_AREA);
        Question noStaffQ = new Question().name(NO_STAFF);
        answers.add(new Answer().response("1000").question(floorAreaQ).addSurveyReturn(rtn1617));
        answers.add(new Answer().response("500").question(noStaffQ).addSurveyReturn(rtn1617));
//        answers.add(new SurveyAnswer().response("1100").question(floorAreaQ).addSurveyReturn(rtn1516));
        answers.add(new Answer().response("550").question(noStaffQ).addSurveyReturn(rtn1516));
        
        TabularDataSet tds = new TabularDataSetHelper().tabulate(new String[] { FLOOR_AREA, NO_STAFF }, answers, new DecimalFormat());
        
        assertEquals(2, tds.rows().length);
        assertEquals("1,000", tds.rows()[0][0]);
        assertEquals("500", tds.rows()[0][1]);
        assertNull(tds.rows()[1][0]);
        assertEquals("550", tds.rows()[1][1]);
        System.out.println(tds.toString());
    }
}
