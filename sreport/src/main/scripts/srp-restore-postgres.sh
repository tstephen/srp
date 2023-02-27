#!/bin/bash
SRC=~/git
DB_NAME='srp_'`date +"%Y%m%d_%H%M"`

# set env vars inc. pwds
. $SRC/k8s-kp/env.sh
# establish connection to database
kubectl port-forward --namespace default svc/kp-postgres-postgresql 5432:5432 &
# create new database to restore to
echo "CREATE DATABASE ..."
kubectl run kp-postgres-postgresql-client --rm --tty -i --restart='Never' --namespace default --image docker.io/bitnami/postgresql:11.11.0-debian-10-r92 --env="PGPASSWORD=$POSTGRES_PASSWORD" --command -- psql --host kp-postgres-postgresql -U postgres -d postgres -p 5432 -c "CREATE DATABASE "$DB_NAME" WITH OWNER srp;"


export PGPASSWORD=$POSTGRES_PASSWORD
cat srp.sql.gz | gunzip | psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tmp_id;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tmp_org;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_disclosure;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_scorecard;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_pledge;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_fin_txn;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_fin_txn_summary;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_fin_year;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_org_intvn;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_intvn;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_intvn_type;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_eclass;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_code;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_category;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_model_param;" 
psql --host 127.0.0.1 -U postgres -d $DB_NAME -p 5432 -c "DROP TABLE tr_org_type;" 

echo "DON'T FORGET TO STOP THE PORT FORWARD"

