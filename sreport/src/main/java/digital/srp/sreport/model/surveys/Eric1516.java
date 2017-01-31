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
                        new SurveyQuestion().text(ORG_CODE).required(true),
                        new SurveyQuestion().text(ORG_NAME).required(true),
                        new SurveyQuestion().text(COMMISSIONING_REGION).required(true),
                        new SurveyQuestion().text(ORG_TYPE).required(true)
                ));

        SurveyCategory catProfile = new SurveyCategory()
                .name("Trust Profile")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(NO_ACUTE_SITES).required(true),
                        new SurveyQuestion().text(NO_SPECIALIST_SITES).required(true),
                        new SurveyQuestion().text(NO_MIXED_SITES).required(true),
                        new SurveyQuestion().text(NO_MENTAL_HEALTH_SITES).required(true),
                        new SurveyQuestion().text(NO_LD_SITES).required(true),
                        new SurveyQuestion().text(NO_MENTAL_HEALTH_LD_SITES).required(true),
                        new SurveyQuestion().text(NO_COMMUNITY_HOSPITAL_SITES).required(true),
                        new SurveyQuestion().text(NO_OTHER_INPATIENT_SITES).required(true),
                        new SurveyQuestion().text(NO_OUTPATIENT_SITES).required(true),
                        new SurveyQuestion().text(NO_SUPPORT_SITES).required(true),
                        new SurveyQuestion().text(NO_OTHER_SITES).required(true)
                ));
        
        SurveyCategory catStrategy = new SurveyCategory()
                .name("Strategies and Policies")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(ESTATES_DEV_STRATEGY).required(true),
                        new SurveyQuestion().text(HEALTHY_TRANSPORT_PLAN).required(true),
                        new SurveyQuestion().text(BOARD_ADAPTATION_PLAN).required(true),
                        new SurveyQuestion().text(SDMP_CRMP).required(true),
                        new SurveyQuestion().text(CARBON_REDUCTION_TARGET).required(true),
                        new SurveyQuestion().text(PFA_ASSESSMENT).required(true),
                        new SurveyQuestion().text(PFA_ACTION_PLAN).required(true)
                ));

        SurveyCategory catContractingOut = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(CONTRACTING_OUT_VAL).required(true),
                        new SurveyQuestion().text(CONTRACTING_OUT_PCT).required(true)
                ));

        SurveyCategory catFinance = new SurveyCategory()
                .name("Contracted Out Services")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(CAPITAL_NEW_BUILD).required(true),
                        new SurveyQuestion().text(CAPITAL_IMPROVING_EXISTING).required(true),
                        new SurveyQuestion().text(CAPITAL_EQUIPMENT).required(true),
                        new SurveyQuestion().text(PRIVATE_SECTOR).required(true),
                        new SurveyQuestion().text(BACKLOG_MAINTENANCE_VAL).required(true),
                        new SurveyQuestion().text(PFA_ACTION_PLAN_VAL).required(true),
                        new SurveyQuestion().text(NON_EMERGENCY_TRANSPORT_VAL).required(true),
                        new SurveyQuestion().text(INCOME_CATERING).required(true),
                        new SurveyQuestion().text(INCOME_LAUNDRY).required(true),
                        new SurveyQuestion().text(INCOME_OTHER).required(true)
                ));
        
        SurveyCategory catSafety = new SurveyCategory()
                .name("Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(NO_RIDDOR_INCIDENTS).required(true),
                        new SurveyQuestion().text(NO_FM_INCIDENTS).required(true),
                        new SurveyQuestion().text(NO_FM_CLINICAL_INCIDENTS).required(true)
                ));

        SurveyCategory catFireSafety = new SurveyCategory()
                .name("Fire Safety")
                .questions(Arrays.asList(
                        new SurveyQuestion().text(NO_FIRES).required(true),
                        new SurveyQuestion().text(NO_FALSE_ALARMS).required(true),
                        new SurveyQuestion().text(NO_DEATHS_FROM_FIRE).required(true),
                        new SurveyQuestion().text(NO_INJURIES_FROM_FIRE).required(true),
                        new SurveyQuestion().text(NO_INJURIES_DURING_EVACUATION).required(true)
                ));
        
        Survey survey = new Survey().name(ID).status(StatusType.Published.name())
                .applicablePeriod("2015-16")
                .categories(Arrays.asList(catOrg, catProfile, catStrategy, catContractingOut, catFinance, catSafety, catFireSafety));
        return survey;
    }
}