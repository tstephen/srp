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
package digital.srp.sreport.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import digital.srp.sreport.internal.PeriodUtil;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.SurveyFactory;
import digital.srp.sreport.validators.DuplicateAnswerConstraintViolation;
import digital.srp.sreport.validators.MultipleAnswerConstraintViolation;

@Component
public class HealthChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthChecker.class);

    @Autowired
    protected final AnswerFactory answerFactory;

    public HealthChecker(final AnswerFactory answerFactory) {
        this.answerFactory = answerFactory;
    }

    public Set<ConstraintViolation<SurveyReturn>> validate(SurveyReturn rtn) {
        Set<ConstraintViolation<SurveyReturn>> issues = new HashSet<ConstraintViolation<SurveyReturn>>();
        for (Q q : rtn.survey().questionCodes()) {
            for (String period : rtn.getIncludedPeriods()) {
                issues.addAll(validateAnswer(rtn, period, q));
            }
        }
        return issues;
    }

    private Set<ConstraintViolation<SurveyReturn>> validateAnswer(SurveyReturn rtn, String period, Q q) {
        Set<ConstraintViolation<SurveyReturn>> violations;
        List<Answer> matches = new ArrayList<Answer>();
        for (Answer a : rtn.answers()) {
            if (q.name().equals(a.question().name()) && period.equals(a.applicablePeriod())) {
                matches.add(a);
            }
        }

        LOGGER.debug("found {} matches for {} of {} in {}", matches.size(), q.name(), rtn.org(), period);
        switch (matches.size()) {
        case 0:
//            violations = new HashSet<ConstraintViolation<SurveyReturn>>();
//            violations.add(new MissingAnswerConstraintViolation(rtn,
//                    Collections.singleton(new Answer().applicablePeriod(period).question(q))));
            violations = Collections.emptySet();
            break;
        case 1:
            Answer a1 = matches.get(0);
            LOGGER.info("Found answer: {}={} to {} for {} in {}",
                    a1.id(), a1.response(), q.name(), rtn.org(), period);
            violations = Collections.emptySet();
            break;
        default:
            Answer a = matches.get(0);
            StringBuffer sb = new StringBuffer();
            HashSet<Answer> matchSet = new HashSet<Answer>();
            matchSet.addAll(matches);
            violations = new HashSet<ConstraintViolation<SurveyReturn>>();
            for (int i = 1; i < matches.size(); i++) {
                Answer b = matches.get(i);
                if (a.response() == b.response()
                        || (b.response() != null && b.response().equals(a.response()))) {
                    LOGGER.warn("Detected duplicate answer: {}={} to {} for {} in {}",
                            b.id(), b.response(), q.name(), rtn.org(), period);
                    violations.add(new DuplicateAnswerConstraintViolation(rtn, matchSet));
                } else {
                    sb.append(b.id() == null ? "" : b.id()).append(",");
                }
            }
            if (sb.length() > 0) {
                sb = sb.append(a.id());
                LOGGER.error("Multiple answers to {} found for {} in {}. Review ids: {}",
                        q.name(), rtn.org(), period, sb.toString());
                violations.add(new MultipleAnswerConstraintViolation(rtn, matchSet));
            }
        }
        return violations;
    }

    protected void ensureInitialised(int yearsToCalc, String surveyName, SurveyReturn rtn) {
        LOGGER.info("Ensuring questions initialised for {} in {} and {} years prior", rtn.org(), rtn.applicablePeriod(),
                yearsToCalc - 1);
        List<String> periods = PeriodUtil.fillBackwards(rtn.applicablePeriod(),
                yearsToCalc);

        SurveyFactory fact = SurveyFactory.getInstance(surveyName);
        Q[] requiredQs = fact.getQs();

        for (String period : periods) {
            for (Q q : requiredQs) {
                Optional<Answer> optional = rtn.answer(period, q);
                if (!optional.isPresent()) {
                    LOGGER.warn("Correcting missing answer: {} in {} for {}", q, period, rtn.org());
                    answerFactory.addAnswer(rtn, period, q);
                }
            }
        }
    }


}
