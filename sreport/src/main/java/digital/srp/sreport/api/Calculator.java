package digital.srp.sreport.api;

import digital.srp.sreport.model.SurveyReturn;

public interface Calculator {

    SurveyReturn calculate(final SurveyReturn rtn, final int yearsToCalc);
}