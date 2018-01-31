insert into ACT_ID_USER values ('tstephen',1,'Tim','Stephenson','tim@srp.digital','tstephen',null);

insert into ACT_ID_GROUP values('user',1,'User',null);
insert into ACT_ID_GROUP values('admin',1,'Admin',null);

insert into ACT_ID_MEMBERSHIP values('tstephen','user');
insert into ACT_ID_MEMBERSHIP values('tstephen','admin');

insert into ACT_ID_INFO values(1,1,'tstephen','userinfo','tenant','srp',null,null);
