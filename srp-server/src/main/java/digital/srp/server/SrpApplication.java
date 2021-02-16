/*******************************************************************************
 *Copyright 2014-2021 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package digital.srp.server;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import digital.srp.macc.MaccConfig;
import digital.srp.sreport.internal.SReportConfiguration;

@SpringBootApplication
@Import({ MaccConfig.class, SReportConfiguration.class })
public class SrpApplication {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(SrpApplication.class);

    @Value("${srp.tomcat.ajp.enabled:false}")
    boolean tomcatAjpEnabled;

    @Value("${srp.tomcat.ajp.port:8080}")
    int ajpPort;

    @Value("${srp.tomcat.ajp.secure:false}")
    boolean ajpSecure;

    @Value("${srp.tomcat.ajp.scheme:http2}")
    String ajpScheme;

    @Value("${srp.cors.allowedMethods:DELETE,GET,HEAD,POST,PUT}")
    String corsMethods;

    @Value("${srp.cors.allowedOrigins:http://localhost:8000}")
    String corsOrigins;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
      return server -> {
        if (server instanceof TomcatServletWebServerFactory && tomcatAjpEnabled) {
            ((TomcatServletWebServerFactory) server).addAdditionalTomcatConnectors(redirectConnector());
        } else {
            LOGGER.info("No AJP connector configured, set srp.tomcat.* to enable");
        }
      };
    }

    private Connector redirectConnector() {
        Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(ajpSecure);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme(ajpScheme);
        LOGGER.info("Enabled AJP connector:");
        LOGGER.info("  port: {}", ajpPort);
        LOGGER.info("  secure: {}", ajpSecure);
        LOGGER.info("  scheme: {}", ajpScheme);
        return ajpConnector;
    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                CorsRegistration reg = registry.addMapping("/**");
                reg.allowedOrigins(corsOrigins.split(","));
                reg.allowedMethods(corsMethods.split(","));
            }

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                // String clientContext = systemConfig().getClientContext();
                String clientContext = "";
                LOGGER.debug("client context set to: " + clientContext);
                // Allegedly sets welcome page though does not appear to be working
                registry.addViewController(clientContext + "/").setViewName("index");
                registry.addViewController("/").setViewName("index.html");

                registry.addRedirectViewController("/api-docs/",
                        "/v2/api-docs?group=restful-api");
                registry.addRedirectViewController(
                        "/api-docs/swagger-resources/configuration/ui",
                        "/swagger-resources/configuration/ui");
                registry.addRedirectViewController(
                        "/api-docs/swagger-resources/configuration/security",
                        "/swagger-resources/configuration/security");
                registry.addRedirectViewController("/api-docs/swagger-resources",
                        "/swagger-resources");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SrpApplication.class, args);
    }

}
