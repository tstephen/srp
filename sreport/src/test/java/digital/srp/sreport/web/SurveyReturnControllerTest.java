package digital.srp.sreport.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.Survey;
import digital.srp.sreport.model.SurveyCategory;
import digital.srp.sreport.model.SurveyReturn;
import digital.srp.sreport.model.surveys.Sdu1718;
import digital.srp.sreport.repositories.SurveyReturnRepository;

public class SurveyReturnControllerTest {

    private static SurveyReturnController svc;

    @BeforeAll
    public static void setUpClass() {
        svc = new SurveyReturnController();
        svc.returnRepo = new SurveyReturnControllerTest.MockSurveyReturnRepository();
    }

    @Test
    public void testInferPeriodFromName() {
        assertEquals("2016-17", svc.applicablePeriod("SDU-2016-17"));
        assertEquals("2015-16", svc.applicablePeriod("ERIC-2015-16"));
    }

    @Test
    public void testEnsureInitialized() {
        Question q = new Question().q(Q.ELEC_USED);

        SurveyReturn rtn = new SurveyReturn().id(1l).name("2017-18 return for ACME").applicablePeriod(Sdu1718.PERIOD)
                .answers(Collections.singleton(
                        new Answer().id(1l).question(q).applicablePeriod(Sdu1718.PERIOD).response("123456")
                )).revision(new Short("1"));

        Optional<Answer> answer = rtn.answer(Sdu1718.PERIOD, Q.ELEC_USED);
        assertTrue(answer.isPresent());
        assertEquals(Sdu1718.PERIOD, answer.get().applicablePeriod());

        Survey survey = new Survey().applicablePeriod(Sdu1718.PERIOD).categories(new SurveyCategory().questionEnums(Q.ELEC_USED));
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
}
