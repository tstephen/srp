package digital.srp.server;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.mvc.RepresentationModelProcessorInvoker;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import digital.srp.server.model.mixins.SrpLinkMixIn;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "digital.srp.server" })
public class SrpConfig extends RepositoryRestMvcConfiguration {
    public SrpConfig(ApplicationContext context,
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
