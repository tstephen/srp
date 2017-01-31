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
package digital.srp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = { "digital.srp" })
@EnableAutoConfiguration
@EntityScan({ "digital.srp.disclosure.model", "digital.srp.finance.model" })
@EnableJpaRepositories(basePackages = { "digital.srp.disclosure.repositories",
        "digital.srp.finance.repositories" })
public class SrpConfig {

    @Value("${omny.populator.skip:true}")
    protected boolean skipPopulator;

    @Value("${srp.populator.json:false}")
    protected boolean runJsonPopulator;

    @Value("${srp.populator.csv:false}")
    protected boolean runCsvPopulator;

    // @Bean
    // public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
    // Jackson2RepositoryPopulatorFactoryBean factory = new
    // Jackson2RepositoryPopulatorFactoryBean();
    // // Set a custom ObjectMapper if Jackson customisation is needed
    // // factory.setObjectMapper(…);
    // if (runJsonPopulator) {
    // factory.setResources(new Resource[] { new ClassPathResource(
    // "data.json") });
    // } else {
    // LOGGER.warn("Configured to skip repository population from JSON, change this by setting srp.populator.json=true in application.properties");
    // factory.setResources(new Resource[0]);
    // }
    // return factory;
    // }
    //
    // @Bean
    // public MergingJsonPopulatorFactoryBean jsonPopulator() {
    // MergingJsonPopulatorFactoryBean factory = new
    // MergingJsonPopulatorFactoryBean();
    // if (runJsonPopulator) {
    // factory.setResources(new Resource[] {
    // new ClassPathResource("data/codes.json"),
    // new ClassPathResource("data/eclasss2.json")
    // // new ClassPathResource("data/eclasss2-probs.json")
    // });
    // } else {
    // LOGGER.warn("Configured to skip repository population from JSON, change this by setting srp.populator.json=true in application.properties");
    // factory.setResources(new Resource[0]);
    // }
    // return factory;
    // }

    // @Bean
    // public CsvPopulatorFactoryBean csvRepositoryPopulator() {
    // CsvPopulatorFactoryBean factory = new CsvPopulatorFactoryBean();
    // if (runCsvPopulator) {
    // factory.setResources(new Resource[] { new ClassPathResource(
    // "/data/barts-2013-14.csv") });
    // } else {
    // LOGGER.warn("Configured to skip repository population from CSV, change this by setting srp.populator.csv=false in application.properties");
    // factory.setResources(new Resource[0]);
    // }
    // return factory;
    // }
}
