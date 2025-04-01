package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.dto.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Der AuthController stellt Endpunkte für:
 * - Benutzer-Login (/login)
 * - Registrierung (/register)
 * - Abrufen der aktuellen Benutzerdaten (/me)
 * - Logout (/logout)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Authentifiziert den Benutzer anhand von Benutzername und Passwort.
     * Bei erfolgreicher Authentifizierung wird ein JWT zurückgegeben.
     *
     * @param loginRequest Enthält den Benutzernamen und das Passwort.
     * @return Ein JWT-Token als Antwort.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Authentifizierung des Benutzers mittels Username und Passwort.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Setzt die Authentifizierung in den Security Context.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generiert das JWT anhand der Authentifizierung.
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    /**
     * Registriert einen neuen Benutzer.
     * Überprüft, ob der Benutzername oder die E-Mail bereits existieren.
     *
     * @param signUpRequest Enthält Registrierungsinformationen.
     * @return Erfolgsmeldung oder Fehlermeldung bei Duplikaten.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        // Prüft, ob der Benutzername bereits vergeben ist.
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Prüft, ob die E-Mail bereits verwendet wird.
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>("Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        // Erstellt einen neuen Benutzer mit verschlüsseltem Passwort.
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        // Speichert den neuen Benutzer in der Datenbank.
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Gibt die Informationen des aktuell authentifizierten Benutzers zurück.
     *
     * @param authentication Das aktuelle Authentifizierungsobjekt.
     * @return Benutzerinformationen oder eine Fehlermeldung, wenn nicht authentifiziert.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Ermittelt den Benutzernamen des aktuell angemeldeten Benutzers.
            String username = authentication.getName();
            // Sucht den Benutzer in der Datenbank.
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Erstellt eine Antwort mit Benutzerinformationen.
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        }
        
        // Falls der Benutzer nicht authentifiziert ist.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    /**
     * Meldet den Benutzer ab, indem der Security Context geleert wird.
     *
     * @return Erfolgsmeldung.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Entfernt alle Authentifizierungsdaten.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
