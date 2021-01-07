package digital.srp.sreport.model.surveys;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;

/**
 * Marker interface for surveys that provides a factory for (@code Survey)s.
 * 
 * @author Tim Stephenson
 */
public interface SurveyFactory {

    Survey getSurvey();
    Q[] getQs();

    static SurveyFactory getInstance(String name) {
        switch (name) {
        case Eric1516.ID:
            return Eric1516.getInstance();
        case Sdu1516.ID:
            return Sdu1516.getInstance();
        case Sdu1617.ID:
            return Sdu1617.getInstance();
        case Sdu1718.ID:
            return Sdu1718.getInstance();
        case Sdu1819.ID:
            return Sdu1819.getInstance();
        case Sdu1920.ID:
            return Sdu1920.getInstance();
        case Sdu2021.ID:
            return Sdu2021.getInstance();
        default:
            throw new ObjectNotFoundException(Survey.class,
                    String.format("No survey named %1$s is known", name));
        }
    }

}
