update SR_CAT set questions = REPLACE(questions,'CARBON_REDUCTION_BASELINE_USED,','') where questions like '%CARBON_REDUCTION_BASELINE_USED%';

update SR_QUESTION set 
  label = 'What is your organisation\'s baseline year?',
  hint = 'If you are not sure, use the default of 1990-91',
  placeholder = 'yyyy-yy',
  type = 'text',
  validation = '[\\d]{4}-[\\d]{2}'
  where name = 'CARBON_REDUCTION_BASE_YEAR';

