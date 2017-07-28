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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.views.InterventionViews;
import digital.srp.macc.views.OrganisationInterventionViews;
import digital.srp.macc.views.OrganisationTypeViews;
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
@Table(name = "TR_ORG_TYPE")
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    private Long id;

    @NotNull
    @JsonProperty
    @JsonView({ OrganisationInterventionViews.Summary.class, OrganisationTypeViews.Summary.class, InterventionViews.Summary.class })
    @Column(name = "name")
    private String name;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "sector")
    private String sector;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "count")
    private int count;

    // A set of turnover ranges. TODO Under 10m,
    // 10-50m,50-100m, Over 100m?
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "annual_turnover")
    private int annualTurnover;

    // A set of energy use ranges.
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "annual_energy_use")
    private int annualEnergyUse;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "no_of_staff")
    private double noOfStaff;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "no_of_patient_interactions")
    private int noOfPatientInteractions;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "no_of_in_patients")
    private int noOfInPatients;

    // Useful for quantifying heat and light requirements
    // for various interventions
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "floor_area")
    private int floorArea;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "annual_mileage")
    private long annualMileage;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "typical_journey_distance")
    private int typicalJourneyDistance;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Detailed.class })
    @Column(name = "no_inhalers_prescribed")
    private int noInhalersPrescribed;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "icon")
    private String icon;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "status")
    private String status;

    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "commissioner")
    private boolean commissioner;

    /**
     * If this organisation type is used in sustainability reporting tools.
     */
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "reporting_type")
    private boolean reportingType;
    
    @JsonProperty
    @JsonView({ OrganisationTypeViews.Summary.class })
    @Column(name = "tenant_id")
    private String tenantId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_TYPE_ID")
    @JsonView({ OrganisationTypeViews.Detailed.class })
    private List<Intervention> applicableInterventions;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView({ OrganisationTypeViews.Summary.class })
    private List<Link> links;
}
