package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class PatientTransportTest {

    private static final String VISITOR_MILEAGE = "123008210";
    private static final String PATIENT_MILEAGE = "941955";
    private static final String PATIENT_CONTACTS = "2216364";
    private static final String PERIOD = "2017-18";
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeClass
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testCalcVisitorMileageProvided() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_MILEAGE).response(PATIENT_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_PATIENT_CONTACTS).response(PATIENT_CONTACTS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.VISITOR_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_AND_VISITOR_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E));

        assertEquals(PATIENT_MILEAGE, rtn.answer(PERIOD, Q.PATIENT_MILEAGE).get().response());
        assertEquals(PATIENT_CONTACTS, rtn.answer(PERIOD, Q.NO_PATIENT_CONTACTS).get().response());
        svc.crunchPatientVisitorMileageCo2e(PERIOD, rtn);
        assertEquals("77085139.92", rtn.answer(PERIOD, Q.VISITOR_MILEAGE).get().response());
        assertEquals(new BigDecimal("78027094.92"), rtn.answer(PERIOD, Q.PATIENT_AND_VISITOR_MILEAGE).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("27803"), rtn.answer(PERIOD, Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).get().responseAsBigDecimal());
    }

    @Test
    public void testBothPatientAndVisitorProvided() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_MILEAGE).response(PATIENT_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.VISITOR_MILEAGE).response(VISITOR_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_AND_VISITOR_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PATIENT_AND_VISITOR_MILEAGE_CO2E));

        assertEquals(PATIENT_MILEAGE, rtn.answer(PERIOD, Q.PATIENT_MILEAGE).get().response());
        assertEquals(VISITOR_MILEAGE, rtn.answer(PERIOD, Q.VISITOR_MILEAGE).get().response());
        svc.crunchPatientVisitorMileageCo2e(PERIOD, rtn);
        assertEquals(new BigDecimal("123950165"), rtn.answer(PERIOD, Q.PATIENT_AND_VISITOR_MILEAGE).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("44166"), rtn.answer(PERIOD, Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).get().responseAsBigDecimal());
    }
}
