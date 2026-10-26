package com.iqvia.quickfix.security;

import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.service.JwtService;
import com.iqvia.quickfix.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Liest den Authorization-Header aus der HTTP-Anfrage.
        String authHeader = request.getHeader("Authorization");

        // Wenn kein Authorization-Header vorhanden ist
        // oder er nicht mit "Bearer " beginnt,
        // wird die Anfrage ohne JWT-Prüfung weitergeleitet.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Entfernt "Bearer " aus dem Header
        // und erhält nur den eigentlichen JWT-Token.
        String token = authHeader.substring(7);

        // Liest den Benutzernamen aus dem JWT.
        String username = jwtService.extractUsername(token);

        // Prüft, ob ein Benutzername vorhanden ist
        // und ob Spring Security den Benutzer noch nicht authentifiziert hat.
        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Lädt den Benutzer aus der Datenbank.
            User user = userService.getUserEntityByUsername(username);

            // Prüft, ob der JWT gültig ist
            // und zum geladenen Benutzer gehört.
            if (jwtService.isTokenValid(token, user)) {

                // Erstellt die Rolle für Spring Security.
                // Beispiel: ROLE_ADMIN, ROLE_SUPPORT oder ROLE_USER.
                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

                // Erstellt das Authentication-Objekt für Spring Security.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                List.of(authority)
                        );

                System.out.println("Authorization Header: " + authHeader);
                System.out.println("Username aus Token: " + username);
                System.out.println("Token gültig: " + jwtService.isTokenValid(token, user));

                // Speichert die Authentifizierung im SecurityContext.
                // Ab diesem Punkt gilt die aktuelle Anfrage als authentifiziert.
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                System.out.println("Authentifiziert als: " +
                        SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }


        // Leitet die Anfrage an den nächsten Filter weiter.
        filterChain.doFilter(request, response);
    }
}