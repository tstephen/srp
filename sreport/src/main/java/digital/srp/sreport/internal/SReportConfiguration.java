package digital.srp.sreport.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import digital.srp.sreport.api.Calculator;
import digital.srp.sreport.repositories.CarbonFactorRepository;
import digital.srp.sreport.repositories.WeightingFactorRepository;
import digital.srp.sreport.services.Cruncher;

@Configuration
@ComponentScan(basePackages = { "digital.srp.sreport" })
@EntityScan({ "digital.srp.sreport.model" })
@EnableJpaRepositories({ "digital.srp.sreport.repositories" })
@EnableJpaAuditing
public class SReportConfiguration extends RepositoryRestMvcConfiguration {

    @Autowired
    protected CarbonFactorRepository cFactorRepo;
    
    @Autowired
    protected WeightingFactorRepository wFactorRepo;
    
    @Bean
    public Calculator cruncher() {
        return new Cruncher(cFactorRepo.findAll(), wFactorRepo.findAll());
    }

}