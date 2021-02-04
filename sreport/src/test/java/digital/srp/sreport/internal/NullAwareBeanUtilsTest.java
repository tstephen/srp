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
package digital.srp.sreport.internal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import digital.srp.sreport.model.Question;

public class NullAwareBeanUtilsTest {

    private static final String NAME = "Foo";

    @Test
    public void testCopyNonNullProperties() {
        Question srcBean = new Question();
        srcBean.id(1l);
        srcBean.name(NAME);

        Question trgtBean = new Question();

        NullAwareBeanUtils.copyNonNullProperties(srcBean, trgtBean, "id");

        assertNull(trgtBean.id());
        assertEquals(NAME, trgtBean.name());
    }

    @Test
    public void testTrimStringProperties() {
        Question srcBean = new Question();
        srcBean.id(1l);
        srcBean.name(NAME + " ");

        NullAwareBeanUtils.trimStringProperties(srcBean);

        assertEquals(NAME, srcBean.name());
    }
}
