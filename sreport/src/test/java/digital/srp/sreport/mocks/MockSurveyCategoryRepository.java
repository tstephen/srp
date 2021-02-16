package digital.srp.sreport.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.repositories.SurveyCategoryRepository;

public class MockSurveyCategoryRepository implements SurveyCategoryRepository {
    private Map<Long, SurveyCategory> cats = new HashMap<Long, SurveyCategory>();

    @Override
    public <S extends SurveyCategory> S save(S entity) {
        if (entity.id() == null) {
            entity.id(new Long(cats.size() + 1));
        }
        cats.put(entity.id(), entity);
        return entity;
    }

    @Override
    public <S extends SurveyCategory> Iterable<S> saveAll(Iterable<S> entities) {
        return entities;
    }

    @Override
    public Optional<SurveyCategory> findById(Long id) {
        return cats.values().stream()
                .filter((r) -> id.equals(r.id()))
                .findAny();
    }

    @Override
    public long count() {
        return cats.size();
    }

    @Override
    public void deleteById(Long id) {
        cats.remove(id);
    }

    @Override
    public void delete(SurveyCategory entity) {
        cats.remove(entity.id());
    }

    @Override
    public void deleteAll() {
        cats.clear();
    }

    @Override
    public List<SurveyCategory> findAll() {
        List<SurveyCategory> list = new ArrayList<SurveyCategory>();
        list.addAll(cats.values());
        return list;
    }

    @Override
    public List<SurveyCategory> findPage(Pageable pageable) {
        try {
            return findAll().subList((int) pageable.getOffset(),
                (int) pageable.getOffset()+pageable.getPageSize());
        } catch (IndexOutOfBoundsException e) {
            return findAll();
        }
    }

    @Override
    public List<SurveyCategory> findBySurveyName(String surveyName) {
        return cats.values().stream()
                .filter((r) -> surveyName.equals(r.survey().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<SurveyCategory> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAll(Iterable<? extends SurveyCategory> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public Optional<SurveyCategory> findByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<SurveyCategory> findBySurveyAndCategory(String survey,
            String category) {
        // TODO Auto-generated method stub
        return null;
    }

}