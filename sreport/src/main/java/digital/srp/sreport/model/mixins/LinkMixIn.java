package digital.srp.sreport.model.mixins;

import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.model.views.SurveyViews;

public abstract class LinkMixIn {


    @JsonView( {SurveyViews.Summary.class, SurveyReturnViews.Summary.class} )
    private long rel;

    @JsonView( {SurveyViews.Summary.class, SurveyReturnViews.Summary.class} )
    private long href;
    
}
