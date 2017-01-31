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
package digital.srp.finance.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import digital.srp.finance.model.FinancialTransactionSummary;

@RepositoryEventHandler(FinancialTransactionSummary.class)
public class FinancialTransactionSummaryEventHandler {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FinancialTransactionSummaryEventHandler.class);

    @Autowired
    protected EClassS2Repository eClassRepository;

    @Autowired
    protected FinancialYearRepository financialYearRepository;

    @HandleBeforeSave
    public void handleBeforeSave(FinancialTransactionSummary ts) {
        LOGGER.info("handleBeforeSave");
        if (ts.getId() == null) {
            if (ts.getFinancialYear() != null
                    && ts.getFinancialYear().getName() != null) {
                ts.setFinancialYear(financialYearRepository.findByName(ts
                        .getFinancialYear().getName()));
            }
            if (ts.getEClass() != null && ts.getEClass().getCode() != null) {
                ts.setEClass(eClassRepository.findByCode(ts.getEClass()
                        .getCode()));
            } else if (ts.getEClass() != null
                    && ts.getEClass().getAbbreviation() != null) {
                ts.setEClass(eClassRepository.findByAbbreviation(ts.getEClass()
                        .getAbbreviation()));
            } else if (ts.getEClass() != null
                    && ts.getEClass().getDescription() != null) {
                ts.setEClass(eClassRepository.findByDescription(ts.getEClass()
                        .getDescription()));
            }
        }
    }

    // @HandleAfterDelete public void handleAfterDelete(Person p) {
    // ...
    // }

}
