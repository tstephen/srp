alter table SR_RETURN drop column created ;
alter table SR_RETURN drop column last_updated ;
alter table SR_RETURN drop column submitted_date ;
alter table SR_RETURN add column created datetime default null ;
alter table SR_RETURN add column last_updated datetime default null ;
alter table SR_RETURN add column submitted_date datetime default null ;

alter table SR_SURVEY drop column created ;
alter table SR_SURVEY drop column last_updated ;
-- alter table SR_SURVEY drop column submitted_date ;
alter table SR_SURVEY add column created datetime default null ;
alter table SR_SURVEY add column last_updated datetime default null ;
alter table SR_SURVEY add column submitted_date datetime default null ;

alter table SR_ANSWER drop column created ;
alter table SR_ANSWER drop column last_updated ;
alter table SR_ANSWER drop column submitted_date ;
alter table SR_ANSWER add column created datetime default null ;
alter table SR_ANSWER add column last_updated datetime default null ;
alter table SR_ANSWER add column submitted_date datetime default null ;

alter table SR_CFACTOR drop column created ;
alter table SR_CFACTOR drop column last_updated ;
-- alter table SR_CFACTOR drop column submitted_date ;
alter table SR_CFACTOR add column created datetime default null ;
alter table SR_CFACTOR add column last_updated datetime default null ;
alter table SR_CFACTOR add column submitted_date datetime default null ;

alter table SR_WFACTOR drop column created ;
alter table SR_WFACTOR drop column last_updated ;
-- alter table SR_WFACTOR drop column submitted_date ;
alter table SR_WFACTOR add column created datetime default null ;
alter table SR_WFACTOR add column last_updated datetime default null ;
alter table SR_WFACTOR add column submitted_date datetime default null ;


-- ALTER TABLE SR_ANSWER
-- ADD CONSTRAINT UC_Q_PERIOD_REVISION UNIQUE (question_id, applicable_period, revision); 