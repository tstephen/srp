/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.validators;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;

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
