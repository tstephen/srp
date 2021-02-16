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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Health status of a single organisation's return for a single period.
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true, chain = true)
@Data
//@ToString(exclude = { "survey" })
//@EqualsAndHashCode(exclude = { "id", "survey", "revision", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
public class HealthCheck {

    @NotNull
    @Size(max = 50)
    @JsonProperty
    private String name;

    /**
     * Commonly this will be a unique organisation identifier maintained in
     * another system, though it could simply be a friendly name too.
     */
    @NotNull
    @Size(max = 50)
    @JsonProperty
    private String org;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    private String status = StatusType.Draft.name();

    /**
     * Period this set of responses apply to.
     *
     * <p>For example: calendar or financial year, quarter etc.
     */
    @NotNull
    @JsonProperty
    @Size(max = 20)
    private String applicablePeriod;

    /**
     * Typically there will be one revision of a survey submitted but this
     * allows for a re-statement if needed.
     */
    @JsonProperty
    private Short revision = 1;

    @JsonProperty
    private Date submittedDate;

    /**
     * Username of submitter.
     */
    @JsonProperty
    private String submittedBy;

    @JsonProperty
    @CreatedDate
    private Date created;

    @JsonProperty
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @JsonProperty
    @LastModifiedDate
    private Date lastUpdated;

    @JsonProperty
    private String updatedBy;

    @JsonProperty
    private int answerCount;

    @JsonProperty
    private Set<String> issues;

    public HealthCheck(SurveyReturn rtn) {
        this();
        this.name = rtn.name();
        this.org = rtn.org();
        this.status = rtn.status();
        this.applicablePeriod = rtn.applicablePeriod();
        this.revision = rtn.revision();
        this.answerCount = rtn.answers().size();
        this.issues = Collections.emptySet();
    }

    public HealthCheck(SurveyReturn rtn, Set<ConstraintViolation<SurveyReturn>> violations) {
        this(rtn);
        HashSet<String> issues = new HashSet<String>();
        for (ConstraintViolation<SurveyReturn> cv : violations) {
            issues.add(cv.getMessage());
        }
        this.issues = issues;
    }

}
