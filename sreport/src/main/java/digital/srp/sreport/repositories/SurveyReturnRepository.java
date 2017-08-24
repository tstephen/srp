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

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<SurveyReturn> findAll();

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<SurveyReturn> findPage(Pageable pageable);
    
    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' AND o.org = :org ORDER BY o.name ASC")
    List<SurveyReturn> findByOrg(@Param("org") String org);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' AND o.survey.id = :surveyId ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' AND o.survey.name = :surveyName ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurveyName(@Param("surveyName") String surveyName);

    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' AND o.survey.name = :surveyName AND o.org = :org ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findBySurveyAndOrg(@Param("surveyName") String surveyName, 
            @Param("org") String org);
    
//    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' AND o.survey.name = :surveyName AND o.org = :org ORDER BY o.name ASC")
//    List<SurveyReturn> findBySurveyOrgAndPeriod(@Param("surveyName") String surveyName, 
//            @Param("org") String org, @Param("period") String period);
    
    @Query("SELECT o FROM SurveyReturn o WHERE o.status != 'deleted' ORDER BY o.applicablePeriod DESC, o.name ASC")
    List<SurveyReturn> findPageBySurvey(@Param("surveyId") Long surveyId, Pageable pageable);
    
    @Override
    @Query("UPDATE #{#entityName} x set x.status = 'deleted' where x.id = :id")
    @Modifying(clearAutomatically = true)
    void delete(@Param("id") Long id);

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
