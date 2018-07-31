package digital.srp.server.model.mixins;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.views.InterventionTypeViews;
import digital.srp.macc.views.InterventionViews;
import digital.srp.macc.views.ModelParameterViews;
import digital.srp.macc.views.OrganisationInterventionViews;
import digital.srp.macc.views.OrganisationTypeViews;
import digital.srp.sreport.model.views.QuestionViews;
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
        SurveyReturnViews.Summary.class
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
        SurveyReturnViews.Summary.class
    } )
    private long href;

}
