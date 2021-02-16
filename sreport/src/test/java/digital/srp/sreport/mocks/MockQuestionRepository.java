package digital.srp.sreport.mocks;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.sreport.importers.QuestionCsvImporter;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.repositories.QuestionRepository;

public class MockQuestionRepository implements QuestionRepository {

    private Map<Long, Question> questions = new HashMap<Long, Question>();

    public MockQuestionRepository() throws IOException {
        try {
            new QuestionCsvImporter().readQuestions().stream().forEach((q) -> save(q));
        } catch (IOException e) {
            fail("Unable to load questions to run test with");
        }
    }

    @Override
    public <S extends Question> S save(S entity) {
        if (entity.id() == null) {
            entity.id(new Long(questions.size() + 1));
        }
        questions.put(entity.id(), entity);
        return entity;
    }

    @Override
    public <S extends Question> Iterable<S> saveAll(Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questions.values().stream()
                .filter((r) -> id.equals(r.id()))
                .findAny();
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<Question> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        return questions.size();
    }

    @Override
    public void deleteById(Long id) {
        questions.remove(id);
    }

    @Override
    public void delete(Question entity) {
        questions.remove(entity.id());
    }

    @Override
    public void deleteAll(Iterable<? extends Question> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteAll() {
        questions.clear();
    }

    @Override
    public Optional<Question> findByName(String name) {
        return Optional.ofNullable(questions.values().stream()
                .filter((q) -> name.equals(q.name()))
                .findAny()
                .orElse(null));
    }

    @Override
    public List<Question> findAll() {
        return questions.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Question> findPage(Pageable pageable) {
        try {
            return findAll().subList((int) pageable.getOffset(),
                (int) pageable.getOffset()+pageable.getPageSize());
        } catch (IndexOutOfBoundsException e) {
            return findAll();
        }
    }

    @Override
    public Long countBySurveyName(String surveyName) {
        // TODO Auto-generated method stub
        return null;
    }

}