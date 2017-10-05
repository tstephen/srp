package digital.srp.sreport.api;

import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.services.AnswerFactory;

public interface Calculator {

    SurveyReturn calculate(SurveyReturn rtn, int yearsToCalc, AnswerFactory answerFactory);

}