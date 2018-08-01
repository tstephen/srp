/* 
 * Count answers for a given org 
 */
select count(1) from SR_RETURN_ANSWER ra 
  join SR_RETURN r on ra.survey_return_id = r.id 
where r.org = 'RHA' and r.survey_id = 5 /* SDU 2016-17 */;

/*
 * Count questions answered by org, status and revision
 */
select count(q.id), r.org, r.revision, a.revision, r.status, a.status 
FROM SR_RETURN_ANSWER ra  
  inner join SR_ANSWER a on ra.answer_id=a.id   
  inner join SR_QUESTION q on a.question_id=q.id
  INNER JOIN SR_RETURN r on r.id = ra.survey_return_id 
where  a.applicable_period = '2017-18' and a.status != 'Superseded' 
and r.status != 'Superseded' 
group by r.org, r.revision, a.revision, r.status, a.status;

/* 
 * Select answers by org 
 */
select ra.survey_return_id, a.id, a.applicable_period, q.name, a.response 
from SR_ANSWER a
  INNER JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
  INNER JOIN SR_RETURN r on ra.survey_return_id = r.id
  INNER JOIN SR_QUESTION q on a.question_id = q.id
  where r.org = 'RDR';