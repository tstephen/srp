package digital.srp.sreport.model.surveys;

import digital.srp.sreport.api.exceptions.ObjectNotFoundException;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Survey;

/**
 * Marker interface for surveys that provides a factory for {@link Survey}s.
 * 
 * @author Tim Stephenson
 */
public interface SurveyFactory {

    Survey getSurvey();
    Q[] getQs();

    static SurveyFactory getInstance(String name) {
        switch (name) {
        case Eric0708.ID:
            return Eric0708.getInstance();
        case Eric0809.ID:
            return Eric0809.getInstance();
        case Eric0910.ID:
            return Eric0910.getInstance();
        case Eric1011.ID:
            return Eric1011.getInstance();
        case Eric1112.ID:
            return Eric1112.getInstance();
        case Eric1213.ID:
            return Eric1213.getInstance();
        case Eric1314.ID:
            return Eric1314.getInstance();
        case Eric1415.ID:
            return Eric1415.getInstance();
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
