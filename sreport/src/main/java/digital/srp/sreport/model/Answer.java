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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;

import digital.srp.macc.maths.SignificantFiguresFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * A single question's response.
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@EqualsAndHashCode(exclude = { "id", "surveyReturns" })
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name= "SR_ANSWER")
public class Answer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Answer.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Column(name = "id")
    private Long id;

    @JsonProperty
    @Column(name = "response")
    private String response;

    @NotNull
    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    private Question question;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @Column(name = "status")
    private String status = StatusType.Draft.name();

    /**
     * Period this answer applies to.
     *
     * <p>For example: calendar or financial year, quarter etc.
     */
    @NotNull
    @JsonProperty
    @Size(max = 20)
    @Column(name = "applicable_period")
    private String applicablePeriod;

    /**
     * Typically there will be one revision of a survey submitted but this
     * allows for a re-statement if needed.
     */
    @JsonProperty
    @Column(name = "revision")
    private Short revision = 1;

    /**
     * True is we have calculated this answer on behalf of the user.
     */
    @JsonProperty
    @Column(name = "derived")
    private boolean derived = false;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    @Column(name = "submitted_date")
    private Date submittedDate;

    /**
     * Username of submitter.
     */
    @JsonProperty
    @Column(name = "submitted_by")
    private String submittedBy;

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

    @ManyToMany
    @JoinTable(name="SR_RETURN_ANSWER",
            joinColumns=@JoinColumn(name="answer_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="survey_return_id", referencedColumnName="id"))
    private Set<SurveyReturn> surveyReturns;

    public Answer(long id, String response, Question q, StatusType status,
            String applicablePeriod, short revision, boolean derived,
            Date submittedDate, String submittedBy, Date created,
            String createdBy, Date lastUpdated, String updatedBy) {
        this();
        id(id).response(response).question(q).status(status.name())
                .applicablePeriod(applicablePeriod).revision(revision)
                .derived(derived).submittedDate(submittedDate)
                .submittedBy(submittedBy).created(created).createdBy(createdBy)
                .lastUpdated(lastUpdated).updatedBy(updatedBy);
    }

    public String response3sf() {
        return SignificantFiguresFormat.getInstance().format(responseAsBigDecimal());
    }

    public Answer question(Question q) {
        question = q;
        return this;
    }

    public Answer question(Q q) {
        question = new Question().name(q.name());
        return this;
    }

    public String response() {
        return typeConversion(response);
    }

    public BigDecimal responseAsBigDecimal() {
        return responseAsOptional().orElse(BigDecimal.ZERO);
    }

    public Optional<BigDecimal> responseAsOptional() {
        try {
            return response == null || response.trim().length() == 0
                    ? Optional.empty() : Optional.of(new BigDecimal(response));
        } catch (NumberFormatException e) {
            LOGGER.warn("Cannot parse BigDecimal from {} for answer id {}", response, id);
            return Optional.empty();
        }
    }

    public Answer response(String response) {
        this.response = typeConversion(response);
        return this;
    }

    private String typeConversion(String response) {
        // TODO l10n of decimal separator
        try {
            String tmp = response.replaceAll(",", "");
            Double.parseDouble(tmp);
            response = tmp;
        } catch (NullPointerException | NumberFormatException e) {
            ; // ok
        }
        return response;
    }

    public Answer response(BigDecimal decimal) {
        response = decimal.toPlainString();
        return this;
    }

    public Set<SurveyReturn> surveyReturns() {
        if (surveyReturns==null) {
            surveyReturns = new HashSet<SurveyReturn>();
        }
        return surveyReturns;
    }

    public Answer addSurveyReturn(SurveyReturn rtn) {
        surveyReturns().add(rtn);
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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

    public Short getRevision() {
        return revision;
    }

    public void setRevision(Short revision) {
        this.revision = revision;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
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

    @Override
    public String toString() {
        return String.format(
                "Answer [id=%s, response=%s, question=%s, status=%s, applicablePeriod=%s, revision=%s, derived=%s, submittedDate=%s, submittedBy=%s, created=%s, createdBy=%s, lastUpdated=%s, updatedBy=%s]",
                id, response, question.name(), status, applicablePeriod, revision,
                derived, submittedDate, submittedBy, created, createdBy,
                lastUpdated, updatedBy);
    }

}
