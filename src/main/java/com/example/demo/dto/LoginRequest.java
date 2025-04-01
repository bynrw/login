package com.example.demo.dto;

/**
 * Diese Klasse repräsentiert die Login-Anfrage.
 * Sie enthält den Benutzernamen und das Passwort.
 */
public class LoginRequest {
    private String username;
    private String password;

    // Getter und Setter für den Benutzernamen.
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter und Setter für das Passwort.
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
