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

import digital.srp.sreport.model.SurveyReturn;

/**
 *
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface SurveyReturnRepository extends CrudRepository<SurveyReturn, Long> {

    @Query("SELECT o FROM SurveyReturn o WHERE o.name = :name AND o.status != 'Superseded'")
    List<SurveyReturn> findByName(@Param("name") String name);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'Superseded' "
            + "ORDER BY o.name ASC")
    List<SurveyReturn> findAll();

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'Superseded' "
            + "ORDER BY o.name ASC")
    List<SurveyReturn> findPage(Pageable pageable);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'Superseded' "
            + "AND o.org = :org ORDER BY o.name ASC")
    List<SurveyReturn> findByOrg(@Param("org") String org);

    @Query("SELECT o FROM SurveyReturn o "
            + "JOIN o.survey s "
            + "WHERE o.status != 'Superseded' "
            + "AND o.survey.id = :surveyId ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(o) FROM SurveyReturn o "
            + "JOIN o.survey s "
            + "WHERE o.status != 'Superseded' "
            + "AND o.survey.name = :surveyName ")
    Long countBySurveyName(@Param("surveyName") String surveyName);

    @Query("SELECT o FROM SurveyReturn o "
            + "JOIN o.survey s "
            + "WHERE o.status != 'Superseded' "
            + "AND o.survey.name = :surveyName ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurveyName(@Param("surveyName") String surveyName);

    @Query("SELECT o FROM SurveyReturn o "
            + "JOIN o.survey s "
            + "WHERE o.status != 'Superseded' "
            + "AND o.survey.name = :surveyName AND o.org = :org "
            + "ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurveyAndOrg(@Param("surveyName") String surveyName,
            @Param("org") String org);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'Superseded' "
            + "ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findPageBySurvey(@Param("surveyId") Long surveyId, Pageable pageable);

    @Query(value = "INSERT INTO SR_RETURN_ANSWER (survey_return_id, answer_id) "
            + "SELECT :id, a.id from SR_ANSWER a "
            + "INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id "
            + "INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id "
            + "INNER JOIN SR_SURVEY s ON r.survey_id = s.id "
            + "WHERE r.org = :org "
            + "AND s.name = :surveyToImport "
            + "AND a.id NOT IN (SELECT answer_id FROM SR_RETURN_ANSWER WHERE survey_return_id = :id)", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    void importAnswers(@Param("id") Long id, @Param("org") String org, @Param("surveyToImport") String surveyToImport);

}
