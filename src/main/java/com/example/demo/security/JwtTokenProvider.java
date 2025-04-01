package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Diese Klasse ist verantwortlich für:
 * - Das Erzeugen von JWTs (JSON Web Tokens) für authentifizierte Benutzer.
 * - Das Auslesen von Informationen aus einem JWT.
 * - Die Validierung eines JWT.
 *
 * Sie verwendet die Bibliothek JJWT.
 */
@Component
public class JwtTokenProvider {

    // Geheimschlüssel, der zum Signieren des JWTs verwendet wird.
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Gültigkeitsdauer des Tokens in Millisekunden.
    @Value("${app.jwt.expiration-in-ms}")
    private int jwtExpirationInMs;

    // UserDetailsService, um Benutzerdetails aus dem Token zu laden.
    private final UserDetailsService userDetailsService;

    // Konstruktor, der UserDetailsService injiziert.
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Generiert ein JWT basierend auf der Authentifizierung.
     * @param authentication Das Authentifizierungsobjekt.
     * @return Ein signiertes JWT als String.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        // Berechnet das Ablaufdatum basierend auf der aktuellen Zeit.
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        // Erzeugt einen geheimen Schlüssel aus dem jwtSecret.
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Baut das JWT, setzt Claims und signiert es.
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Setzt den Benutzernamen als Subjekt.
                .setIssuedAt(new Date())               // Setzt das Ausstellungsdatum.
                .setExpiration(expiryDate)             // Setzt das Ablaufdatum.
                .signWith(key)                         // Signiert das Token mit dem geheimen Schlüssel.
                .compact();
    }

    /**
     * Liest den Benutzernamen aus einem JWT aus.
     * @param token Das JWT.
     * @return Den Benutzernamen, der im Token gespeichert ist.
     */
    public String getUsernameFromJWT(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        // Parst das Token und extrahiert die Claims.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validiert das JWT.
     * @param authToken Das zu validierende JWT.
     * @return true, wenn das Token gültig ist, andernfalls false.
     */
    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            // Versucht, das Token zu parsen.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Bei einem Fehler beim Parsen ist das Token ungültig.
            return false;
        }
    }
    
    /**
     * Erzeugt ein Authentication-Objekt basierend auf einem JWT.
     * @param token Das JWT.
     * @return Ein Authentication-Objekt, das den Benutzer repräsentiert.
     */
    public Authentication getAuthentication(String token) {
        // Lädt die Benutzerdetails anhand des im Token enthaltenen Benutzernamens.
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromJWT(token));
        // Erstellt ein Authentifizierungsobjekt ohne weitere Anmeldeinformationen.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
