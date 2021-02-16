package digital.srp.macc.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.OrganisationTypeRepository;

public class MockOrganisationTypeRepository
        implements OrganisationTypeRepository {

    public Map<Long, OrganisationType> orgTypes = new HashMap<Long, OrganisationType>();

    @Override
    public <S extends OrganisationType> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(new Long(orgTypes.size() + 1));
        }
        orgTypes.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends OrganisationType> Iterable<S> saveAll(
            Iterable<S> entities) {
        for (S s : entities) {
            save(s);
        }
        return entities;
    }

    @Override
    public Optional<OrganisationType> findById(Long id) {
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
    public void delete(OrganisationType entity) {
        orgTypes.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
        orgTypes.clear();
    }

    @Override
    public List<OrganisationType> findAll() {
        return orgTypes.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<OrganisationType> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAll(Iterable<? extends OrganisationType> entities) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<OrganisationType> findAllForTenant(String tenantId) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganisationType> findPageForTenant(String tenantId,
            Pageable pageable) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganisationType> findByStatusForTenant(String tenantId,
            String status) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> status.equals(ot.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganisationType> findAllReportingTypeForTenant(
            String tenantId) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> ot.isReportingType())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrganisationType> findByName(String tenantId, String name) {
        return orgTypes.values().stream()
                .filter(ot -> tenantId.equals(ot.getTenantId()))
                .filter(ot -> name.equals(ot.getName()))
                .findAny();
    }

}