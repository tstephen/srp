package digital.srp.sreport.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * Root object defining questions and categories to present to user.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name= "SR_SURVEY")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private String name;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private String status = "Draft";
    
    /** 
     * Period this set of responses apply to. 
     * 
     * <p>For example: calendar or financial year, quarter etc.
     */
    @NotNull
    @Size(max = 20)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private String applicablePeriod;

    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @OneToMany(orphanRemoval=true, cascade= CascadeType.ALL)
    private List<SurveyCategory> categories;
    
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
    @Transient
    public List<SurveyQuestion> questions() {
        ArrayList<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
        for (SurveyCategory cat : categories) {
            questions.addAll(cat.questions());
        }
        return questions;
    }
}
