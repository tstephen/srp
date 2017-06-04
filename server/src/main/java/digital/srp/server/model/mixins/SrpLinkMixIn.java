package digital.srp.server.model.mixins;

import com.fasterxml.jackson.annotation.JsonView;

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

        // sreport
        SurveyViews.Summary.class, SurveyReturnViews.Summary.class
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

        //sreport
        SurveyViews.Summary.class, SurveyReturnViews.Summary.class
    } )
    private long href;

}
