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
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.finance.model.DefraDeccCategory;
import digital.srp.finance.model.DefraDeccCode;
import digital.srp.finance.model.FinancialTransactionSummary;
import digital.srp.finance.repositories.FinancialTransactionSummaryRepository;
import digital.srp.model.Event;

@Controller
@RequestMapping("/{tenantId}/financialTransactionsSummary")
public class FinancialTransactionSummaryController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(FinancialTransactionSummaryController.class);

    @Autowired
    @Qualifier("objectMapper")
    // TODO would one of these be better? halObjectMapper,_halObjectMapper
    protected ObjectMapper om;

    @Autowired
    protected FinancialTransactionSummaryRepository repo;

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

                FinancialTransactionSummary[] entities = om.readValue(is,
                        FinancialTransactionSummary[].class);
                for (FinancialTransactionSummary entity : entities) {
                    repo.save(entity);
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

    // Use http://shancarter.github.io/mr-data-converter/ to convert to JSON
    // if needed.
    @RequestMapping(method = RequestMethod.GET, value = "/byDefraDeccCategory")
    public final @ResponseBody List<FinancialTransactionSummary> byDefraDeccCategory() {
        LOGGER.info("byDefraCategory");

        List<FinancialTransactionSummary> list = new ArrayList<FinancialTransactionSummary>();

        Query query = entityManager
                .createQuery("SELECT SUM(ts.amount) as amount, code.category as defraDeccCategory"
                        + " FROM FinancialTransactionSummary ts"
                        + " LEFT JOIN ts.eClass eclass"
                        + " LEFT JOIN eclass.defraDeccCode code"
                        + " GROUP BY code.category");
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        LOGGER.info(String.format("Rows found %1$d", resultList.size()));
        for (Object[] o : resultList) {
            list.add(new FinancialTransactionSummary(
                    o[0] == null ? 0 : (Double) o[0],
                    o[1] == null ? "Uncategorised" : ((DefraDeccCategory) o[1])
                            .getCode(), o[1] == null ? "Uncategorised"
                            : ((DefraDeccCategory) o[1]).getDescription()));
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/byDefraDeccCode")
    public final @ResponseBody List<FinancialTransactionSummary> byDefraDeccCode(
            @RequestParam(required = false, value = "filter") String filter) {
        LOGGER.info(String.format("byDefraCode for filter %1$s", filter));

        List<FinancialTransactionSummary> list = new ArrayList<FinancialTransactionSummary>();

        Query query = entityManager
                .createQuery("SELECT SUM(ts.amount) as amount, eclass.defraDeccCode as defraDeccCode"
                        + " FROM FinancialTransactionSummary ts"
                        + " LEFT JOIN ts.eClass eclass"
                        + " LEFT JOIN eclass.defraDeccCode code"
                        + (filter == null ? ""
                                : " WHERE code.category = :filter ")
                        + " GROUP BY eclass.defraDeccCode");
        if (filter != null) {
            DefraDeccCategory cat = new DefraDeccCategory();
            cat.setCode(filter);
            query.setParameter("filter", cat);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        LOGGER.info(String.format("Rows found %1$d", resultList.size()));
        for (Object[] o : resultList) {
            list.add(new FinancialTransactionSummary((Double) o[0],
                    o[1] == null ? "Uncategorised" : ((DefraDeccCode) o[1])
                            .getCode()));
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/byEClass")
    public final @ResponseBody List<FinancialTransactionSummary> byEClass(
            @RequestParam(required = false, value = "filter") String filter) {
        LOGGER.info(String.format("byEClass for filter %1$s", filter));

        List<FinancialTransactionSummary> list = new ArrayList<FinancialTransactionSummary>();

        Query query = entityManager
                .createQuery("SELECT SUM(ts.amount) as amount, eclass.code, eclass.description"
                        + " FROM FinancialTransactionSummary ts"
                        + " LEFT JOIN ts.eClass eclass"
                        + (filter == null ? ""
                                : " WHERE eclass.defraDeccCode = :filter ")
                        + " GROUP BY eclass.code");
        if (filter != null) {
            DefraDeccCode code = new DefraDeccCode();
            code.setCode(filter);
            query.setParameter("filter", code);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        LOGGER.info(String.format("Rows found %1$d", resultList.size()));
        for (Object[] o : resultList) {
            list.add(new FinancialTransactionSummary((Double) o[0],
                    (String) (o[1] == null ? "Uncategorised" : o[1]),
                    (String) (o[2])));
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bySupplier")
    public final @ResponseBody List<FinancialTransactionSummary> bySupplier(
            @RequestParam(required = false, value = "filter") String filter) {
        LOGGER.info(String.format("bySupplier for filter %1$s", filter));

        List<FinancialTransactionSummary> list = new ArrayList<FinancialTransactionSummary>();

        Query query = entityManager
                .createQuery("SELECT SUM(ts.amount) as amount, eclass.code, eclass.description, org.code"
                        + " FROM FinancialTransactionSummary ts"
                        + " LEFT JOIN ts.eClass eclass"
                        + " JOIN ts.organisation org"
                        + (filter == null ? ""
                                : " WHERE eclass.code = :filter ")
                        + " GROUP BY org.code");
        if (filter != null) {
            query.setParameter("filter", filter);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        LOGGER.info(String.format("Rows found %1$d", resultList.size()));
        for (Object[] o : resultList) {
            list.add(new FinancialTransactionSummary((Double) o[0],
                    (String) (o[3] == null ? "n/a" : o[3]), (String) (o[3])));
        }

        return list;
    }

}
