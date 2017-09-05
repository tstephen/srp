package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.internal.SReportObjectNotFoundException;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.WeightingFactors;
import digital.srp.sreport.repositories.AnswerRepository;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyCategoryRepository;

@Component
public class Cruncher implements digital.srp.sreport.model.surveys.SduQuestions {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Cruncher.class);

    protected static final BigDecimal m2km = new BigDecimal("1.60934");

    protected final List<CarbonFactor> cfactors;

    protected final List<WeightingFactor> wfactors;

    @Autowired
    protected SurveyCategoryRepository catRepo;

    @Autowired
    protected QuestionRepository qRepo;

    @Autowired
    protected AnswerRepository answerRepo;

    // TODO parameterise
    protected int yearsToCrunch = 4;

    public Cruncher(final List<CarbonFactor> cfactors2,
            final List<WeightingFactor> wfactors2) {
        this.cfactors = cfactors2;
        this.wfactors = wfactors2;
    }


    public SurveyReturn calculate(SurveyReturn rtn) {
//        String currentPeriod = rtn.applicablePeriod();
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(), 4);
        for (String period : periods) {
//            rtn.applicablePeriod(period);
            calcScope1(period, rtn);
            calcScope2(period, rtn);

            calcScope3(period, rtn);
            sumAnswers(period, rtn, Q.SCOPE_ALL, Q.SCOPE_1, Q.SCOPE_2,
                    Q.SCOPE_3);

            // TODO Outside scopes -Breakdown - not included in carbon emissions
            // totals
            try {
                String eClassUser = rtn.answer(Q.ECLASS_USER, period).response();
                if (Boolean.parseBoolean(eClassUser)
                        || "0-eClass".equals(eClassUser)) {
                    calcCarbonProfileEClassMethod(period, rtn);
                } else {
                    calcCarbonProfileSduMethod(period, rtn);
                }
                calcTrendOverTime(period, rtn);
            } catch (IllegalStateException | SReportObjectNotFoundException e) {
                LOGGER.warn(e.getMessage());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
//            } finally {
//                rtn.applicablePeriod(currentPeriod);
            }
        }

        return rtn;
    }

    private void calcScope3(String period, SurveyReturn rtn) {
        crunchScope3Travel(period, rtn);
        crunchScope3Water(period, rtn);
        crunchScope3Waste(period, rtn);

        // TODO treasury row 68: Capital Spend

        crunchScope3BiomassWtt(period, rtn);
        crunchScope3Biomass(period, rtn);

        sumAnswers(period, rtn, Q.SCOPE_3,
                Q.SCOPE_3_TRAVEL, Q.SCOPE_3_WATER, Q.SCOPE_3_WASTE, Q.CAPITAL_CO2E, Q.SCOPE_3_BIOMASS);
    }

    private void crunchScope3Water(String period, SurveyReturn rtn) {
        try {
            // If necessary estimate waste water as 0% of incoming
            Answer wasteWater = getAnswer(period,rtn, Q.WASTE_WATER);
            if (wasteWater.response() == null || wasteWater.response().equals(BigDecimal.ZERO)) {
                wasteWater.response(
                        new BigDecimal(rtn.answer(Q.WATER_VOL, period).response())
                        .multiply(new BigDecimal("0.80")));
            }

            // Treasury row 57: Water Use
            CarbonFactor cFactor = cFactor(CarbonFactors.WATER_SUPPLY, period);
            BigDecimal waterUseCo2e = new BigDecimal(rtn.answer(Q.WATER_VOL, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WATER_CO2E).response(waterUseCo2e);

            // Treasury row 58: Water Treatment
            cFactor = cFactor(CarbonFactors.WATER_TREATMENT, period);
            BigDecimal waterTreatmentCo2e = new BigDecimal(rtn.answer(Q.WASTE_WATER, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WATER_TREATMENT_CO2E)
                    .response(waterTreatmentCo2e);

            sumAnswers(period, rtn, Q.SCOPE_3_WATER,
                    Q.WATER_CO2E, Q.WATER_TREATMENT_CO2E);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from water");
        }
    }

    private void crunchScope3Waste(String period, SurveyReturn rtn) {
        try {
            // Treasury row 62: Waste Recycling
            CarbonFactor cFactor = cFactor(CarbonFactors.CLOSED_LOOP_OR_OPEN_LOOP, period);
            BigDecimal recyclingCo2e = new BigDecimal(rtn.answer(Q.RECYCLING_WEIGHT, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.RECYCLING_CO2E).response(recyclingCo2e);

            // Treasury row 63: Other Recovery
            cFactor = cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE_WITH_ENERGY_RECOVERY, period);
            BigDecimal recoveryCo2e = new BigDecimal(rtn.answer(Q.OTHER_RECOVERY_WEIGHT, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period, rtn, Q.OTHER_RECOVERY_CO2E).response(recoveryCo2e);

            // Treasury row 64: Incineration waste
            cFactor = cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE, period);
            BigDecimal incinerationCo2e = new BigDecimal(rtn.answer(Q.INCINERATION_WEIGHT, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.INCINERATION_CO2E).response(incinerationCo2e);

            // Treasury row 65: Landfill disposal waste
            cFactor = cFactor(CarbonFactors.NON_BURN_TREATMENT_DISPOSAL_WASTE, period);
            BigDecimal landfillCo2e = new BigDecimal(rtn.answer(Q.LANDFILL_WEIGHT, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.LANDFILL_CO2E).response(landfillCo2e);

            sumAnswers(period, rtn, Q.SCOPE_3_WASTE,
                    Q.RECYCLING_CO2E, Q.OTHER_RECOVERY_CO2E, Q.INCINERATION_CO2E, Q.LANDFILL_CO2E);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from waste");
        }
    }

    private Answer getAnswer(String period, SurveyReturn rtn, Q q) {
        if (answerRepo == null) { // unit test calcs not persistence
            Answer a = rtn.answer(q, period);
            rtn.answers().add(a);
            return a;
        } else {
            try {
                Answer answer = answerRepo.findByOrgPeriodAndQuestion(rtn.org(), period, q.name());
                if (answer==null) {
                    Question existingQ = qRepo.findByName(q.name());
                    if (existingQ == null) {
                        existingQ = qRepo.save(new Question().q(q));
                    }
                    answer = new Answer()
                            .addSurveyReturn(rtn)
                            .question(existingQ)
                            .applicablePeriod(period)
                            .status(StatusType.Draft.name());
//                    answerRepo.save(answer);
                    rtn.answers().add(answer);
                }
                return answer;
            } catch (Exception e) {
                LOGGER.error(String.format("looking for answer '%1$s' for '%2$s' in '%3$s'",
                        q.name(), rtn.org(), period));
                throw e;
            }
        }
    }

    private void crunchScope3BiomassWtt(String period, SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_WTT, period);
            BigDecimal logsCo2e = new BigDecimal(rtn.answer(Q.WOOD_LOGS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_LOGS_WTT_CO2E).response(logsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            getAnswer(period,rtn, Q.WOOD_LOGS_WTT_CO2E).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_WTT, period);
            BigDecimal chipsCo2e = new BigDecimal(rtn.answer(Q.WOOD_CHIPS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_CHIPS_WTT_CO2E).response(chipsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            getAnswer(period,rtn, Q.WOOD_CHIPS_WTT_CO2E).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_WTT, period);
            BigDecimal pelletsCo2e = new BigDecimal(rtn.answer(Q.WOOD_PELLETS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_PELLETS_WTT_CO2E).response(pelletsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            getAnswer(period,rtn, Q.WOOD_PELLETS_WTT_CO2E).response(BigDecimal.ZERO);
        }

        sumAnswers(period, rtn, Q.SCOPE_3_BIOMASS_WTT,
                Q.WOOD_LOGS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_PELLETS_WTT_CO2E);
    }

    private void crunchScope3Biomass(String period, SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_TOTAL, period);
            BigDecimal logsCo2e = new BigDecimal(rtn.answer(Q.WOOD_LOGS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_LOGS_CO2E).response(logsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            getAnswer(period,rtn, Q.WOOD_LOGS_CO2E).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_TOTAL, period);
            BigDecimal chipsCo2e = new BigDecimal(rtn.answer(Q.WOOD_CHIPS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_CHIPS_CO2E).response(chipsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            getAnswer(period,rtn, Q.WOOD_CHIPS_CO2E).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_TOTAL, period);
            BigDecimal pelletsCo2e = new BigDecimal(rtn.answer(Q.WOOD_PELLETS_USED, period).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_PELLETS_CO2E).response(pelletsCo2e);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            getAnswer(period,rtn, Q.WOOD_PELLETS_CO2E).response(BigDecimal.ZERO);
        }

        sumAnswers(period, rtn, Q.SCOPE_3_BIOMASS,
                Q.WOOD_LOGS_CO2E, Q.WOOD_CHIPS_CO2E, Q.WOOD_PELLETS_CO2E);
    }

    private void crunchScope3Travel(String period, SurveyReturn rtn) {
        try {
            CarbonFactor cFactor = cFactor(CarbonFactors.CAR_TOTAL, period);
            // TODO this is a derived figure, why including alongside the 'raw' ones?
            new BigDecimal(rtn.answer(Q.BIZ_MILEAGE_CO2E, period).response());

            // Treasury row 52: Patient and Visitor
            BigDecimal patientVisitorCo2e = new BigDecimal(rtn.answer(Q.PATIENT_MILEAGE, period).response())
                    .add(new BigDecimal(rtn.answer(Q.PATIENT_AND_VISITOR_MILEAGE, period).response()))
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).response(patientVisitorCo2e);


            BigDecimal totalStaffMiles = new BigDecimal(rtn.answer(Q.TOTAL_EMPLOYEES, period).response())
                    .multiply(new BigDecimal(rtn.answer(Q.STAFF_COMMUTE_MILES_PP, period).response()));
            getAnswer(period,rtn, Q.STAFF_COMMUTE_MILES_TOTAL).response(totalStaffMiles);

            // Treasury row 53: Staff commute
            getAnswer(period,rtn, Q.STAFF_COMMUTE_MILES_CO2E)
                    .response(totalStaffMiles
                            .multiply(cFactor.value())
                            .multiply(m2km)
                            .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP));

            BigDecimal fleetMileage = new BigDecimal(rtn.answer(Q.FLEET_ROAD_MILES, period).response());
            BigDecimal bizTravelRoadCo2e
                    = new BigDecimal(rtn.answer(Q.PERSONAL_ROAD_MILES, period).response())
                    .add(fleetMileage)
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_ROAD_CO2E).response(bizTravelRoadCo2e);

            cFactor = cFactor(CarbonFactors.NATIONAL_RAIL_TOTAL, period);
            BigDecimal bizTravelRailCo2e
                    = new BigDecimal(rtn.answer(Q.RAIL_MILES, period).response())
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_RAIL_CO2E).response(bizTravelRailCo2e);

            BigDecimal bizTravelDomesticAirCo2e
                    = new BigDecimal(rtn.answer(Q.DOMESTIC_AIR_MILES, period).response())
                    .multiply(cFactor(CarbonFactors.DOMESTIC_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            BigDecimal bizTravelShortHaulAirCo2e
                    = new BigDecimal(rtn.answer(Q.SHORT_HAUL_AIR_MILES, period).response())
                    .multiply(cFactor(CarbonFactors.SHORT_HAUL_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            BigDecimal bizTravelLongHaulAirCo2e
                    = new BigDecimal(rtn.answer(Q.LONG_HAUL_AIR_MILES, period).response())
                    .multiply(cFactor(CarbonFactors.LONG_HAUL_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_AIR_CO2E)
                    .response(bizTravelDomesticAirCo2e.add(bizTravelShortHaulAirCo2e).add(bizTravelLongHaulAirCo2e));

            // Treasury row 51: Owned vehicles  Fuel Well to Tank
            getAnswer(period,rtn, Q.OWNED_FLEET_TRAVEL_CO2E)
                    .response(fleetMileage
                            .multiply(cFactor(CarbonFactors.CAR_WTT_AVERAGE_SIZE, period).value())
                            .multiply(m2km)
                            .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP));

            sumAnswers(period, rtn, Q.SCOPE_3_TRAVEL,
                            Q.BIZ_MILEAGE_CO2E, Q.BIZ_MILEAGE_ROAD_CO2E,
                            Q.BIZ_MILEAGE_RAIL_CO2E, Q.BIZ_MILEAGE_AIR_CO2E,
                            Q.OWNED_FLEET_TRAVEL_CO2E,
                            Q.PATIENT_AND_VISITOR_MILEAGE_CO2E,
                            Q.STAFF_COMMUTE_MILES_CO2E);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from travel");
        }
    }


    private void calcScope1(String period, SurveyReturn rtn) {
        crunchOwnedBuildings(period, rtn);
        CarbonFactor cFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, period);
        crunchCO2e(period, rtn, Q.FLEET_ROAD_MILES, oneThousandth(cFactor), Q.OWNED_VEHICLES);
        crunchAnaestheticGases(period, rtn);

        sumAnswers(period, rtn, Q.SCOPE_1,
                Q.OWNED_BUILDINGS, Q.LEASED_ASSETS_ENERGY_USE,
                Q.OWNED_VEHICLES, Q.ANAESTHETIC_GASES_CO2E);
    }

    private void calcScope2(String period, SurveyReturn rtn) {
        crunchElectricityUsed(period, rtn);
        crunchHeatSteam(period, rtn);

        sumAnswers(period, rtn, Q.SCOPE_2, Q.NET_THERMAL_ENERGY_CO2E, Q.NET_ELEC_CO2E);
    }

    private void calcCarbonProfileSduMethod(String period, SurveyReturn rtn) {
        sumAnswers(period, rtn, Q.WASTE_AND_WATER_CO2E, Q.SCOPE_3_WASTE, Q.SCOPE_3_WATER);

        String orgType = rtn.answer(Q.ORG_TYPE, period).response();
        if (isEmpty(orgType)) {
            String msg = String.format("Cannot model carbon profile of %1$s as no org type specified", rtn.org());
            throw new IllegalStateException(msg);
        }
        Answer nonPaySpendAns = rtn.answer(Q.NON_PAY_SPEND, period);
        BigDecimal nonPaySpend;
        if (nonPaySpendAns == null || nonPaySpendAns.response() == null || nonPaySpendAns.response().equals(BigDecimal.ZERO)) {
          // TODO calc from operating expenditure
            LOGGER.error(String.format("Need to calc non pay spend from op ex"));
            nonPaySpend = calcNonPaySpendFromOpEx(period, rtn);
          return;
        } else {
            nonPaySpend = new BigDecimal(nonPaySpendAns.response());
        }

        WeightingFactor wFactor = wFactor(WeightingFactors.BIZ_SVCS, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.BIZ_SVCS_SPEND, wFactor, Q.BIZ_SVCS_CO2E);
        wFactor = wFactor(WeightingFactors.CONSTRUCTION, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.CONSTRUCTION_SPEND, wFactor, Q.CONSTRUCTION_CO2E);
        wFactor = wFactor(WeightingFactors.CATERING, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.CATERING_SPEND, wFactor, Q.CATERING_CO2E);
        wFactor = wFactor(WeightingFactors.FREIGHT, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.FREIGHT_SPEND, wFactor, Q.FREIGHT_CO2E);
        wFactor = wFactor(WeightingFactors.ICT, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.ICT_SPEND, wFactor, Q.ICT_CO2E);
        wFactor = wFactor(WeightingFactors.FUEL_CHEM_AND_GASES, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.CHEM_AND_GAS_SPEND, wFactor, Q.CHEM_AND_GAS_CO2E);
        wFactor = wFactor(WeightingFactors.MED_INSTRUMENTS, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.MED_INSTR_SPEND, wFactor, Q.MED_INSTR_CO2E);
        wFactor = wFactor(WeightingFactors.OTHER_MANUFACTURING, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.OTHER_MANUFACTURED_SPEND, wFactor, Q.OTHER_MANUFACTURED_CO2E);
        wFactor = wFactor(WeightingFactors.OTHER_PROCURMENT, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.OTHER_SPEND, wFactor, Q.OTHER_PROCUREMENT_CO2E);
        wFactor = wFactor(WeightingFactors.PAPER, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.PAPER_SPEND, wFactor, Q.PAPER_CO2E);
        wFactor = wFactor(WeightingFactors.PHARMA, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.PHARMA_SPEND, wFactor, Q.PHARMA_CO2E);
        wFactor = wFactor(WeightingFactors.BIZ_TRAVEL, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.TRAVEL_SPEND, wFactor, Q.TRAVEL_CO2E);
        wFactor = wFactor(WeightingFactors.COMMISSIONING, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.COMMISSIONING_SPEND, wFactor, Q.COMMISSIONING_CO2E);

        // intentionally omit waste and water and travel here
        sumAnswers(period, rtn, Q.PROCUREMENT_CO2E,
                Q.BIZ_SVCS_CO2E, Q.CONSTRUCTION_CO2E,
                Q.CATERING_CO2E, Q.FREIGHT_CO2E, Q.ICT_CO2E, Q.CHEM_AND_GAS_CO2E,
                Q.MED_INSTR_CO2E, Q.OTHER_MANUFACTURED_CO2E,
                Q.OTHER_PROCUREMENT_CO2E, Q.PAPER_CO2E, Q.PHARMA_CO2E,
                Q.COMMISSIONING_CO2E);
    }

    private BigDecimal calcNonPaySpendFromOpEx(String period, SurveyReturn rtn) {
        String orgType = rtn.answer(Q.ORG_TYPE, period).response();
        String opEx = rtn.answer(Q.OP_EX, period).response();
        if (isEmpty(opEx) || isEmpty(orgType)) {
            throw new IllegalStateException(String.format("Cannot calc non-pay spend from op-ex for %1$s (%2$s) in %3$s. Either op-ex or org type is missing.",
                    rtn.org(), orgType, period));
        }
        WeightingFactor wFactor = wFactor(WeightingFactors.NON_PAY_OP_EX_PORTION, period, orgType);

        return new BigDecimal(opEx).multiply(wFactor.moneyValue());
    }


    private boolean isEmpty(String value) {
        return value == null || "0".equals(value);
    }


    private void calcCarbonProfileEClassMethod(String period, SurveyReturn rtn) {
        // Logic here is that *1000 for thousands of pounds to pounds the
        // /1000 to convert factor from kg to tonnes, i.e. cancel each other out
        CarbonFactor cFactor = cFactor(CarbonFactors.PROVISIONS, period);
        crunchCO2e(period, rtn, Q.PROVISIONS, cFactor.value(),
                Q.PROVISIONS_CO2E);
        cFactor = cFactor(CarbonFactors.STAFF_CLOTHING, period);
        crunchCO2e(period, rtn, Q.STAFF_CLOTHING, cFactor.value(),
                Q.STAFF_CLOTHING_CO2E);
        cFactor = cFactor(CarbonFactors.PATIENTS_CLOTHING_FOOTWEAR, period);
        crunchCO2e(period, rtn, Q.PATIENTS_CLOTHING_AND_FOOTWEAR,
                cFactor.value(), Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E);
        cFactor = cFactor(
                CarbonFactors.PHARMACEUTICALS_BLOOD_PRODUCTS_MEDICAL_GASES,
                period);
        crunchCO2e(period, rtn, Q.PHARMA_BLOOD_PROD_AND_MED_GASES,
                cFactor.value(), Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E);
        cFactor = cFactor(CarbonFactors.DRESSINGS, period);
        crunchCO2e(period, rtn, Q.DRESSINGS, cFactor.value(), Q.DRESSINGS_CO2E);
        cFactor = cFactor(CarbonFactors.MEDICAL_SURGICAL_EQUIPMENT, period);
        crunchCO2e(period, rtn, Q.MEDICAL_AND_SURGICAL_EQUIPT, cFactor.value(),
                Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E);
        cFactor = cFactor(CarbonFactors.PATIENTS_APPLIANCES, period);
        crunchCO2e(period, rtn, Q.PATIENTS_APPLIANCES, cFactor.value(),
                Q.PATIENTS_APPLIANCES_CO2E);
        cFactor = cFactor(CarbonFactors.CHEMICALS_REAGENTS, period);
        crunchCO2e(period, rtn, Q.CHEMICALS_AND_REAGENTS, cFactor.value(),
                Q.CHEMICALS_AND_REAGENTS_CO2E);
        cFactor = cFactor(CarbonFactors.DENTAL_OPTICAL_EQUIPMENT, period);
        crunchCO2e(period, rtn, Q.DENTAL_AND_OPTICAL_EQUIPT, cFactor.value(),
                Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E);
        cFactor = cFactor(
                CarbonFactors.DIAGNOSTIC_IMAGING_RADIOTHERAPY_EQUIPMENT_SERVICES,
                period);
        crunchCO2e(period, rtn, Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS,
                cFactor.value(),
                Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E);
        cFactor = cFactor(CarbonFactors.LABORATORY_EQUIPMENT_SERVICES, period);
        crunchCO2e(period, rtn, Q.LAB_EQUIPT_AND_SVCS, cFactor.value(),
                Q.LAB_EQUIPT_AND_SVCS_CO2E);
        // cFactor = cFactor(CarbonFactors.FUEL_LIGHT_POWER_WATER, period);
        // crunchCO2e(period, rtn, Q.FUEL_LIGHT_POWER_WATER,
        // cFactor.value(), Q.FUEL_LIGHT_POWER_WATER_CO2E);
        cFactor = cFactor(
                CarbonFactors.HOTEL_SERVICES_EQUIPMENT_MATERIALS_SERVICES,
                period);
        crunchCO2e(period, rtn, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS,
                cFactor.value(), Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E);
        cFactor = cFactor(CarbonFactors.BUILDING_ENGINEERING_PRODUCTS_SERVICES,
                period);
        crunchCO2e(period, rtn, Q.BLDG_AND_ENG_PROD_AND_SVCS, cFactor.value(),
                Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E);
        cFactor = cFactor(CarbonFactors.PURCHASED_HEALTHCARE, period);
        crunchCO2e(period, rtn, Q.PURCHASED_HEALTHCARE, cFactor.value(),
                Q.PURCHASED_HEALTHCARE_CO2E);
        cFactor = cFactor(CarbonFactors.GARDENING_FARMING, period);
        crunchCO2e(period, rtn, Q.GARDENING_AND_FARMING, cFactor.value(),
                Q.GARDENING_AND_FARMING_CO2E);
        cFactor = cFactor(CarbonFactors.FURNITURE_FITTINGS, period);
        crunchCO2e(period, rtn, Q.FURNITURE_FITTINGS, cFactor.value(),
                Q.FURNITURE_FITTINGS_CO2E);
        cFactor = cFactor(CarbonFactors.HARDWARE_CROCKERY, period);
        crunchCO2e(period, rtn, Q.HARDWARE_CROCKERY, cFactor.value(),
                Q.HARDWARE_CROCKERY_CO2E);
        cFactor = cFactor(CarbonFactors.BEDDING_LINEN_TEXTILES, period);
        crunchCO2e(period, rtn, Q.BEDDING_LINEN_AND_TEXTILES, cFactor.value(),
                Q.BEDDING_LINEN_AND_TEXTILES_CO2E);
        cFactor = cFactor(
                CarbonFactors.OFFICE_EQUIPMENT_TELECOMMS_COMPUTERS_STATIONERY,
                period);
        crunchCO2e(period, rtn, Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY,
                cFactor.value(),
                Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E);
        // cFactor = cFactor(CarbonFactors.TRANSPORTATION, period);
        // crunchCO2e(period, rtn, Q.TRANSPORTATION, cFactor.value(),
        // Q.TRANSPORTATION_CO2E);
        cFactor = cFactor(CarbonFactors.RECREATIONAL_EQUIPMENT_SOUVENIRS,
                period);
        crunchCO2e(period, rtn, Q.REC_EQUIPT_AND_SOUVENIRS, cFactor.value(),
                Q.REC_EQUIPT_AND_SOUVENIRS_CO2E);
        cFactor = cFactor(
                CarbonFactors.STAFF_PATIENT_CONSULTING_SERVICES_EXPENSES,
                period);
        crunchCO2e(period, rtn, Q.CONSULTING_SVCS_AND_EXPENSES, cFactor.value(),
                Q.CONSULTING_SVCS_AND_EXPENSES_CO2E);
    }

    private void calcTrendOverTime(String period, SurveyReturn rtn) {
        sumAnswers(period, rtn, Q.CORE_CO2E,
                /* All energy */
                Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL,
                Q.NET_ELEC_CO2E, Q.NET_THERMAL_ENERGY_CO2E,
                Q.SCOPE_3_BIOMASS, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E,
                /* water and waste */
                Q.WASTE_AND_WATER_CO2E, Q.ANAESTHETIC_GASES_CO2E,
                /* all travel excepting individual citizens (patients, visitors and staff) */
                Q.BIZ_MILEAGE_CO2E, Q.BIZ_MILEAGE_ROAD_CO2E,
                Q.BIZ_MILEAGE_RAIL_CO2E, Q.BIZ_MILEAGE_AIR_CO2E,
                Q.OWNED_FLEET_TRAVEL_CO2E);

        sumAnswers(period, rtn, Q.CITIZEN_CO2E,
                Q.STAFF_COMMUTE_MILES_CO2E, Q.PATIENT_AND_VISITOR_MILEAGE_CO2E);

    }

    private void crunchHeatSteam(String period, SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
        crunchCO2e(period, rtn, Q.STEAM_USED, oneThousandth(cFactor), Q.STEAM_CO2E);
        cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
        crunchCO2e(period, rtn, Q.HOT_WATER_USED, oneThousandth(cFactor), Q.HOT_WATER_CO2E);
        cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
        crunchCO2e(period, rtn, Q.EXPORTED_THERMAL_ENERGY, oneThousandth(cFactor), Q.EXPORTED_THERMAL_ENERGY_CO2E);

        BigDecimal netHeatSteamVal = new BigDecimal("0.000");
        netHeatSteamVal = netHeatSteamVal
                .add(new BigDecimal(rtn.answer(Q.STEAM_CO2E, period).response()))
                .add(new BigDecimal(rtn.answer(Q.HOT_WATER_CO2E, period).response()))
                .subtract(new BigDecimal(rtn.answer(Q.EXPORTED_THERMAL_ENERGY_CO2E, period).response()));
        getAnswer(period,rtn, Q.NET_THERMAL_ENERGY_CO2E).response(netHeatSteamVal.toPlainString());
    }


    private BigDecimal oneThousandth(CarbonFactor cFactor) {
        return cFactor.value().divide(new BigDecimal("1000"), cFactor.value().scale(), RoundingMode.HALF_UP);
    }


    private void crunchElectricityUsed(String period, SurveyReturn rtn) {
        try {
            BigDecimal nonRenewableElecUsed = new BigDecimal(rtn.answer(Q.ELEC_USED, period).response());
            BigDecimal greenTariffUsed = new BigDecimal(rtn.answer(Q.ELEC_USED_GREEN_TARIFF, period).response()).multiply(
                    new BigDecimal("1.000000").subtract(new BigDecimal(rtn.answer(Q.GREEN_TARIFF_ADDITIONAL_PCT, period).response())));
            BigDecimal thirdPtyRenewableUsed = new BigDecimal(rtn.answer(Q.ELEC_3RD_PTY_RENEWABLE_USED, period).response()).multiply(
                    new BigDecimal("1.000000").subtract(new BigDecimal(rtn.answer(Q.THIRD_PARTY_ADDITIONAL_PCT, period).response())));

            BigDecimal elecFactor = oneThousandth(cFactor(CarbonFactors.ELECTRICITY_UK, period));
            BigDecimal elecUsed = nonRenewableElecUsed.add(greenTariffUsed).add(thirdPtyRenewableUsed).multiply(elecFactor);
            getAnswer(period,rtn, Q.ELEC_CO2E).response(elecUsed.toPlainString());

            BigDecimal elecExported = new BigDecimal(rtn.answer(Q.ELEC_EXPORTED, period).response()).multiply(oneThousandth(cFactor(CarbonFactors.GAS_FIRED_CHP, period)));
            getAnswer(period,rtn, Q.ELEC_EXPORTED_CO2E).response(elecExported.toPlainString());

            getAnswer(period,rtn, Q.NET_ELEC_CO2E).response(elecUsed.subtract(elecExported).toPlainString());
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from electricity");
        }
    }


    private void crunchOwnedBuildings(String period, SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.NATURAL_GAS, period);
        // TODO why divide 1000?
        crunchCO2e(period, rtn, Q.GAS_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_GAS);

        cFactor = cFactor(CarbonFactors.FUEL_OIL, period);
        crunchCO2e(period, rtn, Q.OIL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_OIL);

        cFactor = cFactor(CarbonFactors.COAL_INDUSTRIAL, period);
        crunchCO2e(period, rtn, Q.COAL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_COAL);
        sumAnswers(period, rtn,Q.OWNED_BUILDINGS, Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL);
    }

    private void crunchAnaestheticGases(String period, SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.DESFLURANE, period);
        crunchCO2e(period, rtn, Q.DESFLURANE, cFactor.value(), Q.DESFLURANE_CO2E);
        cFactor = cFactor(CarbonFactors.ISOFLURANE, period);
        crunchCO2e(period, rtn, Q.ISOFLURANE, cFactor.value(), Q.ISOFLURANE_CO2E);
        cFactor = cFactor(CarbonFactors.SEVOFLURANE, period);
        crunchCO2e(period, rtn, Q.SEVOFLURANE, cFactor.value(), Q.SEVOFLURANE_CO2E);
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE, period);
        crunchCO2e(period, rtn, Q.NITROUS_OXIDE, cFactor.value(), Q.NITROUS_OXIDE_CO2E);
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT, period);
        crunchCO2e(period, rtn, Q.PORTABLE_NITROUS_OXIDE_MIX, cFactor.value(), Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E);
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT, period);
        crunchCO2e(period, rtn, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY, cFactor.value(), Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E);
        sumAnswers(period, rtn, Q.ANAESTHETIC_GASES_CO2E, Q.DESFLURANE_CO2E, Q.ISOFLURANE_CO2E, Q.SEVOFLURANE_CO2E, Q.NITROUS_OXIDE_CO2E, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E);
    }


    private Answer sumAnswers(String period, SurveyReturn rtn, Q trgtQName,
            Q... srcQs) {
        BigDecimal calcVal = new BigDecimal("0.000");
        for (Q src : srcQs) {
            try {
                calcVal = calcVal.add(new BigDecimal(rtn.answer(src, period).response()));
            } catch (NullPointerException e) {
                LOGGER.warn("Insufficient data to calculate CO2e from {}", src);
            }
        }
        return getAnswer(period, rtn, trgtQName).response(calcVal.toPlainString());
    }


    private Answer crunchCO2e(String period, SurveyReturn rtn, Q srcQ,
            BigDecimal cFactor, Q trgtQ) {
        BigDecimal calcVal = new BigDecimal("0.00");
        try {
            calcVal = new BigDecimal(rtn.answer(srcQ, period).response())
                    .multiply(cFactor);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from {}", srcQ);
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot calculate CO2e from {}", srcQ);
        }
        return getAnswer(period, rtn, trgtQ).response(calcVal.toPlainString());
    }

    CarbonFactor cFactor(CarbonFactors cfName, String period) {
        for (CarbonFactor cfactor : cfactors) {
            if (cfName.name().equals(cfactor.name()) && period.equals(cfactor.applicablePeriod())) {
                return cfactor;
            }
        }
        LOGGER.error("Unable to find Carbon Factor {} for period {}", cfName, period);
        throw new SReportObjectNotFoundException(CarbonFactor.class, cfName);
    }

    private Answer crunchWeighting(String period, SurveyReturn rtn, BigDecimal nonPaySpend,
            Q srcQ, WeightingFactor wFactor, Q trgtQ) {
        BigDecimal calcVal = new BigDecimal("0.00");
        try {
            String spend = rtn.answer(srcQ, period).response();
            if (isEmpty(spend)) {
                calcVal = nonPaySpend.multiply(wFactor.proportionOfTotal());
            } else {
                calcVal = new BigDecimal(spend);
            }
            calcVal = calcVal.multiply(wFactor.intensityValue());
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to estimate CO2e from spend");
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot estimate CO2e from spend");
        }
        return getAnswer(period, rtn, trgtQ).response(calcVal.toPlainString());
    }

    private WeightingFactor wFactor(WeightingFactors wName, String period, String orgType) {
        switch (orgType.toLowerCase()) {
        case "acute":
        case "acute - small":
        case "acute - medium":
        case "acute - large":
        case "acute - teaching":
        case "acute - specialist":
        case "acute - multi-service":
            orgType = "Acute";
            break;
        case "ambulance":
        case "ambulance trust":
            orgType = "Ambulance";
            break;
        case "clinical commissioning group":
            orgType = "Ccg";
            break;
        case "community":
            orgType = "Community";
            break;
        case "mental health and learning disability":
            break;
        case "independent sector":
            break;
        default:
            LOGGER.warn("Unhandled org type when looking up weighting factor {}: {}", wName, orgType);
        }

        for (WeightingFactor wfactor : wfactors) {
            if (wName.name().equals(wfactor.category())
                    && period.equals(wfactor.applicablePeriod())
                    && orgType.equals(wfactor.orgType())) {
                return wfactor;
            }
        }
        LOGGER.warn("Unable to find exact Weighting Factor, now looking for {} and org type {} alone",
                wName, orgType);
        for (WeightingFactor wfactor : wfactors) {
            if (wName.name().equals(wfactor.category())
                    && orgType.equals(wfactor.orgType())) {
                return wfactor;
            }
        }
        LOGGER.error("Unable to find Weighting Factor {} for period {} and org type {}",
                wName, period, orgType);
        throw new SReportObjectNotFoundException(WeightingFactor.class, wName);
    }
}
