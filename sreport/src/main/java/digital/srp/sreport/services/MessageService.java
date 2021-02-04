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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MessageService.class);

    public Map<String, String> getAll() {
        return getAll(Locale.UK);
    }

    public Map<String, String> getAll(Locale locale) {
        LOGGER.info("getAll massaged for {}", locale);
        ResourceBundle bundle = ResourceBundle.getBundle("digital.srp.sreport.Messages", locale);
        Map<String, String> map = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        LOGGER.info("Found {} messages", map.size());
        return map;
    }
}
