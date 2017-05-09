package digital.srp.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@ComponentScan(basePackages = { "digital.srp.server" })
@EnableAutoConfiguration
public class SrpConfig extends RepositoryRestMvcConfiguration {
}