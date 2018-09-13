update  SR_QUESTION set type = 'number' where type is null and name like '%PCT';
-- Query OK, 7 rows affected (0.24 sec)
-- Rows matched: 7  Changed: 7  Warnings: 0

update  SR_QUESTION set type = 'number' where type is null and name like '%MILEAGE';
-- Query OK, 2 rows affected (0.00 sec)
-- Rows matched: 2  Changed: 2  Warnings: 0

update  SR_QUESTION set type = 'number' where type is null and name like 'ELEC_TOTAL_RENEWABLE_USED';
-- Query OK, 1 row affected (0.01 sec)
-- Rows matched: 1  Changed: 1  Warnings: 0

update  SR_QUESTION set type = 'number' where type is null and name like 'OWNED%';
-- Query OK, 6 rows affected (0.01 sec)
-- Rows matched: 6  Changed: 6  Warnings: 0

update  SR_QUESTION set type = 'number' where type is null and name like '%CO2E';
-- Query OK, 83 rows affected (0.44 sec)
-- Rows matched: 83  Changed: 83  Warnings: 0
