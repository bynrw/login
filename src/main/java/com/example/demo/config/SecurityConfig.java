package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Diese Klasse konfiguriert die Sicherheitsaspekte der Anwendung:
 * - Definiert, welche Endpunkte geschützt sind und welche nicht.
 * - Konfiguriert JWT-Authentifizierung, Passwortverschlüsselung und CORS-Einstellungen.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Bean zur Erstellung des JWT-Authentifizierungsfilters, der jede Anfrage abfängt.
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    // Bean für den PasswordEncoder, der Passwörter sicher verschlüsselt.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean für den AuthenticationManager, der die Authentifizierung von Benutzerdaten übernimmt.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Konfiguration der HTTP-Sicherheitsregeln.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Aktiviert CORS und verwendet die eigene Konfiguration.
            .cors().configurationSource(corsConfigurationSource())
            .and()
            // Deaktiviert CSRF, da JWT verwendet wird.
            .csrf().disable()
            // Setzt das Session Management auf STATELESS, da keine serverseitigen Sessions genutzt werden.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // Definiert, welche Endpunkte ohne Authentifizierung zugänglich sind.
            .authorizeHttpRequests()
            // Erlaubt den Zugriff auf alle Endpunkte unter /api/auth/** (z.B. Login, Registrierung).
            .requestMatchers("/api/auth/**").permitAll()
            // Alle anderen Anfragen erfordern eine Authentifizierung.
            .anyRequest().authenticated();

        // Fügt den JWT-Filter vor dem Standard-Authentifizierungsfilter hinzu.
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    // Bean, die die CORS-Konfiguration bereitstellt, um Anfragen von anderen Domains (z.B. React-Frontend) zu erlauben.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Erlaubt Anfragen vom Frontend, das unter localhost:3000 läuft.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Erlaubt gängige HTTP-Methoden.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Erlaubt bestimmte HTTP-Header.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Wendet diese CORS-Regeln auf alle Endpunkte an.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
