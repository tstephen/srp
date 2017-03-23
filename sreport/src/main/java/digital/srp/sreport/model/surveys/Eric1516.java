package digital.srp.sreport.model.surveys;

import java.util.Arrays;

import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyQuestion;
import digital.srp.sreport.model.returns.EricQuestions;

/**
 * 
 * @author Tim Stephenson
 */
public class Eric1516 implements EricQuestions {
    
    public static final String ID = "Eric-201516";

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("orgCode").label(ORG_CODE).required(true),
                        new SurveyQuestion().name("orgName").label(ORG_NAME).required(true),
                        new SurveyQuestion().name("orgCommissioningRegion").label(COMMISSIONING_REGION).required(true),
                        new SurveyQuestion().name("orgType").label(ORG_TYPE).required(true)
                ));

        SurveyCategory catProfile = new SurveyCategory()
                .name("Trust Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("noAcuteSites").label(NO_ACUTE_SITES).required(true),
                        new SurveyQuestion().name("noSpecialistSites").label(NO_SPECIALIST_SITES).required(true),
                        new SurveyQuestion().name("noMixedSites").label(NO_MIXED_SITES).required(true),
                        new SurveyQuestion().name("noMentalHealthSites").label(NO_MENTAL_HEALTH_SITES).required(true),
                        new SurveyQuestion().name("noLDSites").label(NO_LD_SITES).required(true),
                        new SurveyQuestion().name("noMentalHealthLDSites").label(NO_MENTAL_HEALTH_LD_SITES).required(true),
                        new SurveyQuestion().name("noCommunityHosiptalSites").label(NO_COMMUNITY_HOSPITAL_SITES).required(true),
                        new SurveyQuestion().name("noOtherInpatientSites").label(NO_OTHER_INPATIENT_SITES).required(true),
                        new SurveyQuestion().name("noOutPatientSites").label(NO_OUTPATIENT_SITES).required(true),
                        new SurveyQuestion().name("noSupportSites").label(NO_SUPPORT_SITES).required(true),
                        new SurveyQuestion().name("noOtherSites").label(NO_OTHER_SITES).required(true)
                ));
        
        SurveyCategory catStrategy = new SurveyCategory()
                .name("Strategies and Policies")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("estatesDevStrategy").label(ESTATES_DEV_STRATEGY).required(true),
                        new SurveyQuestion().name("healthyTransportPlan").label(HEALTHY_TRANSPORT_PLAN).required(true),
                        new SurveyQuestion().name("boardAdaptationPlan").label(BOARD_ADAPTATION_PLAN).required(true),
                        new SurveyQuestion().name("sdmpCrmp").label(SDMP_CRMP).required(true),
                        new SurveyQuestion().name("carbonReductionTarget").label(CARBON_REDUCTION_TARGET).required(true),
                        new SurveyQuestion().name("pfaAssessment").label(PFA_ASSESSMENT).required(true),
                        new SurveyQuestion().name("pfaActionPlan").label(PFA_ACTION_PLAN).required(true)
                ));

        SurveyCategory catContractingOut = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("contractingOutVal").label(CONTRACTING_OUT_VAL).required(true),
                        new SurveyQuestion().name("contractingOutPct").label(CONTRACTING_OUT_PCT).required(true)
                ));

        SurveyCategory catFinance = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("capitalNewBuild").label(CAPITAL_NEW_BUILD).required(true),
                        new SurveyQuestion().name("capitalImprovingExisting").label(CAPITAL_IMPROVING_EXISTING).required(true),
                        new SurveyQuestion().name("capitalEquipment").label(CAPITAL_EQUIPMENT).required(true),
                        new SurveyQuestion().name("privateSector").label(PRIVATE_SECTOR).required(true),
                        new SurveyQuestion().name("backogMaintenanceVal").label(BACKLOG_MAINTENANCE_VAL).required(true),
                        new SurveyQuestion().name("pfaActionPlanVal").label(PFA_ACTION_PLAN_VAL).required(true),
                        new SurveyQuestion().name("nonEmergencyTransportVal").label(NON_EMERGENCY_TRANSPORT_VAL).required(true),
                        new SurveyQuestion().name("incomeCatering").label(INCOME_CATERING).required(true),
                        new SurveyQuestion().name("incomeLaundry").label(INCOME_LAUNDRY).required(true),
                        new SurveyQuestion().name("incomeOther").label(INCOME_OTHER).required(true)
                ));
        
        SurveyCategory catSafety = new SurveyCategory()
                .name("Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("noRiddorIncidents").label(NO_RIDDOR_INCIDENTS).required(true),
                        new SurveyQuestion().name("noFmIncidents").label(NO_FM_INCIDENTS).required(true),
                        new SurveyQuestion().name("noFmClinicalIncidents").label(NO_FM_CLINICAL_INCIDENTS).required(true)
                ));

        SurveyCategory catFireSafety = new SurveyCategory()
                .name("Fire Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().name("noFires").label(NO_FIRES).required(true),
                        new SurveyQuestion().name("noFalseAlarms").label(NO_FALSE_ALARMS).required(true),
                        new SurveyQuestion().name("noFireDeaths").label(NO_DEATHS_FROM_FIRE).required(true),
                        new SurveyQuestion().name("noFireInjuries").label(NO_INJURIES_FROM_FIRE).required(true),
                        new SurveyQuestion().name("noEvacInjuries").label(NO_INJURIES_DURING_EVACUATION).required(true)
                ));
        
        Survey survey = new Survey().name(ID).status(StatusType.Published.name())
                .applicablePeriod("2015-16")
                .categories(Arrays.asList(catOrg, catProfile, catStrategy, catContractingOut, catFinance, catSafety, catFireSafety));
        return survey;
    }
}