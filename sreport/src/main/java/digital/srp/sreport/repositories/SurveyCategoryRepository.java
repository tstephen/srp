package digital.srp.sreport.repositories;

import java.util.List;

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
    SurveyCategory findByName(@Param("name") String name);
    
    @Query("SELECT o FROM SurveyCategory o ORDER BY o.name ASC")
    List<SurveyCategory> findAll();

    @Query("SELECT o FROM SurveyCategory o ORDER BY o.name ASC")
    List<SurveyCategory> findPage(Pageable pageable);
}
