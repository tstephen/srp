package digital.srp.sreport.model.surveys;

public interface SduQuestions {

    String COMMISSIONING_SPEND = "commissioningSpend";
    String TRAVEL_SPEND = "travelSpend";
    String PHARMA_SPEND = "pharmaSpend";
    String PAPER_SPEND = "paperSpend";
    String OTHER_SPEND = "otherSpend";
    String OTHER_MANUFACTURED_SPEND = "otherManufacturedSpend";
    String MED_INSTRUMENTS_SPEND = "medInstrumentsSpend";
    String CHEM_AND_GAS_SPEND = "chemAndGasSpend";
    String ICT_SPEND = "ictSpend";
    String FREIGHT_SPEND = "freightSpend";
    String CATERING_SPEND = "cateringSpend";
    String CONSTRUCTION_SPEND = "constructionSpend";
    String BIZ_SVC_SPEND = "bizSvcSpend";
    String LEASED_ASSETS_ENERGY_USE = "leasedAssetsEnergyUse";
    String ELEC_OWNED_RENEWABLE_CONSUMPTION = "elecOwnedRenewableConsumption";
    String WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION = "woodPelletsOwnedRenewableConsumption";
    String WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION = "woodChipsOwnedRenewableConsumption";
    String WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION = "woodLogsOwnedRenewableConsumption";
    String EXPORTED_THERMAL_ENERGY = "exportedThermalEnergy";
    String CHP_ELECTRICAL_OUTPUT = "chpElectricalOutput";
    String PORTABLE_NITROUS_OXIDE_MIX = "portableNitrousOxideMix";
    String NITROUS_OXIDE = "nitrousOxide";
    String SEVOFLURANE = "sevoflurane";
    String ISOFLURANE = "isoflurane";
    String DESFLURANE = "desflurane";
    String STAFF_COMMUTE_MILES = "staffCommuteMiles";
    String TOTAL_EMPLOYEES = "totalEmployees";
    String VISITOR_MILEAGE = "visitorMileage";
    String PATIENT_MILEAGE = "patientMileage";
    String LONG_HAUL_AIR_MILES = "longHaulAirMiles";
    String SHORT_HAUL_AIR_MILES = "shortHaulAirMiles";
    String DOMESTIC_AIR_MILES = "domesticAirMiles";
    String RAIL_MILES = "railMiles";
    String LEASED_FLEET_TRAVEL = "leasedFleetTravel";
    String OWNED_FLEET_TRAVEL = "ownedFleetTravel";
    String BIZ_MILEAGE_ROAD = "bizMileageRoad";
    String WASTE_WATER = "wasteWater";
    String RECYCLING_WEIGHT = "recyclingWeight";
    String COMPOSTED_WEIGHT = "compostedWeight";
    String REUSE_WEIGHT = "reuseWeight";
    String NON_BURN_WEIGHT = "nonBurnWeight";
    String WEEE_WEIGHT = "weeeWeight";
    String LANDFILL_WEIGHT = "landfillWeight";
    String HIGH_TEMP_DISPOSAL_WEIGHT = "highTempDisposalWeight";
    String HIGH_TEMP_DISPOSAL_WITH_RECOVERY_WEIGHT = "highTempDisposalWithRecoveryWeight";
    String WATER_VOL = "waterVol";
    String ELEC_EXPORTED = "elecExported";
    String RENEWABLE_USED = "renewableUsed";
    String ELEC_USED_RENEWABLE = "elecUsedRenewable";
    String HOT_WATER_USED = "hotWaterUsed";
    String STEAM_USED = "steamUsed";
    String ELEC_USED_LOCAL = "elecUsedLocal";
    String COAL_USED = "coalUsed";
    String OIL_USED = "oilUsed";
    String GAS_USED = "gasUsed";
    String ELEC_USED = "elecUsed";
    String CAPITAL_SPEND = "capitalSpend";
    String NON_PAY_SPEND = "nonPaySpend";
    String OP_EX = "opEx";
    String WASTE_RECYLING_COST = "wasteRecylingCost";
    String WATER_COST = "waterCost";
    String TOTAL_ENERGY_COST = "totalEnergyCost";
    String CCGS_SERVED = "ccgsServed";
    String ADAPTATION_PLAN_INCLUDED = "adaptationPlanIncluded";
    String BOARD_SUSTAINABILITY_AS_RISK = "boardSustainabilityAsRisk";
    String BOARD_LEAD_FOR_SUSTAINABILITY = "boardLeadForSustainability";
    String PATIENT_CONTACT_MEASURE = "patientContactMeasure";
    String CARBON_REDUCTION_DEADLINE_YEAR = "carbonReductionDeadlineYear";
    String CARBON_REDUCTION_TARGET_PCT = "carbonReductionTargetPct";
    String CARBON_REDUCTION_BASE_YEAR = "carbonReductionBaseYear";
    String CARBON_REDUCTION_BASELINE_USED = "carbonReductionBaselineUsed";
    String CARBON_REDUCTION_TARGET = "carbonReductionTarget";
    String ECLASS_USER = "eClassUser";
    String PROMOTE_SUSTAINABILITY_TO_STAFF = "promoteSustainabilityToStaff";
    String LAST_GCC_SCORE = "lastGccScore";
    String LAST_GCC_DATE = "lastGccDate";
    String GCC_USER = "gccUser";
    String STRATEGIC_SUSTAINABILITY_PARTNERS = "strategicSustainabilityPartners";
    String STRATEGIC_SUSTAINABILITY_PARTNERSHIPS = "strategicSustainabilityPartnerships";
    String PROCUREMENT_SUPPLIER_SUSTAINABILITY = "procurementSupplierSustainability";
    String PROCUREMENT_SOCIAL_ASSESSMENT = "procurementSocialAssessment";
    String PROCUREMENT_ENVIRONMENTAL_ASSESSMENT = "procurementEnvironmentalAssessment";
    String PROMOTE_HEALTHY_TRAVEL = "promoteHealthyTravel";
    String HEALTHY_TRANSPORT_PLAN = "healthyTransportPlan";
    String SDMP_MISSION_STATEMENT = "sdmpMissionStatement";
    String SDMP_BOARD_REVIEWED_WITHIN12_MONTHS = "sdmpBoardReviewedWithin12Months";
    String SDMP_CRMP = "sdmpCrmp";
    String NO_PATIENT_CONTACTS = "noPatientContacts";
    String NO_BEDS = "noBeds";
    String NO_STAFF = "noStaff";
    String POPULATION = "population";
    String FLOOR_AREA = "floorArea";
    String ORG_TYPE = "orgType";
    String ORG_NICKNAME = "orgNickname";
    String ORG_CODE = "orgCode";
    String ORG_NAME = "orgName";

