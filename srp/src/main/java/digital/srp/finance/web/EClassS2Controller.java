/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.finance.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.finance.model.EClassS2;
import digital.srp.finance.repositories.EClassS2Repository;
import digital.srp.model.Event;

@Controller
@RequestMapping("/{tenantId}/eclasses")
public class EClassS2Controller {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(EClassS2Controller.class);

    @Autowired
    @Qualifier("objectMapper")
    // TODO would one of these be better? halObjectMapper,_halObjectMapper
    protected ObjectMapper om;

    @Autowired
    protected EClassS2Repository eClassRepo;

    // Use http://shancarter.github.io/mr-data-converter/ to convert to JSON
    // if needed.
    @RequestMapping(method = RequestMethod.POST, value = "/import")
    public final @ResponseBody List<Event> importMultipleFiles(
            @RequestParam MultipartFile... resourceFile) {
        LOGGER.info(String.format("Import data from %1$s resources",
                resourceFile.length));

        List<Event> events = new ArrayList<Event>();
        InputStream is = null;
        for (MultipartFile resource : resourceFile) {
            try {
                LOGGER.info(String.format("Importing file: %1$s",
                        resource.getOriginalFilename()));
                int count = 0;
                is = resource.getInputStream();

                EClassS2[] entities = om.readValue(is, EClassS2[].class);
                for (EClassS2 entity : entities) {
                    if (entity.getCode() == null) {
                        String msg = String.format("No code in %1$s",
                                entity.getCode());
                        LOGGER.error(msg);
                        events.add(new Event(null, msg));
                    } else {
                        eClassRepo.save(entity);
                        count++;
                    }
                    eClassRepo.save(entity);
                }

                String msg = String.format(
                        "UPLOADED: %1$s records from resource %2$s", count,
                        resource.getOriginalFilename());
                events.add(new Event(null, msg));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                String msg = String.format("UPLOAD FAILED: for resource %1$s",
                        resource.getOriginalFilename());
                // events.add(new Event(null, msg));
                throw new RuntimeException(msg, e);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return events;
    }

}
