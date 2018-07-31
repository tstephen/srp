package digital.srp.server;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.server.model.mixins.SrpLinkMixIn;

@Configuration
@ComponentScan(basePackages = { "digital.srp.server" })
public class SrpConfig extends RepositoryRestMvcConfiguration {

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    // DO NOT put this in a reusable configuration to avoid clashes
    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .mixIn(Link.class, SrpLinkMixIn.class)
                .build();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        super.configureMessageConverters(converters);
    }
}
