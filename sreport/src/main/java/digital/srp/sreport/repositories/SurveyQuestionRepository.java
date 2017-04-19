package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.SurveyQuestion;

/**
 * 
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Q> {

    @Query("SELECT o FROM SurveyQuestion o WHERE o.name = :name AND o.category.survey.name = :surveyName")
    SurveyQuestion findBySurveyAndName(@Param("surveyName") String surveyName, @Param("name") String name);
    
    @Query("SELECT o FROM SurveyQuestion o ORDER BY o.name ASC")
    List<SurveyQuestion> findAll();

    @Query("SELECT o FROM SurveyQuestion o ORDER BY o.name ASC")
    List<SurveyQuestion> findPage(Pageable pageable);
}
