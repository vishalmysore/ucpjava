package io.github.vishalmysore.ucp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for UCP.
 * Enforces HTTPS and basic authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .requiresChannel(channel -> channel.anyRequest().requiresSecure()) // Force HTTPS
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ucp/**").authenticated() // Require auth for UCP endpoints
                .requestMatchers("/.well-known/ucp").permitAll() // Allow profile access
                .anyRequest().permitAll()
            )
            .httpBasic(httpBasic -> {}); // Enable HTTP Basic auth

        return http.build();
    }
}


