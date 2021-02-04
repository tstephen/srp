/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.sreport.model;

import java.util.Arrays;
import java.util.Collections;
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
@ToString
@EqualsAndHashCode(exclude = { "id" })
//@JsonIgnoreProperties(value={ "optionNamesAsList" }, allowGetters=true)
@NoArgsConstructor
@Entity
@Table(name= "SR_QUESTION")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @JsonView( {SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class} )
    @Column(name = "id", columnDefinition = "int(11) NOT NULL")
    protected Long id;

    @Transient
    protected Q q;

    @NotNull
    @JsonProperty
    @JsonView( { AnswerViews.Summary.class, QuestionViews.Summary.class, SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class } )
    @Column(name = "name")
    protected String name;

    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, QuestionViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "label")
    protected String label;

    @NotNull
    @JsonProperty
    @JsonView({ AnswerViews.Summary.class, QuestionViews.Summary.class, SurveyViews.Detailed.class, SurveyReturnViews.Detailed.class })
    @Column(name = "required")
    protected boolean required;

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
    @JsonView({ QuestionViews.Summary.class, SurveyViews.Detailed.class, SurveyReturnViews.Summary.class })
    @Column(name = "type")
    protected String type;

    @Size(max = 20)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "unit")
    protected String unit;

    @Size(max = 50)
    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "validation")
    protected String validation;

    @Size(max = 50)
    @JsonProperty
    @JsonView({ QuestionViews.Summary.class, SurveyViews.Detailed.class })
    @Column(name = "source")
    protected String source;

    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "def_val")
    protected String defaultValue;

    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    @Column(name = "options")
    protected String optionNames;

    @JsonProperty
    @JsonView({ QuestionViews.Detailed.class })
    @Column(name = "tenant_id")
    protected String tenantId;

    @Transient
    @JsonProperty("categories")
    @JsonView({ QuestionViews.Detailed.class, AnswerViews.Detailed.class })
    protected List<String> categories;

    @Transient
    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    @JsonView(QuestionViews.Summary.class)
    protected List<Link> links;

    public Question q(Q q) {
        this.name = q.name();
        return this;
    }

    public Q q() {
        return Q.valueOf(name);
    }

    @JsonProperty("optionNames")
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    public List<String> optionNames() {
        if (optionNames == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(optionNames.split(","));
        }
    }

    @JsonProperty("optionNames")
    @JsonView({ QuestionViews.Detailed.class, SurveyViews.Detailed.class })
    public Question optionNames(List<String> optionNames) {
        this.optionNames = String.join(",", optionNames);
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Q getQ() {
        return q;
    }

    public void setQ(Q q) {
        this.q = q;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOptionNames() {
        return optionNames;
    }

    public void setOptionNames(String optionNames) {
        this.optionNames = optionNames;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
