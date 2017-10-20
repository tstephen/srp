update TR_ORG_TYPE set name = 'Clinical Commissioning Group' where name = 'Clinical Commissioning Groups';
update SR_ANSWER set response = 'Clinical Commissioning Group' where response = 'Clinical Commissioning Groups';

update TR_ORG_TYPE set name = 'Dept and ALB' where name = 'Dept and ALBs';

update SR_WFACTOR set org_type = 'Clinical Commissioning Group' where org_type = 'Ccg';

UPDATE SR_ANSWER set question_id = 559 where question_id = 671;
UPDATE SR_QUESTION set name = 'BIZ_MILEAGE_ROAD' where name = 'PERSONAL_ROAD_MILES';
 
 update SR_CAT set questions  = REPLACE(questions,'NO_BEDS','OCCUPIED_BEDS');
 update SR_CAT set questions  = REPLACE(questions,'PERSONAL_ROAD_MILES','BIZ_MILEAGE_ROAD');

 