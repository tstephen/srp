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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import digital.srp.sreport.model.Answer;
import digital.srp.sreport.model.Q;
import digital.srp.sreport.model.Question;
import digital.srp.sreport.model.StatusType;
import digital.srp.sreport.model.SurveyReturn;

public class HistoricDataMergerTest {

    private static final String SRC_PERIOD = "2016-17";

    private static final String ELEC_USED = "100.00";
    private static final String ELEC_USED_ALT = "101.01";
    private static HistoricDataMerger svc;

    @BeforeAll
    public static void setUpClass() {
        svc = new HistoricDataMerger();
    }

    @Test
    public void testWhenAnswerMissingAddToReturn() {
        SurveyReturn trgt = new SurveyReturn("SDU-2017-18", "ZZ1", "Draft", "2017-18", (short) 1);
        Set<Answer> answersToImport = new HashSet<Answer>();
        answersToImport.add(new Answer()
                .applicablePeriod(SRC_PERIOD)
                .revision((short) 1)
                .status(StatusType.Published.name())
                .response(ELEC_USED)
                .question(new Question().q(Q.ELEC_USED)));

        long added = svc.merge(answersToImport, trgt);
        assertEquals(1, added);

        Answer trgtA = trgt.answer(SRC_PERIOD, Q.ELEC_USED).get();
        assertEquals(ELEC_USED, trgtA.response());
        assertEquals(StatusType.Draft.name(), trgtA.status());
    }

    @Test
    public void testWhenAnswerPresentDoNotAddToReturn() {
        short returnRevision = (short) 1;
        SurveyReturn trgt = new SurveyReturn("SDU-2017-18", "ZZ1", "Draft", "2017-18", returnRevision);
        trgt.answers().add(new Answer()
                .applicablePeriod(SRC_PERIOD)
                .revision((short) 1)
                .status(StatusType.Draft.name())
                .response(ELEC_USED_ALT)
                .question(new Question().q(Q.ELEC_USED)));

        Set<Answer> answersToImport = new HashSet<Answer>();
        answersToImport.add(new Answer()
                .applicablePeriod(SRC_PERIOD)
                .revision((short) 1)
                .status(StatusType.Published.name())
                .response(ELEC_USED)
                .question(new Question().q(Q.ELEC_USED)));

        long added = svc.merge(answersToImport, trgt);
        assertEquals(0, added);

        Answer trgtA = trgt.answer(SRC_PERIOD, Q.ELEC_USED).get();
        assertEquals(ELEC_USED_ALT, trgtA.response());
        assertEquals(StatusType.Draft.name(), trgtA.status());
        assertEquals((Short) returnRevision, trgtA.revision());
    }

    @Test
    public void testWhenRevisionGreaterThan1MissingAnswerAddedToReturnWithCorrectRevision() {
        short returnRevision = (short) 2;
        SurveyReturn trgt = new SurveyReturn("SDU-2017-18", "ZZ1", "Draft", "2017-18", returnRevision);
        Set<Answer> answersToImport = new HashSet<Answer>();
        answersToImport.add(new Answer()
                .applicablePeriod(SRC_PERIOD)
                .revision((short) 1)
                .status(StatusType.Published.name())
                .response(ELEC_USED)
                .question(new Question().q(Q.ELEC_USED)));

        long added = svc.merge(answersToImport, trgt);
        assertEquals(1, added);

        Answer trgtA = trgt.answer(SRC_PERIOD, Q.ELEC_USED).get();
        assertEquals(ELEC_USED, trgtA.response());
        assertEquals(StatusType.Draft.name(), trgtA.status());
        assertEquals((Short) returnRevision, trgtA.revision());
    }
}
