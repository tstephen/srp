package digital.srp.sreport.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.WeightingFactor;

/**
 * 
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface WeightingFactorRepository extends CrudRepository<WeightingFactor, Long> {

    @Query("SELECT o FROM WeightingFactor o WHERE o.orgType = :orgType ORDER BY o.orgType ASC")
    WeightingFactor findByOrgType(@Param("orgType") String orgType);
    
    @Query("SELECT o FROM WeightingFactor o ORDER BY o.orgType ASC")
    List<WeightingFactor> findAll();

    @Query("SELECT o FROM WeightingFactor o ORDER BY o.orgType ASC")
    List<WeightingFactor> findPage(Pageable pageable);
}
