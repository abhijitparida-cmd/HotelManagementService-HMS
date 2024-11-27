package com.hms.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {

    private JWTFilterConfig jwtFilter;

    public SecurityConfig(JWTFilterConfig jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // h(cd)2
        http.csrf()
                .disable()
                .cors()
                .disable();

        // JWTFilter Class
        http.addFilterBefore(jwtFilter, AuthorizationFilter.class);

        // haap
        http.authorizeHttpRequests().anyRequest().permitAll();

        // harpraa
//        http.authorizeHttpRequests()
//                .requestMatchers("/api/v1/users/login", "/api/v1/users/signup-owner", "/api/v1/users/signup-user")
//                .permitAll()
//                .requestMatchers("/api/v1/country").hasAnyRole("OWNER", "ADMIN")
//                .anyRequest()
//                .authenticated();

        // return build
        return http.build();
    }
}
