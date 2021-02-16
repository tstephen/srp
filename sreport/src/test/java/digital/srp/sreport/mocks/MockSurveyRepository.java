package digital.srp.sreport.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.Survey;
import digital.srp.sreport.repositories.SurveyRepository;

public class MockSurveyRepository implements SurveyRepository {

    private Map<Long, Survey> surveys = new HashMap<Long, Survey>();

    @Override
    public <S extends Survey> S save(S entity) {
        if (entity.id() == null) {
            entity.id(new Long(surveys.size() + 1));
        }
        surveys.put(entity.id(), entity);
        return entity;
    }

    @Override
    public <S extends Survey> Iterable<S> saveAll(Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<Survey> findById(Long id) {
        return surveys.values().stream()
                .filter((r) -> id.equals(r.id()))
                .findAny();
    }

    @Override
    public long count() {
        return surveys.size();
    }

    @Override
    public void deleteById(Long id) {
        surveys.remove(id);
    }

    @Override
    public void delete(Survey entity) {
        surveys.remove(entity.id());
    }

    @Override
    public void deleteAll() {
        surveys.clear();
    }

    @Override
    public List<Survey> findAll() {
        return surveys.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Survey> findPage(Pageable pageable) {
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
    public Iterable<Survey> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAll(Iterable<? extends Survey> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public Survey findByName(String name) {
        return surveys.values().stream()
                        .filter((r) -> name.equals(r.name()))
                        .findAny()
                        .orElse(null);
    }

    @Override
    public List<Survey> findEricSurveys() {
        return findSurveysWithNameLike("ERIC");
    }

    private List<Survey> findSurveysWithNameLike(String namePrefix) {
        return surveys.values().stream()
                .filter((r) -> r.name().startsWith(namePrefix))
                .collect(Collectors.toList());
    }

    @Override
    public List<Survey> findSduSurveys() {
        return findSurveysWithNameLike("SDU");
    }

}