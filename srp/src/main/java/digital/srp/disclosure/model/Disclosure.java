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
package digital.srp.disclosure.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TR_DISCLOSURE")
@Data
// this is apparently not enough for jackson to figure out how to parse
// at least when invoked via spring-data-rest
// @JsonAutoDetect
@AllArgsConstructor
public class Disclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    /**
     * Full name of the person making the disclosure.
     */
    @NotNull
    @JsonProperty
    private String name;

    /**
     * Name of the organisation making the disclosure.
     */
    @NotNull
    @JsonProperty
    private String organisation;

    /**
     */
    @NotNull
    @JsonProperty
    private String email;

    /**
     */
    @NotNull
    @JsonProperty
    private String phone;

    /**
     */
    @NotNull
    @JsonProperty
    private String date;

    /**
     */
    @JsonProperty
    private int score;

    /**
     */
    @JsonProperty
    private String eClasses;

    /**
     */
    @JsonProperty
    private String eClassCategories;

    /**
     */
    @JsonProperty
    private boolean codeOfConduct;

    /**
     */
    @JsonProperty
    private String certifications;

    /**
     */
    @JsonProperty
    private String rawMaterials;

    /**
     */
    @JsonProperty
    private boolean carbonAccounting;

    /**
     */
    @JsonProperty
    private String supplierMemberOfCdp;

    /**
     */
    @JsonProperty
    private boolean signatoryToCdp;

    /**
     */
    @JsonProperty
    private boolean sustainabilityPolicy;

    /**
     */
    @JsonProperty
    private boolean useSpotContracts;

    /**
     */
    @JsonProperty
    private boolean useSubcontractors;

    /**
     */
    @JsonProperty
    private String tradeBodiesSubscribedTo;

    /**
     */
    @JsonProperty
    private String namedPersonResponsibleForEthics;

    /**
     */
    @JsonProperty
    private boolean staffEthicalDevelopment;

    /**
     */
    @JsonProperty
    private String upstreamCodeOfConductAudit;

}
