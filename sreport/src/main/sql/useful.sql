
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

/* answers in one return missing from other */
 select a.id,a.applicable_period,a.status, a.revision,a.question_id,a.response
 from SR_RETURN r
 JOIN SR_RETURN_ANSWER ra on ra.survey_return_id = r.id
 JOIN SR_ANSWER a on a.id = ra.answer_id
 where org = '02H'
 and r.id = 1473
 and a.response is not null
 and a.response != 0
 and question_id not in (select a.question_id
		 from SR_RETURN r
		 JOIN SR_RETURN_ANSWER ra on ra.survey_return_id = r.id
		 JOIN SR_ANSWER a on a.id = ra.answer_id
		 where org = '02H'
		 and r.id = 1613
		 and a.response is not null
		 and a.response != 0
 );

/* add the missing ones identified */
insert into SR_ANSWER (
  response,
  question_id,
  applicable_period,
  revision,
  status,
  derived)
 select a.response,a.question_id,a.applicable_period,a.revision,a.status,a.derived
 from SR_RETURN r
 JOIN SR_RETURN_ANSWER ra on ra.survey_return_id = r.id
 JOIN SR_ANSWER a on a.id = ra.answer_id
 where org = '02H'
 and r.id = 1473
 and a.response is not null
 and a.response != 0
 and question_id not in (select a.question_id
		 from SR_RETURN r
		 JOIN SR_RETURN_ANSWER ra on ra.survey_return_id = r.id
		 JOIN SR_ANSWER a on a.id = ra.answer_id
		 where org = '02H'
		 and r.id = 1613
		 and a.response is not null
		 and a.response != 0
 )
 and question_id = 976;