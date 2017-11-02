delete from SR_RETURN_ANSWER where answer_id in (79800,80750,80751);
-- Query OK, 3 rows affected (0.01 sec)

delete from SR_ANSWER where id in (79800,80750,80751);
-- Query OK, 3 rows affected (0.01 sec)

update SR_RETURN set applicable_period = '2016-17' where id in(480,481,975);

-- remove all survey returns for org = undefined
delete from SR_RETURN_ANSWER where survey_return_id = 977;
delete from SR_RETURN where id = 977;

update SR_QUESTION set tenant_id = 'sdu' where tenant_id is null;
-- 14

-- remove detached answers
delete from SR_ANSWER where id not in (Select answer_id from SR_RETURN_ANSWER);

-- remove non-unique answers 
-- RD1
delete from SR_RETURN_ANSWER where answer_id in (81899, 81901, 81904, 81903, 79898, 79803, 79900);
delete from SR_ANSWER where id in (81899, 81901, 81904, 81903, 81903, 79898, 79803, 79900);

-- RH5
delete from SR_RETURN_ANSWER where answer_id in(81059, 81060, 80892, 80893, 80747, 80748, 80609, 80608, 80847, 80848);
delete from SR_ANSWER where id in (81059, 81060, 80892, 80893, 80747, 80748, 80609, 80608, 80847, 80848);

delete from SR_RETURN_ANSWER where answer_id in(select id from SR_ANSWER where question_id in (select id from SR_QUESTION where source is null));
delete from SR_ANSWER where question_id in (select id from SR_QUESTION where source is null);

delete from SR_QUESTION where id = 577;

-- select * from ACT_ID_INFO where user_id_ = 'tstephen';
update ACT_ID_INFO set value_ = 'RD1' where id_ = 100;

