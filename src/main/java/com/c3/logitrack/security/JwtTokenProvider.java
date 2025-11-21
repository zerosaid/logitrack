package com.c3.logitrack.security;

import com.c3.logitrack.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final long EXPIRATION = 30000000;

    // Usar una clave segura generada por Keys.secretKeyFor
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // === GENERAR TOKEN DESDE USER (para login) ===
public String generateToken(User user) {
    return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
}

    // === GENERAR TOKEN DESDE AUTHENTICATION (para JwtAuthFilter) ===
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // === OBTENER USERNAME DEL TOKEN ===
    public String getUsernameFromJwt(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Error al extraer username del token: {}", e.getMessage());
            throw new JwtException("Token inválido", e);
        }
    }

    // === VALIDAR TOKEN ===
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                logger.warn("Token expirado");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }
}