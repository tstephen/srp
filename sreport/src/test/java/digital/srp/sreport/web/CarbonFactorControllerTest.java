package digital.srp.sreport.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

import digital.srp.sreport.model.CarbonFactor;
import digital.srp.sreport.repositories.CarbonFactorRepository;

public class CarbonFactorControllerTest {

    private static CarbonFactorController svc;

    @BeforeClass
    public static void setUpClass() {
        svc = new CarbonFactorController();
        svc.cfactorRepo = new CarbonFactorControllerTest.MockCarbonFactorRepository();
    }

    @Test
    public void testLifecycle() {

        List<CarbonFactor> existingFactors = svc.list(null, null);
        assertNotNull("Unable to find any existing factors, should be at least empty list", existingFactors);
        svc.create(newCarbonFactor());
        
        List<CarbonFactor> augmentedFactors = svc.list(null, null);
        assertThat(augmentedFactors.size() == existingFactors.size()+1);
    }

    private CarbonFactor newCarbonFactor() {
        return new CarbonFactor().applicablePeriod("2020-21")
                .category("TEST").comments("N/A").name("FICTITIOUS")
                .value(new BigDecimal("100.00"));
    }

    static class MockCarbonFactorRepository implements CarbonFactorRepository {

        private List<CarbonFactor> cfactors = new ArrayList<CarbonFactor>();
        
        @Override
        public <S extends CarbonFactor> S save(S entity) {
            cfactors.add(entity);
            return entity;
        }

        @Override
        public <S extends CarbonFactor> Iterable<S> save(Iterable<S> entities) {
            for (S entity : entities) {
                cfactors.add(entity);
            }
            return entities;
        }

        @Override
        public CarbonFactor findOne(Long id) {
            return null;
        }

        @Override
        public boolean exists(Long id) {
            throw new RuntimeException("exists is not mocked");
        }

        @Override
        public Iterable<CarbonFactor> findAll(Iterable<Long> ids) {
            return cfactors;
        }

        @Override
        public long count() {
            return cfactors.size();
        }

        @Override
        public void delete(Long id) {
            for (CarbonFactor carbonFactor : cfactors) {
                if (id.equals(carbonFactor.id())) {
                    cfactors.remove(carbonFactor);
                }
            }
        }

        @Override
        public void delete(CarbonFactor entity) {
            cfactors.remove(entity);
        }

        @Override
        public void delete(Iterable<? extends CarbonFactor> entities) {
            for (CarbonFactor carbonFactor : entities) {
                delete(carbonFactor);
            }
        }

        @Override
        public void deleteAll() {
            cfactors.clear();
        }

        @Override
        public List<CarbonFactor> findAll() {
            return cfactors;
        }

        @Override
        public List<CarbonFactor> findPage(Pageable pageable) {
            return null;
        }

        @Override
        public List<CarbonFactor> findByName(String name) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
