--DROP DATABASE srp_db;
CREATE DATABASE srp_db;
--CREATE USER 'tstephen'@'localhost' IDENTIFIED BY 'tstephen';
GRANT ALL ON srp_db.* TO 'tstephen'@'localhost'; 

--DROP INDEX AK_FULL_NAME ON contact ;
--CREATE UNIQUE INDEX AK_FULL_NAME ON model (name, namespace);
