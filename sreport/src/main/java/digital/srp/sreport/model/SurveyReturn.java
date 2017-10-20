package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.api.MandatoryCurrentPeriodAnswersProvided;
import digital.srp.sreport.api.MinimumPeriodsProvided;
import digital.srp.sreport.internal.EntityAuditorListener;
import digital.srp.sreport.model.views.AnswerViews;
import digital.srp.sreport.model.views.SurveyReturnViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * Root object making up a single organisation's submission for a single period.
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true, chain = true)
@Data
@ToString(exclude = { "survey" })
@EqualsAndHashCode(exclude = { "id", "survey", "revision", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
/* For whatever reason AuditingEntityListener is not adding auditor, hence own listener as well */
@EntityListeners( { AuditingEntityListener.class, EntityAuditorListener.class})
@Table(name= "SR_RETURN")
@MinimumPeriodsProvided(noPeriods = 4)
@MandatoryCurrentPeriodAnswersProvided(requiredAnswers = {Q.ORG_CODE, Q.ORG_NAME, Q.ORG_TYPE, Q.SDMP_CRMP, Q.HEALTHY_TRANSPORT_PLAN, Q.PROMOTE_HEALTHY_TRAVEL})
public class SurveyReturn implements AuditorAware<String> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyReturn.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class, SurveyReturnViews.Summary.class })
    @Column(name = "name")
    private String name;

    /**
     * Commonly this will be a unique organisation identifier maintained in
     * another system, though it could simply be a friendly name too.
     */
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class, SurveyReturnViews.Summary.class })
    @Column(name = "org")
    private String org;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "status")
    private String status = StatusType.Draft.name();

    /**
     * Period this set of responses apply to.
     *
     * <p>For example: calendar or financial year, quarter etc.
     */
    @NotNull
    @JsonProperty
    @Size(max = 20)
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "applicable_period")
    private String applicablePeriod;

    /**
     * Typically there will be one revision of a survey submitted but this
     * allows for a re-statement if needed.
     */
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "revision")
    private Short revision = 1;


    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "submitted_date")
    private Date submittedDate;

    /**
     * Username of submitter.
     */
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "submitted_by")
    private String submittedBy;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView(SurveyReturnViews.Summary.class)
    private List<Link> links;

    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "last_updated")
    @LastModifiedDate
    private Date lastUpdated;

    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class, SurveyReturnViews.Summary.class })
    @ManyToOne(fetch = FetchType.EAGER)
    private Survey survey;

    @JsonProperty
    @JsonView(SurveyReturnViews.Detailed.class)
//    @Transient
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "surveyReturns")
    private Set<Answer> answers;

    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
    @Transient
    private Set<String> completeness;

    public SurveyReturn(String name, String org, String status,
            String applicablePeriod, Short revision) {
        this();
        this.name = name;
        this.org = org;
        this.status = status;
        this.applicablePeriod = applicablePeriod;
        this.revision = revision;
    }

    public Set<Answer> answers() {
        if (answers == null) {
            answers = new HashSet<Answer>();
        }
        return answers;
    }

    public Set<Answer> derivedAnswers() {
        HashSet<Answer> derivedAnswers = new HashSet<Answer>();
        for (Answer answer : answers()) {
            if (answer.derived() && answer.response() != null ) {
                derivedAnswers.add(answer);
            }
        }
        return derivedAnswers;
    }

    public Set<Answer> underivedAnswers() {
        HashSet<Answer> underivedAnswers = new HashSet<Answer>();
        for (Answer answer : answers()) {
            if (!answer.derived() && answer.response() != null ) {
                underivedAnswers.add(answer);
            }
        }
        return underivedAnswers;
    }

    public Optional<Answer> answer(String period, Q q) {
        return answer(q.name(), period);
    }

    protected Optional<Answer> answer(String qName, String period) {
        Answer a = null;
        int count = 0;
        for (Answer answer : answers) {
            if (qName.equals(answer.question().name()) && period.equals(answer.applicablePeriod())) {
                count++;
                a = answer;
            }
        }
        if (count > 1) {
            LOGGER.error("Multiple answers to {} found for {} in {}", org, qName, period);
        }
        return Optional.ofNullable(a);
    }

    public Answer createEmptyAnswer(String period, Question existingQ) {
        return new Answer()
                .addSurveyReturn(this)
                .derived(true)
                .applicablePeriod(period)
                .question(existingQ)
                .status(StatusType.Draft.name());
    }

    public synchronized Answer initAnswer(SurveyReturn rtn, String period, Question existingQ) {
        Answer answer = rtn.createEmptyAnswer(period, existingQ);
        rtn.answers().add(answer);
        return answer;
    }

    public BigDecimal answerResponseAsBigDecimal(String period, Q q) {
        Optional<Answer> answer = answer(period, q);
        if (answer.isPresent()) {
            return new BigDecimal(answer.get().response());
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
          return null;
        }

        return ((Principal) authentication.getPrincipal()).getName();
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

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
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

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

}
