package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.CarbonFactor;

/**
 * 
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface CarbonFactorRepository extends CrudRepository<CarbonFactor, Long> {

    @Query("SELECT o FROM CarbonFactor o WHERE o.name = :name ORDER BY o.name ASC")
    CarbonFactor findByName(@Param("name") String name);
    
    @Query("SELECT o FROM CarbonFactor o ORDER BY o.name ASC")
    List<CarbonFactor> findAll();

    @Query("SELECT o FROM CarbonFactor o ORDER BY o.name ASC")
    List<CarbonFactor> findPage(Pageable pageable);
}
