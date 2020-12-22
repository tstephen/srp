CREATE DATABASE srp_db;

CREATE USER 'thorsfield'@'%' IDENTIFIED BY 'secret';
CREATE USER 'itennison'@'%' IDENTIFIED BY 'secret';
GRANT ALL ON srp_db.* TO 'thorsfield'@'%'; 
GRANT ALL ON srp_db.* TO 'itennison'@'%'; 

FLUSH privileges;