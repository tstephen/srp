drop table ol_tenant; 

CREATE TABLE `ol_tenant` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remote_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into ol_tenant values('sdu', 'SDU', null);

alter table ol_memo modify column `last_updated` timestamp NULL DEFAULT NULL;

alter table ol_memo add column  `short_content` varchar(255) DEFAULT NULL;

-- RENAME TABLES REQUIRED WITH MOVE TO BOOT 1.4; HIBERNATE 5 ETC AND JWT
RENAME TABLE ol_account         TO OL_ACCOUNT;
RENAME TABLE ol_account_custom  TO OL_ACCOUNT_CUSTOM;
RENAME TABLE ol_activity        TO OL_ACTIVITY;
RENAME TABLE ol_cohort_custom   TO OL_COHORT_CUSTOM;
RENAME TABLE ol_cohort_perf     TO OL_COHORT_PERF;
RENAME TABLE ol_contact         TO OL_CONTACT;
RENAME TABLE ol_contact_custom  TO OL_CONTACT_CUSTOM;
RENAME TABLE ol_dmn_model       TO OL_DMN_MODEL;
RENAME TABLE ol_document        TO OL_DOCUMENT;
RENAME TABLE ol_domain_entity   TO OL_DOMAIN_ENTITY;
RENAME TABLE ol_domain_field    TO OL_DOMAIN_FIELD;
RENAME TABLE ol_domain_model    TO OL_DOMAIN_MODEL;
RENAME TABLE ol_memo            TO OL_MEMO;
RENAME TABLE ol_memo_dist       TO OL_MEMO_DIST;
RENAME TABLE ol_metric          TO OL_METRIC;
RENAME TABLE ol_model_issue     TO OL_MODEL_ISSUE;
RENAME TABLE ol_note            TO OL_NOTE;
RENAME TABLE ol_process_model   TO OL_PROCESS_MODEL;
RENAME TABLE ol_tenant          TO OL_TENANT;
RENAME TABLE sr_answer          TO SR_ANSWER;
RENAME TABLE sr_cat             TO SR_CAT;
RENAME TABLE sr_question        TO SR_QUESTION;
RENAME TABLE sr_return          TO SR_RETURN;
RENAME TABLE sr_survey          TO SR_SURVEY;
RENAME TABLE tr_category        TO TR_CATEGORY;
RENAME TABLE tr_code            TO TR_CODE;
RENAME TABLE tr_disclosure      TO TR_DISCLOSURE;
RENAME TABLE tr_eclass          TO TR_ECLASS;
RENAME TABLE tr_fin_txn         TO TR_FIN_TXN;
RENAME TABLE tr_fin_txn_summary TO TR_FIN_TXN_SUMMARY;
RENAME TABLE tr_fin_year        TO TR_FIN_YEAR;
RENAME TABLE tr_intvn           TO TR_INTVN;
RENAME TABLE tr_intvn_type      TO TR_INTVN_TYPE;
RENAME TABLE tr_model_param     TO TR_MODEL_PARAM;
RENAME TABLE tr_org             TO TR_ORG;
RENAME TABLE tr_org_intvn       TO TR_ORG_INTVN;
RENAME TABLE tr_org_type        TO TR_ORG_TYPE;
RENAME TABLE tr_pledge          TO TR_PLEDGE;
RENAME TABLE tr_scorecard       TO TR_SCORECARD;


