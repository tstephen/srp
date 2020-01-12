
# initialisation
SCRIPT_DIR=`dirname $0`
POD_NAME=kp-mysql-54d74b5c-fhp7v
DEST_DIR=/var/tmp

# copy from code.knowprocess
echo
echo COPYING LATEST BACKUPS TO HERE
echo
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_QUESTION.sql.gz . 
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_CAT.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_SURVEY.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_RETURN.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_ANSWER.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_RETURN_ANSWER.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_CFACTOR.sql.gz .
scp code.knowprocess.com:/var/backups/daily/kp_db.SR_WFACTOR.sql.gz .

# kubectl cp *sql and *sh to database host
echo
echo COPYING LATEST BACKUPS TO $POD_NAME
echo
kubectl cp kp_db.SR_QUESTION.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_CAT.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_SURVEY.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_RETURN.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_ANSWER.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_RETURN_ANSWER.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_CFACTOR.sql.gz $POD_NAME:$DEST_DIR
kubectl cp kp_db.SR_WFACTOR.sql.gz $POD_NAME:$DEST_DIR
kubectl cp $SCRIPT_DIR/_my.cnf $POD_NAME:/root/.my.cnf
kubectl cp $SCRIPT_DIR/srp-restore.sh $POD_NAME:/$DEST_DIR

# kubectl exec shell script on database host
echo
echo RESTORE DATA ON $POD_NAME
echo
#echo kubectl exec $POD_NAME -- $DEST_DIR/srp-restore.sh
kubectl exec $POD_NAME -- $DEST_DIR/srp-restore.sh