update SR_QUESTION set label = 'If your organisation uses the <a href=”http://www.sduhealth.org.uk/sdat/”>Sustainable Development Assessment Tool (SDAT)</a> tool when was your last self assessment?'
where name = 'LAST_GCC_DATE';

update SR_QUESTION set label = 'And what was your last SDAT score?' 
where name = 'LAST_GCC_SCORE';
