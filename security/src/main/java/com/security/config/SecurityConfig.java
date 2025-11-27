package com.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private IpGeoBlockingFilter ipGeoBlockingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login", "/public/**","/auth/internal/**").permitAll()
                .anyRequest().authenticated()
            )
            // Thứ tự filter: ipGeoBlockingFilter → jwtAuthFilter → UsernamePasswordAuthenticationFilter
            // Đảo ngược: thêm ipGeoBlockingFilter SAU jwtAuthFilter để nó ở đầu danh sách
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(ipGeoBlockingFilter, jwtAuthFilter.getClass());
        
        return http.build();
    }
} 