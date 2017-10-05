package digital.srp.sreport.services;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;

public class MemoryAnswerFactory implements AnswerFactory {

    @Override
    public Answer addAnswer(SurveyReturn rtn, String period, Q q) {
        Answer answer = new Answer()
                .addSurveyReturn(rtn)
                .applicablePeriod(period)
                .question(q)
                .derived(true);
        rtn.answers().add(answer);
        return answer;
    }

}
