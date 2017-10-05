package digital.srp.sreport.services;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;

public interface AnswerFactory {

    Answer addAnswer(SurveyReturn rtn, String period, Q q);
}
