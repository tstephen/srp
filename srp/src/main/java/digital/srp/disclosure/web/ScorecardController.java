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
package digital.srp.disclosure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import digital.srp.disclosure.model.Disclosure;
import digital.srp.disclosure.repositories.DisclosureRepository;

@Controller
@RequestMapping("/{tenantId}/scorecard")
public class ScorecardController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ScorecardController.class);

    @Autowired
    protected DisclosureRepository disclosureRepo;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "text/html" })
    public String getScorecardHtml(@PathVariable("id") Long id,
            @PathVariable("tenantId") String tenantId, Model model) {
        LOGGER.info(String.format("Seeking scorecard %1$s for tenant %2$s",
                id, tenantId));

        Disclosure disclosure = disclosureRepo.findOne(id);
        model.addAttribute("disclosure", disclosure);

        return "disclosure";
    }

}
