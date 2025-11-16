package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.User;
import com.c3.logitrack.repository.UserRepository;
import com.c3.logitrack.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public User crearUsuario(User user, String creadorUsername) throws SecurityException, IllegalArgumentException {
        // Verificar que el creador sea ADMIN
        User creador = buscarPorUsername(creadorUsername)
                .orElseThrow(() -> new SecurityException("Creador no encontrado"));
        if (!"ADMIN".equals(creador.getRole().name())) {
            throw new SecurityException("Solo administradores pueden crear usuarios");
        }

        // Validar que username y email no existan
        if (buscarPorUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya está en uso");
        }
        if (findByEmail(user.getEmail()).isPresent()) { // Usar findByEmail directamente
            throw new IllegalArgumentException("El email ya está en uso");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivo(true); // Por defecto activo
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
        return userRepository.findByEmail(email); // Delegar al repositorio
    }
}