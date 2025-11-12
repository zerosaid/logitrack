package com.c3.logitrack.controller;

import com.c3.logitrack.model.User;
import com.c3.logitrack.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // === LISTAR TODOS LOS USUARIOS ===
    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        List<User> usuarios = userService.listarTodos();
        usuarios.forEach(u -> u.setPassword(null)); // Ocultamos contraseÃ±a
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

    // === CREAR NUEVO USUARIO (solo ADMIN) ===
    @PostMapping("/crear/{creadorUsername}")
    public ResponseEntity<?> crearUsuario(@PathVariable String creadorUsername, @RequestBody User nuevoUsuario) {
        try {
            User creado = userService.crearUsuario(nuevoUsuario, creadorUsername);
            creado.setPassword(null);
            return ResponseEntity.ok(creado);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el usuario: " + e.getMessage());
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
            return ResponseEntity.internalServerError().body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    // === DESACTIVAR USUARIO ===
    @DeleteMapping("/{id}/{editorUsername}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id, @PathVariable String editorUsername) {
        try {
            userService.desactivarUsuario(id, editorUsername);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al desactivar el usuario: " + e.getMessage());
        }
    }
}