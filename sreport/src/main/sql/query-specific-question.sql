## Data explorer: Have any organisations entered *any* financial information for procurement in multiple years? Could we see these data points to have a visual check before making any changes to the code? We just want a sense of whether there’s only a few organisations with information here, either opex or breakdown, if we could see the breakdown figures that would be great.

SELECT org,response,a.applicable_period,a.status
FROM SR_ANSWER a 
  JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
  JOIN SR_RETURN r on ra.survey_return_id = r.id
  JOIN SR_QUESTION q on a.question_id = q.id 
WHERE q.name = 'OP_EX'
AND a.status in ('Draft','Published')
AND a.response IS NOT NULL AND a.response >0
ORDER BY a.applicable_period;

## Are we clear already about ambulance anaesthetics? I think the question is: have any ambulance trusts entered Entonox/Equanox (nitrous oxide) information?

SELECT ac.name, acc.value FROM OL_ACCOUNT ac
  JOIN OL_ACCOUNT_CUSTOM acc on ac.id = acc.account_id
WHERE account_type LIKE 'Ambulance%'
AND acc.name = 'orgCode';

SELECT org,response,a.applicable_period,a.status
FROM SR_ANSWER a 
  JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
  JOIN SR_RETURN r on ra.survey_return_id = r.id
  JOIN SR_QUESTION q on a.question_id = q.id 
WHERE q.name like '%NITROUS%'
AND a.status in ('Draft','Published')
AND a.response IS NOT NULL AND a.response >0
ORDER BY a.applicable_period;

This does not join to ACCOUNT correctly
```
SELECT org,response,a.applicable_period,a.status
FROM SR_ANSWER a 
  JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
  JOIN SR_RETURN r on ra.survey_return_id = r.id
  JOIN SR_QUESTION q on a.question_id = q.id 
WHERE q.name like '%NITROUS%'
AND a.status in ('Draft','Published')
AND a.response IS NOT NULL AND a.response >0
AND r.org in (SELECT acc.value FROM OL_ACCOUNT ac
	  JOIN OL_ACCOUNT_CUSTOM acc on ac.id = acc.account_id
	WHERE account_type LIKE 'Ambulance%'
	AND acc.name = 'orgCode')
ORDER BY a.applicable_period;
```
