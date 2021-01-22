package digital.srp.sreport.services;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class SocialValueTest {

    private static final String PERIOD = "2017-18";
    
    private static final String SV_GROWTH = "1000";
    private static final String SV_ENVIRONMENT = "2000";
    private static final String SV_INNOVATION = "3000";
    private static final String SV_JOBS = "4000";
    private static final String SV_SOCIAL = "5000";
    private static final String SV_TOTAL = "15,000";
    
    private static final String SI_GROWTH = "1001";
    private static final String SI_ENVIRONMENT = "2002";
    private static final String SI_INNOVATION = "3003";
    private static final String SI_JOBS = "4004";
    private static final String SI_SOCIAL = "5005";
    private static final String SI_TOTAL = "15,000";    
    
    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeAll
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
    }

    @Test
    public void testSocialValue() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_ENVIRONMENT).response(SV_ENVIRONMENT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_GROWTH).response(SV_GROWTH));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_INNOVATION).response(SV_INNOVATION));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_JOBS).response(SV_JOBS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_SOCIAL).response(SV_SOCIAL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SV_TOTAL));

        svc.crunchSocialValue(PERIOD, rtn);

        assertEquals(SV_ENVIRONMENT, rtn.answer(PERIOD, Q.SV_ENVIRONMENT).get().response());
        assertEquals(SV_GROWTH, rtn.answer(PERIOD, Q.SV_GROWTH).get().response());
        assertEquals(SV_INNOVATION, rtn.answer(PERIOD, Q.SV_INNOVATION).get().response());
        assertEquals(SV_JOBS, rtn.answer(PERIOD, Q.SV_JOBS).get().response());
        assertEquals(SV_SOCIAL, rtn.answer(PERIOD, Q.SV_SOCIAL).get().response());
        assertEquals(SV_TOTAL, rtn.answer(PERIOD, Q.SV_TOTAL).get().response3sf());
    }

    @Test
    public void testSocialInvestmentRecorded() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_ENVIRONMENT).response(SI_ENVIRONMENT));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_GROWTH).response(SI_GROWTH));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_INNOVATION).response(SI_INNOVATION));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_JOBS).response(SI_JOBS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_SOCIAL).response(SI_SOCIAL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SI_TOTAL));

        svc.crunchSocialInvestmentRecorded(PERIOD, rtn);

        assertEquals(SI_ENVIRONMENT, rtn.answer(PERIOD, Q.SI_ENVIRONMENT).get().response());
        assertEquals(SI_GROWTH, rtn.answer(PERIOD, Q.SI_GROWTH).get().response());
        assertEquals(SI_INNOVATION, rtn.answer(PERIOD, Q.SI_INNOVATION).get().response());
        assertEquals(SI_JOBS, rtn.answer(PERIOD, Q.SI_JOBS).get().response());
        assertEquals(SI_SOCIAL, rtn.answer(PERIOD, Q.SI_SOCIAL).get().response());
        assertEquals(SI_TOTAL, rtn.answer(PERIOD, Q.SI_TOTAL).get().response3sf());        
    }
}
