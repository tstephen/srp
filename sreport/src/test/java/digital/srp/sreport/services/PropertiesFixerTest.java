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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import digital.srp.sreport.internal.StringUtils;

public class PropertiesFixerTest {

    @Test
    public void test() {
        try (InputStream is = getClass().getResourceAsStream("/digital/srp/sreport/Messages.properties");
                OutputStream os = new FileOutputStream(new File("target", "messages.properties"))) {
            Properties props = new Properties();

            props.load(is);

            SortedProperties sp = new SortedProperties();
            sp.putAll(props);
            sp.store(os, "");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private class SortedProperties extends Properties {
        private static final long serialVersionUID = 823027721880514243L;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Enumeration keys() {
            Enumeration<Object> keysEnum = super.keys();
            Vector<String> keyList = new Vector<String>();
            while (keysEnum.hasMoreElements()) {
                keyList.add(StringUtils.camelCaseToConst((String) keysEnum.nextElement()));
            }
            Collections.sort(keyList);
            return keyList.elements();
        }

    }

}
