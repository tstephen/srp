package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.hateoas.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import digital.srp.sreport.model.views.SurveyViews;
import digital.srp.sreport.model.views.WeightingFactorViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id" })
@EqualsAndHashCode(exclude = { "id", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@Table(name= "SR_WFACTOR")
public class WeightingFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "org_type")
    private String orgType;
    
    @NotNull
    @Size(max = 60)
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "category")
    private String category;
    
    /**
     * Currently only done once in 2014-15
     */
    @NotNull
    @Size(max = 7)
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "period")
    private String applicablePeriod;
    
    /**
     * Volume of carbon emissions in kgCO<sub>2</sub>e.
     */
    @NotNull
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "c_val", precision = 12, scale = 0)
    private BigDecimal carbonValue;
  
    /**
     * Value of spend in £s.
     */
    @NotNull
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "m_val", precision = 12, scale = 0)
    private BigDecimal moneyValue;

    
    /**
     * Proportion of total spending spent on this category (derived empirically).
     */
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "p_val", precision = 6, scale = 3)
    private BigDecimal proportionOfTotal;
    
    /**
     * Intensity of carbon emissions in kgCO<sub>2</sub>e / £.
     */
    @JsonProperty
    @JsonView(WeightingFactorViews.Summary.class)
    @Column(name = "i_val", precision = 6, scale = 3)
    private BigDecimal intensityValue;
    
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

    public BigDecimal intensityValue() {
        if (carbonValue == null || moneyValue == null || moneyValue.intValue() == 0) {
            return BigDecimal.ZERO;
        }
        return carbonValue.divide(moneyValue,3, java.math.RoundingMode.HALF_UP);
    }

    public void intensityValue(BigDecimal intensityValue) {
        //this.intensityValue = intensityValue;
    }

}
