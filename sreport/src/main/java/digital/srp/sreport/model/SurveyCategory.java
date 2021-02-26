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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Container for survey questions.
 *
 * @author Tim Stephenson
 */
@Accessors(fluent=true)
@Data
@ToString(exclude = { "id", "survey" })
@EqualsAndHashCode(exclude = { "id", "survey" })
@NoArgsConstructor
@Entity
@Table(name= "SR_CAT")
public class SurveyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @JsonProperty
    @Column(name = "name")
    private String name;

    @JsonProperty
    @Column(name = "questions")
    @Size(max=1000)
    private String questionNames;

    @JsonProperty
    @Transient
    private List<Question> questions;

    @JsonProperty
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Survey survey;

    public SurveyCategory questionEnums(Q... questions) {
        questionEnums(Arrays.asList(questions));
        return this;
    }

    public SurveyCategory questionEnums(List<Q> questions) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Q> it = questions.iterator(); it.hasNext();) {
            Q q = it.next();
            sb.append(q.code());
            if (it.hasNext()) {
                sb.append(',');
            }
        }
        this.questionNames = sb.toString();
        return this;
    }

    public List<Q> questionEnums() {
        if (questionNames == null) {
            return Collections.emptyList();
        }

        ArrayList<Q> list = new ArrayList<Q>();
        String[] qNames = questionNames.split(",");
        for (String name : qNames) {
            list.add(Q.valueOf(name));
        }
        return list;
    }

    public List<Question> questions() {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        return questions;
    }

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

    public String getQuestionNames() {
        return questionNames;
    }

    public void setQuestionNames(String questionNames) {
        this.questionNames = questionNames;
    }
}
