package com.c3.logitrack.service.impl;

import com.c3.logitrack.dto.UserCreateDTO;
import com.c3.logitrack.model.User;
import com.c3.logitrack.repository.UserRepository;
import com.c3.logitrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> listarTodosActivos() {
        return userRepository.findAllByActivoTrue();
    }

    @Override
    public Optional<User> obtenerPorId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> buscarPorUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User crearUsuario(UserCreateDTO userDTO, String creadorUsername) throws SecurityException, IllegalArgumentException {
        if (userDTO == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (userDTO.getRole() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        User creador = buscarPorUsername(creadorUsername)
                .orElseThrow(() -> new SecurityException("Creador no encontrado"));
        if (!"ADMIN".equals(creador.getRole().name())) {
            throw new SecurityException("Solo administradores pueden crear usuarios");
        }

        if (buscarPorUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya está en uso");
        }
        if (findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setNombre(userDTO.getNombre());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActivo(true);
        user.setFechaRegistro(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User actualizarUsuario(Long id, User user, String editorUsername) throws Exception {
        User existente = obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User editor = buscarPorUsername(editorUsername)
                .orElseThrow(() -> new SecurityException("Editor no encontrado"));
        if (!"ADMIN".equals(editor.getRole().name()) && !editor.getUsername().equals(existente.getUsername())) {
            throw new SecurityException("No tienes permiso para actualizar este usuario");
        }

        existente.setNombre(user.getNombre());
        existente.setEmail(user.getEmail());
        existente.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existente);
    }

    @Override
    public void desactivarUsuario(Long id, String editorUsername) throws SecurityException, Exception {
        User usuario = obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User editor = buscarPorUsername(editorUsername)
                .orElseThrow(() -> new SecurityException("Editor no encontrado"));
        if (!"ADMIN".equals(editor.getRole().name()) && !editor.getUsername().equals(usuario.getUsername())) {
            throw new SecurityException("No tienes permiso para desactivar este usuario");
        }
        usuario.setActivo(false);
        userRepository.save(usuario);
    }

    @SuppressWarnings("unused")
    @Override
    public void eliminarUsuario(Long id, String editorUsername) throws SecurityException, Exception {
        User usuario = obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        User editor = buscarPorUsername(editorUsername)
                .orElseThrow(() -> new SecurityException("Editor no encontrado"));
        if (!"ADMIN".equals(editor.getRole().name())) {
            throw new SecurityException("Solo administradores pueden eliminar usuarios");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}