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

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class DemoApplicationSecurity extends
        WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**", "/data/**", "/fonts/**", "/images/**",
                        "/js/**", "/webjars/**")
                .permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .antMatchers("/*.html", "/process-instances/**", "/tasks/**",
                        "/users/**")
                .hasRole("user")
                .antMatchers("/admin.html", "/deployments/**",
                        "/process-definitions/**").hasRole("admin")
                .anyRequest().authenticated().and().formLogin()
                .loginPage("/login").failureUrl("/login?error")
                .successHandler(getSuccessHandler()).permitAll().and().csrf()
                .disable().httpBasic();
    }

    private AuthenticationSuccessHandler getSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler(
                "/");
        successHandler.setTargetUrlParameter("redirect");
        return successHandler;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.userDetailsService(activitiUserDetailsService);
        // auth.jdbcAuthentication().dataSource(dataSource)
        // .withDefaultSchema().withUser("user").password("password")
        // .roles("USER").and().withUser("admin").password("password")
        // .roles("USER", "ADMIN");
        auth.inMemoryAuthentication().withUser("user").password("user")
                .roles("user");
    }
}
