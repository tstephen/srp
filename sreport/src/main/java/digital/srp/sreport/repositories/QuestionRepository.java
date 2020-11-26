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

    @Query("SELECT COUNT(o) FROM Question o "
            + "WHERE EXISTS ( "
            + "  SELECT 'found' "
            + "   FROM SurveyCategory sc "
            + "   JOIN sc.survey s "
            + "  WHERE s.name = :surveyName "
            + "    AND sc.questionNames LIKE CONCAT('%',o.name,'%') "
            + ")")
    Long countBySurveyName(@Param("surveyName") String surveyName);
}
