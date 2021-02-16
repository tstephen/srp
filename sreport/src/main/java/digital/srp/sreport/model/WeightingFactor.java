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
package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id" })
@EqualsAndHashCode(exclude = { "id", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@Table(name= "SR_WFACTOR")
public class WeightingFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @Column(name = "org_type")
    private String orgType;

    @NotNull
    @Size(max = 60)
    @JsonProperty
    @Column(name = "category")
    private String category;

    /**
     * Currently only done once in 2014-15
     */
    @NotNull
    @Size(max = 7)
    @JsonProperty
    @Column(name = "period")
    private String applicablePeriod;

    /**
     * Volume of carbon emissions in kgCO<sub>2</sub>e.
     */
    @NotNull
    @JsonProperty
    @Column(name = "c_val", precision = 12, scale = 0)
    private BigDecimal carbonValue;

    /**
     * Value of spend in £s.
     */
    @NotNull
    @JsonProperty
    @Column(name = "m_val", precision = 12, scale = 0)
    private BigDecimal moneyValue;


    /**
     * Proportion of total spending spent on this category (derived empirically).
     */
    @JsonProperty
    @Column(name = "p_val", precision = 6, scale = 3)
    private BigDecimal proportionOfTotal;

    /**
     * Intensity of carbon emissions in kgCO<sub>2</sub>e / £.
     */
    @JsonProperty
    @Column(name = "i_val", precision = 6, scale = 3)
    private BigDecimal intensityValue;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "last_updated")
    @LastModifiedDate
    private Date lastUpdated;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    public BigDecimal intensityValue() {
        if (carbonValue == null || moneyValue == null || moneyValue.intValue() == 0) {
            return BigDecimal.ZERO;
        }
        return carbonValue.divide(moneyValue,3, java.math.RoundingMode.HALF_UP);
    }

    public void intensityValue(BigDecimal intensityValue) {
        //this.intensityValue = intensityValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getApplicablePeriod() {
        return applicablePeriod;
    }

    public void setApplicablePeriod(String applicablePeriod) {
        this.applicablePeriod = applicablePeriod;
    }

    public BigDecimal getCarbonValue() {
        return carbonValue;
    }

    public void setCarbonValue(BigDecimal carbonValue) {
        this.carbonValue = carbonValue;
    }

    public BigDecimal getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(BigDecimal moneyValue) {
        this.moneyValue = moneyValue;
    }

    public BigDecimal getProportionOfTotal() {
        return proportionOfTotal;
    }

    public void setProportionOfTotal(BigDecimal proportionOfTotal) {
        this.proportionOfTotal = proportionOfTotal;
    }

    public BigDecimal getIntensityValue() {
        return intensityValue;
    }

    public void setIntensityValue(BigDecimal intensityValue) {
        this.intensityValue = intensityValue;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

}
