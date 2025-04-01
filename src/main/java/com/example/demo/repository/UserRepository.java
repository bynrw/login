package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Dieses Interface ermöglicht den Datenbankzugriff auf Benutzer.
 * Es erweitert JpaRepository und stellt CRUD-Methoden zur Verfügung.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Sucht einen Benutzer anhand des Benutzernamens.
    Optional<User> findByUsername(String username);
    
    // Sucht einen Benutzer anhand der E-Mail-Adresse.
    Optional<User> findByEmail(String email);
    
    // Prüft, ob ein Benutzer mit dem angegebenen Benutzernamen existiert.
    Boolean existsByUsername(String username);
    
    // Prüft, ob ein Benutzer mit der angegebenen E-Mail existiert.
    Boolean existsByEmail(String email);
}
