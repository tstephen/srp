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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * Root object making up a single organisation's submission for a single period.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@NoArgsConstructor
@Entity
@Table(name= "SR_RETURN")
public class SurveyReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private String name;
    
    /**
     * Commonly this will be a unique organisation identifier maintained in
     * another system, though it could simply be a friendly name too.
     */
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
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

    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    private Date submittedDate;

    /** 
     * Username of submitter.
     */
    @JsonProperty
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
    @ManyToOne(fetch = FetchType.EAGER)
    private Survey survey;
    
    @JsonProperty
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
    private List<SurveyAnswer> answers;
    
}
