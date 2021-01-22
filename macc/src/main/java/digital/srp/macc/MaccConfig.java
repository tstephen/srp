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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.mvc.RepresentationModelProcessorInvoker;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.macc.model.Intervention;
import digital.srp.macc.model.InterventionType;
import digital.srp.macc.model.OrganisationType;
import digital.srp.macc.repositories.InterventionTypeRepository;
import digital.srp.macc.web.internal.CsvConverter;

@Configuration
@ComponentScan(basePackages = { "digital.srp.macc" })
@EntityScan({ "digital.srp.macc.model" })
@EnableJpaRepositories({ "digital.srp.macc.repositories" })
public class MaccConfig extends RepositoryRestMvcConfiguration {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(MaccConfig.class);

    public MaccConfig(ApplicationContext context,
            ObjectFactory<ConversionService> conversionService,
            Optional<LinkRelationProvider> relProvider,
            Optional<CurieProvider> curieProvider,
            Optional<HalConfiguration> halConfiguration,
            ObjectProvider<ObjectMapper> objectMapper,
            ObjectProvider<RepresentationModelProcessorInvoker> invoker,
            MessageResolver resolver, GeoModule geoModule) {
        super(context, conversionService, relProvider, curieProvider, halConfiguration,
                objectMapper, invoker, resolver, geoModule);
    }

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
