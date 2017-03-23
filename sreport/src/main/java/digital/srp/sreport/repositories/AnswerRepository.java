package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.SurveyAnswer;

/**
 * 
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface AnswerRepository extends CrudRepository<SurveyAnswer, Long> {

    @Query("SELECT o FROM SurveyAnswer o WHERE o.question.name IN :qNames AND o.surveyReturn.org = :org ORDER BY o.surveyReturn.applicablePeriod DESC")
    List<SurveyAnswer> findByOrgAndQuestion(@Param("org") String org, @Param("qNames") String[] qNames);
}
