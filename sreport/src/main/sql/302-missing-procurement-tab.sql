update SR_CAT set questions = replace(questions, 'ECLASS_USER,','') where name = 'Policy';  

update SR_CAT set name = 'Procurement', questions = replace(questions, 'PROVISIONS','ECLASS_USER,PROVISIONS')
where name = 'E-Class Profile' and questions not like '%ECLASS_USER%';

update SR_CAT set questions = 'BIZ_SVCS_SPEND,CAPITAL_SPEND,CONSTRUCTION_SPEND,CATERING_SPEND,FREIGHT_SPEND,ICT_SPEND,CHEM_AND_GAS_SPEND,OTHER_MANUFACTURED_SPEND,MED_INSTR_SPEND,OTHER_SPEND,PAPER_AND_CARD_SPEND,PHARMA_SPEND,TRAVEL_SPEND,COMMISSIONING_SPEND'
where name = 'Spend Profile' and questions not like '%BIZ_SVCS_SPEND%';
