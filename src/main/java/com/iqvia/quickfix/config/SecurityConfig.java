package com.iqvia.quickfix.config;

import com.iqvia.quickfix.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Dieser wird später verwendet, um Benutzername und Passwort zu prüfen.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Definiert die Sicherheitsregeln für die HTTP-Anfragen der Anwendung.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deaktiviert CSRF, da die REST-API später mit JWT arbeitet.
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                // Es wird keine serverseitige Session gespeichert.
                // Jede Anfrage muss sich selbst mit einem JWT authentifizieren.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Legt fest, welche Endpunkte öffentlich sind
                // und welche eine Authentifizierung benötigen.
                .authorizeHttpRequests(auth -> auth

                        // ---------- Öffentlich
                        // Login darf ohne vorherige Anmeldung aufgerufen werden.
                        .requestMatchers("/api/auth/login").permitAll()

                        // Temporärer Test-Endpunkt für den E-Mail-Versand.
                        .requestMatchers("/api/test/**").permitAll()

                        // ---------- Nur ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ---------- ADMIN und SUPPORT -- Ticket-Aktionen
                        .requestMatchers("/api/support/**").hasAnyRole("ADMIN", "SUPPORT")

                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/assign")
                            .hasAnyRole("SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/resolve")
                            .hasAnyRole("SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/reopen")
                            .hasAnyRole("SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/close")
                            .hasAnyRole("SUPPORT", "ADMIN")



                        // ---------- Alle angemeldeten Benutzer
                        .requestMatchers("/api/tickets/**").authenticated()
                        // Alle anderen Anfragen benötigen eine Authentifizierung.
                        .anyRequest().authenticated()
                )

                // Deaktiviert die standardmäßige Spring-Security-Loginseite.
                // Angular hat eine eigene Anmeldeseite.
                .formLogin(form -> form.disable())

                // Deaktiviert HTTP Basic Authentication.
                // Wir verwenden stattdessen JWT.
                .httpBasic(basic -> basic.disable())

                // Führt den JWT-Filter vor dem normalen Username/Password-Filter aus.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // Erstellt die konfigurierte SecurityFilterChain.
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}