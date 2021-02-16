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
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.SurveyCategory;

/**
 *
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface SurveyCategoryRepository extends CrudRepository<SurveyCategory, Long> {

    @Query("SELECT o FROM SurveyCategory o WHERE o.name = :name ORDER BY o.name ASC")
    Optional<SurveyCategory> findByName(@Param("name") String name);

    @Query("SELECT o FROM SurveyCategory o WHERE o.survey.name = :survey ORDER BY o.name ASC")
    List<SurveyCategory> findBySurveyName(@Param("survey") String survey);

    @Query("SELECT o FROM SurveyCategory o WHERE o.name = :category AND o.survey.name = :survey ORDER BY o.name ASC")
    Optional<SurveyCategory> findBySurveyAndCategory(@Param("survey") String survey, @Param("category") String category);

    @Query("SELECT o FROM SurveyCategory o ORDER BY o.name ASC")
    List<SurveyCategory> findAll();

    @Query("SELECT o FROM SurveyCategory o ORDER BY o.name ASC")
    List<SurveyCategory> findPage(Pageable pageable);

}
