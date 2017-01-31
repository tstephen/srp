package digital.srp.sreport.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Holds a single survey question.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@NoArgsConstructor
@Entity
@Table(name= "SR_QUESTION")
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;
    
    @NotNull
    @JsonProperty
    private String text;
    
    @NotNull
    @JsonProperty
    private boolean required;
    
    @Size(max = 20)
    @JsonProperty
    private String unit;
    
}
