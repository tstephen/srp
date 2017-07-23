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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationIntervention;
import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.InterventionRepository;
import digital.srp.macc.repositories.InterventionTypeRepository;
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

    /**
     * @return Just the plan with the specified id.
     */
    @RequestMapping(value = "/plan/{orgTypeName}", method = RequestMethod.GET)
    public @ResponseBody List<OrganisationIntervention> getPlan(
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

        return addLinks(plan);
    }

    /**
     * @return Just the plan with the specified id.
     */
    @RequestMapping(value = "/plan/Clinical Commissioning Groups", method = RequestMethod.GET)
    public @ResponseBody List<OrganisationIntervention> getPlanForCommissioners(
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

        return addLinks(plan);
    }

    private List<OrganisationIntervention> addLinks(
            List<OrganisationIntervention> orgInterventions) {
        for (OrganisationIntervention oi : orgInterventions) {
            addLinks(oi);
        }
        return orgInterventions;
    }

    private OrganisationIntervention addLinks(OrganisationIntervention orgIntervention) {
        List<Link> links = new ArrayList<Link>();
        links.add(new Link(String.format("/%1$s/organisation-interventions/%2$d", orgIntervention.getTenantId(), orgIntervention.getId())));

        orgIntervention.setLinks(links);
        return orgIntervention;
    }
}
