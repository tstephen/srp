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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.macc.model.Intervention;
import digital.srp.macc.repositories.InterventionRepository;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.repositories.OrganisationTypeRepository;

/**
 * REST endpoint for accessing {@link Interventions}
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/interventions")
public class InterventionController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionController.class);

    @Autowired
    private InterventionTypeRepository interventionTypeRepo;

    @Autowired
    private OrganisationTypeRepository organisationTypeRepo;

    @Autowired
    private InterventionRepository interventionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Imports JSON representation of interventions.
     * 
     * <p>
     * This is a handy link: http://shancarter.github.io/mr-data-converter/
     * 
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the JSON.
     */
    @RequestMapping(value = "/uploadjson", method = RequestMethod.POST)
    public @ResponseBody Iterable<Intervention> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String
                .format("Uploading interventions for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<Intervention> list = objectMapper.readValue(content,
                new TypeReference<List<Intervention>>() {
                });
        LOGGER.info(String.format("  found %1$d interventions", list.size()));
        ArrayList<Intervention> result = new ArrayList<Intervention>();
        for (Intervention intervention : list) {
            intervention.setTenantId(tenantId);

            try {
                interventionRepo.save(intervention);
            } catch (Exception e) {
                LOGGER.error(String.format("Problem saving %1$s", intervention
                        .getInterventionType().getName()));
            }
            result.add(intervention);
        }

        // Iterable<Intervention> result = interventionRepo.save(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Imports CSV representation of intervention types.
     *
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the CSV.
     */
    // @RequestMapping(value = "/uploadcsv", method = RequestMethod.POST)
    // public @ResponseBody Iterable<Intervention> handleCsvFileUpload(
    // @PathVariable("tenantId") String tenantId,
    // @RequestParam(value = "file", required = true) MultipartFile file)
    // throws IOException {
    // LOGGER.info(String.format("Uploading CSV interventionTypes for: %1$s",
    // tenantId));
    // String content = new String(file.getBytes());
    // List<Intervention> list = new CsvImporter().readInterventions(
    // new StringReader(content),
    // content.substring(0, content.indexOf('\n')).split(","));
    // LOGGER.info(String.format("  found %1$d interventions", list.size()));
    // ArrayList<Intervention> result = new ArrayList<Intervention>();
    //
    // for (Intervention intervention : list) {
    // intervention.setTenantId(tenantId);
    // if (InterventionValidator.isValid(intervention)) {
    // result.add(intervention);
    // // interventionRepo.save(intervention);
    // // break;
    // } else if (intervention.getInterventionType().getStatus()
    // .equalsIgnoreCase("GREEN")) {
    // LOGGER.error(String
    // .format("  Intervention %1$s is intended for deployment is not valid",
    // intervention.getInterventionType().getName()));
    // continue;
    // } else {
    // LOGGER.warn(String.format(
    // "  Ignoring intervention that is excluded: %1$s",
    // intervention.getInterventionType().getName()));
    // continue;
    // }
    //
    // if (intervention.getInterventionType() != null) {
    // InterventionType type = interventionTypeRepo
    // .findByName(intervention.getInterventionType()
    // .getName());
    // if (type == null) {
    // intervention.getInterventionType().setTenantId(tenantId);
    // interventionTypeRepo.save(intervention
    // .getInterventionType());
    // } else {
    // intervention.setInterventionType(type);
    // }
    // }
    // if (intervention.getOrganisationType() != null) {
    // OrganisationType type = organisationTypeRepo
    // .findByName(intervention.getOrganisationType()
    // .getName());
    // if (type == null) {
    // intervention.getOrganisationType().setTenantId(tenantId);
    // organisationTypeRepo.save(intervention
    // .getOrganisationType());
    // } else {
    // intervention.setOrganisationType(type);
    // }
    // }
    // intervention.setTenantId(tenantId);
    // }
    //
    // // for (Intervention intervention : list) {
    // // intervention.setTenantId(tenantId);
    // // if (InterventionValidator.isValid(intervention)) {
    // // // interventionRepo.save(intervention);
    // // } else if (intervention.getInterventionType().getStatus()
    // // .equalsIgnoreCase("GREEN")) {
    // // LOGGER.error(String
    // // .format("  Intervention %1$s is intended for deployment is not valid",
    // // intervention.getInterventionType().getName()));
    // // }
    // // }
    //
    // Iterable<Intervention> saved = interventionRepo.save(result);
    // LOGGER.info(String.format("  saved %1$d interventions.", result.size()));
    // return saved;
    // }

    /**
     * Update an existing intervention.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long interventionId,
            @RequestBody Intervention updatedContact) {
        Intervention contact = interventionRepo.findOne(interventionId);

        BeanUtils.copyProperties(updatedContact, contact, "id");
        contact.setTenantId(tenantId);
        interventionRepo.save(contact);
    }

    /**
     * Return just the interventions for a specific tenant.
     * 
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<ShortIntervention> listForTenantAsJson(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return wrap(listForTenant(tenantId, page, limit));
    }

    /**
     * Export all interventions for the tenant.
     * 
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<Intervention> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return listForTenant(tenantId, page, limit);
    }

    protected List<Intervention> listForTenant(String tenantId, Integer page,
            Integer limit) {
        LOGGER.info(String.format("List interventions for tenant %1$s",
                tenantId));

        List<Intervention> list;
        if (limit == null) {
            list = interventionRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = interventionRepo.findPageForTenant(tenantId, pageable);
        }

        LOGGER.info(String.format("Found %1$s interventions", list.size()));
        return list;
    }

    /**
     * Return just the matching interventions for a specific tenant and
     * organisation type.
     * 
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/status/{status}/{orgTypeName}", method = RequestMethod.GET)
    public @ResponseBody List<ShortIntervention> findByStatusForTenantAndOrgType(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status,
            @PathVariable("orgTypeName") String orgTypeName) {
        LOGGER.info(String
                .format("List interventions for tenant %1$s with status %2$s and org type %3$s",
                        tenantId, status, orgTypeName));

        List<Intervention> list = interventionRepo
                .findByStatusForTenantAndOrgType(tenantId, status, orgTypeName);
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return wrap(list);
    }

    private List<ShortIntervention> wrap(List<Intervention> list) {
        List<ShortIntervention> resources = new ArrayList<ShortIntervention>(
                list.size());
        for (Intervention intervention : list) {
            resources.add(wrap(intervention));
        }
        return resources;
    }

    private ShortIntervention wrap(Intervention intervention) {
        ShortIntervention resource = new ShortIntervention();

        BeanUtils.copyProperties(intervention, resource);
        resource.setName(intervention.getInterventionType().getName());
        resource.setOrgType(intervention.getOrganisationType().getName());
        resource.setShareOfTotal(intervention.getShareOfTotal());

        Link detail = linkTo(InterventionRepository.class, intervention.getId())
                .withSelfRel();
        resource.add(detail);
        resource.setSelfRef(detail.getHref());

        resource.setName(intervention.getInterventionType().getName());
        resource.setDescription(intervention.getInterventionType()
                .getDescription());
        resource.setStatus(intervention.getInterventionType().getStatus());
        resource.setClassification(intervention.getInterventionType()
                .getClassification());
        resource.setConfidence(intervention.getInterventionType()
                .getConfidence());
        resource.setModellingYear(intervention.getInterventionType()
                .getModellingYear());
        resource.setLifetime(intervention.getInterventionType().getLifetime());
        resource.setUptake(intervention.getInterventionType().getUptake());
        resource.setScaling(intervention.getInterventionType().getScaling());
        resource.setFurtherInfo(intervention.getInterventionType()
                .getFurtherInfo());
        // resource.setUnit(intervention.getInterventionType().getUnit());
        // resource.setUnitCount(intervention.getInterventionType().getUnitCount());

        // resource.setTotalNpv(intervention.getTotalNpv().doubleValue());

        int targetYear = intervention.getInterventionType()
                .getTargetYearIndex();

        resource.setCashOutflowsUpFrontNational(intervention
                .getInterventionType().getCashOutflowsUpFrontNational());
        resource.setCashOutflowsUpFront(intervention.getCashOutflowUpFront());
        resource.setAnnualCashInflowsNationalTargetYear(SignificantFiguresFormat
                .round(intervention.getInterventionType()
                        .getAnnualCashInflowsNationalTargetYear()));
        resource.setAnnualCashInflowsTargetYear(SignificantFiguresFormat
                .round(intervention.getAnnualCashInflows(targetYear)));
        resource.setAnnualCashOutflowsNationalTargetYear(SignificantFiguresFormat
                .round(intervention.getInterventionType()
                        .getAnnualCashOutflowsNationalTargetYear()));
        resource.setAnnualCashOutflowsTargetYear(intervention
                .getAnnualCashOutflows());
        resource.setAnnualCashOutflowsTargetYear(intervention
                .getAnnualCashOutflows(targetYear));

        resource.setTonnesCo2eSavedTargetYear(intervention
                .getTonnesCo2eSavedTargetYear());
        resource.setTonnesCo2eSavedNationallyTargetYear(intervention
                .getInterventionType().getTonnesCo2eSavedTargetYear());
        resource.setCostPerTonneCo2e(intervention.getCostPerTonneCo2e());
        // resource.setTargetYearSavings(new BigDecimal(intervention
        // .getTargetYearSavings()));

        resource.setOverlappingInterventionList(intervention
                .getInterventionType().getOverlappingInterventionList());

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
    public static class ShortIntervention extends ResourceSupport {
        private String selfRef;
        private String name;
        private String orgType;
        private String description;
        private String status;
        private String classification;
        private Short confidence;
        private int modellingYear;
        private Short lifetime;
        private Short uptake;
        private Float scaling;
        private BigDecimal cashOutflowsUpFrontNational;
        private BigDecimal cashOutflowsUpFront;
        private BigDecimal annualCashInflowsNationalTargetYear;
        private BigDecimal annualCashInflowsTargetYear;
        private BigDecimal annualCashOutflowsNationalTargetYear;
        private BigDecimal annualCashOutflowsTargetYear;
        // private float annualTonnesCo2eSaved;
        // private BigDecimal annualElecSaved;
        // private BigDecimal annualGasSaved;
        private Long tonnesCo2eSavedTargetYear;
        private BigDecimal tonnesCo2eSavedNationallyTargetYear;
        private BigDecimal costPerTonneCo2e;
        // private Double totalNpv;
        // private BigDecimal targetYearSavings;
        private String furtherInfo;
        private Float shareOfTotal;
        // private String unit;
        // private Integer unitCount;
        private List<String> overlappingInterventionList;
    }
}
