package digital.srp.sreport.model;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Container for survey questions.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id", "survey" })
@EqualsAndHashCode(exclude = { "id", "survey" })
@NoArgsConstructor
@Entity
@Table(name= "SR_CAT")
public class SurveyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "id")
    private long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "name")
    private String name;
    
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, mappedBy = "category")
    private List<SurveyQuestion> questions;
    
//    @JsonProperty
//    @Transient
//    private Long surveyId;

    @JsonProperty
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Survey survey;
    
//    public SurveyCategory category(Survey survey) { 
//        this.survey = survey; 
//        this.surveyId = survey.id();
//        return this;
//    }
    
    public SurveyCategory questions(List<SurveyQuestion> questions) {
        this.questions = questions;
        
        for (SurveyQuestion q : questions) {
            q.category(this);
        }
        
        return this;
    }
    
//    public boolean equivalent(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        SurveyCategory other = (SurveyCategory) obj;
//        if (name == null) {
//            if (other.name != null)
//                return false;
//        } else if (!name.equals(other.name))
//            return false;
//        if (questions == null) {
//            if (other.questions != null)
//                return false;
//        } else if (!questions.equals(other.questions))
//            return false;
//        if (surveyId == null) {
//            if (other.surveyId != null)
//                return false;
//        } else if (!surveyId.equals(other.surveyId))
//            return false;
//        return true;
//    }
}
