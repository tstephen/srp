package digital.srp.sreport.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.AnswerViews;
import digital.srp.sreport.model.views.QuestionViews;
import digital.srp.sreport.model.views.SurveyReturnViews;
import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Holds a single survey question.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = {  })
@EqualsAndHashCode(exclude = { "id" })
@NoArgsConstructor
@Entity
@Table(name= "SR_QUESTION")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView( {SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class} )
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    private Long id;

    @Transient
    private Q q;
    
    @NotNull
    @JsonProperty
    @JsonView( { AnswerViews.Summary.class, QuestionViews.Summary.class, SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class} )
    @Column(name = "name")
    private String name;
    
    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, QuestionViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "label")
    private String label;

    @NotNull
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "required")
    private boolean required;
    
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "hint")
    @Lob
    protected String hint;

    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "placeholder")
    protected String placeholder;

    @Size(max = 20)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "type")
    protected String type;
    
    @Size(max = 20)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "unit")
    private String unit;

    @Size(max = 50)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "validation")
    private String validation;

    @Size(max = 50)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "source")
    protected String source;

    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "def_val")
    protected String defaultValue;
    
    @Transient
    @JsonProperty("categories")
    @JsonView({ QuestionViews.Detailed.class, AnswerViews.Detailed.class })
    private List<String> categories;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView(QuestionViews.Summary.class)
    private List<Link> links;
    
    public Question q(Q q) {
        this.name = q.name();
        return this;
    }
    
    public Q q() {
        return Q.valueOf(name);
    }
}
