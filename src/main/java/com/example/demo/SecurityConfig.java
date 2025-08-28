package com.example.demo;

import com.example.demo.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Pages système (erreurs, etc.)
                        .requestMatchers("/error/**", "/error").permitAll()
                        
                        // Ressources statiques
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        
                        // Pages web publiques
                        .requestMatchers("/", "/login", "/register", "/web-register").permitAll()
                        .requestMatchers("/job/**").permitAll()  // Pages détail offres publiques
                        
                        // API REST publiques  
                        .requestMatchers("/auth/login", "/auth/register", "/auth/logout").permitAll()
                        .requestMatchers("/jobs").permitAll()  // Liste des jobs
                        .requestMatchers("/jobs/{id:[\\d+]}").permitAll()  // Détail job spécifique
                        
                        // Endpoints admin spécifiques (AVANT les règles générales)
                        .requestMatchers("/jobs/{id:[\\d+]}/admin").hasRole("ADMIN")
                        .requestMatchers("/hello/private-admin").hasRole("ADMIN")
                        
                        // Autres endpoints publics
                        .requestMatchers("/h2-console/**", "/hello/public").permitAll()
                        
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtService, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
