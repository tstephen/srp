package digital.srp.sreport.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.repositories.AnswerRepository;

public class MockAnswerRepository implements AnswerRepository {

    private Map<Long, Answer> answers = new HashMap<Long, Answer>();

    @Override
    public <S extends Answer> S save(S entity) {
        if (entity.id() == null) {
            entity.id(Long.valueOf(answers.size() + 1));
        }
        answers.put(entity.id(), entity);
        return entity;
    }

    @Override
    public <S extends Answer> Iterable<S> saveAll(Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return answers.values().stream()
                .filter((r) -> id.equals(r.id()))
                .findAny();
    }

    @Override
    public long count() {
        return answers.size();
    }

    @Override
    public void deleteById(Long id) {
        answers.remove(id);
    }

    @Override
    public void delete(Answer entity) {
        answers.remove(entity.id());
    }

    @Override
    public void deleteAll() {
        answers.clear();
    }

    @Override
    public void deleteAll(Iterable<? extends Answer> entities) {
        entities.forEach(i -> delete(i));
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(i -> deleteById(i));
    }

    @Override
    public void deleteDerivedAnswers(Long[] ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAnswers(Long[] ids) {
        Arrays.asList(ids).forEach(i -> deleteById(i));
    }

    @Override
    public List<Answer> findAll() {
        return answers.values().stream().collect(Collectors.toList());
    }

    @Override
    public Iterable<Answer> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<Long>();
        ids.forEach(idList::add);
        return answers.values().stream()
                        .filter(i -> idList.contains(i.getId()))
                        .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findPage(Pageable pageable) {
        try {
            return findAll().subList((int) pageable.getOffset(),
                (int) pageable.getOffset()+pageable.getPageSize());
        } catch (IndexOutOfBoundsException e) {
            return findAll();
        }
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Long countBySurveyName(String surveyName, String[] qNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Answer> findBySurveyName(String surveyName, String[] qNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Answer> findByOrgPeriodAndQuestion(String org, String rtnPeriod,
            String[] periods, String... qNames) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @SuppressWarnings("unlikely-arg-type")
    public List<Answer> findByOrgPeriodAndQuestionAsc(String org,
            String rtnPeriod, String[] periods, String... qNames) {
        Set<String[]> periodSet = Collections.singleton(periods);
        Set<String[]> questionSet = Collections.singleton(qNames);
        return answers.values().stream()
                .filter((a) -> periodSet.contains(a.applicablePeriod()))
                .filter((a) -> questionSet.contains(a.question().name()))
                .filter(a -> a.surveyReturns().stream().anyMatch(r -> rtnPeriod.equals(r.applicablePeriod())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findByOrg(String org) {
        return answers.values().stream()
                .filter(a -> a.surveyReturns().stream().anyMatch(r -> org.equals(r.org())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findByOrgName(String orgName) {
        return answers.values().stream()
                .filter(a -> a.surveyReturns().stream().anyMatch(r -> orgName.equals(r.org())))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Answer> findAnswersToImport(Long targetReturnId,
            Long sourceReturnId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Answer> findByPeriod(String period) {
        return answers.values().stream()
                .filter((a) -> period.equals(a.applicablePeriod()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findByReturnPeriodAndQ(Long rtnId, String period,
            String q) {
        return answers.values().stream()
                .filter((a) -> period.equals(a.applicablePeriod()))
                .filter((a) -> q.equals(a.question().name()))
                .filter(a -> a.surveyReturns().stream().anyMatch(r -> rtnId.equals(r.id())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findPageByPeriod(Pageable pageable, String period) {
        try {
            return findAll().stream()
                .filter((a) -> period.equals(a.applicablePeriod()))
                .collect(Collectors.toList())
                .subList((int) pageable.getOffset(),
                (int) pageable.getOffset()+pageable.getPageSize());
        } catch (IndexOutOfBoundsException e) {
            return findAll().stream()
            .filter((a) -> period.equals(a.applicablePeriod()))
            .collect(Collectors.toList());
        }
    }

    @Override
    public List<Answer> findByQuestion(String... qNames) {
        List<String> qList = new ArrayList<String>();
        Arrays.asList(qNames).forEach(qList::add);
        return answers.values().stream()
                        .filter(i -> qList.contains(i.question().name()))
                        .collect(Collectors.toList());
    }

}