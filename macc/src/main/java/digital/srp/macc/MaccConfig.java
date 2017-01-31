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
package digital.srp.macc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.converter.HttpMessageConverter;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.web.internal.CsvConverter;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "digital.srp.macc" })
@EntityScan({ "digital.srp.macc.model" })
@EnableJpaRepositories({ "digital.srp.macc.repositories" })
public class MaccConfig extends RepositoryRestMvcConfiguration {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(MaccConfig.class);

    @Override
    protected void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config) {
        config.exposeIdsFor(Intervention.class, InterventionType.class,
                OrganisationType.class);
    }

    @Bean
    public HttpMessageConverters customConverters() {
        HttpMessageConverter<?> interventionCsv = new CsvConverter();
        return new HttpMessageConverters(interventionCsv);
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
