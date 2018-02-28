package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import digital.srp.sreport.importers.CarbonFactorCsvImporter;
import digital.srp.sreport.importers.WeightingFactorCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;

public class CarbonEmissionsProgressTest {

    private static final String FLOOR_AREA = "57283";
    private static final String NO_STAFF = "4725";
    private static final String POPULATION = "210000";
    private static final String NO_PATIENT_CONTACTS = "2100000";
    private static final String OCCUPIED_BEDS = "997.5";
    private static final String OP_EX = "262500";
    
    private static final String OWNED_BUILDINGS = "107740";
    private static final String ANAESTHETIC_GASES_CO2E = "35350";
    private static final String BIZ_SVCS_CO2E = "4146";
    private static final Object COMMISSIONING_CO2E = "948";
    private static final Object PHARMA_CO2E = "3497";
    private static final Object PAPER_CO2E = "1206";
    private static final Object OTHER_MANUFACTURED_CO2E = "1474";
    private static final Object MED_INSTR_CO2E = "8858";
    private static final Object CHEM_AND_GAS_CO2E = "1723";
    private static final Object ICT_CO2E = "628";
    private static final Object FREIGHT_CO2E = "1596";
    private static final Object CATERING_CO2E = "3019";
    private static final Object CONSTRUCTION_CO2E = "1461";    
    private static final String NET_ELEC_CO2E = "35350";
    private static final String TRAVEL_CO2E = "994";
    private static final String PROCUREMENT_CO2E = "191.0";
    private static final String BIOMASS_WTT_CO2E = "314.0";
    private static final String WASTE_CO2E = "191.0";
    private static final String WATER_CO2E = "314.0";

    private static final String PERIOD = "2013-14";

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
    @Ignore
    public void testCalcEmissionsProgress() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ORG_TYPE).response("Acute Trust"));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLOOR_AREA).response(FLOOR_AREA));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_PATIENT_CONTACTS).response(NO_PATIENT_CONTACTS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NO_STAFF).response(NO_STAFF));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OCCUPIED_BEDS).response(OCCUPIED_BEDS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OP_EX).response(OP_EX));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NON_PAY_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.POPULATION).response(POPULATION));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ANAESTHETIC_GASES_CO2E).response(ANAESTHETIC_GASES_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.NET_ELEC_CO2E).response(NET_ELEC_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_BUILDINGS).response(OWNED_BUILDINGS));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PROCUREMENT_CO2E).response(PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_BIOMASS_WTT).response(BIOMASS_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WATER).response(WATER_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_WASTE).response(WASTE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.SCOPE_3_TRAVEL).response(TRAVEL_CO2E));


        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_SVCS_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CONSTRUCTION_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CATERING_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FREIGHT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ICT_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CHEM_AND_GAS_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.MED_INSTR_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_MANUFACTURED_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PAPER_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PHARMA_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TRAVEL_SPEND));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COMMISSIONING_SPEND));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_SVCS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CAPITAL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CONSTRUCTION_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CATERING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FREIGHT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.ICT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.CHEM_AND_GAS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.MED_INSTR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_MANUFACTURED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OTHER_PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.PHARMA_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.COMMISSIONING_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_ENERGY_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TOTAL_PROCUREMENT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.WASTE_AND_WATER_CO2E));

        svc.calcCarbonProfileSduMethod(PERIOD, rtn);
        
        // CO2e
//        assertEquals(BIZ_SVCS_CO2E, rtn.answer(PERIOD, Q.BIZ_SVCS_CO2E).get().response());
        assertEquals(CONSTRUCTION_CO2E, rtn.answer(PERIOD, Q.CONSTRUCTION_CO2E).get().response());
        assertEquals(CATERING_CO2E, rtn.answer(PERIOD, Q.CATERING_CO2E).get().response());
        assertEquals(FREIGHT_CO2E, rtn.answer(PERIOD, Q.FREIGHT_CO2E).get().response());
        assertEquals(ICT_CO2E, rtn.answer(PERIOD, Q.ICT_CO2E).get().response());
        assertEquals(CHEM_AND_GAS_CO2E, rtn.answer(PERIOD, Q.CHEM_AND_GAS_CO2E).get().response());
        assertEquals(MED_INSTR_CO2E, rtn.answer(PERIOD, Q.MED_INSTR_CO2E).get().response());
        assertEquals(OTHER_MANUFACTURED_CO2E, rtn.answer(PERIOD, Q.OTHER_MANUFACTURED_CO2E).get().response());
        assertEquals(PAPER_CO2E, rtn.answer(PERIOD, Q.PAPER_AND_CARD_CO2E).get().response());
        assertEquals(PHARMA_CO2E, rtn.answer(PERIOD, Q.PHARMA_CO2E).get().response());
        assertEquals(TRAVEL_CO2E, rtn.answer(PERIOD, Q.TRAVEL_CO2E).get().response());
        assertEquals(COMMISSIONING_CO2E, rtn.answer(PERIOD, Q.COMMISSIONING_CO2E).get().response());        
    }
}
