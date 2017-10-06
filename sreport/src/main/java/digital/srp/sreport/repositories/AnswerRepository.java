package digital.srp.sreport.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import digital.srp.sreport.model.Answer;

/**
 *
 * @author Tim Stephenson
 */
@RepositoryRestResource(exported = false)
public interface AnswerRepository extends CrudRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames) "
            + "AND a.question.name IN :qNames "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByQuestion(@Param("qNames") String... qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames AND r.org = :org) "
            + "AND a.question.name IN :qNames AND r.org = :org "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByOrgAndQuestion(@Param("org") String org, @Param("qNames") String... qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames AND r.org = :org) "
            + "AND a.question.name IN :qNames AND r.org = :org "
            + "ORDER BY a.applicablePeriod ASC")
    List<Answer> findByOrgAndQuestionAsc(@Param("org") String org, @Param("qNames") String... qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE r.org = :org "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByOrg(@Param("org") String org);


    @Query("SELECT a FROM Answer a JOIN a.surveyReturns r JOIN a.question q "
            + "WHERE a.response = :orgName "
            + "AND q.name = 'ORG_NAME' "
            + "ORDER BY a.applicablePeriod DESC")
    Answer findByOrgName(@Param("orgName") String orgName);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames AND r.org = :org AND o.applicablePeriod = :period)"
            + "AND a.question.name IN :qNames AND r.org = :org AND a.applicablePeriod = :period")
    List<Answer> findByOrgPeriodAndQuestion(@Param("org") String org, @Param("period") String period, @Param("qNames") String... qNames);

    @Query("SELECT o FROM Answer o ORDER BY o.lastUpdated DESC")
    List<Answer> findAll();

    @Query("SELECT o FROM Answer o ORDER BY o.lastUpdated DESC")
    List<Answer> findPage(Pageable pageable);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o WHERE o.applicablePeriod = :period)"
            + "AND a.applicablePeriod = :period")
    List<Answer> findByPeriod(@Param("period") String period);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames AND r.org = :org AND o.applicablePeriod = :period)"
            + "AND a.applicablePeriod = :period")
    List<Answer> findPageByPeriod(Pageable pageable, @Param("period") String period);

    @Query("DELETE FROM Answer a WHERE a.id in :ids and a.derived = true")
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteDerivedAnswers(@Param("ids") Long[] ids);

}