    String THIRD_PARTY_ADDITIONAL_PCT = "3rdPartyAdditionalPct";
    String GREEN_TARIFF_ADDITIONAL_PCT = "greenTariffAdditionalPct";
    String PORTABLE_NITROUS_OXIDE_MIX_MATERNITY = "portableNitrousOxideMixMaternity";
    String INCINERATION_WEIGHT = "incinerationWeight";
    String OTHER_RECOVERY_WEIGHT = "otherRecoveryWeight";
    String ADAPTATION_PERF = "adaptationPerf";
    String BENCHMARK_PERF = "benchmarkPerf";
    String TRAJECTORY_PERF = "trajectoryPerf";
    String WATER_CONTEXT = "waterContext";
    String WATER_PERF = "waterPerf";
    String WASTE_CONTEXT = "wasteContext";
    String WASTE_PERF = "wastePerf";
    String TRAVEL_CONTEXT = "travelContext";
    String TRAVEL_PERF = "travelPerf";
    String ENERGY_CONTEXT = "energyContext";
    String ENERGY_PERF = "energyPerf";
    String SIA = "sia";
    String MOD_SLAVERY = "modSlavery";
    String QUANT_TRAVEL_IMPACTS = "quantTravelImpacts";
    String QUANT_ES_IMPACTS = "quantESImpacts";
    
    /* CALCULATED */
    String ELEC_RENEWABLE = "elecRenewable";
    String ELEC_RENEWABLE_CO2E = "elecRenewableCo2e";
    String HOT_WATER_CO2E = "hotWaterCO2e";

    String STEAM_CO2E = "steamCO2e";

    String COAL_CO2E = "coalCO2e";

    String OIL_CO2E = "oilCO2e";

    String GAS_CO2E = "gasCO2e";

    String ELEC_CO2E = "elecCO2e";

    String ELEC_USED_GREEN_TARIFF = "elecUsedGreenTariff";
    
//    sum PATIENT_MILEAGE, VISITOR_MILEAGE
    String PATIENT_AND_VISITOR_MILEAGE = "patientAndVisitorMileage";
 //  sum LONG_HAUL_AIR_MILES, SHORT_HAUL_AIR_MILES,
    // DOMESTIC_AIR_MILES, RAIL_MILES, LEASED_FLEET_TRAVEL, 
    // OWNED_FLEET_TRAVEL, BIZ_MILEAGE_ROAD
    String BIZ_MILEAGE = "bizMileage";
    String PATIENT_AND_VISITOR_MILEAGE_CO2E = "patientAndVisitorMileageCo2e";
    String BIZ_MILEAGE_CO2E = "bizMileageCo2e";
    String STAFF_COMMUTE_MILES_CO2E = "staffMilesCo2e";
    
    String RECYCLING_CO2E  = "recyclingCo2e";
    String OTHER_RECOVERY_CO2E  = "otherRecoverCo2e";
    String INCINERATION_CO2E = "incinerationCo2e";
    String LANDFILL_CO2E = "landfillCo2e";
    
    String COMMISSIONING_CO2E = "commissioningCo2e";
    String ANAESTHETIC_GASES_CO2E = "anaestheticGasesCo2e";
    String PHARMA_CO2E = "pharmaCo2e";
    String PAPER_CO2E = "paperCo2e";
    String OTHER_PROCUREMENT_CO2E = "otherProcurementCo2e";
    String OTHER_MANUFACTURED_CO2E = "otherManufacturedCo2e";
    String MED_INSTR_CO2E = "medInstrCo2e";
    String CHEMS_AND_GASES_CO2E = "chemsAndGasesCo2e";
    String ICT_CO2E = "ictCo2e";
    String FREIGHT_CO2E = "freightCo2e";
    String CATERING_CO2E = "cateringCo2e";
    String CONSTUCTION_CO2E = "constructionCo2e";
    String BIZ_SVCS_CO2E = "bizSvcsCo2e";
    String CAPITAL_CO2E = "capitalCo2e";
    String WATER_CO2E = "waterCo2e";
    String WASTE_CO2E = "wasteCo2e";
    String TRAVEL_CO2E = "travelCo2e";

}
