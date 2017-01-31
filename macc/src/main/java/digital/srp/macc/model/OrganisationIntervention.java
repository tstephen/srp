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
package digital.srp.macc.model;

import java.math.BigDecimal;

import javax.persistence.Column;
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

import digital.srp.macc.maths.Finance;

/**
 * A segmentation of all the organisations in the NHS, public health and social
 * care.
 *
 * @author Tim Stephenson
 */
@Data
@Entity
@Table(name = "TR_ORG_INTVN")
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationIntervention {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    private String name;

    @JsonProperty
    private int annualTurnover;

    @JsonProperty
    private int annualEnergyUse;

    @JsonProperty
    private int noOfStaff;

    @JsonProperty
    private int noOfPatientInteractions;

    @JsonProperty
    private int noOfInPatients;

    @JsonProperty
    private int floorArea;

    @JsonProperty
    private long annualMileage;

    @JsonProperty
    private int typicalJourneyDistance;

    @JsonProperty
    private double unitCount;

    @JsonProperty
    private String logo;

    @JsonProperty
    private String tenantId;

    @JsonProperty
    @ManyToOne
    private OrganisationType organisationType;

    @JsonProperty
    @ManyToOne
    private Intervention intervention;

    public OrganisationIntervention(OrganisationType orgType,
            Intervention newIntervention) {
        setOrganisationType(orgType);
        setIntervention(newIntervention);
    }

    public BigDecimal getTargetYearSavings() {
        BigDecimal unitCount = getUnitCount();
        if (unitCount == null || unitCount.intValue() == 0) {
            return null;
        } else {
            return getIntervention().getInterventionType()
                    .getTargetYearSavings()
                    .divide(unitCount, Finance.ROUND_MODE);
        }
    }

    public BigDecimal getTonnesCo2eSavedTargetYear() {
        BigDecimal unitCount = getUnitCount();
        if (unitCount == null || unitCount.intValue() == 0) {
            return null;
        } else {
            return getIntervention().getInterventionType()
                    .getTonnesCo2eSavedTargetYear()
                    .multiply(getProportionOfUnits());
        }
    }

    public BigDecimal getUnitCount() {
        if (unitCount == 0 && getOrganisationType().getCount() == 0) {
            return null;
        } else if (unitCount == 0) {
            return getIntervention().getUnitCount().divide(
                    new BigDecimal(getOrganisationType().getCount()),
                    Finance.ROUND_MODE);
        } else {
            return new BigDecimal(unitCount);
        }
    }

    public BigDecimal getProportionOfUnits() {
        return getUnitCount().divide(
                new BigDecimal(getIntervention().getInterventionType()
                        .getUnitCount()), 8,
                Finance.ROUND_MODE);
    }

    public BigDecimal getCashOutflowUpFront() {
        return getIntervention().getInterventionType()
                .getCashOutflowsUpFrontNational()
                .multiply(getProportionOfUnits());
    }

    public BigDecimal getAnnualCashInflowsTargetYear() {
        return getIntervention()
                .getInterventionType()
                .getAnnualCashInflowsNationalTargetYear()
                .multiply(getProportionOfUnits());
    }

    public BigDecimal getAnnualCashOutflowsTargetYear() {
        return getIntervention()
                .getInterventionType()
                .getAnnualCashOutflowsNationalTargetYear()
                .multiply(getProportionOfUnits());
    }
}
