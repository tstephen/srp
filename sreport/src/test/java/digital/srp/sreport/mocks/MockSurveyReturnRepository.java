package digital.srp.sreport.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.repositories.SurveyReturnRepository;

public class MockSurveyReturnRepository implements SurveyReturnRepository {
    private Map<Long, SurveyReturn> returns = new HashMap<Long, SurveyReturn>();

    @Override
    public <S extends SurveyReturn> S save(S entity) {
        if (entity.id() == null) {
            entity.id(Long.valueOf(returns.size() + 1));
        }
        returns.put(entity.id(), entity);
        return entity;
    }

    @Override
    public <S extends SurveyReturn> Iterable<S> saveAll(Iterable<S> entities) {
        return entities;
    }

    @Override
    public Optional<SurveyReturn> findById(Long id) {
        return returns.values().stream()
                .filter((r) -> id.equals(r.id()))
                .findAny();
    }

    @Override
    public long count() {
        return returns.size();
    }

    @Override
    public void deleteById(Long id) {
        returns.remove(id);
    }

    @Override
    public void delete(SurveyReturn entity) {
        returns.remove(entity.id());
    }

    @Override
    public void deleteAll() {
        returns.clear();
    }

    @Override
    public void deleteAll(Iterable<? extends SurveyReturn> entities) {
        entities.forEach(i -> delete(i));
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(i -> deleteById(i));
    }

    @Override
    public List<SurveyReturn> findAll() {
        List<SurveyReturn> list = new ArrayList<SurveyReturn>();
        list.addAll(returns.values());
        return list;
    }

    @Override
    public Iterable<SurveyReturn> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<Long>();
        ids.forEach(idList::add);
        return returns.values().stream()
                        .filter(i -> idList.contains(i.getId()))
                        .collect(Collectors.toList());
    }

    @Override
    public List<SurveyReturn> findPage(Pageable pageable) {
        try {
            return findAll().subList((int) pageable.getOffset(),
                (int) pageable.getOffset()+pageable.getPageSize());
        } catch (IndexOutOfBoundsException e) {
            return findAll();
        }
    }

    @Override
    public List<SurveyReturn> findByOrg(String org) {
        return returns.values().stream()
                .filter((r) -> org.equals(r.org()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SurveyReturn> findBySurvey(Long surveyId) {
        return returns.values().stream()
                .filter((r) -> surveyId.equals(r.survey().id()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SurveyReturn> findBySurveyName(String surveyName) {
        return returns.values().stream()
                .filter((r) -> surveyName.equals(r.survey().name()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SurveyReturn> findBySurveyAndOrg(String surveyName,
            String org) {
        return returns.values().stream()
                .filter((r) -> org.equals(r.org()) && surveyName.equals(r.name()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SurveyReturn> findPageBySurvey(Long surveyId,
            Pageable pageable) {
        return null;
    }

    @Override
    public void importAnswers(Long id, String org, String surveyToImport) {
    }

    @Override
    public Long countBySurveyName(String surveyName) {
        return (long) findBySurveyName(surveyName).size();
    }

    @Override
    public List<SurveyReturn> findByName(String name) {
        return returns.values().stream()
                .filter((r) -> name.equals(r.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

}