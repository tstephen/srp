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
package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.CarbonFactor;

/**
 *
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface CarbonFactorRepository extends CrudRepository<CarbonFactor, Long> {

    @Query("SELECT o FROM CarbonFactor o WHERE o.name = :name ORDER BY o.applicablePeriod ASC")
    List<CarbonFactor> findByName(@Param("name") String name);

    @Query("SELECT o FROM CarbonFactor o ORDER BY o.name ASC")
    List<CarbonFactor> findAll();

    @Query("SELECT o FROM CarbonFactor o ORDER BY o.name ASC")
    List<CarbonFactor> findPage(Pageable pageable);
}
