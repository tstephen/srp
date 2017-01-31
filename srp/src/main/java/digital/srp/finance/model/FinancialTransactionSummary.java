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
package digital.srp.finance.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TR_FIN_TXN_SUMMARY")
@Data
// this is apparently not enough for jackson to figure out how to parse
// at least when invoked via spring-data-rest
// @JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    /**
     */
    @NotNull
    @JsonProperty
    private Double amount;

    @JsonProperty
    private String code;

    @JsonProperty
    private String description;

    @ManyToOne
    @JsonProperty
    private EClassS2 eClass;

    @ManyToOne
    @JsonProperty
    private FinancialYear financialYear;

    @ManyToOne
    @JsonProperty
    private Organisation organisation;

    public FinancialTransactionSummary(Double amount, String code) {
        this();
        setAmount(amount);
        setCode(code);
    }

    public FinancialTransactionSummary(Double amount, String code, String description) {
        this(amount, code);
        setDescription(description);
    }

    public FinancialTransactionSummary(Double amount, String code,
            String description, String orgName) {
        this(amount, code, description);
        setOrganisation(new Organisation(orgName, orgName));
    }

    public String getDescription() {
        return description == null ? code : description;
    }

    public void setAmount(Number number) {
        if (number != null) {
            this.amount = number.doubleValue();
        } else {
            this.amount = 0d;
        }
    }
}
