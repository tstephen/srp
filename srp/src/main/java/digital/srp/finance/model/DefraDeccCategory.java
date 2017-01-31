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
package digital.srp.finance.model;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TR_CATEGORY")
@Data
// this is apparently not enough for jackson to figure out how to parse
// at least when invoked via spring-data-rest
// @JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
public class DefraDeccCategory implements Serializable {

    private static final long serialVersionUID = 3835341729235763935L;

    @Id
    @NotNull
    @Column(unique = true)
    @JsonProperty
    private String code;

    /**
     */
    @JsonProperty
    private String description;

    public String getDescription() {
        return description == null ? code : description;
    }
}
