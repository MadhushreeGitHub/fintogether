package com.fintogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development-profile Spring Security configuration.
 *
 * Behavior in dev:
 *   - Actuator health/info endpoints are public (so smoke tests and CI checks work without auth)
 *   - OpenAPI/Swagger UI paths are public (so Omkar can browse the API contract)
 *   - All other endpoints require authentication via HTTP Basic
 *   - CSRF disabled — this is a stateless REST API, not a browser form-post app
 *   - Sessions are stateless — no JSESSIONID cookies, every request stands alone
 *
 * Phase 2 will introduce the non-dev profile config using JWT bearer tokens.
 */
@Configuration
@Profile("dev")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> {})
                .build();
    }
}