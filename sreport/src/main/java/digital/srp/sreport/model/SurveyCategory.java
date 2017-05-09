package digital.srp.sreport.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "name")
    private String name;
    
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "questions")
    @Lob
//    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, mappedBy = "category")
    private String questionCodes;
    
    @Transient
    @JsonView(SurveyViews.Detailed.class)
    private List<Question> questions;
    
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

    public SurveyCategory questionCodes(Q... questions) {
        questionCodes(Arrays.asList(questions));
        return this;
    }
    
    public SurveyCategory questionCodes(List<Q> questions) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Q> it = questions.iterator(); it.hasNext();) {
            Q q = it.next();
            sb.append(q.code());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        this.questionCodes = sb.toString();
        return this;
    }
    
    public List<Q> questionCodes() {
        if (questionCodes == null) {
            return Collections.emptyList();
        }
        
        ArrayList<Q> list = new ArrayList<Q>();
        String[] qNames = questionCodes.split(",");
        for (String name : qNames) {
            list.add(Q.valueOf(name));
        }
        return list;
    }
    
    public List<Question> questions() {
        if (questions == null) {
            questions = new ArrayList<Question>(); 
        }
        return questions;
    }
    
//    public SurveyQuestion question(String qName) {
//        for (SurveyQuestion q : questions) {
//            if (qName.equals(q.name())) {
//                return q;
//            }
//        }
//        return null;
//    }
//    
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
