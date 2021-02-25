package digital.srp.macc.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.macc.model.InterventionType;
import digital.srp.macc.repositories.InterventionTypeRepository;

public class MockInterventionTypeRepository
        implements InterventionTypeRepository {

    public Map<Long, InterventionType> orgTypes = new HashMap<Long, InterventionType>();

    @Override
    public <S extends InterventionType> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(new Long(orgTypes.size() + 1));
        }
        orgTypes.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends InterventionType> Iterable<S> saveAll(
            Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<InterventionType> findById(Long id) {
        return orgTypes.values().stream().filter((r) -> id.equals(r.getId()))
                .findAny();
    }

    @Override
    public long count() {
        return orgTypes.size();
    }

    @Override
    public void deleteById(Long id) {
        orgTypes.remove(id);
    }

    @Override
    public void delete(InterventionType entity) {
        orgTypes.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
        orgTypes.clear();
    }

    @Override
    public List<InterventionType> findAll() {
        return orgTypes.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<InterventionType> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAll(Iterable<? extends InterventionType> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<InterventionType> findAllForTenant(String tenantId) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findPageForTenant(String tenantId,
            Pageable pageable) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findByStatusForTenant(String tenantId,
            String status) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> status.equals(ot.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findByStatusForTenantAndCommissioners(
            String tenantId, String status) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> status.equals(ot.getStatus()))
                .filter(ot -> ot.isCrossOrganisation())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InterventionType> findByName(String tenantId, String name) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> name.equals(ot.getName()))
                .findAny();
    }

}