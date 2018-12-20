// Find questions with answers in first report not present in second 

select a.id,a.revision,a.applicable_period,a.status,r.org,r.survey_id,a.response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
WHERE org = 'RVR'
and r.id = 1250
/* and a.applicable_period = '2017-18' */
and a.question_id not in (
    select a.question_id
      from SR_ANSWER a
        INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
        INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    WHERE org = 'RVR'
    and r.id = 1249
    and a.status = 'Draft'
    and a.revision = 2
    /* and a.applicable_period = '2017-18' */
);


/*
 * Remove empty responses in the report to be merged FROM
 */
delete from tmp_id;

insert into tmp_id
select a.id
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
WHERE org = 'RVR'
and r.id = 1250
and (a.response is null or a.response = '');

delete from SR_RETURN_ANSWER where answer_id in (select id from tmp_id);
delete from SR_ANSWER where id in (select id from tmp_id);

 