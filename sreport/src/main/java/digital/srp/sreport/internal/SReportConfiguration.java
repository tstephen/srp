package digital.srp.sreport.internal;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = { "digital.srp.sreport" })
@EntityScan({ "digital.srp.sreport.model", "io.onedecision.engine.decisions.model",
    "io.onedecision.engine.domain.model" })
@EnableJpaRepositories({ "digital.srp.sreport.repositories",
    "io.onedecision.engine.decisions.repositories",
    "io.onedecision.engine.domain.repositories" })
@EnableJpaAuditing
public class SReportConfiguration {

}