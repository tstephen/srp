/* How many answers entered for a given org */
mysql> select count(1) from SR_RETURN_ANSWER ra join SR_RETURN r on ra.survey_return_id = r.id where r.org = 'RWH' and r.survey_id = 5 /* SDU 2016-17 */;

mysql> insert into SR_ANSWER values(1,'2015-16',0, 'tstephen',null,'201516',1,'Published',null,null,null,4398,521);

/* All answers for an org */
select * from SR_ANSWER a
  INNER JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
  INNER JOIN SR_RETURN r on ra.survey_return_id = r.id
  where r.org = 'RD1';

select ra.survey_return_id, a.id, a.applicable_period, q.name, a.response from SR_ANSWER a
  INNER JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
  INNER JOIN SR_RETURN r on ra.survey_return_id = r.id
  INNER JOIN SR_QUESTION q on a.question_id = q.id
  where r.org = 'RDR';


/* Detached answers */
select * from SR_ANSWER where id not in (Select answer_id from SR_RETURN_ANSWER);

/* find non-unique answers */
select count(id), question_id from SR_ANSWER where id in (
  Select answer_id from SR_RETURN_ANSWER where survey_return_id = 480)
group by question_id having count(id) > 1;

select count(id), question_id from SR_ANSWER where id in (
  Select answer_id from SR_RETURN_ANSWER)
group by question_id having count(id) > 1;

/* remove non-unique answers, most recent first */
insert into tmp_id select max(id) from SR_ANSWER where id in (
  Select answer_id from SR_RETURN_ANSWER where survey_return_id = 956)
group by question_id having count(id) > 1;
delete from SR_RETURN_ANSWER where answer_id in (select id from tmp_id);

/* import answers from other return */
INSERT INTO SR_RETURN_ANSWER (survey_return_id, answer_id)
SELECT 956, a.id from SR_ANSWER a
INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id
INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id
INNER JOIN SR_SURVEY s ON r.survey_id = s.id
WHERE r.org = 'RDR'
AND s.name = 'ERIC-2015-16';

/*
 * Investigate duplicated answers
 */
select a.id,a.revision,r.org,r.survey_id,q.id,q.name,response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where q.name = 'ELEC_USED'
  -- and a.applicable_period = '2015-16'
  and org = 'RD1'
  order by org;

select a.revision,r.org,q.id,q.name,response,a.applicable_period,count(a.id) as cnt
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
where org = 'RD1'
GROUP BY a.revision,r.org,q.id,q.name,a.applicable_period,response
HAVING cnt>1
ORDER BY q.name,a.applicable_period;


