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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.Survey;

/**
 *
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface SurveyRepository extends CrudRepository<Survey, Long> {

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' AND o.name = :name ORDER BY o.name ASC")
    <Optional>Survey findByName(@Param("name") String name);

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<Survey> findAll();

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<Survey> findPage(Pageable pageable);

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' AND o.name LIKE 'ERIC%' ORDER BY o.name ASC")
    List<Survey> findEricSurveys();

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' AND o.name LIKE 'SDU%' ORDER BY o.name ASC")
    List<Survey> findSduSurveys();

    @Override
    @Query("UPDATE #{#entityName} x set x.status = 'deleted' where x.id = :id")
    @Modifying(clearAutomatically = true)
    public void deleteById(@Param("id") Long id);

}
