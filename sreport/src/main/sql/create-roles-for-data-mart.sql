CREATE USER thorsfield WITH PASSWORD 'secret';
grant connect ON DATABASE srp TO thorsfield;

grant usage on schema srp to "thorsfield";

grant select on sr_answer to thorsfield;
grant select on sr_cat to thorsfield;
grant select on sr_cfactor to thorsfield;
grant select on sr_question to thorsfield;
grant select on sr_return to thorsfield;
grant select on sr_return_answer to thorsfield;
grant select on sr_survey to thorsfield;
grant select on sr_wfactor to thorsfield;


CREATE USER itennison WITH PASSWORD 'secret';
grant connect ON DATABASE srp TO itennison;

grant usage on schema srp to "itennison";

grant select on sr_answer to itennison;
grant select on sr_cat to itennison;
grant select on sr_cfactor to itennison;
grant select on sr_question to itennison;
grant select on sr_return to itennison;
grant select on sr_return_answer to itennison;
grant select on sr_survey to itennison;
grant select on sr_wfactor to itennison;

