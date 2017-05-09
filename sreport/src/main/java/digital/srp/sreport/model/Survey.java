package digital.srp.sreport.model;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.xml.bind.annotation.XmlElement;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * Root object defining questions and categories to present to user.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id" })
@EqualsAndHashCode(exclude = { "id", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name= "SR_SURVEY")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    private Long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    @Column(name = "name")
    private String name;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(SurveyViews.Summary.class)
    @Column(name = "status")
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
    @Column(name = "applicable_period")
    private String applicablePeriod;

    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @OneToMany(orphanRemoval=true, cascade= CascadeType.ALL, mappedBy = "survey")
    private List<SurveyCategory> categories;
    
    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView(SurveyViews.Summary.class)
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
    
    public List<SurveyCategory> categories() {
        if (categories == null) {
            categories = new ArrayList<SurveyCategory>();
        }
        return categories;
    }
    
    public Survey categories(SurveyCategory... categories) {
        categories(Arrays.asList(categories));
        return this;
    }
    
    public Survey categories(List<SurveyCategory> categories) {
        this.categories = categories;
        
        for (SurveyCategory cat : categories) {
            cat.survey(this);
        }
        
        return this;
    }
    
    @JsonProperty
    @Transient
    public List<Q> questionCodes() {
        ArrayList<Q> questions = new ArrayList<Q>();
        for (SurveyCategory cat : categories) {
            questions.addAll(cat.questionCodes());
        }
        return questions;
    }

    public Q questionCode(String qName) {
        for (Q q : questionCodes()) {
            if (qName.equals(q.name())) {
                return q;
            }
        }
        return null;
    }

    @JsonProperty
    @Transient
    public List<Question> questions() {
        ArrayList<Question> questions = new ArrayList<Question>();
        for (SurveyCategory cat : categories) {
            questions.addAll(cat.questions());
        }
        return questions;
    }

    public Question question(String qName) {
        for (Question q : questions()) {
            if (qName.equals(q.name())) {
                return q;
            }
        }
        return null;
    }
    
    public SurveyCategory category(String catName) {
        for (SurveyCategory cat : categories()) {
            if (catName.equals(cat.name())) {
                return cat;
            }
        }
        return null;
    }

}
