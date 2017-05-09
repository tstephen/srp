package digital.srp.sreport.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

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
@Accessors(fluent=true)
@Data
@ToString(exclude = { "survey" })
@EqualsAndHashCode(exclude = { "id", "survey", "revision", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@Table(name= "SR_RETURN")
public class SurveyReturn {
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
    @JsonView(SurveyReturnViews.Summary.class)
    @Column(name = "name")
    private String name;
    
    /**
     * Commonly this will be a unique organisation identifier maintained in
     * another system, though it could simply be a friendly name too.
     */
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
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
    
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private long created;
 
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
 
    @Column(name = "last_updated")
    @LastModifiedDate
    private long lastUpdated;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    private Survey survey;
    
    @JsonProperty
    @JsonView(SurveyReturnViews.Detailed.class)
//    @Transient
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "surveyReturns")
    private List<Answer> answers;

    public Answer answer(Q q, String period) {
        return answer(q.name(), period);
    }
    
    protected Answer answer(String qName, String period) {
        for (Answer answer : answers) {
            if (qName.equals(answer.question().name()) && period.equals(answer.applicablePeriod())) {
                return answer;
            }
        }
        LOGGER.info("Assuming zero for {}", qName);
        return new Answer().question(new Question()
                .name(qName))
                .applicablePeriod(period)
                .response("0")
                .addSurveyReturn(this);
    }
    
}
