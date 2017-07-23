select CONCAT("update SR_WFACTOR set p_val = WHERE category = '",category,"' and org_type = '",org_type,"'") from SR_WFACTOR where org_type = 'community';                                         ";


update SR_WFACTOR set p_val = 0.1834677047 WHERE category = 'Energy' and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0802108752 WHERE category = 'Gas' and org_type = 'Community';
update SR_WFACTOR set p_val = 0.1025683297 WHERE category = 'Electricity' and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0006884998 WHERE category = 'Oil' and org_type = 'Community';
update SR_WFACTOR set p_val = 0 WHERE category = 'Coal' and org_type = 'Community';
update SR_WFACTOR set p_val = 0.1470024647 WHERE category IN ( 'Business services', 'BIZ_SVCS' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0289542061 WHERE category = 'Construction' and org_type = 'Community';

update SR_WFACTOR set p_val = 0.0498505598 WHERE category IN ( 'Food and catering', 'CATERING' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0747718079 WHERE category IN ( 'Freight transport', 'FREIGHT' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.01331034 WHERE category IN ('Information and communication technologies','ICT') and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0436220151 WHERE category IN ('Manufactured fuels chemicals and gases', 'FUEL_CHEM_AND_GASES' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.1561155687 WHERE category IN ( 'Medical Instruments /equipment', 'MED_INSTRUMENTS' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0176290712 WHERE category IN ( 'Other manufactured products', 'OTHER_MANUFACTURING' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0 WHERE category IN ( 'Other procurement', 'OTHER_PROCURMENT' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0346533841 WHERE category IN ( 'Paper products', 'PAPER' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0043506097 WHERE category IN ( 'Pharmaceuticals', 'PHARMA' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0364698453 WHERE category IN ( 'Waste products and recycling', 'WASTE' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0145067467 WHERE category IN ( 'Water and sanitation', 'WATER' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0 WHERE category IN ( 'Anaesthetic gases', 'ANAESTHETIC_GASES' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0705085032 WHERE category IN ( 'Patient Travel', 'PATIENT_TRAVEL' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0017042075 WHERE category IN ( 'Visitor Travel', 'VISITOR_TRAVEL' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0396081122 WHERE category IN ( 'Staff Travel', 'STAFF_TRAVEL' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0834748531 WHERE category IN ( 'Business Travel and fleet', 'BIZ_TRAVEL' ) and org_type = 'Community';
update SR_WFACTOR set p_val = 0.0439085102 WHERE category IN ( 'Commissioned health and social care services', 'COMMISSIONING' ) and org_type = 'Community';

update SR_QUESTION set name = 'BIZ_SVCS_SPEND' where name = 'BIZ_SVC_SPEND';
update SR_QUESTION set name = 'MED_INSTR_SPEND' where name = 'MED_INSTRUMENTS_SPEND';
update SR_CAT set questions = REPLACE(questions,'BIZ_SVC_SPEND','BIZ_SVCS_SPEND');
update SR_CAT set questions = REPLACE(questions,'MED_INSTRUMENTS_SPEND','MED_INSTR_SPEND');
 