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
package digital.srp.server;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.knowprocess.auth.AuthConfig;
import com.knowprocess.bpm.BpmConfiguration;

import digital.srp.macc.MaccConfig;
import digital.srp.sreport.internal.SReportConfiguration;
import io.onedecision.engine.OneDecisionConfig;
import io.onedecision.engine.domain.OneDecisionDomainConfig;
import link.omny.acctmgmt.AcctMgmtConfig;
import link.omny.acctmgmt.model.SystemConfig;
import link.omny.catalog.CatalogConfig;
import link.omny.custmgmt.CustMgmtConfig;
import link.omny.supportservices.SupportServicesConfig;

// See https://github.com/spring-projects/spring-boot/issues/6529 for alternative if JMX needed
//@EnableAutoConfiguration(exclude = { EndpointMBeanExportAutoConfiguration.class })
@Configuration
@Import({ 
        AuthConfig.class, OneDecisionConfig.class, OneDecisionDomainConfig.class,
        AcctMgmtConfig.class, BpmConfiguration.class, 
        CatalogConfig.class, CustMgmtConfig.class,
        MaccConfig.class, SReportConfiguration.class, SrpConfig.class,
        SupportServicesConfig.class })
public class Application extends WebMvcConfigurerAdapter {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(Application.class);

    @Value("${srp.tomcat.ajp.enabled:false}")
    boolean tomcatAjpEnabled;

    @Value("${srp.tomcat.ajp.port:8080}")
    int ajpPort;

    @Value("${srp.tomcat.ajp.secure:false}")
    boolean ajpSecure;

    @Value("${srp.tomcat.ajp.scheme:http2}")
    String ajpScheme;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        if (tomcatAjpEnabled) {
            Connector ajpConnector = new Connector("AJP/1.3");
            ajpConnector.setProtocol("AJP/1.3");
            ajpConnector.setPort(ajpPort);
            ajpConnector.setSecure(ajpSecure);
            ajpConnector.setAllowTrace(false);
            ajpConnector.setScheme(ajpScheme);
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
            LOGGER.info("Enabled AJP connector:");
            LOGGER.info("  port: {}", ajpPort);
            LOGGER.info("  secure: {}", ajpSecure);
            LOGGER.info("  scheme: {}", ajpScheme);
        } else {
            LOGGER.info("No AJP connector configured, set srp.tomcat.* to enable");
        }

        return tomcat;
    }
    
    @Bean
    public SystemConfig systemConfig() {
        return new SystemConfig();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // String clientContext = systemConfig().getClientContext();
        String clientContext = "";
        LOGGER.debug("client context set to: " + clientContext);
        // Allegedly sets welcome page though does not appear to be working
        registry.addViewController(clientContext + "/").setViewName("index");
        registry.addViewController(clientContext + "/login").setViewName(
                "login");
        registry.addViewController(clientContext + "/error").setViewName(
                "loginPage?error");
    }

//    @Bean
//    public WebSecurityConfigurerAdapter applicationSecurity() {
//        return new ActivitiApplicationSecurity();
//        // return new SrpActivitiApplicationSecurity();
//    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
