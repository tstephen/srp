-- Gas use in 2018-19
SELECT r.org, r.survey_id, q.name, a.applicable_period, a.response, a.status
  FROM SR_ANSWER a
       INNER JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
       INNER JOIN SR_RETURN r ON r.id = ra.survey_return_id
       INNER JOIN SR_QUESTION q ON q.id = a.question_id
 WHERE q.name in ('DESFLURANE', 'SEVOFLURANE')
   AND response is not null
   AND a.status != 'Superseded'
   AND r.survey_id = 12
ORDER BY r.org, r.survey_id, a.applicable_period, q.name;


-- Area of the relevant trusts (target: 97496)
SELECT r.org, q.name, a.applicable_period, a.response, a.status
  FROM SR_ANSWER a
       INNER JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
       INNER JOIN SR_RETURN r ON r.id = ra.survey_return_id
       INNER JOIN SR_QUESTION q ON q.id = a.question_id
 WHERE q.name in ('FLOOR_AREA')
   AND response is not null
   AND a.status != 'Superseded'
   AND r.survey_id = 12
   AND a.applicable_period = '2018-19'
   AND r.org in ('00J','R0A','RKB','RWA','RCB','RY6','RVJ','01D','05D','RA7','RAL','RAX','RCF','RDR','RDU','RH8','RJ1','RJF','RJL','RN5','RP4','RT3','RTP','RW1','RW5','RWF','RWH','RWX','RX2','RX4','RXC','RYE','RYG','RYV','TAH','RCU','RV9','RNA','R1L','RAS','RFS')
ORDER BY a.response;