package digital.srp.sreport.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * A single question's response.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@NoArgsConstructor
@Entity
@Table(name= "SR_ANSWER")
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    @ManyToOne
    private SurveyQuestion question;

    @JsonProperty
    private String response;
    
}
