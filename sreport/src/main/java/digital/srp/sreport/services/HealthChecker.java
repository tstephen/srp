package digital.srp.sreport.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.validators.DuplicateAnswerConstraintViolation;
import digital.srp.sreport.validators.MultipleAnswerConstraintViolation;

@Component
public class HealthChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthChecker.class);

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
    
    
}
