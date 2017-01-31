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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tr_fin_txn")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransaction implements Serializable {

    private static final long serialVersionUID = 5462714188461760691L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @JsonProperty
    private Long poNum;

    @JsonProperty
    private OrderType orderType;

    @JsonProperty
    private String supplierName;

    @JsonProperty
    private Long reqNo;

    @JsonProperty
    private Date orderDate;

    @JsonProperty
    private String deliverTo;

    @JsonProperty
    private Long contract;

    @JsonProperty
    private LineType lineType;

    @JsonProperty
    private Short line;

    @JsonProperty
    private Short shipment;

    @JsonProperty
    private Short distribution;

    @JsonProperty
    private String description;

    // @JsonProperty
    // private String supplierItem;

    @JsonProperty
    private UnitType uom;

    @JsonProperty
    // TODO int?
    private Float qtyOrdered;

    @JsonProperty
    // TODO int?
    private Float qtyReceived;

    @JsonProperty
    // TODO int?
    private Float qtyInvoiced;

    @JsonProperty
    private Float valInvoiced;

    @JsonProperty
    private Float amount;

    @JsonProperty
    private Short qtyCandidate;

    @JsonProperty
    private Float unitPrice;

    @JsonProperty
    @Pattern(regexp = "[a-zA-Z]{3}")
    private String currency;

    @JsonProperty
    private String buyer;

    @JsonProperty
    private Date needByDate;

    @JsonProperty
    @Pattern(regexp = "[0-9a-zA-Z]{6}")
    private String costCentre;

    @JsonProperty
    private Long account;

    // @JsonProperty
    // private String chargeAccount;
    //
    // @JsonProperty
    // private String accountDesc;
    //
    // @JsonProperty
    // private String chargeAccountDesc;

    @JsonProperty
    private StatusType status;

    @JsonProperty
    @Pattern(regexp = "[a-zA-Z]{3}")
    private String eClass;

    // @JsonProperty
    // private String eClassDesc;

    @JsonProperty
    private String requisitionPreparer;

    @JsonProperty
    private Short month;

    @JsonProperty
    @Pattern(regexp = "[A-Z]{5,10}")
    private String trust;

    @JsonProperty
    private ClinicalGroup cag;

    @JsonProperty
    private ServiceLine serviceLine;

    // @JsonProperty
    // private String costCentreDesc;

    // @JsonProperty
    // private String group;

    public void setEClass(String eClass) { 
        if (eClass.indexOf('.')!=-1){
            eClass = eClass.substring(eClass.lastIndexOf('.') + 1);
        }
        // Pattern p;
        this.eClass = eClass;
    }

}
