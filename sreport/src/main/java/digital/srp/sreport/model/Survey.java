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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * Root object defining questions and categories to present to user.
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id" })
@EqualsAndHashCode(exclude = { "id", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name= "SR_SURVEY")
public class Survey {

    private static final Logger LOGGER = LoggerFactory.getLogger(Survey.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonView(SurveyViews.Summary.class)
    @JsonProperty
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonView(SurveyViews.Summary.class)
    @JsonProperty
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(max = 50)
    @JsonView(SurveyViews.Summary.class)
    @JsonProperty
    @Column(name = "status")
    private String status = "Draft";

    /**
     * Period this set of responses apply to.
     *
     * <p>For example: calendar or financial year, quarter etc.
     */
    @NotNull
    @Size(max = 20)
    @JsonView(SurveyViews.Summary.class)
    @JsonProperty
    @Column(name = "applicable_period")
    private String applicablePeriod;

    @JsonView(SurveyViews.Detailed.class)
    @JsonProperty
    @OneToMany(orphanRemoval=true, cascade= CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "survey")
    private List<SurveyCategory> categories;

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

    public List<SurveyCategory> categories() {
        if (categories == null) {
            categories = new ArrayList<SurveyCategory>();
        }
        return categories;
    }

    public Survey categories(SurveyCategory... categories) {
        categories(Arrays.asList(categories));
        return this;
    }

    public Survey categories(List<SurveyCategory> categories) {
        this.categories = categories;

        for (SurveyCategory cat : categories) {
            cat.survey(this);
        }

        return this;
    }

    @JsonProperty
    @Transient
    public List<Q> questionCodes() {
        ArrayList<Q> questions = new ArrayList<Q>();
        try {
            for (SurveyCategory cat : categories) {
                questions.addAll(cat.questionEnums());
            }
        } catch (NullPointerException e) {
            LOGGER.warn("Have no categories in survey {} ({})", name(), id());
        }
        return questions;
    }

    public Q questionCode(String qName) {
        for (Q q : questionCodes()) {
            if (qName.equals(q.name())) {
                return q;
            }
        }
        return null;
    }

    @JsonProperty
    @Transient
    public List<Question> questions() {
        ArrayList<Question> questions = new ArrayList<Question>();
        try {
            for (SurveyCategory cat : categories) {
                questions.addAll(cat.questions());
            }
        } catch (NullPointerException e) {
            LOGGER.warn("Have no categories in survey {} ({})", name(), id());
        }
        return questions;
    }

    public SurveyCategory category(String catName) {
        for (SurveyCategory cat : categories()) {
            if (catName.equals(cat.name())) {
                return cat;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicablePeriod() {
        return applicablePeriod;
    }

    public void setApplicablePeriod(String applicablePeriod) {
        this.applicablePeriod = applicablePeriod;
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
