/*******************************************************************************
 *Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.server.model.mixins;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.views.InterventionTypeViews;
import digital.srp.macc.views.InterventionViews;
import digital.srp.macc.views.ModelParameterViews;
import digital.srp.macc.views.OrganisationInterventionViews;
import digital.srp.macc.views.OrganisationTypeViews;
import digital.srp.sreport.model.views.QuestionViews;
import digital.srp.sreport.model.views.HealthCheckViews;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.model.views.SurveyViews;

public abstract class SrpLinkMixIn {

    @JsonView( {
        // macc
        InterventionViews.Summary.class,
        InterventionTypeViews.Detailed.class,
        ModelParameterViews.Summary.class,
        OrganisationInterventionViews.Summary.class,
        OrganisationTypeViews.Summary.class,

        // sreport
        QuestionViews.Summary.class,
        SurveyViews.Summary.class,
        SurveyReturnViews.Summary.class,
        HealthCheckViews.Summary.class
    } )
    private long rel;

    @JsonView( {
        // macc
        InterventionViews.Summary.class,
        InterventionTypeViews.Detailed.class,
        ModelParameterViews.Summary.class,
        OrganisationInterventionViews.Summary.class,
        OrganisationTypeViews.Summary.class,

        //sreport
        QuestionViews.Summary.class,
        SurveyViews.Summary.class,
        SurveyReturnViews.Summary.class,
        HealthCheckViews.Summary.class
    } )
    private long href;

}
