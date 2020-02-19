#!/bin/bash
SCRIPT_DIR=/var/tmp

cat $SCRIPT_DIR/kp_db.SR_QUESTION.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_CAT.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_SURVEY.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_RETURN.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_ANSWER.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_RETURN_ANSWER.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_CFACTOR.sql.gz | gunzip | mysql srp_db
cat $SCRIPT_DIR/kp_db.SR_WFACTOR.sql.gz | gunzip | mysql srp_db