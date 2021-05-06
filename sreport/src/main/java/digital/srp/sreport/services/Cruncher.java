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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digital.srp.macc.model.OrganisationType;
import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.model.CarbonFactors;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.WeightingFactor;
import digital.srp.sreport.model.WeightingFactors;
import digital.srp.sreport.model.surveys.SduQuestions;

@Component
public class Cruncher extends AbstractEmissionsService
        implements digital.srp.sreport.model.surveys.SduQuestions, Calculator {

    static final Logger LOGGER = LoggerFactory
            .getLogger(Cruncher.class);

    protected static final BigDecimal BUS_PER_MILE = new BigDecimal("0.1200");
    protected static final BigDecimal RAIL_PER_MILE = new BigDecimal("0.3000");
    protected static final BigDecimal TAXI_PER_MILE = new BigDecimal("2.8300");

    protected List<CarbonFactor> cfactors;

    protected List<WeightingFactor> wfactors;

    @Autowired
    protected HealthChecker healthChecker;

    @Autowired
    protected EnergyEmissionsService energyEmissionsService;

    @Autowired
    protected RoadEmissionsService roadEmissionsService;

    @Autowired
    protected WasteEmissionsService wasteEmissionsService;

    public Cruncher() {
    }

    protected Cruncher(final List<CarbonFactor> cfactors2,
            final List<WeightingFactor> wfactors2) {
        this();
        init(cfactors2, wfactors2);
    }

    public Cruncher init(final List<CarbonFactor> cfactors2,
            final List<WeightingFactor> wfactors2) {
        this.cfactors = cfactors2;
        this.wfactors = wfactors2;
        return this;
    }

    /**
     * @deprecated Use {@link digital.srp.sreport.services.Cruncher#calculate(SurveyReturn, int)} instead
     */
    public SurveyReturn calculate(SurveyReturn rtn, int yearsToCalc, AnswerFactory answerFactory) {
        return calculate(rtn, yearsToCalc);
    }

    @Override
    public synchronized SurveyReturn calculate(SurveyReturn rtn, int yearsToCalc) {
        long start = System.currentTimeMillis();
        healthChecker.ensureInitialised(yearsToCalc, rtn.survey().name(), rtn);
        LOGGER.info("Calculating for {} in {}", rtn.org(), rtn.applicablePeriod());
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(), yearsToCalc);
        for (String period : periods) {
            calcEnergyConsumption(period, rtn);

            calcScope1(period, rtn);
            calcScope2(period, rtn);
            calcScope3(period, rtn);

            calcCarbonProfileSimplifiedEClassMethod(period, rtn);
            calcCarbonProfileSimplifiedSduMethod(period, rtn);

            sumAnswers(period, rtn, Q.SCOPE_1, SduQuestions.SCOPE_1_HDRS);
            sumAnswers(period, rtn, Q.SCOPE_2, SduQuestions.SCOPE_2_HDRS);
            sumAnswers(period, rtn, Q.SCOPE_3, SduQuestions.SCOPE_3_HDRS);
            sumAnswers(period, rtn, Q.SCOPE_ALL, Q.SCOPE_1, Q.SCOPE_2,
                    Q.SCOPE_3);

            crunchEnergyCostChange(period, rtn);

            crunchSocialValue(period, rtn);
            crunchSocialInvestmentRecorded(period, rtn);

            calcBenchmarking(period, rtn);
        }
        rtn.setLastUpdated(new Date());
        LOGGER.warn("Calculations took {}ms", (System.currentTimeMillis() - start));
        return rtn;
    }

    protected void calcEnergyConsumption(String period, SurveyReturn rtn) {
        sumAnswers(period, rtn, Q.TOTAL_ENERGY, SduQuestions.ENERGY_HDRS);
        BigDecimal noStaff = getAnswerForPeriodWithFallback(period, rtn, Q.NO_STAFF).responseAsBigDecimal();
        try {
            getAnswer(period, rtn, Q.TOTAL_ENERGY_BY_WTE).response(
                    getAnswer(period, rtn, Q.TOTAL_ENERGY).responseAsBigDecimal().divide(noStaff, RoundingMode.HALF_UP));
        } catch (ArithmeticException e) {
            LOGGER.warn("Insufficient data to calculate energy by WTE");
        }
    }

    /**
     * @deprecated Use {@link digital.srp.sreport.services.HealthChecker#ensureInitialised(int,String,SurveyReturn)} instead
     */
    protected void ensureInitialised(SurveyReturn rtn, int yearsToCalc, String surveyName, AnswerFactory answerFactory) {
        healthChecker.ensureInitialised(yearsToCalc, surveyName, rtn);
    }

    boolean isEClassUser(SurveyReturn rtn) {
        // Intentional use of rtn period. If EClass user in current year
        // calculate all years on that basis
        Optional<Answer> answer = rtn.answer(rtn.applicablePeriod(), Q.ECLASS_USER);
        if (answer.isPresent()) {
            LOGGER.info("Checking {} to see if {} is E-Class user in {}",
                    answer.get().response(), rtn.org(), rtn.applicablePeriod());
            return Boolean.parseBoolean(answer.get().response())
                    || SduQuestions.ANALYSE_BY_E_CLASS.equals(answer.get().response());
        } else {
            LOGGER.info("{} NOT E-Class user in {}", rtn.org(), rtn.applicablePeriod());
            return false;
        }
    }

    protected void calcBenchmarking(String period, SurveyReturn rtn) {
        Answer totalEnergyCo2eA = sumAnswers(period, rtn, Q.TOTAL_ENERGY_CO2E, Q.OWNED_BUILDINGS,
                Q.NET_ELEC_CO2E, Q.SCOPE_3_BIOMASS_WTT);
        BigDecimal totalEnergyCo2e = totalEnergyCo2eA.responseAsBigDecimal();

        // SCOPE_3_TRAVEL already calculated
        BigDecimal totalTravelCo2e = BigDecimal.ZERO;
        try {
            totalTravelCo2e = getAnswer(period, rtn, Q.SCOPE_3_TRAVEL).responseAsBigDecimal();
        } catch (Exception e) { }

        Answer totalProcurementCo2eA = sumAnswers(period, rtn, Q.TOTAL_PROCUREMENT_CO2E,
                Q.PROCUREMENT_CO2E, Q.ANAESTHETIC_GASES_CO2E,
                Q.SCOPE_3_WASTE, Q.SCOPE_3_WATER, Q.CAPITAL_CO2E);
        BigDecimal totalProcurementCo2e = totalProcurementCo2eA.responseAsBigDecimal();

        // CORE CO2E
        BigDecimal totalCoreCo2e = sumAnswers(period, rtn,
                Q.TOTAL_CORE_CO2E,
                Q.ANAESTHETIC_GASES_CO2E,
                /* water and waste */
                Q.WASTE_AND_WATER_CO2E,
                /* all travel excepting individual citizens (patients, visitors and staff) */
                Q.BIZ_MILEAGE_CO2E,
                /* All energy */
                // TODO this is more complete than totalEnergyCo2e already calculated above????
                Q.LEASED_ASSETS_ENERGY_USE,
                Q.NET_ELEC_CO2E, Q.NET_THERMAL_ENERGY_CO2E,
                Q.OWNED_BUILDINGS_COAL,
                Q.OWNED_BUILDINGS_OIL,
                Q.OWNED_BUILDINGS_GAS,
                Q.OTHER_AOI_CORE).responseAsBigDecimal();

        BigDecimal totalCommissioningCo2e;
        if (isEClassUser(rtn)) {
            totalCommissioningCo2e = sumAnswers(period, rtn,
                    Q.TOTAL_COMMISSIONING_CO2E,
                    Q.PURCHASED_HEALTHCARE_CO2E).responseAsBigDecimal();
        } else {
            totalCommissioningCo2e = sumAnswers(period, rtn,
                    Q.TOTAL_COMMISSIONING_CO2E,
                    Q.COMMISSIONING_CO2E).responseAsBigDecimal();
        }


        BigDecimal totalCommunityCo2e = sumAnswers(period, rtn,
                Q.TOTAL_COMMUNITY_CO2E,
                Q.STAFF_COMMUTE_MILES_CO2E,
                Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).responseAsBigDecimal();

        BigDecimal totalProcurement2017Co2e;
        if (isEClassUser(rtn)) {
            totalProcurement2017Co2e = sumAnswers(period, rtn,
                    Q.TOTAL_PROCUREMENT_2017_CO2E,
                    Q.PROVISIONS_CO2E,
                    Q.STAFF_CLOTHING_CO2E,
                    Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E,
                    Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E,
                    Q.DRESSINGS_CO2E,
                    Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E,
                    Q.PATIENTS_APPLIANCES_CO2E,
                    Q.CHEMICALS_AND_REAGENTS_CO2E,
                    Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E,
                    Q.LAB_EQUIPT_AND_SVCS_CO2E,
                    Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E,
                    Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E,
                    Q.GARDENING_AND_FARMING_CO2E,
                    Q.FURNITURE_FITTINGS_CO2E,
                    Q.HARDWARE_CROCKERY_CO2E,
                    Q.BEDDING_LINEN_AND_TEXTILES_CO2E,
                    Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E,
                    Q.REC_EQUIPT_AND_SOUVENIRS_CO2E,
                    Q.CONSULTING_SVCS_AND_EXPENSES_CO2E,
                    Q.PHARMA_CO2E).responseAsBigDecimal();
        } else {
            totalProcurement2017Co2e = sumAnswers(period, rtn,
                    Q.TOTAL_PROCUREMENT_2017_CO2E,
                    Q.CAPITAL_CO2E,
                    Q.BIZ_SVCS_CO2E,
                    Q.CONSTRUCTION_CO2E,
                    Q.CATERING_CO2E,
                    Q.FREIGHT_CO2E,
                    Q.ICT_CO2E,
                    Q.CHEM_AND_GAS_CO2E,
                    Q.MED_INSTR_CO2E,
                    Q.OTHER_MANUFACTURED_CO2E,
                    /* #203 drop as no Carbon factor Q.OTHER_PROCUREMENT_CO2E */
                    Q.PAPER_AND_CARD_CO2E,
                    Q.PHARMA_CO2E).responseAsBigDecimal();
        }

        Answer totalCo2eA = sumAnswers(period, rtn, Q.TOTAL_CO2E,
                Q.TOTAL_CORE_CO2E,
                Q.TOTAL_COMMUNITY_CO2E, Q.TOTAL_PROCUREMENT_2017_CO2E,
                Q.TOTAL_COMMISSIONING_CO2E);
        BigDecimal totalCo2e = totalCo2eA.responseAsBigDecimal();

        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.POPULATION).responseAsBigDecimal(),
                "population");
        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.FLOOR_AREA).responseAsBigDecimal(),
                "floorArea");
        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.NO_STAFF).responseAsBigDecimal(),
                "noStaff");
        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.OCCUPIED_BEDS).responseAsBigDecimal(),
                "occupiedBeds");
        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.NO_PATIENT_CONTACTS).responseAsBigDecimal(),
                "patientContacts");
        benchmarkPerMetric(period, rtn, totalCo2e, totalCoreCo2e,
                totalCommissioningCo2e, totalProcurementCo2e, totalProcurement2017Co2e,
                totalCommunityCo2e, totalEnergyCo2e, totalTravelCo2e,
                getAnswerForPeriodWithFallback(period, rtn, Q.OP_EX).responseAsBigDecimal(),
                "opex");

        try {
            getAnswer(period, rtn, Q.CORE_CO2E_PCT)
                    .derived(true)
                    .response(totalCoreCo2e.divide(totalCo2e, RoundingMode.HALF_UP).multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate core CO2E percentage for {} in {}", rtn.org(), period);
        }
        try {
            getAnswer(period, rtn, Q.ENERGY_CO2E_PCT)
                    .derived(true)
                    .response(totalEnergyCo2e.divide(totalCo2e, RoundingMode.HALF_UP).multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate energy CO2E percentage for {} in {}", rtn.org(), period);
        }
        try {
            getAnswer(period, rtn, Q.COMMISSIONING_CO2E_PCT)
                    .derived(true)
                    .response(totalCommissioningCo2e.divide(totalCo2e, RoundingMode.HALF_UP)
                                .multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate commissioning CO2E percentage for {} in {}", rtn.org(), period);
        }
        try {
            getAnswer(period, rtn, Q.COMMUNITY_CO2E_PCT)
                    .derived(true)
                    .response(totalCommunityCo2e.divide(totalCo2e, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate community CO2E percentage for {} in {}", rtn.org(), period);
        }
        try {
            getAnswer(period, rtn, Q.PROCUREMENT_CO2E_PCT)
                    .derived(true)
                    .response(totalProcurementCo2e.divide(totalCo2e, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate procurement CO2E percentage for {} in {}", rtn.org(), period);
        }

        try {
            getAnswer(period, rtn, Q.PROCUREMENT_2017_CO2E_PCT)
                    .derived(true)
                    .response(totalProcurement2017Co2e.divide(totalCo2e, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate procurement 2017 CO2E percentage for {} in {}", rtn.org(), period);
        }
        try {
            getAnswer(period, rtn, Q.TRAVEL_CO2E_PCT)
                    .derived(true)
                    .response(totalTravelCo2e.divide(totalCo2e, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.warn("Insufficient data to calculate travel CO2E percentage for {} in {}", rtn.org(), period);
        }
    }

    protected void benchmarkPerMetric(String period, SurveyReturn rtn,
            BigDecimal totalCo2e, BigDecimal totalCoreCo2e,
            BigDecimal totalCommissioningCo2e, BigDecimal totalProcurementCo2e,
            BigDecimal totalProcurement2017Co2e, BigDecimal totalCommunityCo2e,
            BigDecimal totalEnergyCo2e, BigDecimal totalTravelCo2e,
            BigDecimal metric, String metricName) {
        try {
            getAnswer(period, rtn, getTargetQ("TOTAL_CO2E",metricName))
                    .derived(true)
                    .response(totalCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
            getAnswer(period, rtn, getTargetQ("TOTAL_CORE_CO2E",metricName))
                    .derived(true)
                    .response(totalCoreCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
            getAnswer(period, rtn, getTargetQ("TOTAL_COMMUNITY_CO2E",metricName))
                    .derived(true)
                    .response(totalCommunityCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
            getAnswer(period, rtn, getTargetQ("TOTAL_PROCUREMENT_2017_CO2E",metricName))
                    .derived(true)
                    .response(totalProcurement2017Co2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
            getAnswer(period, rtn, getTargetQ("TOTAL_COMMISSIONING_CO2E",metricName))
                    .derived(true)
                    .response(totalCommissioningCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
            getAnswer(period, rtn, getTargetQ("TOTAL_PROCUREMENT_CO2E",metricName))
                    .derived(true)
                    .response(totalProcurementCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
        } catch (ArithmeticException e) {
            LOGGER.warn("Unable to calculate CO2e by {} due to insuffient data. Cause: {}",
                    metricName, e.getMessage());
        } catch (IllegalStateException e) {
            LOGGER.error("Unable to calculate CO2e by {}. Cause: {}",
                    metricName, e.getMessage());
        }
        try {
            getAnswer(period, rtn, getTargetQ("TOTAL_ENERGY_CO2E",metricName))
                    .derived(true)
                    .response(totalEnergyCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.info("Unable to calculate deprecated benchmark {}. Cause: {}",
                    metricName, e.getMessage());
        }
        try {
            getAnswer(period, rtn, getTargetQ("TOTAL_TRAVEL_CO2E",metricName))
                    .derived(true)
                    .response(totalTravelCo2e // change tonnes to kgs
                            .multiply(ONE_THOUSAND)
                            .divide(metric, RoundingMode.HALF_UP));
        } catch (ArithmeticException | IllegalStateException e) {
            LOGGER.info("Unable to calculate deprecated benchmark {}. Cause: {}",
                    metricName, e.getMessage());
        }
    }

    protected Q getTargetQ(String string, String metricName) {
        String suffix;
        switch(metricName) {
        case "floorArea":
            suffix = "_BY_FLOOR";break;
        case "noStaff":
            suffix = "_BY_WTE";break;
        case "occupiedBeds":
            suffix = "_BY_BEDS";break;
        case "opex":
            suffix = "_BY_OPEX";break;
        case "patientContacts":
            suffix = "_BY_PATIENT_CONTACT";break;
        case "population":
            suffix = "_BY_POP";break;
        default:
            suffix = "";
        }
        return Q.valueOf(string+suffix);
    }

    protected void calcScope3(String period, SurveyReturn rtn) {
        // TODO move leased assets here
        crunchScope3Travel(period, rtn);
        crunchScope3Water(period, rtn);
        crunchScope3Waste(period, rtn);
        crunchScope3BiomassWtt(period, rtn);
        crunchScope3Biomass(period, rtn);
        crunchScope3EnergyWtt(period, rtn);
        crunchPaperCo2e(period, rtn);
    }

    protected void crunchScope3Water(String period, SurveyReturn rtn) {
        try {
            // If necessary estimate waste water as 0% of incoming
            BigDecimal wasteWater = getAnswer(period,rtn, Q.WASTE_WATER).responseAsBigDecimal();
            if (wasteWater == null || wasteWater.equals(BigDecimal.ZERO)
                    || getAnswer(period,rtn, Q.WASTE_WATER).derived()) {
                getAnswer(period,rtn, Q.WASTE_WATER)
                        .derived(true)
                        .response(rtn.answerResponseAsBigDecimal(period, Q.WATER_VOL).multiply(new BigDecimal("0.80")));
            }

            // Treasury row 57: Water Use
            CarbonFactor cFactor = cFactor(CarbonFactors.WATER_SUPPLY, period);
            BigDecimal waterUseCo2e = rtn.answerResponseAsBigDecimal(period, Q.WATER_VOL)
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WATER_CO2E).response(waterUseCo2e);
            try {
                BigDecimal noStaff = getAnswerForPeriodWithFallback(period, rtn, Q.NO_STAFF).responseAsBigDecimal();
                getAnswer(period,rtn, Q.WATER_VOL_BY_WTE).derived(true).response(rtn.answerResponseAsBigDecimal(period, Q.WATER_VOL).divide(noStaff));
            } catch (IllegalStateException | NullPointerException | ArithmeticException e) {
                LOGGER.warn("Insufficient data to calculate water use by WTE for {} in {}", rtn.org(), period);
            }

            // Treasury row 58: Water Treatment
            cFactor = cFactor(CarbonFactors.WATER_TREATMENT, period);
            BigDecimal waterTreatmentCo2e = rtn.answerResponseAsBigDecimal(period, Q.WASTE_WATER)
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WATER_TREATMENT_CO2E)
                    .derived(true)
                    .response(waterTreatmentCo2e);

            sumAnswers(period, rtn, Q.SCOPE_3_WATER,
                    Q.WATER_CO2E, Q.WATER_TREATMENT_CO2E);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from water");
        }
    }

    protected void crunchScope3Waste(String period, SurveyReturn rtn) {
        if (rtn.applicablePeriod().startsWith("202")
                && rtn.answer(period, Q.INCINERATION_WEIGHT).isPresent()) {
            wasteEmissionsService.calcIncinerationCo2e(
                    getAnswer(period, rtn, Q.INCINERATION_WEIGHT).responseAsBigDecimal(),
                    cFactor(CarbonFactors.DOMESTIC_WASTE_INCINERATION, period),
                    getAnswer(period, rtn, Q.INCINERATION_CO2E));
        } else if (rtn.answer(period, Q.INCINERATION_WEIGHT).isPresent()) {
            wasteEmissionsService.calcIncinerationCo2e(
                    getAnswer(period, rtn, Q.INCINERATION_WEIGHT).responseAsBigDecimal(),
                    cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE, period),
                    getAnswer(period, rtn, Q.INCINERATION_CO2E));
        }
        wasteEmissionsService.calcRecylingCo2e(
                rtn.answerResponseAsBigDecimal(period, Q.RECYCLING_WEIGHT),
                cFactor(CarbonFactors.CLOSED_LOOP_OR_OPEN_LOOP, period),
                getAnswer(period, rtn, Q.RECYCLING_CO2E));
        if (rtn.answer(period, Q.WASTE_CONFIDENTIAL).isPresent()) {
            wasteEmissionsService.calcRecylingCo2e(
                    getAnswer(period, rtn, Q.WASTE_CONFIDENTIAL).responseAsBigDecimal(),
                    cFactor(CarbonFactors.CLOSED_LOOP_OR_OPEN_LOOP, period),
                    getAnswer(period, rtn, Q.WASTE_CONFIDENTIAL_CO2E));
        }
        // factor changed from weighted avg of industrial and domestic to solely domestic in 2020-21
        wasteEmissionsService.calcLandfillCo2e(
                getAnswer(period, rtn, Q.LANDFILL_WEIGHT).responseAsBigDecimal(),
                cFactor(CarbonFactors.LANDFILL_DOMESTIC_WASTE, period),
                getAnswer(period, rtn, Q.LANDFILL_CO2E));
        if (rtn.answer(period, Q.WASTE_CLINICAL_INCINERATED).isPresent()) {
            wasteEmissionsService.calcIncinerationCo2e(
                    getAnswer(period, rtn, Q.WASTE_CLINICAL_INCINERATED).responseAsBigDecimal(),
                    cFactor(CarbonFactors.CLINICAL_WASTE, period),
                    getAnswer(period, rtn, Q.WASTE_CLINICAL_INCINERATED_CO2E));
        }
        if (rtn.answer(period, Q.WASTE_OFFENSIVE).isPresent()) {
            wasteEmissionsService.calcOffensiveWasteCo2e(
                    getAnswer(period, rtn, Q.WASTE_OFFENSIVE).responseAsBigDecimal(),
                    cFactor(CarbonFactors.CLINICAL_WASTE, period),
                    getAnswer(period, rtn, Q.WASTE_OFFENSIVE_CO2E));
        }
        if (rtn.answer(period, Q.ALT_WASTE_DISPOSAL_WEIGHT).isPresent() && rtn.answer(period, Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E).isPresent()) {
            wasteEmissionsService.calcAltDisposalCo2e(
                    getAnswer(period, rtn, Q.ALT_WASTE_DISPOSAL_WEIGHT).responseAsBigDecimal(),
                    cFactor(CarbonFactors.CLINICAL_WASTE, period),
                    getAnswer(period, rtn, Q.ALT_WASTE_DISPOSAL_WEIGHT_CO2E));
        }
        if (rtn.answer(period, Q.WASTE_FOOD).isPresent()) {
            wasteEmissionsService.calcFoodWasteCo2e(
                    getAnswer(period, rtn, Q.WASTE_FOOD).responseAsBigDecimal(),
                    cFactor(CarbonFactors.ORGANIC_FOOD_AND_DRINK_WASTE, period),
                    getAnswer(period, rtn, Q.WASTE_FOOD_CO2E));
        }
        if (rtn.answer(period, Q.WASTE_TEXTILES).isPresent()) {
            wasteEmissionsService.calcTextilesCo2e(
                    getAnswer(period, rtn, Q.WASTE_TEXTILES).responseAsBigDecimal(),
                    cFactor(CarbonFactors.NON_BURN_TREATMENT_DISPOSAL_WASTE, period),
                    getAnswer(period, rtn, Q.WASTE_TEXTILES_CO2E));
        }
        if (rtn.answer(period, Q.WASTE_PROCESSED_ON_SITE).isPresent()) {
            wasteEmissionsService.calcOnsiteCo2e(
                    getAnswer(period, rtn, Q.WASTE_PROCESSED_ON_SITE).responseAsBigDecimal(),
                    cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period),
                    getAnswer(period, rtn, Q.WASTE_PROCESSED_ON_SITE_CO2E));
        }
        if (rtn.answer(period, Q.WEEE_WEIGHT).isPresent() && rtn.answer(period, Q.WEEE_WEIGHT_CO2E).isPresent()) {
            wasteEmissionsService.calcWeeeCo2e(
                    rtn.answerResponseAsBigDecimal(period, Q.WEEE_WEIGHT),
                    cFactor(CarbonFactors.WEEE_AVERAGE, period),
                    getAnswer(period, rtn, Q.WEEE_WEIGHT_CO2E));
        }
        if (rtn.answer(period, Q.OTHER_RECOVERY_WEIGHT).isPresent() && rtn.answer(period, Q.OTHER_RECOVERY_CO2E).isPresent()) {
            wasteEmissionsService.calcOtherRecoveryCo2e(
                    rtn.answerResponseAsBigDecimal(period, Q.OTHER_RECOVERY_WEIGHT),
                    cFactor(CarbonFactors.HIGH_TEMPERATURE_DISPOSAL_WASTE_WITH_ENERGY_RECOVERY, period),
                    getAnswer(period, rtn, Q.OTHER_RECOVERY_CO2E));
        }
        sumAnswers(period, rtn, Q.SCOPE_3_WASTE, SduQuestions.WASTE_CO2E_HDRS_POST2020);
    }

    protected Answer getAnswerForPeriodWithFallback(String period,
            SurveyReturn rtn, Q q) {
        try {
            Answer answer = getAnswer(period, rtn, q);
            if (answer.response() == null) {
                throw new IllegalStateException();
            }
            return answer;
        } catch (IllegalStateException e) {
            LOGGER.warn("Return does not contain response for {} in {}, use current year as estimate", q, period);
            return getAnswer(rtn.applicablePeriod(), rtn, q);
        }
    }

    @Transactional
    private Answer getAnswer(String period, SurveyReturn rtn, Q q) {
        List<Answer> matches = new ArrayList<Answer>();
        for (Answer a : rtn.answers()) {
            if (q.name().equals(a.question().name()) && period.equals(a.applicablePeriod())) {
                matches.add(a);
            }
        }
        switch (matches.size()) {
        case 0:
            throw new IllegalStateException(
                    String.format("Return does not contain the expected response for %1$s in %2$s", q, period));
        case 1:
            return matches.get(0);
        default:
            Answer a = matches.get(0);
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i < matches.size(); i++) {
                Answer b = matches.get(i);
                if (a.response() == b.response()
                        || (b.response() != null && b.response().equals(a.response()))) {
                    LOGGER.error("Remove duplicate answer: {}={} to {} for {} in {}",
                            b.id(), b.response(), q.name(), rtn.org(), period, sb.toString());
//                    rtn.answers().remove(b);
//                    answerRepo.delete(b.id());
                } else {
                    sb.append(b == null ? "" : b.getId()).append(",");
                }
            }
            if (sb.length() > 0) {
                sb = sb.append(a.id());
                LOGGER.error("Multiple different answers to {} found for {} in {}. Review ids: {}",
                        q.name(), rtn.org(), period, sb.toString());
            }
            return a;
        }
    }

    private void crunchScope3BiomassWtt(String period, SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_WTT, period);
            BigDecimal logsCo2e = getAnswer(period, rtn, Q.WOOD_LOGS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_LOGS_WTT_CO2E).derived(true).response(logsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            getAnswer(period,rtn, Q.WOOD_LOGS_WTT_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_WTT, period);
            BigDecimal chipsCo2e = getAnswer(period, rtn, Q.WOOD_CHIPS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_CHIPS_WTT_CO2E).derived(true).response(chipsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            getAnswer(period,rtn, Q.WOOD_CHIPS_WTT_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_WTT, period);
            BigDecimal pelletsCo2e = getAnswer(period, rtn, Q.WOOD_PELLETS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_PELLETS_WTT_CO2E).derived(true).response(pelletsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            getAnswer(period,rtn, Q.WOOD_PELLETS_WTT_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        try {
            sumAnswers(period, rtn, Q.SCOPE_3_BIOMASS_WTT,
                    Q.WOOD_LOGS_WTT_CO2E, Q.WOOD_CHIPS_WTT_CO2E, Q.WOOD_PELLETS_WTT_CO2E);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from biomass WTT");
        }
    }

    protected void crunchScope3Biomass(String period, SurveyReturn rtn) {
        try {
            // Treasury row 72: Wood logs
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_LOGS_TOTAL, period);
            BigDecimal logsCo2e = getAnswer(period, rtn, Q.WOOD_LOGS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_LOGS_CO2E).derived(true).response(logsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood logs");
            getAnswer(period,rtn, Q.WOOD_LOGS_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 73: Wood chips
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_CHIPS_TOTAL, period);
            BigDecimal chipsCo2e = getAnswer(period, rtn, Q.WOOD_CHIPS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_CHIPS_CO2E).derived(true).response(chipsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood chips");
            getAnswer(period,rtn, Q.WOOD_CHIPS_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        try {
            // Treasury row 74: Wood pellets
            CarbonFactor cFactor = cFactor(CarbonFactors.WOOD_PELLETS_TOTAL, period);
            BigDecimal pelletsCo2e = getAnswer(period, rtn, Q.WOOD_PELLETS_USED).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.WOOD_PELLETS_CO2E).derived(true).response(pelletsCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from wood pellets");
            getAnswer(period,rtn, Q.WOOD_PELLETS_CO2E).derived(true).response(BigDecimal.ZERO);
        }

        sumAnswers(period, rtn, Q.SCOPE_3_BIOMASS,
                Q.WOOD_LOGS_CO2E, Q.WOOD_CHIPS_CO2E, Q.WOOD_PELLETS_CO2E);
    }

    public void crunchScope3EnergyWtt(String period, SurveyReturn rtn) {
        sumAnswers(period, rtn, Q.SCOPE_3_ENERGY_WTT, Q.ELEC_WTT_CO2E);
    }

    protected void crunchScope3Travel(String period, SurveyReturn rtn) {
        crunchPatientVisitorMileageCo2e(period, rtn);
        crunchStaffCommuteCo2e(period, rtn);
        crunchFleetAndBizTravel(period, rtn);

        try {
            sumAnswers(period, rtn, Q.SCOPE_3_TRAVEL,
                    SduQuestions.SCOPE_3_TRAVEL_HDRS);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from travel");
        }
    }

    protected void crunchFleetAndBizTravel(String period, SurveyReturn rtn) {
        crunchRoadCo2e(period, rtn);
        crunchRailCo2e(period, rtn);
        crunchAirCo2e(period, rtn);

        sumAnswers(period, rtn, Q.BIZ_MILEAGE, SduQuestions.BIZ_MILEAGE_HDRS);
        sumAnswers(period, rtn, Q.BIZ_MILEAGE_CO2E, SduQuestions.BIZ_MILEAGE_CO2E_HDRS);
    }

    private void crunchFleetCo2ePre2020(String period, SurveyReturn rtn) {
        try {
            getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_CO2E)
            .derived(true)
            .response(roadEmissionsService.calculate(
                            cFactor(CarbonFactors.ELECTRICITY_FROM_GRID, period),
                            getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).responseAsOptional(),
                            cFactor(CarbonFactors.AVERAGE_BATTERY_AND_PLUGIN_HYBRID_EV_TOTAL, period),
                            getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_MILES).responseAsOptional())
                    .get());
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from low carbon fleet miles");
        }

        CarbonFactor mileageFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, period);
        CarbonFactor mileageWttFactor = cFactor(CarbonFactors.CAR_WTT_AVERAGE_SIZE, period);

        try {
            getAnswer(period, rtn, Q.OWNED_FLEET_TRAVEL_CO2E)
                    .derived(true)
                    .response(getAnswer(period, rtn, Q.FLEET_ROAD_MILES).responseAsBigDecimal()
                            .multiply(mileageFactor.value())
                            .multiply(m2km)
                            .divide(ONE_THOUSAND, 0, RoundingMode.HALF_UP));
            // Treasury row 51: Owned vehicles Well to Tank
            getAnswer(period, rtn, Q.OWNED_FLEET_TRAVEL_WTT_CO2E)
                    .derived(true)
                    .response(getAnswer(period, rtn, Q.FLEET_ROAD_MILES).responseAsBigDecimal()
                            .multiply(mileageWttFactor.value())
                            .multiply(m2km)
                            .divide(ONE_THOUSAND, 0, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from fleet miles");
        }

        // Fleet WTT is included within biz mileage as both are scope 3
        sumAnswers(period, rtn, Q.OWNED_FLEET_TRAVEL_CO2E,
                Q.OWNED_LEASED_LOW_CARBON_CO2E, Q.OWNED_FLEET_TRAVEL_CO2E);
    }

    protected void crunchBizTravelCarCo2e(String period, SurveyReturn rtn) {
        CarbonFactor mileageFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, period);
        CarbonFactor mileageWttFactor = cFactor(CarbonFactors.CAR_WTT_AVERAGE_SIZE, period);
        try {
            getAnswer(period,rtn, Q.BIZ_MILEAGE_ROAD_CO2E)
                    .derived(true)
                    .response(
                            getAnswer(period, rtn, Q.BIZ_MILEAGE_ROAD).responseAsBigDecimal()
                            .multiply(mileageFactor.value())
                            .multiply(m2km)
                            .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP)
                    );
            getAnswer(period,rtn, Q.BIZ_MILEAGE_ROAD_WTT_CO2E)
                    .derived(true)
                    .response(
                            getAnswer(period, rtn, Q.BIZ_MILEAGE_ROAD).responseAsBigDecimal()
                            .multiply(mileageWttFactor.value())
                            .multiply(m2km)
                            .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP)
                    );
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from personal road miles");
        }
    }

    protected void crunchRoadCo2e(String period, SurveyReturn rtn) {
        if (rtn.applicablePeriod().startsWith("202")) {
            crunchFleetCo2e(period, rtn);
        } else {
            crunchFleetCo2ePre2020(period, rtn);
        }
        crunchBizTravelCarCo2e(period, rtn);
        crunchBusCo2e(period, rtn);
        crunchTaxiCo2e(period, rtn);

        sumAnswers(period, rtn, Q.BIZ_MILEAGE_ALL_ROAD_CO2E,
                Q.BIZ_MILEAGE_BUS_CO2E, Q.BIZ_MILEAGE_TAXI_CO2E,
                Q.BIZ_MILEAGE_ROAD_CO2E, Q.BIZ_MILEAGE_ROAD_WTT_CO2E,
                // Fleet WTT is included within biz mileage as both are scope 3
                Q.OWNED_FLEET_TRAVEL_WTT_CO2E);
        sumAnswers(period, rtn, Q.FLEET_AND_BIZ_ROAD_CO2E,
                Q.OWNED_FLEET_TRAVEL_CO2E, Q.OWNED_LEASED_LOW_CARBON_CO2E, Q.BIZ_MILEAGE_ALL_ROAD_CO2E);
    }

    protected void crunchRailCo2e(String period, SurveyReturn rtn) {
        CarbonFactor cFactor;
        try {
            if (getAnswer(period, rtn, Q.RAIL_MILES).response() == null
                    && getAnswer(period, rtn, Q.RAIL_COST).response() != null) {
                BigDecimal railMiles = getAnswer(period, rtn, Q.RAIL_COST).responseAsBigDecimal()
                        .divide(RAIL_PER_MILE, RoundingMode.HALF_UP);
                getAnswer(period, rtn, Q.RAIL_MILES).derived(true).response(railMiles);
            }

            cFactor = cFactor(CarbonFactors.NATIONAL_RAIL_TOTAL, period);
            BigDecimal bizTravelRailCo2e
                    = getAnswer(period, rtn, Q.RAIL_MILES).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_RAIL_CO2E).response(bizTravelRailCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from rail travel");
        }
    }

    protected void crunchAirCo2e(String period, SurveyReturn rtn) {
        try {
            sumAnswers(period, rtn, Q.AIR_MILES,
                    Q.DOMESTIC_AIR_MILES, Q.SHORT_HAUL_AIR_MILES, Q.LONG_HAUL_AIR_MILES);

            BigDecimal bizTravelDomesticAirCo2e
                    = getAnswer(period, rtn, Q.DOMESTIC_AIR_MILES).responseAsBigDecimal()
                    .multiply(cFactor(CarbonFactors.DOMESTIC_FLIGHT_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            BigDecimal bizTravelShortHaulAirCo2e
                    = getAnswer(period, rtn, Q.SHORT_HAUL_AIR_MILES).responseAsBigDecimal()
                    .multiply(cFactor(CarbonFactors.SHORT_HAUL_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            BigDecimal bizTravelLongHaulAirCo2e
                    = getAnswer(period, rtn, Q.LONG_HAUL_AIR_MILES).responseAsBigDecimal()
                    .multiply(cFactor(CarbonFactors.LONG_HAUL_TOTAL, period).value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_AIR_CO2E)
                    .response(bizTravelDomesticAirCo2e.add(bizTravelShortHaulAirCo2e).add(bizTravelLongHaulAirCo2e));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from air travel");
        }
    }

    protected void crunchBusCo2e(String period, SurveyReturn rtn) {
        CarbonFactor cFactor;
        try {
            if (getAnswer(period, rtn, Q.BUS_MILES).response() == null
                    && getAnswer(period, rtn, Q.BUS_COST).response() != null) {
                BigDecimal busMiles = getAnswer(period, rtn, Q.BUS_COST).responseAsBigDecimal()
                        .divide(BUS_PER_MILE, RoundingMode.HALF_UP);
                getAnswer(period, rtn, Q.BUS_MILES).derived(true).response(busMiles);
            }

            cFactor = cFactor(CarbonFactors.LOCAL_BUS_TOTAL, period);
            BigDecimal bizTravelBusCo2e
                    = getAnswer(period, rtn, Q.BUS_MILES).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_BUS_CO2E).response(bizTravelBusCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from bus travel");
        }
    }

    protected void crunchTaxiCo2e(String period, SurveyReturn rtn) {
        CarbonFactor cFactor;
        try {
            if (getAnswer(period, rtn, Q.TAXI_MILES).response() == null
                    && getAnswer(period, rtn, Q.TAXI_COST).response() != null) {
                BigDecimal taxiMiles = getAnswer(period, rtn, Q.TAXI_COST).responseAsBigDecimal()
                        .divide(TAXI_PER_MILE, RoundingMode.HALF_UP);
                getAnswer(period, rtn, Q.TAXI_MILES).derived(true).response(taxiMiles);
            }

            cFactor = cFactor(CarbonFactors.TAXI_TOTAL, period);
            BigDecimal bizTravelTaxiCo2e
                    = getAnswer(period, rtn, Q.TAXI_MILES).responseAsBigDecimal()
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.BIZ_MILEAGE_TAXI_CO2E).response(bizTravelTaxiCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from taxi travel");
        }
    }

    // From 2020-21 FLEET_ROAD_MILES is sub-divided by fuel
    protected void crunchFleetCo2e(String period, SurveyReturn rtn) {
        CarbonFactor mileageFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, period);
        CarbonFactor mileageWttFactor = cFactor(CarbonFactors.CAR_WTT_AVERAGE_SIZE, period);

        try {
            getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_CO2E)
                    .derived(true)
                    .response(roadEmissionsService.calculate(
                                    cFactor(CarbonFactors.ELECTRICITY_FROM_GRID, period),
                                    getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_FUEL_USED).responseAsOptional(),
                                    cFactor(CarbonFactors.AVERAGE_BATTERY_AND_PLUGIN_HYBRID_EV_TOTAL, period),
                                    getAnswer(period, rtn, Q.OWNED_LEASED_LOW_CARBON_MILES).responseAsOptional())
                            .get());
            getAnswer(period, rtn, Q.CAR_DIESEL_USED_CO2E)
                    .derived(true)
                    .response(roadEmissionsService.calculate(
                            cFactor(CarbonFactors.DIESEL_DIRECT, period),
                            getAnswer(period, rtn, Q.CAR_DIESEL_USED).responseAsOptional(),
                            mileageFactor,
                            getAnswer(period, rtn, Q.CAR_MILES_DIESEL).responseAsOptional())
                    .get());
            getAnswer(period, rtn, Q.CAR_DIESEL_WTT_CO2E)
                    .derived(true)
                    .response(roadEmissionsService.calculate(
                            cFactor(CarbonFactors.DIESEL_WTT, period),
                            getAnswer(period, rtn, Q.CAR_DIESEL_USED).responseAsOptional(),
                            mileageWttFactor,
                            getAnswer(period, rtn, Q.CAR_MILES_DIESEL).responseAsOptional())
                    .get());
            getAnswer(period, rtn, Q.CAR_PETROL_USED_CO2E)
                    .derived(true)
                    .response(roadEmissionsService.calculate(
                            cFactor(CarbonFactors.PETROL_DIRECT, period),
                            getAnswer(period, rtn, Q.CAR_PETROL_USED).responseAsOptional(),
                            mileageFactor,
                            getAnswer(period, rtn, Q.CAR_MILES_PETROL).responseAsOptional())
                    .get());
            getAnswer(period, rtn, Q.CAR_PETROL_WTT_CO2E)
                    .derived(true)
                    .response(roadEmissionsService.calculate(
                            cFactor(CarbonFactors.PETROL_WTT, period),
                            getAnswer(period, rtn, Q.CAR_PETROL_USED).responseAsOptional(),
                            mileageWttFactor,
                            getAnswer(period, rtn, Q.CAR_MILES_PETROL).responseAsOptional())
                    .get());
        } catch (NoSuchElementException | IllegalStateException e) {
            // almost certainly OK since these are newer questions
            LOGGER.warn(e.getMessage());
        }

        sumAnswers(period, rtn, Q.OWNED_FLEET_TRAVEL_CO2E,
                        Q.CAR_DIESEL_USED_CO2E, Q.CAR_DIESEL_WTT_CO2E,
                        Q.CAR_PETROL_USED_CO2E, Q.CAR_PETROL_WTT_CO2E,
                        Q.OWNED_LEASED_LOW_CARBON_CO2E);
    }

    // Treasury row 53: Staff commute
    protected void crunchStaffCommuteCo2e(String period, SurveyReturn rtn) {
        // NOTE: XXX_TOTAL means primary and WTT combined due to XXX
        CarbonFactor cFactor = cFactor(CarbonFactors.CAR_TOTAL, period);
        try {
            BigDecimal totalStaffMiles = getAnswer(period, rtn, Q.STAFF_COMMUTE_MILES_TOTAL).responseAsBigDecimal();
            if (totalStaffMiles.equals(BigDecimal.ZERO)) {
                totalStaffMiles = getAnswerForPeriodWithFallback(period, rtn, Q.NO_STAFF).responseAsBigDecimal()
                        .multiply(getAnswer(period, rtn, Q.STAFF_COMMUTE_MILES_PP).responseAsBigDecimal());
                getAnswer(period, rtn, Q.STAFF_COMMUTE_MILES_TOTAL).response(totalStaffMiles);
            }

            getAnswer(period,rtn, Q.STAFF_COMMUTE_MILES_CO2E)
                    .derived(true)
                    .response(totalStaffMiles
                            .multiply(cFactor.value())
                            .multiply(m2km)
                            .divide(ONE_THOUSAND, 0, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from staff commuting");
        }
    }

    protected void crunchPaperCo2e(String period, SurveyReturn rtn) {
        BigDecimal paperCo2e;
        try {
            paperCo2e = getAnswer(period, rtn, Q.PAPER_USED).responseAsBigDecimal()
                    .multiply(oneThousandth(cFactor(CarbonFactors.PAPER, period)));
            getAnswer(period,rtn, Q.PAPER_CO2E).derived(true).response(paperCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from paper used");
            paperCo2e = BigDecimal.ZERO;
        }
    }

    protected void crunchPatientVisitorMileageCo2e(String period,
            SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.CAR_TOTAL, period);
        try {
            // Treasury row 52: Patient and Visitor
            BigDecimal visitorMileage = getAnswer(period, rtn, Q.VISITOR_MILEAGE).responseAsBigDecimal();
            if (visitorMileage.equals(BigDecimal.ZERO)) {
                visitorMileage =  getAnswer(period, rtn, Q.NO_PATIENT_CONTACTS).responseAsBigDecimal().multiply(new BigDecimal("3.7")).multiply(new BigDecimal("9.4"));
                getAnswer(period, rtn, Q.VISITOR_MILEAGE).response(visitorMileage);
            }
            BigDecimal patientVisitorMileage = getAnswer(period, rtn, Q.PATIENT_MILEAGE).responseAsBigDecimal()
                    .add(getAnswer(period, rtn, Q.VISITOR_MILEAGE).responseAsBigDecimal());
            getAnswer(period, rtn, Q.PATIENT_AND_VISITOR_MILEAGE).response(patientVisitorMileage);
            BigDecimal patientVisitorCo2e = patientVisitorMileage
                    .multiply(cFactor.value())
                    .multiply(m2km)
                    .divide(ONE_THOUSAND, divisionScale , RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.PATIENT_AND_VISITOR_MILEAGE_CO2E).derived(true).response(patientVisitorCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from patient and visitor travel");
        }
    }

    protected void calcScope1(String period, SurveyReturn rtn) {
        crunchOwnedBuildings(period, rtn);
        CarbonFactor cFactor = cFactor(CarbonFactors.CAR_AVERAGE_SIZE, period);
        crunchCO2e(period, rtn, Q.FLEET_ROAD_MILES,oneThousandth(cFactor), Q.OWNED_FLEET_TRAVEL_CO2E);
        crunchAnaestheticGases(period, rtn);
    }

    protected void calcScope2(String period, SurveyReturn rtn) {
        crunchElectricityUsed(period, rtn);
        crunchHeatSteam(period, rtn);
        // TODO EV Owned Vehicles
    }

    protected void calcCarbonProfileSimplifiedSduMethod(String period, SurveyReturn rtn) {
        LOGGER.info("calcCarbonProfileSduMethod for {} in {}", rtn.getOrg(), rtn.applicablePeriod());

        calcCarbonProfileSduMethod(period, rtn);

        sumAnswers(period, rtn, Q.SDU_MEDICINES_AND_CHEMICALS, Q.PHARMA_SPEND, Q.CHEM_AND_GAS_SPEND);
        sumAnswers(period, rtn, Q.SDU_MEDICINES_AND_CHEMICALS_CO2E, Q.PHARMA_CO2E, Q.CHEM_AND_GAS_CO2E);

        sumAnswers(period, rtn, Q.SDU_MEDICAL_EQUIPMENT, Q.MED_INSTR_SPEND);
        sumAnswers(period, rtn, Q.SDU_MEDICAL_EQUIPMENT_CO2E, Q.MED_INSTR_CO2E);

        sumAnswers(period, rtn, Q.SDU_NON_MEDICAL_EQUIPMENT, Q.ICT_SPEND, Q.OTHER_MANUFACTURED_SPEND, Q.PAPER_AND_CARD_SPEND);
        sumAnswers(period, rtn, Q.SDU_NON_MEDICAL_EQUIPMENT_CO2E, Q.ICT_CO2E, Q.OTHER_MANUFACTURED_CO2E, Q.PAPER_AND_CARD_CO2E);

        sumAnswers(period, rtn, Q.SDU_BUSINESS_SERVICES, Q.BIZ_SVCS_SPEND, Q.OTHER_SPEND);
        sumAnswers(period, rtn, Q.SDU_BUSINESS_SERVICES_CO2E, Q.BIZ_SVCS_CO2E, Q.OTHER_PROCUREMENT_CO2E);

        sumAnswers(period, rtn, Q.SDU_CONSTRUCTION_AND_FREIGHT, Q.CONSTRUCTION_SPEND, Q.FREIGHT_SPEND);
        sumAnswers(period, rtn, Q.SDU_CONSTRUCTION_AND_FREIGHT_CO2E, Q.CONSTRUCTION_CO2E, Q.FREIGHT_CO2E);

        sumAnswers(period, rtn, Q.SDU_FOOD_AND_CATERING, Q.CATERING_SPEND);
        sumAnswers(period, rtn, Q.SDU_FOOD_AND_CATERING_CO2E, Q.CATERING_CO2E);

        sumAnswers(period, rtn, Q.SDU_WATER, Q.WATER_COST);
        sumAnswers(period, rtn, Q.SDU_WATER_CO2E, Q.WATER_CO2E);

        sumAnswers(period, rtn, Q.SDU_WASTE, Q.WASTE_RECYLING_COST);
        sumAnswers(period, rtn, Q.SDU_WASTE_CO2E, Q.WASTE_CO2E);

        sumAnswers(period, rtn, Q.SDU_BUSINESS_TRAVEL_AND_FLEET, Q.TOTAL_BIZ_TRAVEL_COST);
        sumAnswers(period, rtn, Q.SDU_BUSINESS_TRAVEL_AND_FLEET_CO2E, Q.FLEET_AND_BIZ_ROAD_CO2E);

        sumAnswers(period, rtn, Q.SDU_COMMISSIONED_HEALTH_SERVICES, Q.COMMISSIONING_SPEND);
        sumAnswers(period, rtn, Q.SDU_COMMISSIONED_HEALTH_SERVICES_CO2E, Q.COMMISSIONING_CO2E);
    }

    protected void calcCarbonProfileSduMethod(String period, SurveyReturn rtn) {
        LOGGER.info("calcCarbonProfileSduMethod for {} in {}", rtn.getOrg(), rtn.applicablePeriod());
        sumAnswers(period, rtn, Q.WASTE_AND_WATER_CO2E, Q.SCOPE_3_WASTE, Q.SCOPE_3_WATER);

        // Intentional use of return period for org type
        String orgType = getAnswer(rtn.applicablePeriod(), rtn, Q.ORG_TYPE).response();
        if (isEmpty(orgType)) {
            String msg = String.format(
                    "Cannot model carbon profile of %1$s in %2$s as no org type specified",
                    rtn.org(), period);
            throw new IllegalStateException(msg);
        }
        BigDecimal nonPaySpend = getNonPaySpend(period, rtn);
        crunchCapitalSpend(period, rtn, orgType, nonPaySpend);

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
        crunchWeighting(period, rtn, nonPaySpend, Q.PAPER_AND_CARD_SPEND, wFactor, Q.PAPER_AND_CARD_CO2E);
        wFactor = wFactor(WeightingFactors.PHARMA, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.PHARMA_SPEND, wFactor, Q.PHARMA_CO2E);
        wFactor = wFactor(WeightingFactors.BIZ_TRAVEL, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.TRAVEL_SPEND, wFactor, Q.TRAVEL_CO2E);
        wFactor = wFactor(WeightingFactors.COMMISSIONING, period, orgType);
        crunchWeighting(period, rtn, nonPaySpend, Q.COMMISSIONING_SPEND, wFactor, Q.COMMISSIONING_CO2E);

        // intentionally omit waste and water and travel here
        sumAnswers(period, rtn, Q.PROCUREMENT_CO2E,
                Q.BIZ_SVCS_CO2E, Q.CAPITAL_CO2E, Q.CONSTRUCTION_CO2E,
                Q.CATERING_CO2E, Q.FREIGHT_CO2E, Q.ICT_CO2E, Q.CHEM_AND_GAS_CO2E,
                Q.MED_INSTR_CO2E, Q.OTHER_MANUFACTURED_CO2E,
                Q.OTHER_PROCUREMENT_CO2E, Q.PAPER_AND_CARD_CO2E, Q.PHARMA_CO2E);
    }

    private BigDecimal getNonPaySpend(String period, SurveyReturn rtn) {
        BigDecimal nonPaySpend = getAnswer(period, rtn, Q.NON_PAY_SPEND).responseAsBigDecimal();
        if (nonPaySpend.equals(BigDecimal.ZERO)
                || getAnswer(period, rtn, Q.NON_PAY_SPEND).derived()) {
            nonPaySpend = calcNonPaySpendFromOpEx(period, rtn);
            getAnswer(period, rtn, Q.NON_PAY_SPEND).derived(true).response(nonPaySpend);
        }
        return nonPaySpend;
    }

    // This is probably a poor approximation.
    // The rational is that capital does not deal with major construction
    // projects but 'small works'. Therefore it combines a carbon intensity for
    // construction in 2013-14 with a weighting of different org types
    protected void crunchCapitalSpend(String period, SurveyReturn rtn,
            String orgType, BigDecimal nonPaySpend) {
        WeightingFactor wFactor = wFactor(WeightingFactors.CAPITAL, period, orgType);
        BigDecimal calcVal;
        try {
            Answer capEx = getAnswer(period, rtn, Q.CAPITAL_SPEND);
            if (capEx.derived() || capEx.response() == null
                    || capEx.response().trim().length() == 0) {
                LOGGER.info("No directly entered capital spend, estimate from non-pay spend");
                calcVal = nonPaySpend.multiply(wFactor.proportionOfTotal());
                LOGGER.info("Estimated capital spend from non-pay spend as {}", calcVal);
                capEx.derived(true).response(calcVal.toPlainString());
            } else {
                calcVal = capEx.responseAsBigDecimal();
            }
            // This intentionally uses proportion of total not intensity
            // because as explained above it is a special combined factor
            calcVal = calcVal.multiply(wFactor.proportionOfTotal());
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot estimate CO2e from capital spend");
            calcVal = BigDecimal.ZERO;
        }
        LOGGER.info("Calculated {} emissions as {}", Q.CAPITAL_CO2E, calcVal);
        getAnswer(period, rtn, Q.CAPITAL_CO2E).derived(true).response(calcVal.toPlainString());
    }

    protected BigDecimal calcNonPaySpendFromOpEx(String period,
            SurveyReturn rtn) {
        LOGGER.info("Need to calc non pay spend from op ex for '{}' of '{}", period, rtn.id());
        // Intentionally use rtn period for org type
        Answer orgTypeA = rtn.answer(rtn.applicablePeriod(), Q.ORG_TYPE)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Cannot calc non-pay spend from op-ex for %1$s in %2$s as no org type specified.",
                        rtn.org(), rtn.applicablePeriod())));
        BigDecimal opEx;
        try {
            Answer opExA = rtn.answer(period, Q.OP_EX)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Cannot calc non-pay spend from op-ex for %1$s (%2$s) in %3$s as op-ex is missing.",
                            rtn.org(), orgTypeA.response(), period)));
            opEx = opExA.responseAsBigDecimal();
        } catch (IllegalArgumentException e) {
            // Warn is enough, opex may not be provided in some years.
            LOGGER.warn(e.getMessage());
            opEx = BigDecimal.ZERO;
        }

        WeightingFactor wFactor = wFactor(
        WeightingFactors.NON_PAY_OP_EX_PORTION, period, orgTypeA.response());

        BigDecimal nonPaySpend = opEx.multiply(wFactor.proportionOfTotal());
        LOGGER.info("Calculated non pay spend {} from op ex {}", nonPaySpend, opEx.toPlainString());
        return nonPaySpend;
    }

    private boolean isEmpty(String value) {
        return value == null || "0".equals(value);
    }

    protected void calcCarbonProfileSimplifiedEClassMethod(String period, SurveyReturn rtn) {
        LOGGER.info("calcCarbonProfileSimplifiedEClassMethod for {} in {}", rtn.getOrg(), rtn.applicablePeriod());

        calcCarbonProfileEClassMethod(period, rtn);
        sumAnswers(period, rtn, Q.EC_MEDICINES_AND_CHEMICALS, Q.PHARMA_BLOOD_PROD_AND_MED_GASES, Q.CHEMICALS_AND_REAGENTS);
        sumAnswers(period, rtn, Q.EC_MEDICINES_AND_CHEMICALS_CO2E, Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E, Q.CHEMICALS_AND_REAGENTS_CO2E);

        sumAnswers(period, rtn, Q.EC_MEDICAL_EQUIPMENT, Q.DRESSINGS,Q.MEDICAL_AND_SURGICAL_EQUIPT, Q.PATIENTS_APPLIANCES, Q.DENTAL_AND_OPTICAL_EQUIPT,Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS);
        sumAnswers(period, rtn, Q.EC_MEDICAL_EQUIPMENT_CO2E, Q.DRESSINGS_CO2E, Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E, Q.PATIENTS_APPLIANCES_CO2E, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E,Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E);

        sumAnswers(period, rtn, Q.EC_NON_MEDICAL_EQUIPMENT, Q.PATIENTS_CLOTHING_AND_FOOTWEAR, Q.STAFF_CLOTHING, Q.LAB_EQUIPT_AND_SVCS, Q.BEDDING_LINEN_AND_TEXTILES, Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY, Q.REC_EQUIPT_AND_SOUVENIRS);
        sumAnswers(period, rtn, Q.EC_NON_MEDICAL_EQUIPMENT_CO2E, Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E, Q.STAFF_CLOTHING_CO2E, Q.LAB_EQUIPT_AND_SVCS_CO2E, Q.BEDDING_LINEN_AND_TEXTILES_CO2E, Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E, Q.REC_EQUIPT_AND_SOUVENIRS_CO2E);

        sumAnswers(period, rtn, Q.EC_BUSINESS_SERVICES, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS, Q.BLDG_AND_ENG_PROD_AND_SVCS, Q.CONSULTING_SVCS_AND_EXPENSES);
        sumAnswers(period, rtn, Q.EC_BUSINESS_SERVICES_CO2E, Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E, Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E, Q.CONSULTING_SVCS_AND_EXPENSES_CO2E);

        sumAnswers(period, rtn, Q.EC_CONSTRUCTION_AND_FREIGHT, Q.GARDENING_AND_FARMING, Q.FURNITURE_FITTINGS);
        sumAnswers(period, rtn, Q.EC_CONSTRUCTION_AND_FREIGHT_CO2E, Q.GARDENING_AND_FARMING_CO2E, Q.FURNITURE_FITTINGS_CO2E);

        sumAnswers(period, rtn, Q.EC_FOOD_AND_CATERING, Q.PROVISIONS, Q.HARDWARE_CROCKERY);
        sumAnswers(period, rtn, Q.EC_FOOD_AND_CATERING_CO2E, Q.PROVISIONS_CO2E, Q.HARDWARE_CROCKERY_CO2E);

        sumAnswers(period, rtn, Q.EC_COMMISSIONED_HEALTH_SERVICES, Q.PURCHASED_HEALTHCARE);
        sumAnswers(period, rtn, Q.EC_COMMISSIONED_HEALTH_SERVICES_CO2E, Q.PURCHASED_HEALTHCARE_CO2E);

        sumAnswers(period, rtn, Q.ECLASS_SPEND, ECLASS_PROFILE_SPEND_HDRS);
    }

    protected void calcCarbonProfileEClassMethod(String period, SurveyReturn rtn) {
        LOGGER.info("calcCarbonProfileEClassMethod for {} in {}", rtn.getOrg(), rtn.applicablePeriod());
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

        sumAnswers(period, rtn, Q.WASTE_AND_WATER_CO2E, Q.SCOPE_3_WASTE, Q.SCOPE_3_WATER);
        sumAnswers(period, rtn, Q.PROCUREMENT_CO2E, Q.PROVISIONS_CO2E,
                Q.STAFF_CLOTHING_CO2E, Q.PATIENTS_CLOTHING_AND_FOOTWEAR_CO2E,
                Q.PHARMA_BLOOD_PROD_AND_MED_GASES_CO2E,
                Q.MEDICAL_AND_SURGICAL_EQUIPT_CO2E, Q.PATIENTS_APPLIANCES_CO2E,
                Q.CHEMICALS_AND_REAGENTS_CO2E, Q.DENTAL_AND_OPTICAL_EQUIPT_CO2E,
                Q.IMAGING_AND_RADIOTHERAPY_EQUIPT_AND_SVCS_CO2E,
                Q.LAB_EQUIPT_AND_SVCS_CO2E,
                Q.HOTEL_EQUIPT_MATERIALS_AND_SVCS_CO2E,
                Q.BLDG_AND_ENG_PROD_AND_SVCS_CO2E, Q.PURCHASED_HEALTHCARE_CO2E,
                Q.GARDENING_AND_FARMING_CO2E, Q.FURNITURE_FITTINGS_CO2E,
                Q.HARDWARE_CROCKERY_CO2E, Q.BEDDING_LINEN_AND_TEXTILES_CO2E,
                Q.OFFICE_EQUIPT_TELCO_COMPUTERS_AND_STATIONERY_CO2E,
                Q.REC_EQUIPT_AND_SOUVENIRS_CO2E,
                Q.CONSULTING_SVCS_AND_EXPENSES_CO2E);
    }

    protected void crunchEnergyCostChange(String period, SurveyReturn rtn) {
        try {
            BigDecimal currentEnergyCost = getAnswer(period, rtn, Q.TOTAL_ENERGY_COST).responseAsBigDecimal();
            BigDecimal previousEnergyCost = getAnswer(PeriodUtil.previous(period), rtn, Q.TOTAL_ENERGY_COST).responseAsBigDecimal();

            BigDecimal change = currentEnergyCost.divide(previousEnergyCost, 5, RoundingMode.HALF_UP).multiply(ONE_HUNDRED).subtract(ONE_HUNDRED);
            getAnswer(period, rtn, Q.ENERGY_COST_CHANGE_PCT).response(change);
        } catch (IllegalStateException | NullPointerException | ArithmeticException e) {
            LOGGER.warn("Insufficient data to calculate change of energy costs in {}", period);
        }
    }

    private void crunchHeatSteam(String period, SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
        crunchCO2e(period, rtn, Q.STEAM_USED, oneThousandth(cFactor), Q.STEAM_CO2E);
        cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
        crunchCO2e(period, rtn, Q.HOT_WATER_USED, oneThousandth(cFactor), Q.HOT_WATER_CO2E);
        BigDecimal exportedThermalEnergy;
        try {
            cFactor = cFactor(CarbonFactors.ONSITE_HEAT_AND_STEAM, period);
            Optional<Answer> crunchCO2e = crunchCO2e(period, rtn, Q.EXPORTED_THERMAL_ENERGY, oneThousandth(cFactor), Q.EXPORTED_THERMAL_ENERGY_CO2E);
            exportedThermalEnergy = crunchCO2e.orElseThrow(() -> new IllegalStateException()).responseAsBigDecimal();
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from {}", Q.EXPORTED_THERMAL_ENERGY);
            exportedThermalEnergy = BigDecimal.ZERO;
        }

        BigDecimal netHeatSteamVal = new BigDecimal("0.000");
        netHeatSteamVal = netHeatSteamVal
                .add(getAnswer(period, rtn, Q.STEAM_CO2E).responseAsBigDecimal())
                .add(getAnswer(period, rtn, Q.HOT_WATER_CO2E).responseAsBigDecimal())
                .subtract(exportedThermalEnergy);
        getAnswer(period,rtn, Q.NET_THERMAL_ENERGY_CO2E).response(netHeatSteamVal.toPlainString());
    }

    private BigDecimal oneThousandth(CarbonFactor cFactor) {
        return cFactor.value().divide(ONE_THOUSAND, cFactor.value().scale()+3, RoundingMode.HALF_UP);
    }

    protected void crunchElectricityUsed(String period, SurveyReturn rtn) {
        CarbonFactor elecFactor = cFactor(CarbonFactors.ELECTRICITY_UK_TOTAL, period);

        energyEmissionsService.calcGridElecCo2e(
                getAnswer(period, rtn, Q.ELEC_USED).responseAsBigDecimal(),
                elecFactor, getAnswer(period,rtn, Q.ELEC_CO2E));
        energyEmissionsService.calcGridElecWttCo2e(
                getAnswer(period, rtn, Q.ELEC_USED).responseAsBigDecimal(),
                cFactor(CarbonFactors.ELECTRICITY_UK_WTT, period),
                getAnswer(period, rtn, Q.ELEC_WTT_CO2E));

        calcExportedElecCo2e(period, rtn);

        BigDecimal renewableElecUsed = sumAnswers(period, rtn,
                Q.ELEC_TOTAL_RENEWABLE_USED, Q.ELEC_USED_GREEN_TARIFF,
                Q.ELEC_3RD_PTY_RENEWABLE_USED, Q.ELEC_OWNED_RENEWABLE_USED_SDU)
                .responseAsBigDecimal();
        getAnswer(period,rtn, Q.ELEC_TOTAL_RENEWABLE_USED).derived(true).response(renewableElecUsed);
        try {
            BigDecimal greenTariffGridEquivUsed = calcGreenTariffElecCo2e(
                    period, rtn, oneThousandth(elecFactor));
            BigDecimal thirdPtyRenewableGridEquivUsed;
            thirdPtyRenewableGridEquivUsed = calcThirdPartyRenewableCo2e(period,
                    rtn, oneThousandth(elecFactor));
            BigDecimal renewableElecCo2e = greenTariffGridEquivUsed.add(thirdPtyRenewableGridEquivUsed)
                    .multiply(oneThousandth(elecFactor)).divide(ONE_HUNDRED, RoundingMode.HALF_UP);
            getAnswer(period,rtn, Q.ELEC_RENEWABLE_CO2E).derived(true).response(renewableElecCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from renewable electricity");
        }
    }

    private BigDecimal calcThirdPartyRenewableCo2e(String period,
            SurveyReturn rtn, BigDecimal elecFactor) {
        BigDecimal thirdPtyRenewableGridEquivUsed;
        try {
            thirdPtyRenewableGridEquivUsed = getAnswer(period, rtn, Q.ELEC_3RD_PTY_RENEWABLE_USED).responseAsBigDecimal().multiply(
                    ONE_HUNDRED.subtract(getAnswer(period, rtn, Q.THIRD_PARTY_ADDITIONAL_PCT).responseAsBigDecimal()));
            getAnswer(period,rtn, Q.ELEC_NON_RENEWABLE_3RD_PARTY).derived(true).response(thirdPtyRenewableGridEquivUsed);
            getAnswer(period,rtn, Q.ELEC_NON_RENEWABLE_3RD_PARTY_CO2E).derived(true).response(
                    thirdPtyRenewableGridEquivUsed.multiply(elecFactor).divide(ONE_HUNDRED, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from third party renewable elec used for {} in {}", rtn.org(), period);
            LOGGER.error(e.getMessage(), e);
            thirdPtyRenewableGridEquivUsed = BigDecimal.ZERO;
        }
        return thirdPtyRenewableGridEquivUsed;
    }

    private BigDecimal calcGreenTariffElecCo2e(String period, SurveyReturn rtn,
            BigDecimal elecFactor) {
        BigDecimal greenTariffGridEquivUsed;
        try {
            greenTariffGridEquivUsed = getAnswer(period, rtn, Q.ELEC_USED_GREEN_TARIFF).responseAsBigDecimal().multiply(
                    ONE_HUNDRED.subtract(getAnswer(period, rtn, Q.GREEN_TARIFF_ADDITIONAL_PCT).responseAsBigDecimal()));
            getAnswer(period,rtn, Q.ELEC_NON_RENEWABLE_GREEN_TARIFF).derived(true).response(greenTariffGridEquivUsed);
            getAnswer(period,rtn, Q.ELEC_NON_RENEWABLE_GREEN_TARIFF_CO2E).derived(true).response(
                     greenTariffGridEquivUsed.multiply(elecFactor).divide(ONE_HUNDRED, RoundingMode.HALF_UP));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e saved from green tariff elec used for {} in {}", rtn.org(), period);
            LOGGER.error(e.getMessage(), e);
            greenTariffGridEquivUsed = BigDecimal.ZERO;
        }
        return greenTariffGridEquivUsed;
    }

    private void calcExportedElecCo2e(String period, SurveyReturn rtn) {
        BigDecimal elecExportedCo2e;
        try {
            elecExportedCo2e = getAnswer(period, rtn, Q.ELEC_EXPORTED).responseAsBigDecimal()
                    .multiply(oneThousandth(cFactor(CarbonFactors.ELEC_EXPORTED_GAS_FIRED_CHP_TOTAL, period)));
            getAnswer(period,rtn, Q.ELEC_EXPORTED_CO2E).derived(true).response(elecExportedCo2e);
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from elec exported");
            elecExportedCo2e = BigDecimal.ZERO;
        }
        getAnswer(period,rtn, Q.NET_ELEC_CO2E).derived(true).response(
                getAnswer(period,rtn, Q.ELEC_CO2E).responseAsBigDecimal().subtract(elecExportedCo2e));
    }

    protected void crunchOwnedBuildings(String period, SurveyReturn rtn) {
        CarbonFactor cFactor = cFactor(CarbonFactors.GAS_TOTAL, period);
        crunchCO2e(period, rtn, Q.GAS_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_GAS);

        cFactor = cFactor(CarbonFactors.FUEL_OIL_TOTAL, period);
        crunchCO2e(period, rtn, Q.OIL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_OIL);

        cFactor = cFactor(CarbonFactors.COAL_INDUSTRIAL_TOTAL, period);
        crunchCO2e(period, rtn, Q.COAL_USED, oneThousandth(cFactor), Q.OWNED_BUILDINGS_COAL);
        sumAnswers(period, rtn,Q.OWNED_BUILDINGS, Q.OWNED_BUILDINGS_GAS, Q.OWNED_BUILDINGS_OIL, Q.OWNED_BUILDINGS_COAL);
    }

    protected void crunchAnaestheticGases(String period, SurveyReturn rtn) {
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

    public void crunchSocialValue(String period, SurveyReturn rtn) {
        try {
            sumAnswers(period, rtn, Q.SV_TOTAL, Q.SV_ENVIRONMENT, Q.SV_GROWTH,
                    Q.SV_INNOVATION, Q.SV_JOBS, Q.SV_SOCIAL);
        } catch (IllegalStateException e) {
            if (PeriodUtil.before(period, "2018-19")) {
                LOGGER.debug("Ignoring question not expected in {}: {}", period, e.getMessage());
            } else {
                throw e;
            }
        }
    }

    public void crunchSocialInvestmentRecorded(String period, SurveyReturn rtn) {
        try {
            sumAnswers(period, rtn, Q.SI_TOTAL, Q.SI_ENVIRONMENT, Q.SI_GROWTH,
                    Q.SI_INNOVATION, Q.SI_JOBS, Q.SI_SOCIAL);
        } catch (IllegalStateException e) {
            if (PeriodUtil.before(period, "2018-19")) {
                LOGGER.debug("Ignoring question not expected in {}: {}", period, e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private Answer sumAnswers(String period, SurveyReturn rtn, Q trgtQName,
            Q... srcQs) {
        BigDecimal calcVal = new BigDecimal("0.000");
        for (Q src : srcQs) {
            try {
                calcVal = calcVal.add(getAnswer(period, rtn, src).responseAsBigDecimal());
            } catch (IllegalStateException e) {
                LOGGER.warn("No CO2e attributable to {}", src);
            } catch (NullPointerException e) {
                LOGGER.warn("Insufficient data to calculate CO2e from {}", src);
            }
        }
        return getAnswer(period, rtn, trgtQName).derived(true).response(calcVal.toPlainString());
    }

    private Optional<Answer> crunchCO2e(String period, SurveyReturn rtn, Q srcQ,
            BigDecimal cFactor, Q trgtQ) {
        BigDecimal calcVal = new BigDecimal("0.00");
        try {
            calcVal = getAnswer(period, rtn, srcQ).responseAsBigDecimal()
                    .multiply(cFactor);
            return Optional.of(getAnswer(period, rtn, trgtQ).derived(true).response(calcVal.toPlainString()));
        } catch (IllegalStateException | NullPointerException e) {
            LOGGER.warn("Insufficient data to calculate CO2e from {}", srcQ);
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot calculate CO2e from {}", srcQ);
        }
        return Optional.empty();
    }

    CarbonFactor cFactor(CarbonFactors cfName, String period) {
        LOGGER.info("Find Carbon Factor {} for period {}", cfName, period);
        for (CarbonFactor cfactor : cfactors) {
            if (cfName.name().equals(cfactor.name()) && period.equals(cfactor.applicablePeriod())) {
                return cfactor;
            }
        }
        LOGGER.error("Unable to find Carbon Factor {} for period {}", cfName, period);
        throw new ObjectNotFoundException(CarbonFactor.class, cfName.name() + "_" + period);
    }

    private Answer crunchWeighting(String period, SurveyReturn rtn, BigDecimal nonPaySpend,
            Q srcQ, WeightingFactor wFactor, Q trgtQ) {
        BigDecimal calcVal = new BigDecimal("0.00");
        try {
            if (BigDecimal.ZERO.equals(getAnswer(period, rtn, srcQ).responseAsBigDecimal())) {
                LOGGER.info("No directly entered spend {}, estimate from non-pay spend", srcQ);
                calcVal = nonPaySpend.multiply(wFactor.proportionOfTotal());
                LOGGER.info("Estimated {} from non-pay spend as {}", srcQ, calcVal);
                getAnswer(period, rtn, srcQ).derived(true).response(calcVal.toPlainString());
            } else {
                LOGGER.info("Have directly entered spend {}, no need to estimate", srcQ);
                calcVal = getAnswer(period, rtn, srcQ).responseAsBigDecimal();
            }
            calcVal = calcVal.multiply(wFactor.intensityValue());
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot estimate CO2e from spend");
        }
        LOGGER.info("Calculated {} emissions as {}", trgtQ, calcVal);
        return getAnswer(period, rtn, trgtQ).derived(true).response(calcVal.toPlainString());
    }

    private WeightingFactor wFactor(WeightingFactors wName, String period, String orgType) {
        LOGGER.info("Looking up weighting factor '{}' for '{}' in '{}'",
                wName, orgType, period);
        switch (orgType.toLowerCase()) {
        case "acute":
        case "acute - small":
        case "acute - medium":
        case "acute - large":
        case "acute - teaching":
        case "acute - specialist":
        case "acute - multi-service":
        case "acute trust":
            orgType = "Acute";
            break;
        case "ambulance":
        case "ambulance trust":
            orgType = "Ambulance";
            break;
        case "care":
        case "adult social care":
        case "social care":
            orgType = "Care";
            break;
        case "clinical commissioning group":
            orgType = "Clinical commissioning group";
            break;
        case "community":
            orgType = "Community";
            break;
        case "mental health":
        case "mental health / learning disability":
        case "mental health learning disability":
        case "mental health and learning disability":
            orgType = "Mental health learning disability";
            break;
        case "independent sector":
            break;
        case "social enterprise":
            break;
        default:
            LOGGER.error("Unhandled org type when looking up weighting factor '{}' for '{}' in '{}'",
                    wName, orgType, period);
            throw new ObjectNotFoundException(OrganisationType.class, orgType);
        }

        for (WeightingFactor wfactor : wfactors) {
            if (wName.name().equals(wfactor.category())
                    && period.equals(wfactor.applicablePeriod())
                    && orgType.equalsIgnoreCase(wfactor.orgType())) {
                return wfactor;
            }
        }
        LOGGER.info(
                "Unable to find exact Weighting Factor, now looking for '{}' and org type '{}' alone",
                wName, orgType);
        for (WeightingFactor wfactor : wfactors) {
            if (wName.name().equals(wfactor.category())
                    && orgType.equalsIgnoreCase(wfactor.orgType())) {
                return wfactor;
            }
        }
        LOGGER.error("No weighting factor '{}' found for '{}' in '{}'",
                wName, orgType, period);
        throw new ObjectNotFoundException(WeightingFactor.class,
                String.format("%1$s-%2$s-%3$s", wName, period, orgType));
    }

}
