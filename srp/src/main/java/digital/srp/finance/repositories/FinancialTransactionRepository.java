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

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import digital.srp.finance.model.FinancialTransaction;

public interface FinancialTransactionRepository extends
        CrudRepository<FinancialTransaction, Long> {
    /* WHERE t.tenantId = :tenantId */
    @Query("SELECT sum(t.amount), t.eClass FROM FinancialTransaction t GROUP BY t.eClass")
    List<Object[]> findAllGroupByEClass(
    /* @Param("tenantId") String tenantId, */);

    @Query("SELECT sum(t.amount), t.eClass, t.supplierName FROM FinancialTransaction t GROUP BY t.eClass, t.supplierName")
    List<Object[]> findAllGroupByEClassAndSupplier(
    /* @Param("tenantId") String tenantId, */);

    @Query("SELECT t FROM FinancialTransaction t WHERE t.eClass = :eClass")
    List<FinancialTransaction> findAllForTenantByEClass(String eClass);

}
