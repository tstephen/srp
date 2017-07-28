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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.macc.model.OrganisationIntervention;

@RepositoryRestResource(path = "/organisation-interventions")
public interface OrganisationInterventionRepository extends
        CrudRepository<OrganisationIntervention, Long> {

    @Query("SELECT oi FROM OrganisationIntervention oi LEFT OUTER JOIN oi.intervention i INNER JOIN oi.organisationType ot WHERE ot.name = :orgTypeName AND oi.tenantId = :tenantId ORDER BY ot.name ASC")
    List<OrganisationIntervention> findByOrgType(
            @Param("orgTypeName") String orgTypeName,
            @Param("tenantId") String tenantId);

}
