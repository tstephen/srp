package digital.srp.sreport.model;

import java.math.BigDecimal;
import java.util.Date;
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

import digital.srp.sreport.model.views.CarbonFactorViews;
import digital.srp.sreport.model.views.SurveyViews;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id" })
@EqualsAndHashCode(exclude = { "id", "created", "createdBy", "lastUpdated", "updatedBy" })
@NoArgsConstructor
@Entity
@Table(name= "SR_CFACTOR")
public class CarbonFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "name")
    private String name;
    
    @NotNull
    @Size(max = 60)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "category")
    private String category;
    
    @Size(max = 255)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "comments")
    private String comments;
    
    @Size(max = 255)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "source")
    private String source;
    
    @NotNull
    @Size(max = 15)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "unit")
    private String unit;
    
    @Size(max = 1)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "scope")
    private String scope;
    
    @NotNull
    @Size(max = 7)
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "period")
    private String applicablePeriod;
    
    @NotNull
    @JsonProperty
    @JsonView(CarbonFactorViews.Summary.class)
    @Column(name = "val", precision = 9, scale = 6)
    private BigDecimal value;
    
    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView(SurveyViews.Summary.class)
    private List<Link> links;
    
    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    private Date created;
 
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
 
    @Column(name = "last_updated")
    @LastModifiedDate
    private Date lastUpdated;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getApplicablePeriod() {
        return applicablePeriod;
    }

    public void setApplicablePeriod(String applicablePeriod) {
        this.applicablePeriod = applicablePeriod;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
