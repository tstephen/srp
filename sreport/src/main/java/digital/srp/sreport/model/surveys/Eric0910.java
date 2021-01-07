package digital.srp.sreport.model.surveys;

import java.util.ArrayList;
import java.util.List;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.returns.EricQuestions;

/**
 * 
 * @author Tim Stephenson
 */
public class Eric0910 implements EricQuestions, SurveyFactory {
    public static final String PERIOD = "2009-10";
    public static final String ID = "ERIC-"+PERIOD;
    private static final Eric0910 survey = new Eric0910();

    /**
     * Private constructor to prevent instantiation.
     */
    private Eric0910() {
    }

    public static SurveyFactory getInstance() {
        return survey;
    }

    public Survey getSurvey() {
        SurveyCategory catOrg = new SurveyCategory()
                .name("Organisation")
                .questionEnums(
                        Q.ORG_CODE,
                        Q.ORG_NAME,
                        Q.COMMISSIONING_REGION,
                        Q.ORG_TYPE
                );

        SurveyCategory catProfile = new SurveyCategory()
                .name("Trust Profile")
                .questionEnums(
                        Q.NO_ACUTE_SITES,
                        Q.NO_SPECIALIST_SITES,
                        Q.NO_MIXED_SITES,
                        Q.NO_MENTAL_HEALTH_SITES,
                        Q.NO_LD_SITES,
                        Q.NO_MENTAL_HEALTH_LD_SITES,
                        Q.NO_COMMUNITY_HOSPITAL_SITES,
                        Q.NO_OTHER_INPATIENT_SITES,
                        Q.NO_OUTPATIENT_SITES,
                        Q.NO_SUPPORT_SITES,
                        Q.NO_OTHER_SITES
                );
        
        SurveyCategory catStrategy = new SurveyCategory()
                .name("Strategies and Policies")
                .questionEnums(
                        Q.ESTATES_DEV_STRATEGY,
                        Q.HEALTHY_TRANSPORT_PLAN,
                        Q.BOARD_ADAPTATION_PLAN,
                        Q.SDMP_CRMP,
                        Q.CARBON_REDUCTION_TARGET,
                        Q.PFA_ASSESSMENT,
                        Q.PFA_ACTION_PLAN
                );

        SurveyCategory catContractingOut = new SurveyCategory()
                .name("Contracted Out Services")
                .questionEnums(
                        Q.CONTRACTING_OUT_VAL,
                        Q.CONTRACTING_OUT_PCT
                );

        SurveyCategory catFinance = new SurveyCategory()
                .name("Finance")
                .questionEnums(
                        Q.CAPITAL_NEW_BUILD,
                        Q.CAPITAL_IMPROVING_EXISTING,
                        Q.CAPITAL_EQUIPMENT,
                        Q.PRIVATE_SECTOR,
                        Q.BACKLOG_MAINTENANCE_VAL,
                        Q.PFA_ACTION_PLAN_VAL,
                        Q.NON_EMERGENCY_TRANSPORT_VAL,
                        Q.INCOME_CATERING,
                        Q.INCOME_LAUNDRY,
                        Q.INCOME_OTHER
                );

        SurveyCategory catWaste = new SurveyCategory()
                .name("Waste")
                .questionEnums(
                        Q.WEEE_WEIGHT,
                        Q.OTHER_RECOVERY_WEIGHT
                );
        
        SurveyCategory catSafety = new SurveyCategory()
                .name("Safety")
                .questionEnums(
                        Q.NO_RIDDOR_INCIDENTS,
                        Q.NO_FM_INCIDENTS,
                        Q.NO_FM_CLINICAL_INCIDENTS
                );

        SurveyCategory catFireSafety = new SurveyCategory()
                .name("Fire Safety")
                .questionEnums(
                        Q.NO_FIRES,
                        Q.NO_FALSE_ALARMS,
                        Q.NO_FIRE_DEATHS,
                        Q.NO_FIRE_INJURIES,
                        Q.NO_EVAC_INJURIES
                );
        
        Survey survey = new Survey().name(ID).status(StatusType.Published.name())
                .applicablePeriod(PERIOD)
                .categories(catOrg, catProfile, catStrategy, catContractingOut,
                        catFinance, catWaste, catSafety, catFireSafety);
        return survey;
    }

    public Q[] getQs() {
        ArrayList<Q> list = new ArrayList<Q>();
        for (SurveyCategory category : getSurvey().categories()) {
            list.addAll(category.questionEnums());
        }
        list.addAll(getDerivedQs());
        return list.toArray(new Q[list.size()]);
    }

    // Not a complete set but the ones expected by calculations 
    private List<Q> getDerivedQs() {
        List<Q> list = new ArrayList<Q>();
        list.add(Q.OTHER_RECOVERY_CO2E);
        list.add(Q.WEEE_WEIGHT_CO2E);
        return list;
    }
}
