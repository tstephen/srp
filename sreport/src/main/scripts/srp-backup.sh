#
# TS 10 Jan 20
# Backup just the SRP tables
#
mysqldump kp_db SR_CAT | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_CAT.sql.gz"
mysqldump kp_db SR_QUESTION | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_QUESTION.sql.gz"
mysqldump kp_db SR_SURVEY | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_SURVEY.sql.gz"
mysqldump kp_db SR_RETURN | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_RETURN.sql.gz"
mysqldump kp_db SR_ANSWER | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_ANSWER.sql.gz"
mysqldump kp_db SR_RETURN_ANSWER | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_RETURN_ANSWER.sql.gz"
mysqldump kp_db SR_CFACTOR | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_CFACTOR.sql.gz"
mysqldump kp_db SR_WFACTOR | gzip | ssh code.knowprocess.com "cat > /var/backups/daily/kp_db.SR_WFACTOR.sql.gz"

