/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.macc.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationIntervention;
import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.InterventionRepository;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.repositories.OrganisationInterventionRepository;
import digital.srp.macc.repositories.OrganisationTypeRepository;

/**
 * REST endpoint for accessing {@link OrganisationIntervention}
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/organisation-interventions")
public class OrganisationInterventionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OrganisationInterventionController.class);

    @Autowired
    private InterventionTypeRepository interventionTypeRepo;

    @Autowired
    private InterventionRepository interventionRepo;

    @Autowired
    private OrganisationTypeRepository orgTypeRepo;

    @Autowired
    private OrganisationInterventionRepository orgPlanRepo;

    /**
     * @return Just the plan with the specified id.
     */
    @RequestMapping(value = "/plan/{orgTypeName}", method = RequestMethod.GET)
    public @ResponseBody List<ShortOrganisationIntervention> getPlan(
            @PathVariable("orgTypeName") String orgTypeName,
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("Find default plan for org type %1$s",
                orgTypeName));

        List<Intervention> interventions = interventionRepo
                .findByStatusForTenantAndOrgType(tenantId, "green", orgTypeName);

        List<OrganisationIntervention> plan = new ArrayList<OrganisationIntervention>();
        for (Intervention intervention : interventions) {
            OrganisationIntervention orgIntvn = new OrganisationIntervention();
            orgIntvn.setIntervention(intervention);
            orgIntvn.setOrganisationType(intervention.getOrganisationType());

            if (intervention.getOrganisationType().isCommissioner()) {

            }

            plan.add(orgIntvn);
        }

        return wrap(plan);
    }

    /**
     * @return Just the plan with the specified id.
     */
    @RequestMapping(value = "/plan/Clinical Commissioning Groups", method = RequestMethod.GET)
    public @ResponseBody List<ShortOrganisationIntervention> getPlanForCommissioners(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info("Find default plan for commissioners");

        OrganisationType orgType = orgTypeRepo.findByName(tenantId,
                "Clinical Commissioning Groups");

        List<InterventionType> interventionTypes = interventionTypeRepo
                .findByStatusForTenantAndCommissioners(tenantId, "green");

        List<OrganisationIntervention> plan = new ArrayList<OrganisationIntervention>();
        for (InterventionType it : interventionTypes) {
            Intervention intervention = new Intervention(it, orgType);
            intervention.setShareOfTotal(100.00f);
            OrganisationIntervention orgIntvn = new OrganisationIntervention(
                    orgType, intervention);

            plan.add(orgIntvn);
        }

        return wrap(plan);
    }

    // /**
    // * @return Just the plan with the specified id.
    // */
    // @RequestMapping(value = "/{orgName}", method = RequestMethod.GET)
    // public @ResponseBody List<ShortOrganisationIntervention> getPlan(
    // @PathVariable("orgName") String orgName,
    // @PathVariable("tenantId") String tenantId) {
    // LOGGER.info(String.format("Find plan for org %1$s", orgName));
    //
    // List<OrganisationIntervention> plan = orgPlanRepo.findByName(orgName);
    //
    // return wrap(plan);
    // }

    private List<ShortOrganisationIntervention> wrap(
            List<OrganisationIntervention> plan) {
        ArrayList<ShortOrganisationIntervention> list = new ArrayList<ShortOrganisationIntervention>();
        for (OrganisationIntervention organisationIntervention : plan) {
            list.add(wrap(organisationIntervention));
        }
        return list;
    }

    private ShortOrganisationIntervention wrap(OrganisationIntervention orgIntvn) {
        LOGGER.info(String.format("wrap: %1$s %2$s", orgIntvn.getIntervention()
                .getInterventionType().getName(), orgIntvn
                .getOrganisationType().getName()));
        ShortOrganisationIntervention resource = new ShortOrganisationIntervention();

        // Qualitative info
        resource.setName(orgIntvn.getIntervention().getInterventionType()
                .getName());
        resource.setDescription(orgIntvn.getIntervention()
                .getInterventionType().getDescription());
        resource.setOrgType(orgIntvn.getOrganisationType().getName());
        resource.setStatus(orgIntvn.getIntervention().getInterventionType()
                .getStatus());
        resource.setConfidence(orgIntvn.getIntervention().getInterventionType()
                .getConfidence());
        resource.setFurtherInfo(orgIntvn.getIntervention()
                .getInterventionType().getFurtherInfo());

        resource.setUptake(orgIntvn.getIntervention().getInterventionType()
                .getUptake());
        resource.setCashOutflowsUpFrontNational(orgIntvn.getIntervention()
                .getInterventionType().getCashOutflowsUpFrontNational());
        resource.setCashOutflowsUpFront(orgIntvn.getCashOutflowUpFront());
        resource.setAnnualCashOutflowsNationalTargetYear(orgIntvn
                .getIntervention().getInterventionType()
                .getAnnualCashOutflowsNationalTargetYear());
        resource.setAnnualCashOutflowsTargetYear(orgIntvn
                .getAnnualCashOutflowsTargetYear());
        resource.setAnnualCashInflowsNationalTargetYear(orgIntvn
                .getIntervention().getInterventionType()
                .getAnnualCashInflowsNationalTargetYear());
        resource.setAnnualCashInflowsTargetYear(orgIntvn
                .getAnnualCashInflowsTargetYear());

        resource.setTonnesCo2eSavedTargetYear(orgIntvn
                .getTonnesCo2eSavedTargetYear());
        resource.setTonnesCo2eSavedNationallyTargetYear(orgIntvn
                .getIntervention().getInterventionType()
                .getTonnesCo2eSavedTargetYear());
        resource.setCostPerTonneCo2e(orgIntvn.getIntervention()
                .getCostPerTonneCo2e());
        // resource.setTargetYearSavings(orgIntvn.getTargetYearSavings());

        resource.setUnit(orgIntvn.getIntervention().getInterventionType()
                .getUnit());
        resource.setUnitCount(SignificantFiguresFormat.asDouble(orgIntvn
                .getUnitCount()));
        resource.setUnitDescription(orgIntvn.getIntervention()
                .getInterventionType().getUnitDescription());
        resource.setUnitCountNational(orgIntvn.getIntervention()
                .getInterventionType().getUnitCount().intValue());

        resource.setOverlappingInterventionList(orgIntvn.getIntervention()
                .getInterventionType().getOverlappingInterventionList());

        if (orgIntvn.getId() != null) {
            Link detail = linkTo(OrganisationInterventionRepository.class,
                    orgIntvn.getId()).withSelfRel();
            resource.add(detail);
            resource.setSelfRef(detail.getHref());
        }
        return resource;
    }

    private Link linkTo(
            @SuppressWarnings("rawtypes") Class<? extends CrudRepository> clazz,
            Long id) {
        return new Link(clazz.getAnnotation(RepositoryRestResource.class)
                .path() + "/" + id);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ShortOrganisationIntervention extends ResourceSupport {
        private String selfRef;
        private String name;
        private String orgType;
        private String description;
        private String status;
        // private String classification;
        private Short confidence;
        // private int modellingYear;
        // private Short lifetime;
        private Short uptake;
        private Float scaling;
        private BigDecimal cashOutflowsUpFrontNational;
        private BigDecimal cashOutflowsUpFront;
        private BigDecimal annualCashInflowsNationalTargetYear;
        private BigDecimal annualCashInflowsTargetYear;
        // private BigDecimal annualCashOutflows;
        private BigDecimal annualCashOutflowsNationalTargetYear;
        private BigDecimal annualCashOutflowsTargetYear;
        // private float annualTonnesCo2eSaved;
        // private BigDecimal annualElecSaved;
        // private BigDecimal annualGasSaved;
        private BigDecimal tonnesCo2eSavedTargetYear;
        private BigDecimal tonnesCo2eSavedNationallyTargetYear;
        private BigDecimal costPerTonneCo2e;
        // private Double totalNpv;
        // private BigDecimal targetYearSavings;
        private String furtherInfo;
        // private Float shareOfTotal;
        private String unit;
        private Double unitCount;
        private String unitDescription;
        private Integer unitCountNational;
        private List<String> overlappingInterventionList;
    }
}
