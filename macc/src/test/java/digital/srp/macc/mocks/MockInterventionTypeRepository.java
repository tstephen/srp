package digital.srp.macc.mocks;

import java.util.ArrayList;
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

    public Map<Long, InterventionType> intvnTypes = new HashMap<Long, InterventionType>();

    @Override
    public <S extends InterventionType> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(Long.valueOf(intvnTypes.size() + 1));
        }
        intvnTypes.put(entity.getId(), entity);
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
        return intvnTypes.values().stream().filter((r) -> id.equals(r.getId()))
                .findAny();
    }

    @Override
    public long count() {
        return intvnTypes.size();
    }

    @Override
    public void deleteById(Long id) {
        intvnTypes.remove(id);
    }

    @Override
    public void delete(InterventionType entity) {
        intvnTypes.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
        intvnTypes.clear();
    }

    @Override
    public void deleteAll(Iterable<? extends InterventionType> entities) {
        entities.forEach(i -> delete(i));
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(i -> deleteById(i));
    }

    @Override
    public List<InterventionType> findAll() {
        return intvnTypes.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<InterventionType> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<Long>();
        ids.forEach(idList::add);
        return intvnTypes.values().stream()
                        .filter(i -> idList.contains(i.getId()))
                        .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findAllForTenant(String tenantId) {
        return intvnTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findPageForTenant(String tenantId,
            Pageable pageable) {
        return intvnTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findByStatusForTenant(String tenantId,
            String status) {
        return intvnTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> status.equals(ot.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionType> findByStatusForTenantAndCommissioners(
            String tenantId, String status) {
        return intvnTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> status.equals(ot.getStatus()))
                .filter(ot -> ot.isCrossOrganisation())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InterventionType> findByName(String tenantId, String name) {
        return intvnTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> name.equals(ot.getName()))
                .findAny();
    }

}