insert into tr_intvn_hdr (
  name,
  description,
  status,
  tenant_id,
  confidence,
  modelling_year,
  lifetime,
  uptake,
  scaling
)
select
  name,
  description,
  status,
  tenant_id,
  confidence,
  modelling_year,
  lifetime,
  uptake,
  scaling
from tr_intervention;

update tr_intervention set intvn_hdr_id = 1 where id = 2;
update tr_intervention set org_type_id = 2;

select i.id as id1_29_, i.annual_cash_inflows as annual_c2_29_, i.annual_cash_inflowsts as annual_c3_29_, i.annual_cash_outflows as annual_c4_29_, i.annual_cash_outflowsts as annual_c5_29_, i.tonnes_co2e_saved_pa as tonnes_c6_29_, i.annual_tonnes_co2e_savedts as annual_t7_29_, i.cash_outflow_up_front as cash_out8_29_, i.confidence as confiden9_29_, i.cost_per_tonne_co2e as cost_pe10_29_, i.description as descrip11_29_, i.financial_savings as financi12_29_, i.intvn_hdr_id as intvn_h24_29_, i.intervention_type as interve13_29_, i.lifetime as lifetim14_29_, i.modelling_year as modelli15_29_, i.name as name16_29_, i.organisation_type_id as organis25_29_, i.scaling as scaling17_29_, i.status as status18_29_, i.tenant_id as tenant_19_29_, i.tonnes_co2e_saved as tonnes_20_29_, i.tonnes_co2e_saved_ty as tonnes_21_29_, i.total_npv as total_n22_29_, i.uptake as uptake23_29_ 
from tr_intervention i 
  inner join tr_intvn_hdr ih on i.intvn_hdr_id=ih.id 
  inner join tr_org_type ot on i.org_type_id=ot.id 
where i.tenant_id='sdu' 
order by i.name ASC;

select i.id as id1_29_, i.annual_cash_inflows as annual_c2_29_, i.annual_cash_inflowsts as annual_c3_29_, i.annual_cash_outflows as annual_c4_29_, i.annual_cash_outflowsts as annual_c5_29_, i.tonnes_co2e_saved_pa as tonnes_c6_29_, i.annual_tonnes_co2e_savedts as annual_t7_29_, i.cash_outflow_up_front as cash_out8_29_, i.classification as classifi9_29_, i.confidence as confide10_29_, i.cost_per_tonne_co2e as cost_pe11_29_, i.description as descrip12_29_, i.financial_savings as financi13_29_, i.intvn_hdr_id as intvn_h24_29_, i.lifetime as lifetim14_29_, i.modelling_year as modelli15_29_, i.name as name16_29_, i.org_type_id as org_typ25_29_, i.scaling as scaling17_29_, i.status as status18_29_, i.tenant_id as tenant_19_29_, i.tonnes_co2e_saved as tonnes_20_29_, i.tonnes_co2e_saved_ty as tonnes_21_29_, i.total_npv as total_n22_29_, i.uptake as uptake23_29_ 
from tr_intervention i 
  left outer join tr_intvn_hdr it on i.intvn_hdr_id=it.id 
  left outer join tr_org_type ot on i.org_type_id=ot.id 
where i.tenant_id='sdu' order by i.name ASC


alter table tr_intvn_hdr change intervention_type classification varchar(255);

alter table tr_intervention  drop column  name;
alter table tr_intervention  drop column  intervention_id;
alter table tr_intervention  drop column  description;
alter table tr_intervention  drop column  co2e_savings_elec;
alter table tr_intervention  drop column  co2e_savings_ff;
alter table tr_intervention  drop column  confidence;
alter table tr_intervention  drop column  modelling_year;
alter table tr_intervention  drop column  lifetime;
alter table tr_intervention  drop column  uptake;
alter table tr_intervention  drop column  refs;
alter table tr_intervention  drop column  scaling;
alter table tr_intervention  drop column  intervention_type;
alter table tr_intervention  drop column  classification;
alter table tr_intervention  drop column  intervention_org_type_id;

alter table tr_intervention change intvn_hdr_id intervention_type_id bigint;
alter table tr_intervention change organisation_type_id org_type_id bigint;

ALTER TABLE `tr_intervention` DROP FOREIGN KEY `FK_c6siwnqj9sibfi69rouxtsouk`;
ALTER TABLE `tr_intervention` DROP FOREIGN KEY `FK_gd8bl6515hguru9r0tcw59gc6`;
