select a.id,a.revision,a.applicable_period,a.status,r.org,r.survey_id,q.id,q.name,response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where org = 'RX7'
  -- and a.applicable_period = '2016-17'
  and q.name = 'ELEC_USED'
  and survey_id = 10 
  and response is null
  order by org, a.applicable_period;

delete from tmp_id;

insert into tmp_id
select a.id
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where org = 'RX7'
  and survey_id = 10 
  and response is null
  and q.name not in ('FLOOR_AREA', 'NO_PATIENT_CONTACTS', 'OCCUPIED_BEDS', 'OP_EX', 'POPULATION')
  order by org, a.applicable_period;

-- kp -v https://api.knowprocess.com/admin/health/SDU-2018-19/RX7

delete from SR_RETURN_ANSWER where answer_id in (select id from tmp_id) ;
delete from SR_ANSWER where id in (select id from tmp_id) ;

-- kp -v -X POST https://api.knowprocess.com/returns/import/SDU-2017-18-RX7/SDU-2018-19-RX7




select a.id,a.revision,a.applicable_period,a.status,r.org,r.survey_id,q.id,q.name,response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where org = 'RX7'
  -- and a.applicable_period = '2016-17'
  and q.name = 'POPULATION'
  and survey_id = 10 
  order by org, a.applicable_period;

insert into SR_ANSWER values(null, null, 331, '2018-19',null,1,'Draft',null,null,now(),null,null,b'00');
select max(id) from SR_ANSWER;
select * from SR_ANSWER where id = 376714;
insert into SR_RETURN_ANSWER (survey_return_id, answer_id) values (1370, 376714);

insert into SR_ANSWER values(null, null, 330, '2018-19',null,1,'Draft',null,null,now(),null,null,b'00');
select max(id) from SR_ANSWER;
select * from SR_ANSWER where id = 376715;
insert into SR_RETURN_ANSWER (survey_return_id, answer_id) values (1370, 376715);

insert into SR_ANSWER values(null, null, 559, '2018-19',null,1,'Draft',null,null,now(),null,null,b'00');
insert into SR_RETURN_ANSWER (survey_return_id, answer_id) values (1370, 376716);

insert into SR_ANSWER values(null, null, 334, '2018-19',null,1,'Draft',null,null,now(),null,null,b'00');
insert into SR_RETURN_ANSWER (survey_return_id, answer_id) values (1370, 376717);

insert into SR_ANSWER values(null, null, 366, '2018-19',null,1,'Draft',null,null,now(),null,null,b'00');
insert into SR_RETURN_ANSWER (survey_return_id, answer_id) values (1370, 376718);


