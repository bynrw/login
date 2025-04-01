package com.example.demo.dto;

/**
 * Diese Klasse repräsentiert die Antwort nach einer erfolgreichen Authentifizierung.
 * Sie enthält das JWT und den Token-Typ.
 */
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    // Konstruktor, der das Access Token setzt.
    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter und Setter für das Access Token.
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter und Setter für den Token-Typ.
    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
