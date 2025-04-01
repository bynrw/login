package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Diese Klasse implementiert die Schnittstelle UserDetailsService.
 * Sie wird von Spring Security genutzt, um Benutzerdetails anhand des Benutzernamens zu laden.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Lädt den Benutzer anhand des Benutzernamens.
     * @param username Der Benutzername, nach dem gesucht wird.
     * @return Ein UserDetails-Objekt mit den Benutzerdaten.
     * @throws UsernameNotFoundException, wenn der Benutzer nicht gefunden wird.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Sucht den Benutzer in der Datenbank.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Gibt ein UserDetails-Objekt zurück (ohne zusätzliche Rollen/Berechtigungen).
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                new ArrayList<>());
    }
}
