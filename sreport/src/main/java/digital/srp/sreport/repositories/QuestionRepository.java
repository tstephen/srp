package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.Question;

/**
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface QuestionRepository extends CrudRepository<Question, Long> {

    @Query("SELECT o FROM Question o WHERE o.name = :name")
    Question findByName(@Param("name") String name);
    
    @Query("SELECT o FROM Question o ORDER BY o.name ASC")
    List<Question> findAll();

    @Query("SELECT o FROM Question o ORDER BY o.name ASC")
    List<Question> findPage(Pageable pageable);

    @Query("SELECT COUNT(o) FROM Question o, SurveyCategory sc, Survey s "
            + "WHERE sc.questionNames LIKE CONCAT('%',o.name,'%') "
            + "AND sc.survey.id = s.id "
            + "AND s.name = :surveyName "
            + "ORDER BY o.name ASC")
    Long countBySurveyName(@Param("surveyName") String surveyName);

    @Query("SELECT o FROM Question o, SurveyCategory sc, Survey s "
            + "WHERE sc.questionNames LIKE CONCAT('%',o.name,'%') "
            + "AND sc.survey.id = s.id "
            + "AND s.name = :surveyName "
            + "ORDER BY o.name ASC")
    List<Question> findBySurveyName(@Param("surveyName") String surveyName);
}
