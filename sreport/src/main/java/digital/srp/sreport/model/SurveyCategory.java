package digital.srp.sreport.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Container for survey questions.
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@NoArgsConstructor
@Entity
@Table(name= "SR_CAT")
public class SurveyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;
    
    @NotNull
    @Size(max = 50)
    @JsonProperty
    private String name;
    
    @JsonProperty
    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL)
    private List<SurveyQuestion> questions;
    
}
