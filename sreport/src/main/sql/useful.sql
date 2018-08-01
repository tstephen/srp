
/* Detached answers */
select * from SR_ANSWER where id not in (Select answer_id from SR_RETURN_ANSWER);

/* find non-unique answers */
select count(id) as cnt, question_id, applicable_period from SR_ANSWER where id in (
  Select answer_id from SR_RETURN_ANSWER where survey_return_id = 1259)
group by question_id, applicable_period having count(id) > 1
order by cnt;

/* import answers from other return */
INSERT INTO SR_RETURN_ANSWER (survey_return_id, answer_id)
SELECT 956, a.id from SR_ANSWER a
INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id
INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id
INNER JOIN SR_SURVEY s ON r.survey_id = s.id
WHERE r.org = 'RDR'
AND s.name = 'ERIC-2015-16';

