CREATE TABLE `sdu_etr` (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  org_code char(3) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  nat_grp varchar(255) DEFAULT NULL,
  hl_deo varchar(255) DEFAULT NULL,
  addr1 varchar(255) DEFAULT NULL,
  addr2 varchar(255) DEFAULT NULL,
  addr3 varchar(255) DEFAULT NULL,
  addr4 varchar(255) DEFAULT NULL,
  addr5 varchar(255) DEFAULT NULL,
  post_code varchar(255) DEFAULT NULL,
  open_date char(8) DEFAULT NULL,
  close_date char(8) DEFAULT NULL,
  amended bit DEFAULT b'00',
  gor_code varchar(255) DEFAULT NULL,
  account_id bigint(20) NULL,
  PRIMARY KEY (`id`)
);