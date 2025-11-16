package com.c3.logitrack.controller;

import com.c3.logitrack.dto.JwtResponse;
import com.c3.logitrack.dto.LoginRequest;
import com.c3.logitrack.model.User;
import com.c3.logitrack.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);

            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getRole().name()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body("Credenciales incorrectas o error de autenticación: Bad credentials");
        }
    }
}