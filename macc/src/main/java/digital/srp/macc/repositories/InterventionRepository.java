/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.macc.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.macc.model.Intervention;

@RepositoryRestResource(path = "interventions")
public interface InterventionRepository extends CrudRepository<Intervention, Long> {

    @Query("SELECT c FROM Intervention c "
            + "INNER JOIN c.interventionType it INNER JOIN c.organisationType ot "
            + "WHERE it.tenantId = :tenantId "
            + "AND it.name = :intvnName  AND ot.name = :orgTypeName")
    Optional<Intervention> findByOrganisationTypeAndInterventionTypeNames(
            @Param("tenantId") String tenantId,
            @Param("intvnName") String intvnName, @Param("orgTypeName") String orgTypeName);

    @Query("SELECT c FROM Intervention c INNER JOIN c.interventionType it INNER JOIN c.organisationType ot WHERE c.tenantId = :tenantId ORDER BY it.name ASC, ot.name ASC")
    List<Intervention> findAllForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM Intervention c INNER JOIN c.interventionType it INNER JOIN c.organisationType ot WHERE c.tenantId = :tenantId ORDER BY it.name ASC, ot.name ASC")
    List<Intervention> findPageForTenant(@Param("tenantId") String tenantId,
            Pageable pageable);

    @Query("SELECT c FROM Intervention c INNER JOIN c.interventionType it INNER JOIN c.organisationType ot WHERE LOWER(it.status) IN (:status) AND c.shareOfTotal IS NOT NULL AND c.shareOfTotal >0 AND c.tenantId = :tenantId AND ot.name in (:orgTypeName) ORDER BY it.name ASC, ot.name ASC")
    List<Intervention> findByStatusForTenantAndOrgType(
            @Param("tenantId") String tenantId, @Param("status") String status,
            @Param("orgTypeName") String orgTypeName);
}
