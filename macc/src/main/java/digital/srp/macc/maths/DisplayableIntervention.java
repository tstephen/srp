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
package digital.srp.macc.maths;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayableIntervention extends ResourceSupport {
    
    private String selfRef;
    private String name;
    private String description;
    private String status;
    private String classification;
    private Short confidence;
    private int modellingYear;
    private Short lifetime;
    private Short uptake;
    private Double scaling;
    private Double cashOutflowUpFront;
    private Double annualCashInflows;
    private Double annualCashOutflows;
    private Double annualCashOutflowsTargetYear;
    private Float annualTonnesCo2eSaved;
    private Double annualElecSaved;
    private Double tonnesCo2eSavedTargetYear;
    private Double costPerTonneCo2e;
    private Double totalNpv;
    private Double targetYearSavings;
    private String furtherInfo;
    private String orgType;
    private Double shareOfTotal;
    private String unit;
    private Double unitCount;
    
}