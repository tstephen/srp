package digital.srp.sreport.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

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
@ToString(exclude = { "id", "surveyReturn" })
@EqualsAndHashCode(exclude = { "id", "surveyReturn" })
@NoArgsConstructor
@Entity
@Table(name= "SR_ANSWER")
public class SurveyAnswer {

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
    private SurveyQuestion question;
    
    @JsonProperty
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="survey_return_id")
    private SurveyReturn surveyReturn;
    
}
