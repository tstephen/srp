package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.macc.maths.SignificantFiguresFormat;
import digital.srp.sreport.model.views.SurveyReturnViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * A single question's response.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id", "surveyReturns" })
@EqualsAndHashCode(exclude = { "id", "surveyReturns" })
@NoArgsConstructor
@Entity
@Table(name= "SR_ANSWER")
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(SurveyReturnViews.Detailed.class)
    @Column(name = "id")
    private Long id;

    @JsonProperty
    @JsonView(SurveyReturnViews.Detailed.class)
    @Column(name = "response")
    private String response;

    @NotNull
    @JsonProperty
    @JsonView(SurveyReturnViews.Detailed.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private Question question;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyReturnViews.Summary.class)
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
    @JsonView(SurveyReturnViews.Detailed.class)
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
    @ManyToMany
    @JoinTable(name="SR_RETURN_ANSWER",       
            joinColumns=@JoinColumn(name="answer_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="survey_return_id", referencedColumnName="id"))
    private Set<SurveyReturn> surveyReturns;
    
    public String response3sf() {
        return SignificantFiguresFormat.format(new BigDecimal(response));
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
    
}
