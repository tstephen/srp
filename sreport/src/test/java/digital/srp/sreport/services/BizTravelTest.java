package digital.srp.sreport.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class BizTravelTest {

    private static final String OWNED_LEASED_LOW_CARBON_MILES = "0";
    private static final String FLEET_ROAD_MILES = "2770455"; // 3rd party
    private static final String BIZ_MILEAGE_ROAD = "554091"; // owned fleet
    private static final String BIZ_MILEAGE_ROAD_ONLY = "3324546";
    private static final String BIZ_MILEAGE = "5201516";
    private static final String RAIL_MILES = "1846970";
    private static final String BUS_MILES = "10000";
    private static final String TAXI_MILES = "20000";

    private static final String BUS_COST = "1200";
    private static final String RAIL_COST = "554091";
    private static final String TAXI_COST = "56600";

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
    public void testBusinessMileageRoadOnly() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_ROAD_MILES).response(FLEET_ROAD_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_MILES).response(OWNED_LEASED_LOW_CARBON_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD).response(BIZ_MILEAGE_ROAD));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_MILES));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));

        svc.crunchBizTravel(PERIOD, rtn);
        assertEquals(new BigDecimal("1185"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(BIZ_MILEAGE_ROAD_ONLY), rtn.answer(PERIOD, Q.BIZ_MILEAGE).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        
        // check inputs unchanged
        assertEquals(FLEET_ROAD_MILES, rtn.answer(PERIOD, Q.FLEET_ROAD_MILES).get().response());
        assertEquals(OWNED_LEASED_LOW_CARBON_MILES, rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_MILES).get().response());
    }

    @Test
    public void testBusinessMileageAllForms() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_ROAD_MILES).response(FLEET_ROAD_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_MILES).response(OWNED_LEASED_LOW_CARBON_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD).response(BIZ_MILEAGE_ROAD));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_MILES).response(RAIL_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_MILES).response(BUS_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_MILES).response(TAXI_MILES));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_BUS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_RAIL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_AIR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_TAXI_CO2E));

        svc.crunchBizTravel(PERIOD, rtn);
        assertEquals(new BigDecimal("1185"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2.04"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_BUS_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("166"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_RAIL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("6.70"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_TAXI_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(BIZ_MILEAGE), rtn.answer(PERIOD, Q.BIZ_MILEAGE).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        
        // check inputs unchanged
        assertEquals(FLEET_ROAD_MILES, rtn.answer(PERIOD, Q.FLEET_ROAD_MILES).get().response());
        assertEquals(OWNED_LEASED_LOW_CARBON_MILES, rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_MILES).get().response());
    }
    
    @Test
    public void testBusinessMileagePublicTransportFromCost() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_COST).response(RAIL_COST));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_COST).response(BUS_COST));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_COST).response(TAXI_COST));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_MILES));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_BUS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_RAIL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_AIR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_TAXI_CO2E));

        svc.crunchBizTravel(PERIOD, rtn);
        assertEquals(new BigDecimal(BUS_MILES), rtn.answer(PERIOD, Q.BUS_MILES).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(RAIL_MILES), rtn.answer(PERIOD, Q.RAIL_MILES).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(TAXI_MILES), rtn.answer(PERIOD, Q.TAXI_MILES).get().responseAsBigDecimal());

        assertEquals(new BigDecimal("2.04"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_BUS_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("166"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_RAIL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("6.70"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_TAXI_CO2E).get().responseAsBigDecimal());

        assertEquals(new BigDecimal(BUS_MILES).add(new BigDecimal(RAIL_MILES)).add(new BigDecimal(TAXI_MILES)),
                rtn.answer(PERIOD, Q.BIZ_MILEAGE).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        
        // check inputs unchanged
    }
}
