package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Diese Klasse repräsentiert einen Benutzer in der Anwendung.
 * Sie wird als JPA-Entity in der Datenbank gespeichert.
 */
@Entity
@Table(name = "users")
public class User {
    
    // Eindeutige ID des Benutzers, automatisch generiert.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Benutzername, der eindeutig sein muss.
    @Column(nullable = false, unique = true)
    private String username;
    
    // E-Mail-Adresse, die eindeutig sein muss.
    @Column(nullable = false, unique = true)
    private String email;
    
    // Passwort des Benutzers.
    @Column(nullable = false)
    private String password;
    
    // Standardkonstruktor für JPA.
    public User() {
    }
    
    // Konstruktor, um einen neuen Benutzer mit allen erforderlichen Informationen zu erstellen.
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Getter und Setter für die ID.
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    // Überschreibt equals, um Benutzer anhand von id, username und email zu vergleichen.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }
    
    // Überschreibt hashCode.
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
    
    // Überschreibt toString, um eine stringbasierte Darstellung ohne das Passwort zu liefern.
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
