/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.macc.model.InterventionType;

@RepositoryRestResource(path = "/intervention-types")
public interface InterventionTypeRepository extends
        CrudRepository<InterventionType, Long> {

    InterventionType findByName(@Param("name") String name);

    @Query("SELECT c FROM InterventionType c WHERE c.tenantId = :tenantId ORDER BY c.name ASC")
    List<InterventionType> findAllForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM InterventionType c WHERE c.tenantId = :tenantId ORDER BY c.name ASC")
    List<InterventionType> findPageForTenant(
            @Param("tenantId") String tenantId,
            Pageable pageable);

    @Query("SELECT it FROM InterventionType it WHERE it.status IN (:status) AND it.tenantId = :tenantId ORDER BY it.name ASC")
    List<InterventionType> findByStatusForTenant(
            @Param("tenantId") String tenantId, @Param("status") String status);

    @Query("SELECT it FROM InterventionType it WHERE LOWER(it.status) IN (:status) AND it.tenantId = :tenantId AND it.crossOrganisation = TRUE ORDER BY it.name ASC")
    List<InterventionType> findByStatusForTenantAndCommissioners(
            @Param("tenantId") String tenantId, @Param("status") String status);

}
