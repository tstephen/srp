/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.macc.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import digital.srp.macc.maths.Finance;
import digital.srp.macc.maths.SignificantFiguresFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@JsonIgnoreProperties( allowGetters = true, value = { "unit"} )
public class OrganisationIntervention {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @JsonProperty
    @Column(name = "annual_turnover")
    private int annualTurnover;

    @JsonProperty
    @Column(name = "annual_energy_use")
    private int annualEnergyUse;

    @JsonProperty
    @Column(name = "no_of_staff")
    private int noOfStaff;

    @JsonProperty
    @Column(name = "no_of_patient_interactions")
    private int noOfPatientInteractions;

    @JsonProperty
    @Column(name = "no_of_in_patients")
    private int noOfInPatients;

    @JsonProperty
    @Column(name = "floor_area")
    private int floorArea;

    @JsonProperty
    @Column(name = "annual_mileage")
    private long annualMileage;

    @JsonProperty
    @Column(name = "typical_journey_distance")
    private int typicalJourneyDistance;

    @JsonProperty
    @Column(name = "unit_count")
    private double unitCount;

    @JsonProperty
    @Column(name = "logo")
    private String logo;

    @JsonProperty
    @Column(name = "tenant_id")
    private String tenantId;

    @JsonProperty
    @ManyToOne
    @JoinColumn(name = "organisation_type_id")
    private OrganisationType organisationType;

    @JsonProperty
    @ManyToOne
    private Intervention intervention;

    public OrganisationIntervention(OrganisationType orgType,
            Intervention newIntervention) {
        setOrganisationType(orgType);
        setIntervention(newIntervention);
    }

    @JsonProperty
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

    @JsonProperty
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
            return SignificantFiguresFormat.getInstance().round(
                    getIntervention().getUnitCount().divide(
                    new BigDecimal(getOrganisationType().getCount()),
                    Finance.ROUND_MODE));
        } else {
            return new BigDecimal(unitCount);
        }
    }

    @JsonProperty
    public BigDecimal getProportionOfUnits() {
        return getUnitCount().divide(
                new BigDecimal(getIntervention().getInterventionType()
                        .getUnitCount()), 8,
                Finance.ROUND_MODE);
    }

    @JsonProperty
    public BigDecimal getCashOutflowsUpFront() {
        return getIntervention().getInterventionType()
                .getCashOutflowsUpFrontNational()
                .multiply(getProportionOfUnits());
    }

    @JsonProperty
    public BigDecimal getAnnualCashInflowsTargetYear() {
        return getIntervention()
                .getInterventionType()
                .getAnnualCashInflowsNationalTargetYear()
                .multiply(getProportionOfUnits());
    }

    @JsonProperty
    public BigDecimal getAnnualCashOutflowsTargetYear() {
        return getIntervention()
                .getInterventionType()
                .getAnnualCashOutflowsNationalTargetYear()
                .multiply(getProportionOfUnits());
    }

    /** Convenience method to access intervention type's name. */
    @JsonProperty
    public String getName() {
        return intervention.getInterventionType().getName();
    }

    /** Convenience method to access intervention type's description. */
    @JsonProperty
    public String getDescription() {
        return intervention.getInterventionType().getDescription();
    }

    /** Convenience method to access intervention type's further info URL. */
    @JsonProperty
    public String getFurtherInfo() {
        return intervention.getInterventionType().getFurtherInfo();
    }

    /** Convenience method to access intervention type's unit. */
    @JsonProperty
    public String getUnit() {
        return intervention.getInterventionType().getUnit();
    }

    /** Convenience method to access intervention type's (aka national) unit count. */
    @JsonProperty
    public Integer getUnitCountNational() {
        return intervention.getInterventionType().getUnitCount();
    }

    /** Convenience method to access description of intervention type's unit. */
    @JsonProperty
    public String getUnitDescription() {
        return intervention.getInterventionType().getUnitDescription();
    }

    /** Convenience method to access description of intervention type's uptake. */
    @JsonProperty
    public Short getUptake() {
        return intervention.getInterventionType().getUptake();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getCashOutflowsUpFrontNational() {
        return intervention.getInterventionType().getCashOutflowsUpFrontNational();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getAnnualCashOutflowsNationalTargetYear() {
        return intervention.getInterventionType().getAnnualCashOutflowsNationalTargetYear();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getAnnualCashInflowsNationalTargetYear() {
        return intervention.getInterventionType().getAnnualCashInflowsNationalTargetYear();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getCostPerTonneCo2e() {
        return intervention.getInterventionType().getCostPerTonneCo2e();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public Short getConfidence() {
        return intervention.getInterventionType().getConfidence();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public String getSlug() {
        return intervention.getInterventionType().getSlug();
    }
}
