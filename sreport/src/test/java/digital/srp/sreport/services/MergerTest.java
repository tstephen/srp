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
package digital.srp.sreport.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.internal.ClasspathSurveyReturnHelper;
import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.SurveyReturn;

public class MergerTest {

    private static ClasspathSurveyReturnHelper helper;

    @BeforeAll
    public static void setUpClass() throws IOException {
        helper = new ClasspathSurveyReturnHelper();
    }

    @Test
    public void testMergeTwoRevisions() throws IOException {
        SurveyReturn rw5 = helper.readSurveyReturn("RW5")
                .orElseThrow(IllegalStateException::new);
        writeProperties(rw5);

        SurveyReturn old = helper.readSurveyReturn("RW5.fixed")
                .orElseThrow(IllegalStateException::new);
        writeProperties(old);

        merge(rw5, old);
        rw5.name("RW5 merged");
        writeProperties(rw5);
    }

    private void merge(SurveyReturn src, SurveyReturn trgt) {
        ArrayList<String> toAdd = new ArrayList<String>();
        for (Answer a : src.answers()) {
            if (!trgt.answer(a.applicablePeriod(), a.question().q()).isPresent()) {
                System.out.println("Adding "+a.applicablePeriod()+a.question().q());
                toAdd.add(a.applicablePeriod()+" "+a.question().q());
                trgt.answers().add(a);
            }
        }
        System.out.println("Need to add "+toAdd);
    }

    protected void writeProperties(SurveyReturn rtn) throws IOException {
        int count = 0;
        Properties props = new Properties();
        for (Answer a : rtn.answers()) {
            if (a.response() != null && !a.derived()) {
                count++;
//                System.out.println(String.format("%1$s %2$s = %3$s", a.applicablePeriod(), a.question().name(), a.response()));
                props.setProperty(a.applicablePeriod() + "-" + a.question().name(), a.response());
            }
        }
        System.out.println(String.format("Found %1$d answers", count));
        Writer out = new FileWriter(new File("target", rtn.name()+".properties"));
        props.store(out, null);
    }

}
