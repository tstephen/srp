package digital.srp.sreport.internal;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@ComponentScan(basePackages = { "digital.srp.sreport" })
@EnableAutoConfiguration
@EntityScan({ "digital.srp.sreport" })
@EnableJpaRepositories({ "digital.srp.sreport" })
@EnableJpaAuditing
public class SReportConfiguration extends RepositoryRestMvcConfiguration {

}