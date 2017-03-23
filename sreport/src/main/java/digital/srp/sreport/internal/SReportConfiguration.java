package digital.srp.sreport.internal;

import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.sreport.model.mixins.LinkMixIn;

@Configuration
@ComponentScan(basePackages = { "digital.srp.sreport" })
@EnableAutoConfiguration
@EntityScan({ "digital.srp.sreport" })
@EnableJpaRepositories({ "digital.srp.sreport" })
@EnableJpaAuditing
public class SReportConfiguration extends RepositoryRestMvcConfiguration {

    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .mixIn(Link.class, LinkMixIn.class).build();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        super.configureMessageConverters(converters);
    }
}