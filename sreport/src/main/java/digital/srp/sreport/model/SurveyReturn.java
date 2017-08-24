package digital.srp.sreport.model;

import java.security.Principal;
import java.util.ArrayList;
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
    private List<Answer> answers;

    public SurveyReturn(String name, String org, String status,
            String applicablePeriod, Short revision) {
        this();
        this.name = name;
        this.org = org;
        this.status = status;
        this.applicablePeriod = applicablePeriod;
        this.revision = revision;
    }

    public List<Answer> answers() {
        if (answers == null) { 
            answers = new ArrayList<Answer>();
        }
        return answers;
    }
    
    public Answer answer(Q q, String period) {
        return answer(q.name(), period);
    }
    
    protected Answer answer(String qName, String period) {
        Answer a = null;
        int count = 0;
        for (Answer answer : answers) {
            if (qName.equals(answer.question().name()) && period.equals(answer.applicablePeriod())) {
                count++;
                a = answer;
            }
        }
        if (a != null) {
            if (count != 1){
                LOGGER.error("Multiple answers to {} found for {} in {}", org, qName, period);
            }
            return a;
        }
        LOGGER.info("Assuming zero for {}", qName);
        return new Answer().question(new Question()
                .name(qName))
                .applicablePeriod(period)
                .response("0")
                .addSurveyReturn(this);
    }

    @Override
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
          return null;
        }

        return ((Principal) authentication.getPrincipal()).getName();
    }

    /**
     * @return true if all questions in Organisation, Policy and Performance categries have non-empty answers.
     */
    @JsonProperty
    public boolean isComplete() {
        boolean complete = true;
        for (Answer answer : answers) {
            if (answer.question().required() && answer.response() == null) {
                complete = false;
                break;
            }
        }
        return complete;
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

}
