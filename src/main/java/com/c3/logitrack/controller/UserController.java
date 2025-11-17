package com.c3.logitrack.controller;

import com.c3.logitrack.dto.LoginRequest;
import com.c3.logitrack.dto.UserCreateDTO;
import com.c3.logitrack.model.User;
import com.c3.logitrack.security.JwtTokenProvider;
import com.c3.logitrack.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Usuarios", description = "Gestión de usuarios. Solo ADMIN.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"}, allowCredentials = "true")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // === LISTAR TODOS LOS USUARIOS ACTIVOS ===
    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        List<User> usuarios = userService.listarTodosActivos();
        usuarios.forEach(u -> {
            u.setPassword(null);
            u.setMovimientos(null); // Evitar ciclos infinitos
        });
        return ResponseEntity.ok(usuarios);
    }

    // === OBTENER USUARIO POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable Long id) {
        return userService.obtenerPorId(id)
                .map(u -> {
                    u.setPassword(null);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === OBTENER USUARIO POR USERNAME ===
    @GetMapping("/buscar/{username}")
    public ResponseEntity<User> buscarPorUsername(@PathVariable String username) {
        return userService.buscarPorUsername(username)
                .map(u -> {
                    u.setPassword(null);
                    return ResponseEntity.ok(u);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === OBTENER USUARIO LOGEADO (desde JWT) ===
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioActual(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return userService.buscarPorUsername(userDetails.getUsername())
                .map(u -> {
                    u.setPassword(null);
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("id", u.getId());
                    respuesta.put("username", u.getUsername());
                    respuesta.put("role", u.getRole().name());
                    respuesta.put("descripcionRol", u.getRoleDescripcion());
                    respuesta.put("activo", u.isActivo());
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === LOGIN DE USUARIO ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.buscarPorUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtTokenProvider.generateToken(authentication);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body("Credenciales incorrectas o error de autenticación: " + e.getMessage());
        }
    }

    // === CREAR NUEVO USUARIO (solo ADMIN) ===
    @PostMapping("/crear/{creadorUsername}")
    public ResponseEntity<?> crearUsuario(@PathVariable String creadorUsername, @RequestBody UserCreateDTO userDTO) {
        try {
            if (userDTO == null) {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud no puede ser nulo");
            }
            User creado = userService.crearUsuario(userDTO, creadorUsername);
            creado.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el usuario: " + e.getMessage());
        }
    }

    // === ACTUALIZAR USUARIO EXISTENTE ===
    @PutMapping("/{id}/{editorUsername}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @PathVariable String editorUsername,
            @RequestBody User detalles) {
        try {
            User actualizado = userService.actualizarUsuario(id, detalles, editorUsername);
            actualizado.setPassword(null);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    // === DESACTIVAR USUARIO ===
    @DeleteMapping("/{id}/{editorUsername}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id, @PathVariable String editorUsername) {
        try {
            userService.desactivarUsuario(id, editorUsername);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al desactivar el usuario: " + e.getMessage());
        }
    }

    // === ELIMINAR USUARIO ===
    @DeleteMapping("/eliminar/{id}/{editorUsername}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, @PathVariable String editorUsername) {
        try {
            userService.eliminarUsuario(id, editorUsername);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    // === ENDPOINT PARA DASHBOARD: CONTEO DE USUARIOS ACTIVOS ===
    @GetMapping("/conteo-activos")
    public ResponseEntity<Map<String, Object>> conteoUsuariosActivos() {
        long totalActivos = userService.listarTodosActivos().size();
        Map<String, Object> response = new HashMap<>();
        response.put("usuariosActivos", totalActivos);
        return ResponseEntity.ok(response);
    }
}