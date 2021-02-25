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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import digital.srp.macc.maths.Finance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * An action or related set of actions aimed at providing Carbon Abatement or
 * other sustainability benefit.
 *
 * @author Tim Stephenson
 */
@Data
@Entity
@Table(name = "TR_INTVN")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Intervention implements CsvSerializable {

    private static final long serialVersionUID = -4380531011333907658L;

    public String toCsvHeader() {
        return "Intervention Id, Intervention Type, Organisation Type, Share of Total, Cash Outflow Up Front, Annual Cash Inflows, Annual Cash Outflows, Total Net Present Value, Annual Tonnes CO2e Saved, Annual Elec Saved, Annual Gas Saved, Tonnes CO2e Saved Target Year, Cost Per Tonne CO2e, Target Year Savings";
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "INTERVENTION_TYPE_ID")
    private InterventionType interventionType;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORG_TYPE_ID")
    private OrganisationType organisationType;

    @JsonProperty
    @Min(value = 0)
    @Max(100)
    @Column(name = "SHARE", nullable = true)
    @NotNull
    private Float shareOfTotal;

    @JsonProperty
    @Column(name = "tenant_id")
    private String tenantId;

    public Intervention(InterventionType it, OrganisationType orgType) {
        setInterventionType(it);
        setOrganisationType(orgType);
    }

    public BigDecimal getShareAsBigDecimal() {
        return shareOfTotal == null ? null : new BigDecimal(shareOfTotal)
                .divide(new BigDecimal(100.00f));
    }

    @JsonProperty
    public BigDecimal getAnnualGasSaved() {
        if (getInterventionType().getAnnualGasSaved() == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return getShareAsBigDecimal().multiply(
                    getInterventionType().getAnnualGasSaved());
        }
    }

    @JsonProperty
    public BigDecimal getAnnualElecSaved() {
        if (getInterventionType().getAnnualElecSaved() == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return getShareAsBigDecimal().multiply(
                    getInterventionType().getAnnualElecSaved());
        }
    }

    @JsonProperty
    public BigDecimal getAnnualTonnesCo2eSaved() {
        return getShareAsBigDecimal()
                .multiply(getInterventionType().getAnnualTonnesCo2eSaved());
    }

    @JsonProperty
    public BigDecimal getAnnualCashOutflows() {
        if (getInterventionType().getAnnualCashOutflows() == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return getShareAsBigDecimal()
                    .multiply(getInterventionType().getUptakeFactor())
                    .multiply(getInterventionType().getAnnualCashOutflows());
        }
    }

    @JsonProperty
    public BigDecimal getAnnualCashOutflows(int target) {
        if (getInterventionType().getAnnualCashOutflows() == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return getShareAsBigDecimal()
                    .multiply(getInterventionType().getUptakeFactor())
                    .multiply(getInterventionType()
                            .getAnnualCashOutflowsNationalTargetYear());
        }
    }

    @JsonProperty
    public BigDecimal getAnnualCashInflows() {
        return getShareAsBigDecimal()
                .multiply(getInterventionType().getUptakeFactor())
                .multiply(getInterventionType().getAnnualCashInflows());
    }

    @JsonProperty
    public BigDecimal getAnnualCashInflows(int target) {
        if (getInterventionType().getAnnualCashInflows() == null) {
            return Finance.ZERO_BIG_DECIMAL;
        } else {
            return getShareAsBigDecimal()
                    .multiply(getInterventionType().getUptakeFactor())
                    .multiply(getInterventionType().getAnnualCashInflowsNationalTargetYear());
        }
    }

    @JsonProperty
    public BigDecimal getCashOutflowUpFront() {
        return getShareAsBigDecimal()
                .multiply(getInterventionType().getUptakeFactor())
                .multiply(getInterventionType().getCashOutflowsUpFrontNational());
    }

    @JsonProperty
    public BigDecimal getTotalNpv() {
        return getShareAsBigDecimal().multiply(
                getInterventionType().getTotalNpv());
    }

    @JsonProperty
    public Long getTonnesCo2eSavedTargetYear() {
        return getShareAsBigDecimal()
                .multiply(getInterventionType().getUptakeFactor())
                .multiply(new BigDecimal(getInterventionType()
                        .getTonnesCo2eSavedTargetYear().toString()))
                .longValue();
    }

    @JsonProperty
    public Long getTargetYearSavings() {
        return getShareAsBigDecimal().multiply(
                new BigDecimal(getInterventionType().getTargetYearSavings()
                        .toString())).longValue();
    }

    @JsonProperty
    @Transient
    public BigDecimal getUnitCount() {
        Integer unitCount = getInterventionType().getUnitCount();
        return getShareAsBigDecimal().multiply(
                new BigDecimal(unitCount == null ? "0" : unitCount.toString()));
    }

    /** Convenience method to access intervention type's name. */
    @JsonProperty
    public String getName() {
        return getInterventionType().getName();
    }

    /** Convenience method to access intervention type's description. */
    @JsonProperty
    public String getDescription() {
        return getInterventionType().getDescription();
    }

    /** Convenience method to access intervention type's further info URL. */
    @JsonProperty
    public String getFurtherInfo() {
        return getInterventionType().getFurtherInfo();
    }

    /** Convenience method to access intervention type's unit. */
    @JsonProperty
    public String getUnit() {
        return getInterventionType().getUnit();
    }

    /** Convenience method to access intervention type's (aka national) unit count. */
    @JsonProperty
    public Integer getUnitCountNational() {
        return getInterventionType().getUnitCount();
    }

    /** Convenience method to access description of intervention type's unit. */
    @JsonProperty
    public String getUnitDescription() {
        return getInterventionType().getUnitDescription();
    }

    /** Convenience method to access description of intervention type's uptake. */
    @JsonProperty
    public Short getUptake() {
        return getInterventionType().getUptake();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getCashOutflowsUpFrontNational() {
        return getInterventionType().getCashOutflowsUpFrontNational();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getAnnualCashOutflowsNationalTargetYear() {
        return getInterventionType().getAnnualCashOutflowsNationalTargetYear();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getAnnualCashInflowsNationalTargetYear() {
        return getInterventionType().getAnnualCashInflowsNationalTargetYear();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public BigDecimal getCostPerTonneCo2e() {
        return getInterventionType().getCostPerTonneCo2e();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public Short getConfidence() {
        return getInterventionType().getConfidence();
    }

    /** Convenience method to access intervention type's data. */
    @JsonProperty
    public String getSlug() {
        return getInterventionType().getSlug();
    }

    @Override
    public String toString() {
        return String
                .format("Intervention [id=%s, interventionType=%s, organisationType=%s, shareOfTotal=%s, tenantId=%s]",
                        id, interventionType.getName(),
                        organisationType.getName(), shareOfTotal, tenantId);
    }

    public String toCsv() {
        return String.format(
                "%s,\"%s\",\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%d,%f,%d", id,
                interventionType.getName(), organisationType.getName(),
                shareOfTotal, getCashOutflowUpFront(), getAnnualCashInflows(),
                getAnnualCashOutflows(), getTotalNpv(),
                getAnnualTonnesCo2eSaved(), getAnnualElecSaved(),
                getAnnualGasSaved(), getTonnesCo2eSavedTargetYear(),
                getCostPerTonneCo2e(), getTargetYearSavings());
    }

}
