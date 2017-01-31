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
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.internal.CsvImporter;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.repositories.InterventionTypeRepository;

/**
 * REST endpoint for accessing {@link InterventionType}
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/intervention-types")
public class InterventionTypeController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InterventionTypeController.class);

    @Autowired
    private InterventionTypeRepository interventionTypeRepo;

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
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody Iterable<InterventionType> handleFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String
                .format("Uploading interventions for: %1$s", tenantId));
        String content = new String(file.getBytes());

        List<InterventionType> list = objectMapper.readValue(content,
                new TypeReference<List<InterventionType>>() {
                });
        LOGGER.info(String.format("  found %1$d interventions", list.size()));
        for (InterventionType message : list) {
            message.setTenantId(tenantId);
        }

        Iterable<InterventionType> result = interventionTypeRepo.save(list);
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
    @RequestMapping(value = "/uploadcsv", method = RequestMethod.POST)
    public @ResponseBody Iterable<InterventionType> handleCsvFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading CSV interventionTypes for: %1$s",
                tenantId));
        String content = new String(file.getBytes());
        List<InterventionType> list = new CsvImporter().readInterventionTypes(
                new StringReader(
                content), content.substring(0, content.indexOf('\n'))
                .split(","));
        LOGGER.info(String.format("  found %1$d interventionTypes", list.size()));
        for (InterventionType interventionType : list) {
            interventionType.setTenantId(tenantId);
        }

        Iterable<InterventionType> result = interventionTypeRepo.save(list);
        LOGGER.info("  saved.");
        return result;
    }

    /**
     * Return just the interventions for a specific tenant.
     * 
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<ShortInterventionType> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List interventions for tenant %1$s",
                tenantId));

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return wrap(list);
    }

    /**
     * Export all interventionTypes for the tenant.
     * 
     * @return interventionTypes for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/csv")
    public @ResponseBody List<InterventionType> exportAsCsv(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("Export interventions for tenant %1$s",
                tenantId));

        List<InterventionType> list;
        if (limit == null) {
            list = interventionTypeRepo.findAllForTenant(tenantId);
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = interventionTypeRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return list;
    }

    /**
     * Return just the matching interventions for a specific tenant.
     * 
     * @param tenantId
     * @status Comma separated list of status to include.
     * @return interventions for that tenant.
     */
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    public @ResponseBody List<ShortInterventionType> findByStatusForTenant(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("status") String status) {
        LOGGER.info(String.format(
                "List interventions for tenant %1$s with status %2$s",
                tenantId, status));

        List<InterventionType> list = interventionTypeRepo
                .findByStatusForTenant(tenantId, status);
        LOGGER.info(String.format("Found %1$s interventions", list.size()));

        return wrap(list);
    }

    private List<ShortInterventionType> wrap(List<InterventionType> list) {
        List<ShortInterventionType> resources = new ArrayList<ShortInterventionType>(
                list.size());
        for (InterventionType message : list) {
            resources.add(wrap(message));
        }
        return resources;
    }

    private ShortInterventionType wrap(InterventionType interventionType) {
        ShortInterventionType resource = new ShortInterventionType();
        // BeanUtils.copyProperties(intervention, resource);

        int targetYear = interventionType.getTargetYearIndex();
        resource.setName(interventionType.getName());
        resource.setCashOutflowsUpFront(interventionType
                .getCashOutflowsUpFront());
        resource.setCashOutflowsUpFrontNational(interventionType
                .getCashOutflowsUpFrontNational());
        resource.setAnnualCashInflowsTargetYear(interventionType
                .getAnnualCashInflows(targetYear));
        resource.setAnnualCashInflowsNationalTargetYear(interventionType
                .getAnnualCashInflowsNationalTargetYear());
        resource.setAnnualCashOutflowsTargetYear(interventionType
                .getAnnualCashOutflows(targetYear));
        resource.setAnnualCashOutflowsNationalTargetYear(interventionType
                .getAnnualCashOutflowsNationalTargetYear());
        resource.setAnnualTonnesCo2eSaved(interventionType
                .getAnnualTonnesCo2eSaved());
        resource.setAnnualElecSaved(interventionType.getAnnualElecSaved());

        // System.out.println("  tonnes TY: "
        // + intervention.getTonnesCo2eSavedTargetYear());
        // System.out.println("  mac: " + intervention.getCostPerTonneCo2e());
        //
        // System.out.println("  Mean total outflows: "
        // + intervention.getTotalCashOutflows());
        // System.out.println("  Mean cash inflows: "
        // + intervention.getMeanCashInflows());
        // System.out.println("  NPV: " + intervention.getTotalNpv());
        resource.setTonnesCo2eSavedTargetYear(interventionType
                .getTonnesCo2eSavedTargetYear().longValue());
        resource.setCostPerTonneCo2e(interventionType.getCostPerTonneCo2e());
        resource.setTargetYearSavings(interventionType.getTargetYearSavings()
                .longValue());
        // Apparently BeanUtils does not use accessor
        resource.setTotalNpv(interventionType.getTotalNpv());

        resource.setName(interventionType.getName());
        resource.setDescription(interventionType.getDescription());
        resource.setStatus(interventionType.getStatus());
        resource.setClassification(interventionType.getClassification());
        resource.setConfidence(interventionType.getConfidence());
        resource.setModellingYear(interventionType.getModellingYear());
        resource.setLifetime(interventionType.getLifetime());
        resource.setLeadTime(interventionType.getLeadTime());
        resource.setUptake(interventionType.getUptake());
        resource.setScaling(interventionType.getScaling());
        resource.setFurtherInfo(interventionType.getFurtherInfo());

        resource.setOverlappingInterventionList(interventionType
                .getOverlappingInterventionList());

        Link detail = linkTo(InterventionTypeRepository.class,
                interventionType.getId()).withSelfRel();
        resource.add(detail);
        resource.setSelfRef(detail.getHref());
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
    public static class ShortInterventionType extends ResourceSupport {
        private String selfRef;
        private String name;
        private String description;
        private String status;
        private String classification;
        private Short confidence;
        private int modellingYear;
        private Short lifetime;
        private int leadTime;
        private Short uptake;
        private Float scaling;
        private BigDecimal cashOutflowsUpFront;
        private BigDecimal cashOutflowsUpFrontNational;
        private BigDecimal annualCashInflowsNationalTargetYear;
        private BigDecimal annualCashInflowsTargetYear;
        private BigDecimal annualCashOutflowsNationalTargetYear;
        private BigDecimal annualCashOutflowsTargetYear;
        private BigDecimal annualTonnesCo2eSaved;
        private BigDecimal annualElecSaved;
        private Long tonnesCo2eSavedTargetYear;
        private BigDecimal costPerTonneCo2e;
        private BigDecimal totalNpv;
        private Long targetYearSavings;
        private String furtherInfo;
        private boolean crossOrganisation;
        private List<String> overlappingInterventionList;
    }
}
