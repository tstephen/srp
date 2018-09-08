/*
 * Investigate the problem
 */
select a.id,a.revision,a.applicable_period,a.status,r.org,r.survey_id,q.id,q.name,response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where org = 'RHA'
  -- and a.applicable_period = '2016-17'
  and q.name = 'GAS_USED'
  order by org, a.applicable_period;

select a.id,a.revision,a.applicable_period,a.status,r.org,r.survey_id,q.id,q.name,response
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    INNER JOIN SR_QUESTION q on q.id = a.question_id
  where org = 'RQ6'
  -- and a.applicable_period = '2016-17'
  and a.revision = 1
  order by org, a.applicable_period;

select distinct(a.revision)
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
WHERE org = 'RQ6'
and r.id = 1275;

/*
 * PROBLEM: Multiple answers with same revision, period, question and org
 *          (e.g. 300602, 141469) which will lead to non-deterministic answers
 *          in the reports
 * SOLUTION: Mark as superseded the lowest id (which was entered first)
 */
delete from tmp_id;
insert into tmp_id
  select min(id)
  from SR_ANSWER
  where id in (
    Select answer_id from SR_RETURN_ANSWER where survey_return_id = 1259
  )
  group by question_id, applicable_period, revision, status having count(id) > 1;

delete from SR_RETURN_ANSWER where answer_id in (select id from tmp_id);

update SR_ANSWER set status = 'Superseded'
where id in (select id from tmp_id);


/*
 * PROBLEM: Varying max(revision) across the report. In this subset there are
 *          revisions = 1 and 2 both with status 'Draft'. NOTE: Where the status
 *          is published and a higher revision is Draft, which also occurs, that is ok.
 * SOLUTION: having eliminated multiple answers set all remaining Draft answers
 *          to highest revision.
 */
delete from tmp_id;
insert into tmp_id
select a.id
  from SR_ANSWER a
    INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
    INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
WHERE org = 'RHA'
and r.id = 1259
and a.status = 'Draft'
and a.revision = 1
and a.id not in (
    select a.id
      from SR_ANSWER a
        INNER JOIN SR_RETURN_ANSWER ra on ra.answer_id = a.id
        INNER JOIN SR_RETURN r on r.id = ra.survey_return_id
    WHERE org = 'RHA'
    and r.id = 1259
    and a.status = 'Draft'
    and a.revision = 2
);

update SR_ANSWER set revision = 2
where id in (select id from tmp_id);
