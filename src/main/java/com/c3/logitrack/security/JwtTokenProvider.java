package com.c3.logitrack.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}") // Debe definirse en application.properties
    private String jwtSecret;

    @Value("${jwt.expiration}") // En milisegundos, ejemplo: 86400000 = 1 día
    private int jwtExpirationMs;

    // === GENERAR TOKEN ===
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Key key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // === OBTENER USERNAME DEL TOKEN ===
    public String getUsernameFromJwt(String token) {
        try {
            Key key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
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
            Key key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            // Verificar si el token no ha expirado
            if (claims.getBody().getExpiration().before(new Date())) {
                logger.warn("Token expirado para username: {}", claims.getBody().getSubject());
                return false;
            }
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.error("Token no soportado: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Token mal formado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.error("Firma inválida del token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Token vacío o nulo: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al validar token: {}", e.getMessage());
            return false;
        }
    }
}