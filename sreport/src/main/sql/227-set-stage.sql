/*
 * Copyright 2011-2018 Tim Stephenson and contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 * Update account stage based on return answers.
 *
 * NOTE: This could be done in one multi-table update but performance is
 * prohibitive, hence this tmp table approach.
 * 
 * Invoke (e.g. with cron) using:
 *   echo 'CALL sp_update_account_stage();' | mysql srp_db
 */

delimiter //

DROP PROCEDURE IF EXISTS sp_update_account_stage //
 
CREATE PROCEDURE sp_update_account_stage() 
BEGIN
  DROP TABLE IF EXISTS tmp_org;
  
  CREATE TABLE tmp_org (
    org char(3) NOT NULL
  );
  
  INSERT INTO tmp_org
    SELECT org
    FROM SR_ANSWER a 
      JOIN SR_RETURN_ANSWER ra on a.id = ra.answer_id
      JOIN SR_RETURN r on r.id = ra.survey_return_id
    WHERE a.applicable_period = ( SELECT value from TR_MODEL_PARAM WHERE name = 'REPORTING_PERIOD' )
    GROUP BY r.org
    HAVING count(1) > 1;
  
  UPDATE OL_ACCOUNT a, OL_ACCOUNT_CUSTOM ac SET stage = 'In progress'
  WHERE a.id = ac.account_id
  AND a.tenant_id = 'sdu' 
  AND ac.value IN ( SELECT org from tmp_org );
END //

delimiter ;

CALL sp_update_account_stage();
