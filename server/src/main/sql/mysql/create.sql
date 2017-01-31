--DROP DATABASE srp_db;
CREATE DATABASE srp_db;
--CREATE USER 'trakeo'@'localhost' IDENTIFIED BY 'trakeo';
GRANT ALL ON srp_db.* TO 'trakeo'@'localhost'; 

//DROP INDEX AK_FULL_NAME ON contact ; 
//CREATE UNIQUE INDEX AK_FULL_NAME ON model (name, namespace);
