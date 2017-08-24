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
import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyCategoryViews;
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
    @JsonView({ SurveyCategoryViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "id")
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView({ SurveyCategoryViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "name")
    private String name;
    
    @JsonProperty
    @JsonView({ SurveyCategoryViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "questions")
    @Lob
//    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, mappedBy = "category")
    private String questionNames;
    
    @Transient
    @JsonView({ SurveyCategoryViews.Detailed.class, SurveyViews.Detailed.class })
    private List<Question> questions;
    
    @JsonProperty
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Survey survey;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView({ SurveyCategoryViews.Summary.class, SurveyViews.Detailed.class })
    private List<Link> links;

    public SurveyCategory questionEnums(Q... questions) {
        questionEnums(Arrays.asList(questions));
        return this;
    }
    
    public SurveyCategory questionEnums(List<Q> questions) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Q> it = questions.iterator(); it.hasNext();) {
            Q q = it.next();
            sb.append(q.code());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        this.questionNames = sb.toString();
        return this;
    }
    
    public List<Q> questionEnums() {
        if (questionNames == null) {
            return Collections.emptyList();
        }
        
        ArrayList<Q> list = new ArrayList<Q>();
        String[] qNames = questionNames.split(",");
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

    public String getQuestionNames() {
        return questionNames;
    }

    public void setQuestionNames(String questionNames) {
        this.questionNames = questionNames;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
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
