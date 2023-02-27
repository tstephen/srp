package digital.srp.macc.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.repositories.InterventionRepository;

public class MockInterventionRepository
        implements InterventionRepository {

    public Map<Long, Intervention> interventions = new HashMap<Long, Intervention>();

    @Override
    public <S extends Intervention> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(Long.valueOf(interventions.size() + 1));
        }
        interventions.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Intervention> Iterable<S> saveAll(
            Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<Intervention> findById(Long id) {
        return interventions.values().stream().filter((r) -> id.equals(r.getId()))
                .findAny();
    }

    @Override
    public long count() {
        return interventions.size();
    }

    @Override
    public void deleteById(Long id) {
        interventions.remove(id);
    }

    @Override
    public void delete(Intervention entity) {
        interventions.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
        interventions.clear();
    }

    @Override
    public void deleteAll(Iterable<? extends Intervention> entities) {
        entities.forEach(i -> delete(i));
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(i -> deleteById(i));
    }

    @Override
    public List<Intervention> findAll() {
        return interventions.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<Intervention> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<Long>();
        ids.forEach(idList::add);
        return interventions.values().stream()
                        .filter(i -> idList.contains(i.getId()))
                        .collect(Collectors.toList());
    }

    @Override
    public List<Intervention> findAllForTenant(String tenantId) {
        return interventions.values().stream()
                .filter(i -> tenantId.equals(i.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Intervention> findPageForTenant(String tenantId,
            Pageable pageable) {
        return interventions.values().stream()
                .filter(i -> tenantId.equals(i.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Intervention> findByStatusForTenantAndOrgType(String tenantId,
            String status, String orgTypeName) {
        return interventions.values().stream()
                .filter(i -> tenantId.equals(i.getTenantId()))
                .filter(i -> status.equals(i.getInterventionType().getStatus()))
                .filter(i -> orgTypeName.equals(i.getOrganisationType().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Intervention>  findByOrganisationTypeAndInterventionTypeNames(
            String tenantId, String intvnName, String orgTypeName) {
        return interventions.values().stream()
                .filter(i -> tenantId.equals(i.getTenantId()))
                .filter(i -> intvnName.equals(i.getName()))
                .filter(i -> orgTypeName.equals(i.getName()))
                .findAny();
    }

}