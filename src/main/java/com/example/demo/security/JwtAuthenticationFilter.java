package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Dieser Filter wird bei jeder HTTP-Anfrage ausgeführt, um zu prüfen, ob ein gültiges JWT im Request vorhanden ist.
 * Bei einem gültigen Token wird der Benutzer im Security Context authentifiziert.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider tokenProvider;

    // Konstruktor, der den JwtTokenProvider setzt.
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Wird für jede Anfrage aufgerufen.
     * Extrahiert das JWT, prüft dessen Gültigkeit und setzt die Authentifizierung, wenn das Token gültig ist.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extrahiert das JWT aus dem Authorization-Header.
            String jwt = getJwtFromRequest(request);

            // Wenn ein Token vorhanden ist und gültig ist, wird die Authentifizierung gesetzt.
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Fehler werden geloggt.
            logger.error("Could not set user authentication in security context", ex);
        }

        // Weitergabe der Anfrage an den nächsten Filter in der Kette.
        filterChain.doFilter(request, response);
    }

    /**
     * Extrahiert das JWT aus dem Authorization-Header.
     * @param request Die eingehende HTTP-Anfrage.
     * @return Das JWT als String, falls vorhanden, sonst null.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Prüft, ob der Header vorhanden ist und mit "Bearer " beginnt.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Entfernt das "Bearer "-Präfix.
        }
        return null;
    }
}
