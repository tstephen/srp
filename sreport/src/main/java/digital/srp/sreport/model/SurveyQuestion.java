package digital.srp.sreport.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

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
@ToString(exclude = { "id", "category" })
@EqualsAndHashCode(exclude = { "id", "category" })
@NoArgsConstructor
@Entity
@Table(name= "SR_QUESTION")
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView( {SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class} )
    @Column(name = "id")
    private long id;
    
    @NotNull
    @JsonProperty
    @JsonView( {SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class} )
    @Column(name = "name")
    private String name;
    
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "label")
    private String label;
    
    @NotNull
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "required")
    private boolean required;
    
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "hint")
    protected String hint;
    
    @Size(max = 20)
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "type")
    protected String type;
    
    @Size(max = 20)
    @JsonProperty
    @JsonView(SurveyViews.Detailed.class)
    @Column(name = "unit")
    private String unit;
//
//    @JsonProperty
//    @Transient
//    private Long categoryId;

    @JsonProperty
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SurveyCategory category;
    
//    public SurveyQuestion category(SurveyCategory category) { 
//        this.category = category; 
////        this.categoryId = category.id();
//        return this;
//    }
//    
}
