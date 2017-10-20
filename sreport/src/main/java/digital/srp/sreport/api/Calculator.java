package digital.srp.sreport.api;

import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.services.AnswerFactory;

public interface Calculator {

    SurveyReturn calculate(final SurveyReturn rtn, final int yearsToCalc, final AnswerFactory answerFactory);
}