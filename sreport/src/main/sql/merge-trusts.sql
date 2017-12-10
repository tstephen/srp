-- Check answers are from the right (ERIC) surveys
SELECT distinct(r.name) from SR_ANSWER a INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id INNER JOIN SR_SURVEY s ON r.survey_id = s.id WHERE r.org = 'RM2';

-- find answers to import (used in app)
SELECT a.* from SR_ANSWER a
INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id
INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id
INNER JOIN SR_SURVEY s ON r.survey_id = s.id
WHERE r.org = 'RM2'
AND s.name = 'ERIC-2015-16-RM2'
AND a.id NOT IN (SELECT answer_id FROM SR_RETURN_ANSWER WHERE survey_return_id = ?)


CREATE TABLE `tmp_ans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `response` varchar(1000) DEFAULT NULL,
  `question_id` int(11) NOT NULL,
  `applicable_period` varchar(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `revision` smallint(6) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `created` datetime DEFAULT NULL,
  `derived` bit(1) NOT NULL DEFAULT b'0',
  `name` varchar(50) NOT NULL,
  `org` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
);

-- create synthetic ERIC return for merged trusts 
insert into tmp_ans
SELECT null, sum(a.response), a.question_id, a.applicable_period, 
    'tim@trakeo.com',1,'Draft',now(), b'01',r.name,r.org
from SR_ANSWER a 
INNER JOIN SR_RETURN_ANSWER ra ON a.id = ra.answer_id 
INNER JOIN SR_RETURN r ON ra.survey_return_id = r.id 
INNER JOIN SR_SURVEY s ON r.survey_id = s.id 
WHERE r.org in ('RM2', 'RW3') 
group by question_id,a.applicable_period;

 select max(id) from SR_ANSWER; // 104468

insert into SR_ANSWER (`id`,`response`,`question_id`,`applicable_period`,
  `created_by`,`revision`,`status`,`created`, `derived`)
select 104468+id,`response`,`question_id`,`applicable_period`,
  `created_by`,`revision`,`status`,`created`, `derived`
from tmp_ans;

select max(id) from SR_RETURN; // 996

insert into SR_RETURN values(null,'2015-16','tim@trakeo.com','ERIC-2015-16-R0A','R0A',1,'Published',null,null,4,now(),null,null);
insert into SR_RETURN values(null,'2014-15','tim@trakeo.com','ERIC-2014-15-R0A','R0A',1,'Published',null,null,6,now(),null,null);
insert into SR_RETURN values(null,'2013-14','tim@trakeo.com','ERIC-2013-14-R0A','R0A',1,'Published',null,null,7,now(),null,null);

insert into SR_RETURN_ANSWER (survey_return_id, answer_id)
select 997, 104468+id
from tmp_ans 
where applicable_period = '2015-16';

insert into SR_RETURN_ANSWER (survey_return_id, answer_id)
select 998, 104468+id
from tmp_ans 
where applicable_period = '2014-15';

insert into SR_RETURN_ANSWER (survey_return_id, answer_id)
select 999, 104468+id
from tmp_ans 
where applicable_period = '2013-14';
