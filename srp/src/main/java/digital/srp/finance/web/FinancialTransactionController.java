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
package digital.srp.finance.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.finance.model.EClassS2;
import digital.srp.finance.model.FinancialTransaction;
import digital.srp.finance.model.FinancialTransactionSummary;
import digital.srp.finance.model.Organisation;
import digital.srp.finance.repositories.EClassS2Repository;
import digital.srp.finance.repositories.FinancialTransactionRepository;
import digital.srp.finance.repositories.FinancialTransactionSummaryRepository;
import digital.srp.finance.repositories.OrganisationRepository;
import digital.srp.model.Event;

@Controller
@RequestMapping("/{tenantId}/financialTransactions")
public class FinancialTransactionController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(FinancialTransactionController.class);

    @Autowired
    @Qualifier("objectMapper")
    // TODO would one of these be better? halObjectMapper,_halObjectMapper
    protected ObjectMapper om;

    @Autowired
    protected FinancialTransactionRepository transactionRepo;

    @Autowired
    protected FinancialTransactionSummaryRepository summaryRepo;

    @Autowired
    protected EClassS2Repository eClassRepo;

    @Autowired
    protected OrganisationRepository orgRepo;

    @Autowired
    private EntityManager entityManager;

    // Use http://shancarter.github.io/mr-data-converter/ to convert to JSON
    // if needed.
    @RequestMapping(method = RequestMethod.POST, value = "/import")
    public final @ResponseBody List<Event> importMultipleFiles(
            @RequestParam MultipartFile... resourceFile) {
        LOGGER.info(String.format("Import data from %1$s resources",
                resourceFile.length));

        List<Event> events = new ArrayList<Event>();
        InputStream is = null;
        for (MultipartFile resource : resourceFile) {
            try {
                LOGGER.info(String.format("Importing file: %1$s",
                        resource.getOriginalFilename()));
                int count = 0;
                is = resource.getInputStream();

                FinancialTransaction[] entities = om.readValue(is,
                        FinancialTransaction[].class);
                for (FinancialTransaction entity : entities) {
                    transactionRepo.save(entity);
                    count++;
                }

                String msg = String.format(
                        "UPLOADED: %1$s records from resource %2$s", count,
                        resource.getOriginalFilename());
                events.add(new Event(null, msg));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                String msg = String.format("UPLOAD FAILED: for resource %1$s",
                        resource.getOriginalFilename());
                // events.add(new Event(null, msg));
                throw new RuntimeException(msg, e);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return events;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/summarise")
    public final @ResponseBody List<FinancialTransactionSummary> summarise(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info("summarise");
        List<FinancialTransactionSummary> summaries = new ArrayList<FinancialTransactionSummary>();
        
        List<Object[]> results = transactionRepo
                .findAllGroupByEClassAndSupplier(/* tenantId */);
        for (Object[] objects : results) {
            FinancialTransactionSummary summary = new FinancialTransactionSummary();
            summary.setAmount((Number) objects[0]);
            LOGGER.debug(String.format("... looking for eClass %1$s",
                    (String) objects[1]));
            EClassS2 eClass = eClassRepo.findByCode((String) objects[1]);
            if (eClass == null) {
                LOGGER.warn("No eclass found for " + (String) objects[1]);
            } else {
                summary.setCode(eClass.getCode());
                summary.setEClass(eClass);
            }

            Organisation organisation = orgRepo.findByCode((String) objects[2]);
            if (organisation == null) {
                Organisation org = new Organisation();
                org.setCode((String) objects[2]);
                org.setName((String) objects[2]);
                orgRepo.save(org);
            }
            summary.setOrganisation(organisation);
            summaries.add(summary);
            summaryRepo.save(summary);
        }
         
        return summaries; 
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ByEClass/{eClass}")
    public final @ResponseBody List<FinancialTransaction> byEClass(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("eClass") String eClass) {
        LOGGER.info("byEClass");
        return transactionRepo.findAllForTenantByEClass(/* tenantId, */eClass);
    }

}
