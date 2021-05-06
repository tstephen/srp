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
package digital.srp.sreport.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class BizTravelTest {

    private static final String OWNED_LEASED_LOW_CARBON_MILES = "0";
    private static final String FLEET_ROAD_MILES = "2770455";
    private static final String BIZ_MILEAGE_ROAD = "436918";
    private static final String BIZ_MILEAGE_ROAD_PRE2020 = "554091";
    private static final String BIZ_MILEAGE_ROAD_ONLY = "3324546";
    private static final String BIZ_MILEAGE = "5201516";
    private static final String RAIL_MILES = "1846970";
    private static final String BUS_MILES = "10000";
    private static final String TAXI_MILES = "20000";

    private static final String BUS_COST = "1200";
    private static final String DIESEL_USED = "250000";
    private static final String PETROL_USED = "250000";
    private static final String OWNED_LEASED_LOW_CARBON_FUEL_USED = "1000";
    private static final String RAIL_COST = "554091";
    private static final String TAXI_COST = "56600";

    private static final String PERIOD = "2017-18";
    private static final String PERIOD_2020_21 = "2020-21";

    private static final String BIZ_ROAD_CO2E_PRE2020 = "160";
    private static final String CAR_DIESEL_USED_CO2E = "637";
    private static final String CAR_DIESEL_WTT_CO2E = "153";
    private static final String CAR_PETROL_USED_CO2E = "542";
    private static final String CAR_PETROL_WTT_CO2E = "148";
    private static final String OWNED_LEASED_LOW_CARBON_CO2E_PRE2020 = "0.45";
    private static final String OWNED_LEASED_LOW_CARBON_CO2E = "0.29";
    private static final String FLEET_AND_BIZ_ROAD_CO2E_PRE2020 = "1005";
    private static final String FLEET_AND_BIZ_ROAD_CO2E = "1641";
    private static final String BUS_MILES_RWV = "10708";
    private static final String DOMESTIC_AIR_MILES_RWV = "856";
    private static final String RAIL_MILES_RWV = "12527";
    private static final String TAXI_MILES_RWV = "23053";
    private static final BigDecimal BIZ_MILEAGE_ROAD_INC_WTT_CO2E = new BigDecimal("151.59");
    private static final Object BIZ_MILEAGE_AIR_CO2E = null;
    private static final Object BIZ_MILEAGE_BUS_CO2E = "2.20";
    private static final Object BIZ_MILEAGE_RAIL_CO2E = "0.89";
    private static final Object BIZ_MILEAGE_TAXI_CO2E = "7.22";
    private static final Object BIZ_MILEAGE_ALL_ROAD_CO2E = "161.010";
    private static final String OWNED_FLEET_TRAVEL_CO2E = "798.45";

    private static Cruncher svc;

    private static List<CarbonFactor> cfactors;
    private static List<WeightingFactor> wfactors;

    @BeforeAll
    public static void setUpClass() throws IOException {
        cfactors = new CarbonFactorCsvImporter().readCarbonFactors();
        wfactors = new WeightingFactorCsvImporter().readWeightingFactors();
        svc = new Cruncher(cfactors, wfactors);
        svc.roadEmissionsService = new RoadEmissionsService();
    }

    @Test
    public void testFleetAndBusinessTravelRoadOnlyPre2020() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_ROAD_MILES).response(FLEET_ROAD_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_MILES).response(OWNED_LEASED_LOW_CARBON_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD).response(BIZ_MILEAGE_ROAD_PRE2020));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_MILES));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ALL_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_AND_BIZ_ROAD_CO2E));

        svc.crunchFleetAndBizTravel(PERIOD, rtn);
        assertEquals(new BigDecimal(BIZ_ROAD_CO2E_PRE2020), rtn.answer(PERIOD, Q.BIZ_MILEAGE_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(BIZ_MILEAGE_ROAD_ONLY), rtn.answer(PERIOD, Q.BIZ_MILEAGE).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        // check inputs unchanged
        assertEquals(FLEET_ROAD_MILES, rtn.answer(PERIOD, Q.FLEET_ROAD_MILES).get().response());
        assertEquals(OWNED_LEASED_LOW_CARBON_MILES, rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_MILES).get().response());
    }
    @Test
    public void testBusinessMileageAllPre2020() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD).org("ZZ1");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_ROAD_MILES).response(FLEET_ROAD_MILES));
        // ROAD actually means CAR, bus and taxi are separate
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD).response(BIZ_MILEAGE_ROAD_PRE2020));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.RAIL_MILES).response(RAIL_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BUS_MILES).response(BUS_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.TAXI_MILES).response(TAXI_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).response(OWNED_LEASED_LOW_CARBON_FUEL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_MILES).response(OWNED_LEASED_LOW_CARBON_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ALL_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_BUS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_RAIL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_AIR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_TAXI_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_AND_BIZ_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.LEASED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_LEASED_LOW_CARBON_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_AND_BIZ_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));

        svc.crunchFleetAndBizTravel(PERIOD, rtn);

        // check inputs unchanged
        assertEquals(FLEET_ROAD_MILES, rtn.answer(PERIOD, Q.FLEET_ROAD_MILES).get().response());
        assertEquals(BIZ_MILEAGE_ROAD_PRE2020, rtn.answer(PERIOD, Q.BIZ_MILEAGE_ROAD).get().response());
        assertEquals(RAIL_MILES, rtn.answer(PERIOD, Q.RAIL_MILES).get().response());
        assertEquals(BUS_MILES, rtn.answer(PERIOD, Q.BUS_MILES).get().response());
        assertEquals(TAXI_MILES, rtn.answer(PERIOD, Q.TAXI_MILES).get().response());

        assertEquals(new BigDecimal(BIZ_ROAD_CO2E_PRE2020), rtn.answer(PERIOD, Q.BIZ_MILEAGE_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("2.04"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_BUS_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal("166"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_RAIL_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("6.70"), rtn.answer(PERIOD, Q.BIZ_MILEAGE_TAXI_CO2E).get().responseAsBigDecimal());
        assertEquals(new BigDecimal(BIZ_MILEAGE), rtn.answer(PERIOD, Q.BIZ_MILEAGE).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(OWNED_LEASED_LOW_CARBON_CO2E_PRE2020), rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(OWNED_FLEET_TRAVEL_CO2E), rtn.answer(PERIOD, Q.OWNED_FLEET_TRAVEL_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(FLEET_AND_BIZ_ROAD_CO2E_PRE2020), rtn.answer(PERIOD, Q.FLEET_AND_BIZ_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        assertEquals(OWNED_LEASED_LOW_CARBON_FUEL_USED, rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).get().response());
        assertEquals(OWNED_LEASED_LOW_CARBON_MILES, rtn.answer(PERIOD, Q.OWNED_LEASED_LOW_CARBON_MILES).get().response());
    }

    @Test
    public void testBusinessMileageAllFrom2020() {
        SurveyReturn rtn = new SurveyReturn().applicablePeriod(PERIOD_2020_21).org("RWV");
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.FLEET_ROAD_MILES).response(FLEET_ROAD_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_ROAD).response(BIZ_MILEAGE_ROAD));
//        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.PRIVATE_CAR_MILES_UNKOWN_FUEL).response(PRIVATE_CAR_MILES_UNKOWN_FUEL));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.DOMESTIC_AIR_MILES).response(DOMESTIC_AIR_MILES_RWV));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BUS_MILES).response(BUS_MILES_RWV));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.RAIL_MILES).response(RAIL_MILES_RWV));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.TAXI_MILES).response(TAXI_MILES_RWV));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).response(OWNED_LEASED_LOW_CARBON_FUEL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_LEASED_LOW_CARBON_MILES).response(OWNED_LEASED_LOW_CARBON_MILES));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_PETROL_USED).response(PETROL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_DIESEL_USED).response(DIESEL_USED));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_MILES_PETROL).response(BigDecimal.ZERO));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_MILES_DIESEL).response(BigDecimal.ZERO));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE));

        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_ALL_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_ROAD_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_BUS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_RAIL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_AIR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_TAXI_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.FLEET_AND_BIZ_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.LEASED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_LEASED_LOW_CARBON_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_PETROL_USED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_PETROL_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_DIESEL_USED_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.CAR_DIESEL_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.FLEET_AND_BIZ_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD_2020_21).question(Q.BIZ_MILEAGE_CO2E));

        svc.crunchFleetAndBizTravel(PERIOD_2020_21, rtn);

        // check inputs unchanged
        assertEquals(FLEET_ROAD_MILES, rtn.answer(PERIOD_2020_21, Q.FLEET_ROAD_MILES).get().response());
        assertEquals(BIZ_MILEAGE_ROAD, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ROAD).get().response());
        assertEquals(RAIL_MILES_RWV, rtn.answer(PERIOD_2020_21, Q.RAIL_MILES).get().response());
        assertEquals(BUS_MILES_RWV, rtn.answer(PERIOD_2020_21, Q.BUS_MILES).get().response());
        assertEquals(TAXI_MILES_RWV, rtn.answer(PERIOD_2020_21, Q.TAXI_MILES).get().response());

        assertEquals(new BigDecimal(CAR_DIESEL_USED_CO2E), rtn.answer(PERIOD_2020_21, Q.CAR_DIESEL_USED_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(CAR_DIESEL_WTT_CO2E), rtn.answer(PERIOD_2020_21, Q.CAR_DIESEL_WTT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(CAR_PETROL_USED_CO2E), rtn.answer(PERIOD_2020_21, Q.CAR_PETROL_USED_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(CAR_PETROL_WTT_CO2E), rtn.answer(PERIOD_2020_21, Q.CAR_PETROL_WTT_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(OWNED_LEASED_LOW_CARBON_CO2E), rtn.answer(PERIOD_2020_21, Q.OWNED_LEASED_LOW_CARBON_CO2E).get().responseAsBigDecimal().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal(FLEET_AND_BIZ_ROAD_CO2E), rtn.answer(PERIOD_2020_21, Q.FLEET_AND_BIZ_ROAD_CO2E).get().responseAsBigDecimal().setScale(0, RoundingMode.HALF_UP));

        assertEquals(OWNED_LEASED_LOW_CARBON_FUEL_USED, rtn.answer(PERIOD_2020_21, Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).get().response());
        assertEquals(OWNED_LEASED_LOW_CARBON_MILES, rtn.answer(PERIOD_2020_21, Q.OWNED_LEASED_LOW_CARBON_MILES).get().response());
        assertEquals(PETROL_USED, rtn.answer(PERIOD_2020_21, Q.CAR_PETROL_USED).get().response());
        assertEquals(DIESEL_USED, rtn.answer(PERIOD_2020_21, Q.CAR_DIESEL_USED).get().response());
        assertEquals(BigDecimal.ZERO.toPlainString(), rtn.answer(PERIOD_2020_21, Q.CAR_MILES_PETROL).get().response());
        assertEquals(BigDecimal.ZERO.toPlainString(), rtn.answer(PERIOD_2020_21, Q.CAR_MILES_DIESEL).get().response());

        assertEquals(BIZ_MILEAGE_AIR_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_AIR_CO2E).get().response());
        assertEquals(BIZ_MILEAGE_RAIL_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_RAIL_CO2E).get().response());
//        assertEquals(BIZ_MILEAGE_ROAD_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ROAD_CO2E).get().response());
//        assertEquals(BIZ_MILEAGE_ROAD_WTT_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ROAD_WTT_CO2E).get().response());
        assertEquals(BIZ_MILEAGE_ROAD_INC_WTT_CO2E,
                rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ROAD_CO2E).get().responseAsBigDecimal().add(rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ROAD_WTT_CO2E).get().responseAsBigDecimal()));
        assertEquals(BIZ_MILEAGE_BUS_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_BUS_CO2E).get().response());
        assertEquals(BIZ_MILEAGE_TAXI_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_TAXI_CO2E).get().response());
        assertEquals(BIZ_MILEAGE_ALL_ROAD_CO2E, rtn.answer(PERIOD_2020_21, Q.BIZ_MILEAGE_ALL_ROAD_CO2E).get().response());
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
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_WTT_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_ALL_ROAD_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_BUS_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.OWNED_FLEET_TRAVEL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_RAIL_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_AIR_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.BIZ_MILEAGE_TAXI_CO2E));
        rtn.getAnswers().add(new Answer().applicablePeriod(PERIOD).question(Q.FLEET_AND_BIZ_ROAD_CO2E));

        svc.crunchFleetAndBizTravel(PERIOD, rtn);
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
