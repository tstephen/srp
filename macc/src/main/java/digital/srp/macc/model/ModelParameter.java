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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An action or related set of actions aimed at providing Carbon Abatement or
 * other sustainability benefit.
 *
 * @author Tim Stephenson
 */
@Data
@Entity
@Table(name = "TR_MODEL_PARAM")
@NoArgsConstructor
@AllArgsConstructor
public class ModelParameter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @JsonProperty
    @Column(name = "name")
    private String name;

    @JsonProperty
    @Column(name = "description")
    private String description;

    @JsonProperty
    @Column(name = "value")
    private BigDecimal value;

    @JsonProperty
    @Column(name = "valuets")
    @Lob
    private String valueTS;

    @JsonProperty
    @Column(name = "tenant_id")
    private String tenantId;

    public ModelParameter(String name, String valueTS) {
        super();
        this.name = name;
        this.valueTS = valueTS;
    }

    public ModelParameter(String name, BigDecimal value) {
        super();
        this.name = name;
        this.value = value;
    }

    @JsonProperty
    @Transient
    public List<BigDecimal> getValueTimeSeries() {
        return new TimeSeries(valueTS).asList();
    }

    @JsonProperty
    @Transient
    public void setValueTimeSeries(List<BigDecimal> values) {
        valueTS = TimeSeries.asString(values);
    }

}
