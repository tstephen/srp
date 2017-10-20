package digital.srp.sreport.api;

import digital.srp.sreport.model.SurveyReturn;

public interface CompletenessValidator {

    SurveyReturn validate(final SurveyReturn rtn);

}
