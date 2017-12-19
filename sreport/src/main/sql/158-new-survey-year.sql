-- Assuming that a copy of last year is a good starting point

-- Create new survey
insert into SR_SURVEY values (null,'2017-18','tim@trakeo.com', 'SDU-2017-18', 'Published', NULL, now(), null, null); 


-- Create new categories
insert into SR_CAT select null, name,9 /*new survey id */, questions 
from SR_CAT where survey_id = 5 /* last year survey id */;
