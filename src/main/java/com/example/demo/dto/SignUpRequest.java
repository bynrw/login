package com.example.demo.dto;

/**
 * Diese Klasse repräsentiert die Registrierungsanfrage.
 * Sie enthält den Benutzernamen, die E-Mail-Adresse und das Passwort.
 */
public class SignUpRequest {
    private String username;
    private String email;
    private String password;

    // Getter und Setter für den Benutzernamen.
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter und Setter für die E-Mail-Adresse.
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter und Setter für das Passwort.
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
