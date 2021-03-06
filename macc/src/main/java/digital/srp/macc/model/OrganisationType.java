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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A segmentation of all the organisations in the NHS, public health and social
 * care.
 *
 * @author Tim Stephenson
 */
@Data
@Entity
@Table(name = "TR_ORG_TYPE")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OrganisationType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    @Column(name = "name")
    private String name;

    @JsonProperty
    @Column(name = "sector")
    private String sector;

    @JsonProperty
    @Column(name = "count")
    private int count;

    // A set of turnover ranges. TODO Under 10m,
    // 10-50m,50-100m, Over 100m?
    @JsonProperty
    @Column(name = "annual_turnover")
    private int annualTurnover;

    // A set of energy use ranges.
    @JsonProperty
    @Column(name = "annual_energy_use")
    private int annualEnergyUse;

    @JsonProperty
    @Column(name = "no_of_staff")
    private double noOfStaff;

    @JsonProperty
    @Column(name = "no_of_patient_interactions")
    private int noOfPatientInteractions;

    @JsonProperty
    @Column(name = "no_of_in_patients")
    private int noOfInPatients;

    // Useful for quantifying heat and light requirements
    // for various interventions
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
    @Column(name = "no_inhalers_prescribed")
    private int noInhalersPrescribed;

    @JsonProperty
    @Column(name = "icon")
    private String icon;

    @JsonProperty
    @Column(name = "status")
    private String status;

    @JsonProperty
    @Column(name = "commissioner")
    private boolean commissioner;

    /**
     * If this organisation type is used in sustainability reporting tools.
     */
    @JsonProperty
    @Column(name = "reporting_type")
    private boolean reportingType;

    @JsonProperty
    @Column(name = "tenant_id")
    private String tenantId;

    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_TYPE_ID")
    private List<Intervention> applicableInterventions;
}
