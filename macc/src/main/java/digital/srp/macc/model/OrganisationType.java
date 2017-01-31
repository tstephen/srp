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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class OrganisationType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    private String name;

    @JsonProperty
    private String sector;

    @JsonProperty
    private int count;

    // A set of turnover ranges. TODO Under 10m,
    // 10-50m,50-100m, Over 100m?
    @JsonProperty
    private int annualTurnover;

    // A set of energy use ranges.
    @JsonProperty
    private int annualEnergyUse;

    @JsonProperty
    private double noOfStaff;

    @JsonProperty
    private int noOfPatientInteractions;

    @JsonProperty
    private int noOfInPatients;

    // Useful for quantifying heat and light requirements
    // for various interventions
    @JsonProperty
    private int floorArea;

    @JsonProperty
    private long annualMileage;

    @JsonProperty
    private int typicalJourneyDistance;

    @JsonProperty
    private int noInhalersPrescribed;

    @JsonProperty
    private String icon;

    @JsonProperty
    private String status;

    @JsonProperty
    private boolean commissioner;

    @JsonProperty
    private String tenantId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_TYPE_ID")
    private List<Intervention> applicableInterventions;
}
