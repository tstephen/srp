package digital.srp.sreport.model;

import java.math.BigDecimal;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@ToString(exclude = { "id", "surveyReturn" })
@EqualsAndHashCode(exclude = { "id", "surveyReturn" })
@NoArgsConstructor
@Entity
@Table(name= "SR_ANSWER")
public class SurveyAnswer {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyAnswer.class);

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
    
    public String response3sf() {
        return SignificantFiguresFormat.format(new BigDecimal(response));
    }

    public SurveyAnswer question(SurveyQuestion q) {
        question = q;
        return this;
    }
    
    public SurveyAnswer question(Q q) {
        question = new SurveyQuestion().name(q.name());
        return this;
    }

    public String response() {
        return typeConversion(response);
    }
    
    public SurveyAnswer response(String response) {
        this.response = typeConversion(response);
        return this;
    }

    private String typeConversion(String response) {
        // TODO l10n of decimal separator
        try {
            if ("number".equals(question.type()) && response.contains(",")) {
                response = response.replaceAll(",", "");
            }
        } catch (NullPointerException e) {
            if (response == null) { 
                ; // ok
            } else {
                LOGGER.error("NPE in typeConversion of response {}", response);
            }
        }
        return response;
    }
    
    public SurveyAnswer response(BigDecimal decimal) {
        response = decimal.toPlainString();
        return this;
    }
    
}
