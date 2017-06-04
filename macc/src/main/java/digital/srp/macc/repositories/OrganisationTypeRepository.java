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

import digital.srp.macc.model.OrganisationType;

@RepositoryRestResource(path = "/organisation-types")
public interface OrganisationTypeRepository extends
        CrudRepository<OrganisationType, Long> {

    @Query("SELECT c FROM OrganisationType c WHERE c.tenantId = :tenantId ORDER BY c.name ASC")
    List<OrganisationType> findAllForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM OrganisationType c WHERE c.tenantId = :tenantId ORDER BY c.name ASC")
    List<OrganisationType> findPageForTenant(
            @Param("tenantId") String tenantId,
            Pageable pageable);

    @Query("SELECT c FROM OrganisationType c WHERE c.status IN (:status) AND c.tenantId = :tenantId ORDER BY c.name ASC")
    List<OrganisationType> findByStatusForTenant(
            @Param("tenantId") String tenantId, @Param("status") String status);

    @Query("SELECT c FROM OrganisationType c WHERE c.reportingType = true AND c.tenantId = :tenantId ORDER BY c.name ASC")
    List<OrganisationType> findAllReportingTypeForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM OrganisationType c WHERE c.tenantId = :tenantId AND name = :name")
    OrganisationType findByName(@Param("tenantId") String tenantId,
            @Param("name") String name);

}
