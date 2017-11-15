package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.sreport.internal.AuthUtils;
import digital.srp.sreport.model.views.AnswerViews;
import digital.srp.sreport.model.views.QuestionViews;
import digital.srp.sreport.model.views.SurveyReturnViews;
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
public class Answer implements AuditorAware<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, SurveyReturnViews.Detailed.class })
    @Column(name = "id")
    private Long id;

    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, SurveyReturnViews.Detailed.class })
    @Column(name = "response")
    private String response;

    @NotNull
    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, SurveyReturnViews.Detailed.class })
    @ManyToOne(fetch = FetchType.EAGER)
    private Question question;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, SurveyReturnViews.Detailed.class })
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
    @JsonView({ AnswerViews.Summary.class, SurveyReturnViews.Detailed.class })
    @Column(name = "applicable_period")
    private String applicablePeriod;

    /**
     * Typically there will be one revision of a survey submitted but this
     * allows for a re-statement if needed.
     */
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "revision")
    private Short revision = 1;

    /**
     * True is we have calculated this answer on behalf of the user.
     */
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class, SurveyReturnViews.Detailed.class })
    @Column(name = "derived")
    private boolean derived = false;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "submitted_date")
    private Date submittedDate;

    /**
     * Username of submitter.
     */
    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "submitted_by")
    private String submittedBy;

    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;

    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "last_updated")
    @LastModifiedDate
    private Date lastUpdated;

    @JsonView({ AnswerViews.Detailed.class })
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @JsonProperty
    @JsonView({ AnswerViews.Detailed.class })
    @ManyToMany
    @JoinTable(name="SR_RETURN_ANSWER",
            joinColumns=@JoinColumn(name="answer_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="survey_return_id", referencedColumnName="id"))
    private Set<SurveyReturn> surveyReturns;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView({ AnswerViews.Summary.class, QuestionViews.Summary.class })
    private List<Link> links;

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
        return SignificantFiguresFormat.format(responseAsBigDecimal());
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
        return response == null ? BigDecimal.ZERO : new BigDecimal(response);
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

    @Override
    public String getCurrentAuditor() {
        return AuthUtils.getUserId();
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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
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
