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
import link.omny.catalog.views.FeedbackViews;
import link.omny.catalog.views.MediaResourceViews;
import link.omny.catalog.views.OrderViews;
import link.omny.catalog.views.StockCategoryViews;
import link.omny.catalog.views.StockItemViews;
import link.omny.custmgmt.model.views.MemoViews;

public abstract class SrpLinkMixIn {

    @JsonView( {
        // catalog
        FeedbackViews.Summary.class,
        MediaResourceViews.Summary.class,
        OrderViews.Summary.class,
        StockCategoryViews.Detailed.class,
        StockItemViews.Detailed.class,

        // custmgmt
        MemoViews.Summary.class,

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
        // catalog
        FeedbackViews.Summary.class,
        MediaResourceViews.Summary.class,
        OrderViews.Summary.class,
        StockCategoryViews.Detailed.class,
        StockItemViews.Detailed.class,

        // custmgmt
        MemoViews.Detailed.class,

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
