package com.nickthecloudguy.identity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class AadOAuth2LoginSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.apply(AadResourceServerHttpSecurityConfigurer.aadResourceServer())
                .and()
            // All the paths that match `/api/**`(configurable) work as the resource server. Other paths work as the web application.
            .securityMatcher("/api/**")
            .authorizeHttpRequests()
                .anyRequest().authenticated();
        return http.build();
    }

    /**
     * Add configuration logic as needed.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.apply(AadWebApplicationHttpSecurityConfigurer.aadWebApplication())
                .and()
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .logout(logout -> logout
                            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                            .clearAuthentication(true)
                            .invalidateHttpSession(true));
        // Do some custom configuration.
        return http.build();
    }
}
