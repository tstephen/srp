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
    Survey findByName(@Param("name") String name);
    
    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<Survey> findAll();

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' ORDER BY o.name ASC")
    List<Survey> findPage(Pageable pageable);

    @Query("SELECT o FROM Survey o WHERE o.status != 'deleted' AND o.name LIKE 'ERIC%' ORDER BY o.name ASC")
    List<Survey> findEricSurveys();
    
    @Override
    @Query("UPDATE #{#entityName} x set x.status = 'deleted' where x.id = :id")
    @Modifying(clearAutomatically = true)
    public void delete(@Param("id") Long id);

}
