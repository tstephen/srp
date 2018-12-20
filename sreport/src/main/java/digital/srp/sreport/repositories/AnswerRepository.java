package digital.srp.sreport.repositories;

import java.util.List;
import java.util.Set;

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

    // Unfortunately, and despite the method name, have to pre-find question
    // names to make this join
    @Query("SELECT COUNT(a) FROM Answer a LEFT JOIN a.surveyReturns r "
            + "JOIN r.survey s "
            + "WHERE a.revision = (SELECT MAX(o.revision) "
                    + "FROM Answer o LEFT JOIN o.surveyReturns r "
                    + "WHERE o.question.name IN :qNames) "
            + "AND s.name = :surveyName "
            + "ORDER BY a.applicablePeriod DESC")
    Long countBySurveyName(@Param("surveyName") String surveyName, @Param("qNames") String[] qNames);

    // Unfortunately, and despite the method name, have to pre-find question
    // names to make this join
    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "JOIN r.survey s "
            + "WHERE a.revision = (SELECT MAX(o.revision) "
                    + "FROM Answer o LEFT JOIN o.surveyReturns r "
                    + "WHERE o.question.name IN :qNames) "
            + "AND s.name = :surveyName "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findBySurveyName(@Param("surveyName") String surveyName, @Param("qNames") String[] qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM SurveyReturn o WHERE o.applicablePeriod = :rtnPeriod AND o.org = :org) "
            + "AND a.applicablePeriod IN :periods "
            + "AND a.question.name IN :qNames AND r.org = :org "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByOrgPeriodAndQuestion(@Param("org") String org, @Param("rtnPeriod") String rtnPeriod, @Param("periods") String[] periods, @Param("qNames") String... qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM SurveyReturn o WHERE o.applicablePeriod = :rtnPeriod AND o.org = :org) "
            + "AND a.applicablePeriod IN :periods "
            + "AND a.question.name IN :qNames AND r.org = :org "
            + "ORDER BY a.applicablePeriod ASC")
    List<Answer> findByOrgPeriodAndQuestionAsc(@Param("org") String org, @Param("rtnPeriod") String rtnPeriod, @Param("periods") String[] periods, @Param("qNames") String... qNames);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE r.org = :org "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByOrg(@Param("org") String org);

    @Query("SELECT a FROM Answer a JOIN a.surveyReturns r JOIN a.question q "
            + "WHERE a.response = :orgName "
            + "AND q.name = 'ORG_NAME' "
            + "ORDER BY a.applicablePeriod DESC")
    List<Answer> findByOrgName(@Param("orgName") String orgName);

    @Query(value = "SELECT a.* from SR_ANSWER a "
            + "INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id "
            + "INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id "
            + "WHERE r.id = :sourceReturnId "
            + "AND a.derived = false "
            + "AND a.id NOT IN (SELECT answer_id FROM SR_RETURN_ANSWER WHERE survey_return_id = :targetReturnId)", nativeQuery = true)
    Set<Answer> findAnswersToImport(@Param("targetReturnId") Long targetReturnId, @Param("sourceReturnId") Long sourceReturnId);

    @Query("SELECT o FROM Answer o ORDER BY o.lastUpdated DESC")
    List<Answer> findAll();

    @Query("SELECT o FROM Answer o ORDER BY o.lastUpdated DESC")
    List<Answer> findPage(Pageable pageable);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o WHERE o.applicablePeriod = :period)"
            + "AND a.applicablePeriod = :period")
    List<Answer> findByPeriod(@Param("period") String period);

    @Query("SELECT a FROM Answer a JOIN a.surveyReturns r JOIN a.question q "
            + "WHERE a.applicablePeriod = :period "
            + "AND  r.id = :rtnId "
            + "AND  q.name = :q")
    List<Answer> findByReturnPeriodAndQ(@Param("rtnId") Long rtnId,
            @Param("period") String period,
            @Param("q") String q);

    @Query("SELECT a FROM Answer a LEFT JOIN a.surveyReturns r "
            + "WHERE a.revision = (SELECT MAX(o.revision) FROM Answer o LEFT JOIN o.surveyReturns r WHERE o.question.name IN :qNames AND r.org = :org AND o.applicablePeriod = :period)"
            + "AND a.applicablePeriod = :period")
    List<Answer> findPageByPeriod(Pageable pageable, @Param("period") String period);

    @Query("DELETE FROM Answer a WHERE a.id in :ids and a.derived = true")
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteDerivedAnswers(@Param("ids") Long[] ids);

    @Query("DELETE FROM Answer a WHERE a.id in :ids")
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAnswers(@Param("ids") Long[] ids);
}
