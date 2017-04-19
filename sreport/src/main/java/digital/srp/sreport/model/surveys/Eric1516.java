package digital.srp.sreport.model.surveys;

import java.util.Arrays;

import digital.srp.sreport.model.Q;
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
    
    public static final String PERIOD = "2015-16";
    public static final String ID = "Eric-"+PERIOD;

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.ORG_CODE).label(ORG_CODE).required(true),
                        new SurveyQuestion().q(Q.ORG_NAME).label(ORG_NAME).required(true),
                        new SurveyQuestion().q(Q.COMMISSIONING_REGION).label(COMMISSIONING_REGION).required(true),
                        new SurveyQuestion().q(Q.ORG_TYPE).label(ORG_TYPE).required(true)
                ));

        SurveyCategory catProfile = new SurveyCategory()
                .name("Trust Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.NO_ACUTE_SITES).label(NO_ACUTE_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_SPECIALIST_SITES).label(NO_SPECIALIST_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_MIXED_SITES).label(NO_MIXED_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_MENTAL_HEALTH_SITES).label(NO_MENTAL_HEALTH_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_LD_SITES).label(NO_LD_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_MENTAL_HEALTH_LD_SITES).label(NO_MENTAL_HEALTH_LD_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_COMMUNITY_HOSPITAL_SITES).label(NO_COMMUNITY_HOSPITAL_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_OTHER_INPATIENT_SITES).label(NO_OTHER_INPATIENT_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_OUTPATIENT_SITES).label(NO_OUTPATIENT_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_SUPPORT_SITES).label(NO_SUPPORT_SITES).required(true),
                        new SurveyQuestion().q(Q.NO_OTHER_SITES).label(NO_OTHER_SITES).required(true)
                ));
        
        SurveyCategory catStrategy = new SurveyCategory()
                .name("Strategies and Policies")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.ESTATES_DEV_STRATEGY).label(ESTATES_DEV_STRATEGY).required(true),
                        new SurveyQuestion().q(Q.HEALTHY_TRANSPORT_PLAN).label(HEALTHY_TRANSPORT_PLAN).required(true),
                        new SurveyQuestion().q(Q.ADAPTATION_PLAN_INC).label(BOARD_ADAPTATION_PLAN).required(true),
                        new SurveyQuestion().q(Q.SDMP_CRMP).label(SDMP_CRMP).required(true),
                        new SurveyQuestion().q(Q.CARBON_REDUCTION_TARGET).label(CARBON_REDUCTION_TARGET).required(true),
                        new SurveyQuestion().q(Q.PFA_ASSESSMENT).label(PFA_ASSESSMENT).required(true),
                        new SurveyQuestion().q(Q.PFA_ACTION_PLAN).label(PFA_ACTION_PLAN).required(true)
                ));

        SurveyCategory catContractingOut = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.CONTRACTING_OUT_VAL).label(CONTRACTING_OUT_VAL).required(true),
                        new SurveyQuestion().q(Q.CONTRACTING_OUT_PCT).label(CONTRACTING_OUT_PCT).required(true)
                ));

        SurveyCategory catFinance = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.CAPITAL_NEW_BUILD).label(CAPITAL_NEW_BUILD).required(true),
                        new SurveyQuestion().q(Q.CAPITAL_IMPROVING_EXISTING).label(CAPITAL_IMPROVING_EXISTING).required(true),
                        new SurveyQuestion().q(Q.CAPITAL_EQUIPMENT).label(CAPITAL_EQUIPMENT).required(true),
                        new SurveyQuestion().q(Q.PRIVATE_SECTOR).label(PRIVATE_SECTOR).required(true),
                        new SurveyQuestion().q(Q.BACKLOG_MAINTENANCE_VAL).label(BACKLOG_MAINTENANCE_VAL).required(true),
                        new SurveyQuestion().q(Q.PFA_ACTION_PLAN_VAL).label(PFA_ACTION_PLAN_VAL).required(true),
                        new SurveyQuestion().q(Q.NON_EMERGENCY_TRANSPORT_VAL).label(NON_EMERGENCY_TRANSPORT_VAL).required(true),
                        new SurveyQuestion().q(Q.INCOME_CATERING).label(INCOME_CATERING).required(true),
                        new SurveyQuestion().q(Q.INCOME_LAUNDRY).label(INCOME_LAUNDRY).required(true),
                        new SurveyQuestion().q(Q.INCOME_OTHER).label(INCOME_OTHER).required(true)
                ));
        
        SurveyCategory catSafety = new SurveyCategory()
                .name("Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.NO_RIDDOR_INCIDENTS).label(NO_RIDDOR_INCIDENTS).required(true),
                        new SurveyQuestion().q(Q.NO_FM_INCIDENTS).label(NO_FM_INCIDENTS).required(true),
                        new SurveyQuestion().q(Q.NO_FM_CLINICAL_INCIDENTS).label(NO_FM_CLINICAL_INCIDENTS).required(true)
                ));

        SurveyCategory catFireSafety = new SurveyCategory()
                .name("Fire Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().q(Q.NO_FIRES).label(NO_FIRES).required(true),
                        new SurveyQuestion().q(Q.NO_FALSE_ALARMS).label(NO_FALSE_ALARMS).required(true),
                        new SurveyQuestion().q(Q.NO_FIRE_DEATHS).label(NO_DEATHS_FROM_FIRE).required(true),
                        new SurveyQuestion().q(Q.NO_FIRE_INJURIES).label(NO_INJURIES_FROM_FIRE).required(true),
                        new SurveyQuestion().q(Q.NO_EVAC_INJURIES).label(NO_INJURIES_DURING_EVACUATION).required(true)
                ));
        
        Survey survey = new Survey().name(ID).status(StatusType.Published.name())
                .applicablePeriod("2015-16")
                .categories(Arrays.asList(catOrg, catProfile, catStrategy, catContractingOut, catFinance, catSafety, catFireSafety));
        return survey;
    }
}