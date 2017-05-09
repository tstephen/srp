package digital.srp.sreport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digital.srp.sreport.internal.SReportObjectNotFoundException;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
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
    
    public Cruncher(final List<CarbonFactor> cfactors2,
            final List<WeightingFactor> wfactors2) {
        this.cfactors = cfactors2;
        this.wfactors = wfactors2;
    }


    public SurveyReturn calculate(SurveyReturn rtn) {
        calcScope1(rtn);
        calcScope2(rtn);
        
        crunchScope3(rtn);
        
        // TODO Outside scopes -Breakdown - not included in carbon emissions totals
        
        calcScope3SduMethod(rtn);
        calcScope3EClassMethod(rtn);
        
        return rtn;
    }


    private void crunchScope3(SurveyReturn rtn) {
        crunchScope3Travel(rtn);
        crunchScope3Water(rtn);
        crunchScope3Waste(rtn);
        
        // TODO treasury row 68: Capital Spend
        
        crunchScope3BiomassWtt(rtn);
        crunchScope3Biomass(rtn);
        
        rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3,
                Q.SCOPE_3_TRAVEL, Q.SCOPE_3_WATER, Q.SCOPE_3_WASTE, Q.CAPITAL_CO2E, Q.SCOPE_3_BIOMASS));
    }

    private void crunchScope3Water(SurveyReturn rtn) {
        try {
            // Treasury row 57: Water Use
            CarbonFactor cFactor = cFactor(CarbonFactors.WATER_SUPPLY, rtn.applicablePeriod());
            BigDecimal waterUseCo2e = new BigDecimal(rtn.answer(Q.WATER_VOL, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WATER_CO2E)
                    .response(waterUseCo2e));
    
            // Treasury row 58: Water Treatment
            cFactor = cFactor(CarbonFactors.WATER_TREATMENT, rtn.applicablePeriod());
            BigDecimal waterTreatmentCo2e = new BigDecimal(rtn.answer(Q.WASTE_WATER, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WATER_TREATMENT_CO2E)
                    .response(waterTreatmentCo2e));
            
            rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3_WATER,
                    Q.WATER_CO2E, Q.WATER_TREATMENT_CO2E));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from water");
        }
    }

    private void crunchScope3Waste(SurveyReturn rtn) {
        try {
            // Treasury row 62: Waste Recycling
            CarbonFactor cFactor = cFactor(CarbonFactors.CLOSED_LOOP_OR_OPEN_LOOP, rtn.applicablePeriod());
            BigDecimal recyclingCo2e = new BigDecimal(rtn.answer(Q.RECYCLING_WEIGHT, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.RECYCLING_CO2E)
                    .response(recyclingCo2e));
    
            // Treasury row 63: Other Recovery
            cFactor = cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE_WITH_ENERGY_RECOVERY, rtn.applicablePeriod());
            BigDecimal recoveryCo2e = new BigDecimal(rtn.answer(Q.OTHER_RECOVERY_WEIGHT, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn, Q.OTHER_RECOVERY_CO2E).response(recoveryCo2e));
            
            // Treasury row 64: Incineration waste
            cFactor = cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE, rtn.applicablePeriod());
            BigDecimal incinerationCo2e = new BigDecimal(rtn.answer(Q.INCINERATION_WEIGHT, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.INCINERATION_CO2E)
                    .response(incinerationCo2e));
    
            // Treasury row 65: Landfill disposal waste
            cFactor = cFactor(CarbonFactors.NON_BURN_TREATMENT_DISPOSAL_WASTE, rtn.applicablePeriod());
            BigDecimal landfillCo2e = new BigDecimal(rtn.answer(Q.LANDFILL_WEIGHT, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.LANDFILL_CO2E)
                    .response(landfillCo2e));
            
            rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3_WASTE,
                    Q.RECYCLING_CO2E, Q.OTHER_RECOVERY_CO2E, Q.INCINERATION_CO2E, Q.LANDFILL_CO2E));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from waste");
        }
    }


    private Answer getAnswer(SurveyReturn rtn, Q q) {
        if (answerRepo == null) { // unit test calcs not persistence
            return new Answer().addSurveyReturn(rtn).question(q);
        } else { 
            Answer answer = answerRepo.findByOrgPeriodAndQuestion(rtn.org(), rtn.applicablePeriod(), q.name());
            if (answer==null) {
                Question existingQ = qRepo.findByName(q.name());
                if (existingQ == null) {
                    existingQ = qRepo.save(new Question().q(q));
                }
                answer = answerRepo.save(new Answer()
                        .addSurveyReturn(rtn)
                        .question(existingQ)
                        .applicablePeriod(rtn.applicablePeriod())
                        .status(StatusType.Draft.name()));
            }
            return answer;
        }
    }
    private void crunchScope3BiomassWtt(SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_WTT, rtn.applicablePeriod());
            BigDecimal logsCo2e = new BigDecimal(rtn.answer(Q.WOOD_LOGS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_LOGS_WTT_CO2E).response(logsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_LOGS_WTT_CO2E).response(BigDecimal.ZERO));
        }
        
        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_WTT, rtn.applicablePeriod());
            BigDecimal chipsCo2e = new BigDecimal(rtn.answer(Q.WOOD_CHIPS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_CHIPS_WTT_CO2E)
                    .response(chipsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_CHIPS_WTT_CO2E).response(BigDecimal.ZERO));
        }
        
        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_WTT, rtn.applicablePeriod());
            BigDecimal pelletsCo2e = new BigDecimal(rtn.answer(Q.WOOD_PELLETS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_PELLETS_WTT_CO2E)
                    .response(pelletsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_PELLETS_WTT_CO2E).response(BigDecimal.ZERO));
        }

        rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3_BIOMASS_WTT,
                Q.WOOD_LOGS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_PELLETS_WTT_CO2E));
    }
    
    private void crunchScope3Biomass(SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_TOTAL, rtn.applicablePeriod());
            BigDecimal logsCo2e = new BigDecimal(rtn.answer(Q.WOOD_LOGS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_LOGS_CO2E).response(logsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_LOGS_CO2E).response(BigDecimal.ZERO));
        }
        
        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_TOTAL, rtn.applicablePeriod());
            BigDecimal chipsCo2e = new BigDecimal(rtn.answer(Q.WOOD_CHIPS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_CHIPS_CO2E)
                    .response(chipsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_CHIPS_CO2E).response(BigDecimal.ZERO));
        }
        
        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_TOTAL, rtn.applicablePeriod());
            BigDecimal pelletsCo2e = new BigDecimal(rtn.answer(Q.WOOD_PELLETS_USED, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.WOOD_PELLETS_CO2E)
                    .response(pelletsCo2e));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            rtn.answers().add(getAnswer(rtn,Q.WOOD_PELLETS_CO2E).response(BigDecimal.ZERO));
        }

        rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3_BIOMASS,
                Q.WOOD_LOGS_CO2E, Q.WOOD_CHIPS_CO2E, Q.WOOD_PELLETS_CO2E));
    }
    
    private void crunchScope3Travel(SurveyReturn rtn) {
        try {
            CarbonFactor cFactor = cFactor(CarbonFactors.CAR_TOTAL, rtn.applicablePeriod());
            // TODO this is a derived figure, why including alongside the 'raw' ones?
            new BigDecimal(rtn.answer(Q.BIZ_MILEAGE_CO2E, rtn.applicablePeriod()).response());
            
            // Treasury row 52: Patient and Visitor
            BigDecimal patientVisitorCo2e = new BigDecimal(rtn.answer(Q.PATIENT_MILEAGE, rtn.applicablePeriod()).response())
                    .add(new BigDecimal(rtn.answer(Q.PATIENT_AND_VISITOR_MILEAGE, rtn.applicablePeriod()).response()))
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).response(patientVisitorCo2e));
            
            
            BigDecimal totalStaffMiles = new BigDecimal(rtn.answer(Q.TOTAL_EMPLOYEES, rtn.applicablePeriod()).response())
                    .multiply(new BigDecimal(rtn.answer(Q.STAFF_COMMUTE_MILES_PP, rtn.applicablePeriod()).response()));
            rtn.answers().add(getAnswer(rtn,Q.STAFF_COMMUTE_MILES_TOTAL)
                    .response(totalStaffMiles));
            
            // Treasury row 53: Staff commute
            rtn.answers().add(getAnswer(rtn,Q.STAFF_COMMUTE_MILES_CO2E)
                    .response(totalStaffMiles
                            .multiply(cFactor.value())
                            .multiply(m2km)
                            .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP)));
    
            BigDecimal fleetMileage = new BigDecimal(rtn.answer(Q.FLEET_ROAD_MILES, rtn.applicablePeriod()).response());
            BigDecimal bizTravelRoadCo2e 
                    = new BigDecimal(rtn.answer(Q.PERSONAL_ROAD_MILES, rtn.applicablePeriod()).response())
                    .add(fleetMileage)
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.BIZ_MILEAGE_ROAD_CO2E).response(bizTravelRoadCo2e));
            
            cFactor = cFactor(CarbonFactors.NATIONAL_RAIL_TOTAL, rtn.applicablePeriod());
            BigDecimal bizTravelRailCo2e 
                    = new BigDecimal(rtn.answer(Q.RAIL_MILES, rtn.applicablePeriod()).response())
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.BIZ_MILEAGE_RAIL_CO2E).response(bizTravelRailCo2e));        
            
            BigDecimal bizTravelDomesticAirCo2e
                    = new BigDecimal(rtn.answer(Q.DOMESTIC_AIR_MILES, rtn.applicablePeriod()).response())
                    .multiply(cFactor(CarbonFactors.DOMESTIC_TOTAL, rtn.applicablePeriod()).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            BigDecimal bizTravelShortHaulAirCo2e
                    = new BigDecimal(rtn.answer(Q.SHORT_HAUL_AIR_MILES, rtn.applicablePeriod()).response())
                    .multiply(cFactor(CarbonFactors.SHORT_HAUL_TOTAL, rtn.applicablePeriod()).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            BigDecimal bizTravelLongHaulAirCo2e
                    = new BigDecimal(rtn.answer(Q.LONG_HAUL_AIR_MILES, rtn.applicablePeriod()).response())
                    .multiply(cFactor(CarbonFactors.LONG_HAUL_TOTAL, rtn.applicablePeriod()).value())
                    .multiply(m2km)
                    .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP);
            rtn.answers().add(getAnswer(rtn,Q.BIZ_MILEAGE_AIR_CO2E)
                    .response(bizTravelDomesticAirCo2e.add(bizTravelShortHaulAirCo2e).add(bizTravelLongHaulAirCo2e)));
            
            // Treasury row 51: Owned vehicles  Fuel Well to Tank
            rtn.answers().add(getAnswer(rtn,Q.OWNED_FLEET_TRAVEL_CO2E)
                    .response(fleetMileage
                            .multiply(cFactor(CarbonFactors.CAR_WTT_AVERAGE_SIZE, rtn.applicablePeriod()).value())
                            .multiply(m2km)
                            .divide(new BigDecimal("1000"), 0, RoundingMode.HALF_UP)));
         
            rtn.answers().add(sumAnswers(rtn, Q.SCOPE_3_TRAVEL,
                            Q.BIZ_MILEAGE_CO2E, Q.BIZ_MILEAGE_ROAD_CO2E,
                            Q.BIZ_MILEAGE_RAIL_CO2E, Q.BIZ_MILEAGE_AIR_CO2E,
                            Q.OWNED_FLEET_TRAVEL_CO2E,
                            Q.PATIENT_AND_VISITOR_MILEAGE_CO2E,
                            Q.STAFF_COMMUTE_MILES_CO2E));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from travel");
        }
    }


    private void calcScope1(SurveyReturn rtn) {
        crunchOwnedBuildings(rtn);
        CarbonFactor cFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.FLEET_ROAD_MILES, oneThousandth(cFactor), Q.OWNED_VEHICLES)); 
        crunchAnaestheticGases(rtn);
        
        rtn.answers().add(sumAnswers(rtn, Q.SCOPE_1, SCOPE_1_HDRS));        
    }

    private void calcScope2(SurveyReturn rtn) {
        crunchElectricityUsed(rtn);
        crunchHeatSteam(rtn);
        
        rtn.answers().add(sumAnswers(rtn, Q.SCOPE_2, Q.NET_THERMAL_ENERGY_CO2E, Q.NET_ELEC_CO2E));        
    }

    private void calcScope3SduMethod(SurveyReturn rtn) {
        
//    TODO
    }

    private void calcScope3EClassMethod(SurveyReturn rtn) {
        
//    TODO        
  }
    
    private void crunchHeatSteam(SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.STEAM_USED, oneThousandth(cFactor), Q.STEAM_CO2E)); 
         cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.HOT_WATER_USED, oneThousandth(cFactor), Q.HOT_WATER_CO2E)); 
        cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.EXPORTED_THERMAL_ENERGY, oneThousandth(cFactor), Q.EXPORTED_THERMAL_ENERGY_CO2E));
        
        BigDecimal netHeatSteamVal = new BigDecimal("0.000");
        netHeatSteamVal = netHeatSteamVal
                .add(new BigDecimal(rtn.answer(Q.STEAM_CO2E, rtn.applicablePeriod()).response()))
                .add(new BigDecimal(rtn.answer(Q.HOT_WATER_CO2E, rtn.applicablePeriod()).response()))
                .subtract(new BigDecimal(rtn.answer(Q.EXPORTED_THERMAL_ENERGY_CO2E, rtn.applicablePeriod()).response()));
        rtn.answers().add(getAnswer(rtn,Q.NET_THERMAL_ENERGY_CO2E).response(netHeatSteamVal.toPlainString()));
    }


    private BigDecimal oneThousandth(CarbonFactor cFactor) {
        return cFactor.value().divide(new BigDecimal("1000"), cFactor.value().scale(), RoundingMode.HALF_UP);
    }


    private void crunchElectricityUsed(SurveyReturn rtn) {
        try {
            BigDecimal nonRenewableElecUsed = new BigDecimal(rtn.answer(Q.ELEC_USED, rtn.applicablePeriod()).response());
            BigDecimal greenTariffUsed = new BigDecimal(rtn.answer(Q.ELEC_USED_GREEN_TARIFF, rtn.applicablePeriod()).response()).multiply(
                    new BigDecimal("1.000000").subtract(new BigDecimal(rtn.answer(Q.GREEN_TARIFF_ADDITIONAL_PCT, rtn.applicablePeriod()).response())));
            BigDecimal thirdPtyRenewableUsed = new BigDecimal(rtn.answer(Q.ELEC_3RD_PTY_RENEWABLE_USED, rtn.applicablePeriod()).response()).multiply(
                    new BigDecimal("1.000000").subtract(new BigDecimal(rtn.answer(Q.THIRD_PARTY_ADDITIONAL_PCT, rtn.applicablePeriod()).response())));
            
            BigDecimal elecFactor = oneThousandth(cFactor(CarbonFactors.ELECTRICITY_UK, rtn.applicablePeriod()));
            BigDecimal elecUsed = nonRenewableElecUsed.add(greenTariffUsed).add(thirdPtyRenewableUsed).multiply(elecFactor);
            rtn.answers().add(getAnswer(rtn,Q.ELEC_USED_CO2E).response(elecUsed.toPlainString()));
            
            BigDecimal elecExported = new BigDecimal(rtn.answer(Q.ELEC_EXPORTED, rtn.applicablePeriod()).response()).multiply(oneThousandth(cFactor(CarbonFactors.GAS_FIRED_CHP, rtn.applicablePeriod())));
            rtn.answers().add(getAnswer(rtn,Q.ELEC_EXPORTED_CO2E).response(elecExported.toPlainString()));
            
            rtn.answers().add(getAnswer(rtn,Q.NET_ELEC_CO2E).response(elecUsed.subtract(elecExported).toPlainString()));
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from electricity");
        }
    }


    private void crunchOwnedBuildings(SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.NATURAL_GAS, rtn.applicablePeriod());
        // TODO why divide 1000?
        rtn.answers().add(crunchCO2e(rtn, Q.GAS_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_GAS));
        
        cFactor = cFactor(CarbonFactors.FUEL_OIL, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.OIL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_OIL));

        cFactor = cFactor(CarbonFactors.COAL_INDUSTRIAL, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.COAL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_COAL));
        rtn.answers().add(sumAnswers(rtn,Q.OWNED_BUILDINGS, Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL));
    }

    private void crunchAnaestheticGases(SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.DESFLURANE, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.DESFLURANE, cFactor.value(), Q.DESFLURANE_CO2E));  
        cFactor = cFactor(CarbonFactors.ISOFLURANE, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.ISOFLURANE, cFactor.value(), Q.ISOFLURANE_CO2E)); 
        cFactor = cFactor(CarbonFactors.SEVOFLURANE, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.SEVOFLURANE, cFactor.value(), Q.SEVOFLURANE_CO2E)); 
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.NITROUS_OXIDE, cFactor.value(), Q.NITROUS_OXIDE_CO2E)); 
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.PORTABLE_NITROUS_OXIDE_MIX, cFactor.value(), Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E)); 
        cFactor = cFactor(CarbonFactors.NITROUS_OXIDE_WITH_OXYGEN_50_50_SPLIT, rtn.applicablePeriod());
        rtn.answers().add(crunchCO2e(rtn, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY, cFactor.value(), Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E)); 
        rtn.answers().add(sumAnswers(rtn, Q.ANAESTHETIC_GASES_CO2E, Q.DESFLURANE_CO2E, Q.ISOFLURANE_CO2E, Q.SEVOFLURANE_CO2E, Q.NITROUS_OXIDE_CO2E, Q.PORTABLE_NITROUS_OXIDE_MIX_CO2E, Q.PORTABLE_NITROUS_OXIDE_MIX_MATERNITY_CO2E));
    }


    private Answer sumAnswers(SurveyReturn rtn, Q trgtQName,
            Q... srcQs) {
        BigDecimal calcVal = new BigDecimal("0.000");
        for (Q src : srcQs) {
            try {
                calcVal = calcVal.add(new BigDecimal(rtn.answer(src, rtn.applicablePeriod()).response()));
            } catch (NullPointerException e) {
                LOGGER.warn("Insufficient data to calculate CO2e from %1$s", src);
            }
        }
        return getAnswer(rtn, trgtQName).response(calcVal.toPlainString());
    }


    private Answer crunchCO2e(SurveyReturn rtn, Q srcQ,
            BigDecimal cFactor, Q trgtQ) {
        BigDecimal calcVal = new BigDecimal("0.00");
        try {
            calcVal = new BigDecimal(rtn.answer(srcQ, rtn.applicablePeriod()).response())
                    .multiply(cFactor);
        } catch (NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from %1$s", srcQ);
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot calculate CO2e from %1$s", srcQ);
        }
        return getAnswer(rtn, trgtQ).response(calcVal.toPlainString());
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

}
