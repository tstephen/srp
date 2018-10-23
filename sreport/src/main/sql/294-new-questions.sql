insert into SR_QUESTION values(null,null,'The seventh CCG we provide services to','CCG7_SERVED','Start typing to select from the available providers',b'00','text',null,null,'SDU',null,'','sdu');
insert into SR_QUESTION values(null,null,'The eighth CCG we provide services to','CCG8_SERVED','Start typing to select from the available providers',b'00','text',null,null,'SDU',null,'','sdu');

update SR_CAT set questions = replace(questions,'CCG6_SERVED,','CCG6_SERVED,CCG7_SERVED,CCG8_SERVED,') where survey_id = 9;
