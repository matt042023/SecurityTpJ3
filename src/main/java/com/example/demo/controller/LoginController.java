package com.example.demo.controller;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import com.example.demo.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserAppRepository userAppRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Value("${jwt.cookie_name}")
    private String cookieName;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isPresent() && passwordEncoder.matches(userApp.getPassword(), userAppOptional.get().getPassword())) {
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtService.createAuthenticationToken(userAppOptional.get()).toString()).body("connected");
        }
        throw new Exception();
    }

    @PostMapping("/register")
    public void register(@RequestBody UserApp userApp) throws Exception {
        Optional<UserApp> userAppOptional = userAppRepository.findByUsername(userApp.getUsername());
        if (userAppOptional.isEmpty()) {

            userAppRepository.save(
                    new UserApp(
                            userApp.getUsername(),
                            passwordEncoder.encode(userApp.getPassword()),
                            userApp.getRole() != null ? userApp.getRole() : "USER"
                    )
            );
        }else{
            throw new Exception();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {
        try {
            // 1. Supprimer le contexte de sécurité
            SecurityContextHolder.clearContext();

            // 2. Invalider la session si elle existe
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            // 3. Créer et envoyer un cookie expiré pour supprimer le JWT
            ResponseCookie expiredCookie = ResponseCookie.from(cookieName, "")
                    .httpOnly(true)
                    .secure(false) // Mettre à true en production avec HTTPS
                    .path("/")
                    .maxAge(0) // Expire immédiatement
                    .sameSite("Lax") // Protection CSRF
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

            // 4. Retourner une réponse JSON pour l'API
            return ResponseEntity.ok()
                    .body(Map.of("message", "Déconnexion réussie", "status", "success"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Erreur lors de la déconnexion", "status", "error"));
        }
    }

}
