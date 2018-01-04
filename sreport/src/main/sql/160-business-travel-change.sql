update SR_QUESTION 
  set label = 'Business Mileage - Non-organisation Owned Grey Fleet/Pool/Third party provided used by the organisation (excluding leased vehicles) Road Travel (Hire etc.)'
  where name = 'BIZ_MILEAGE_ROAD';
update SR_QUESTION set name = 'BIZ_MILEAGE_ROAD_COST',
  label = 'Cost of Business Mileage - Non-organisation Grey Owned Fleet/Pool/Third party provided used by the organisation (excluding leased vehicles) Road Travel (Hire etc.)',
  unit = '£'
  where name = '_3RD_PTY_ROAD_MILES';
 update SR_CAT set questions = replace(questions, '_3RD_PTY_ROAD_MILES','BIZ_MILEAGE_ROAD_COST');
