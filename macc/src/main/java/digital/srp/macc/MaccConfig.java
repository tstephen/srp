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
package digital.srp.macc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.InterventionTypeRepository;

@Configuration
@ComponentScan(basePackages = { "digital.srp.macc" })
@EntityScan({ "digital.srp.macc.model" })
@EnableJpaRepositories({ "digital.srp.macc.repositories" })
public class MaccConfig {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(MaccConfig.class);

    protected void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config) {
        config.exposeIdsFor(Intervention.class, InterventionType.class,
                OrganisationType.class);
    }

    @Bean
    public CommandLineRunner init(
            final InterventionTypeRepository interventionTypeRepo) {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format(
                            "Number of intervention types: %1$d",
                            interventionTypeRepo.count()));
                }
            }
        };

    }
}
