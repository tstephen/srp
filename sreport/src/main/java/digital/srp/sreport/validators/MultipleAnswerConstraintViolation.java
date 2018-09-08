package digital.srp.sreport.validators;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.SurveyReturn;

public class MultipleAnswerConstraintViolation
        implements ConstraintViolation<SurveyReturn> {

    private final SurveyReturn rootBean;
    private final Set<Answer> leafBean;

    public MultipleAnswerConstraintViolation(SurveyReturn rtn, Set<Answer> answers) {
        this.rootBean = rtn;
        this.leafBean = answers;
    }
    
    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        Answer a = null;
        for (Answer answer : leafBean) {
            if (a == null) a = answer;
            sb.append(answer.id()).append(",");
        }
        return String.format(getMessageTemplate(),
                a.question().name(), rootBean.org(), a.applicablePeriod(), sb.toString());
    }

    @Override
    public String getMessageTemplate() {
        return "Multiple answers to %1$s for %2$s in %3$s. Review ids: %4$s";
    }

    @Override
    public SurveyReturn getRootBean() {
        return rootBean;
    }

    @Override
    public Class<SurveyReturn> getRootBeanClass() {
        return SurveyReturn.class;
    }

    @Override
    public Object getLeafBean() {
        return leafBean;
    }

    @Override
    public Object[] getExecutableParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getExecutableReturnValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Path getPropertyPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getInvalidValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
