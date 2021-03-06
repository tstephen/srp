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
package digital.srp.sreport.model.surveys;

import java.util.ArrayList;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.returns.EricQuestions;

/**
 *
 * @author Tim Stephenson
 */
public class Eric1516 implements EricQuestions, SurveyFactory {
    public static final String PERIOD = "2015-16";
    public static final String ID = "ERIC-"+PERIOD;
    private static final Eric1516 eric1516 = new Eric1516();

    /**
     * Private constructor to prevent instantiation.
     */
    private Eric1516() {
    }

    public static SurveyFactory getInstance() {
        return eric1516;
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
                        catFinance, catSafety, catFireSafety);
        return survey;
    }

    public Q[] getQs() {
        ArrayList<Q> list = new ArrayList<Q>();
        for (SurveyCategory category : getSurvey().categories()) {
            list.addAll(category.questionEnums());
        }
        return list.toArray(new Q[list.size()]);
    }
}
