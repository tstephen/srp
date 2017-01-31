 select distinct(defra_category_description) from eclasss2;

 insert into defra_category (code, description) 
   select replace(distinct(defra_category_description),' ','-')), distinct(defra_category_description) from eclasss2;
   
 
 insert into defra_category (code) 
   select distinct(defra_category_description) from eclasss2;
   
 update defra_category set description = code; 
 
 update defra_category set code = lower(replace(code,' ','-'));
 
update eclasss2 set defra_decc_code_code = null
where defra_decc_code_code = '';


select distinct( concat('*',category_code,'*')) from defra_decc_code

select distinct( concat('*',defra_decc_code_code,'*')) as code from eclasss2
union 
select distinct( concat('*',code,'*')) as code from defra_decc_code
order by code asc
