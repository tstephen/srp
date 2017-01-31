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