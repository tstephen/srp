package digital.srp.sreport.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import digital.srp.sreport.importers.QuestionCsvImporter;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Sdu1718;
import digital.srp.sreport.repositories.QuestionRepository;
import digital.srp.sreport.repositories.SurveyReturnRepository;

public class SurveyReturnControllerTest {
    private SurveyReturnController svc;

    @BeforeEach
    public void setUpClass() throws IOException {
        svc = new SurveyReturnController();
        svc.returnRepo = new SurveyReturnControllerTest.MockSurveyReturnRepository();
        svc.qRepo = new SurveyReturnControllerTest.MockQuestionRepository();
    }

    @Test
    public void testInferPeriodFromName() {
        assertEquals("2016-17", svc.applicablePeriod("SDU-2016-17"));
        assertEquals("2015-16", svc.applicablePeriod("ERIC-2015-16"));
    }

    @Test
    public void testEnsureInitialized() {
        Question q = new Question().q(Q.ELEC_USED);

        SurveyReturn rtn = new SurveyReturn().id(1l).name("2017-18 return for ACME")
                .applicablePeriod(Sdu1718.PERIOD)
                .answers(Collections.singleton(
                        new Answer().id(1l).question(q).applicablePeriod(Sdu1718.PERIOD).response("123456")
                )).revision(new Short("1"));

        Optional<Answer> answer = rtn.answer(Sdu1718.PERIOD, Q.ELEC_USED);
        assertTrue(answer.isPresent());
        assertEquals(Sdu1718.PERIOD, answer.get().applicablePeriod());

        Survey survey = new Survey().applicablePeriod(Sdu1718.PERIOD)
                .categories(new SurveyCategory().questionEnums(Q.ELEC_USED));
        svc.ensureInitialized(survey, rtn);
        assertEquals(1, rtn.answers().size());

        answer = rtn.answer(Sdu1718.PERIOD, Q.ELEC_USED);
        assertTrue(answer.isPresent());
        assertEquals(Sdu1718.PERIOD, answer.get().applicablePeriod());
    }

    static class MockSurveyReturnRepository implements SurveyReturnRepository {

        @Override
        public <S extends SurveyReturn> S save(S entity) {
            return entity;
        }

        @Override
        public <S extends SurveyReturn> Iterable<S> saveAll(Iterable<S> entities) {
            return entities;
        }

        @Override
        public Optional<SurveyReturn> findById(Long id) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long id) {
        }

        @Override
        public void delete(SurveyReturn entity) {
        }

        @Override
        public void deleteAll() {
        }

        @Override
        public List<SurveyReturn> findAll() {
            return null;
        }

        @Override
        public List<SurveyReturn> findPage(Pageable pageable) {
            return null;
        }

        @Override
        public List<SurveyReturn> findByOrg(String org) {
            return null;
        }

        @Override
        public List<SurveyReturn> findBySurvey(Long surveyId) {
            return null;
        }

        @Override
        public List<SurveyReturn> findBySurveyName(String surveyName) {
            return null;
        }

        @Override
        public List<SurveyReturn> findBySurveyAndOrg(String surveyName,
                String org) {
            return null;
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
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SurveyReturn> findByName(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean existsById(Long id) {
            return findById(id).isPresent();
        }

        @Override
        public Iterable<SurveyReturn> findAllById(Iterable<Long> ids) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void deleteAll(Iterable<? extends SurveyReturn> entities) {
            // TODO Auto-generated method stub
        }

    }


    public class MockQuestionRepository implements QuestionRepository {

        private List<Question> questions;

        public MockQuestionRepository() throws IOException {
            try {
                questions = new QuestionCsvImporter().readQuestions();
            } catch (IOException e) {
                fail("Unable to load questions to run test with");
            }
        }

        @Override
        public <S extends Question> S save(S entity) {
            questions.add(entity);
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
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean existsById(Long id) {
            // TODO Auto-generated method stub
            return false;
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
            // TODO Auto-generated method stub
        }

        @Override
        public void delete(Question entity) {
            // TODO Auto-generated method stub
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
        public Question findByName(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Question> findAll() {
            return questions;
        }

        @Override
        public List<Question> findPage(Pageable pageable) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Long countBySurveyName(String surveyName) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
