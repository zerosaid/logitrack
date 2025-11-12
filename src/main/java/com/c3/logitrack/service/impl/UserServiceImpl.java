package com.c3.logitrack.service.impl;

import com.c3.logitrack.exeption.ResourceNotFoundException;
import com.c3.logitrack.model.User;
import com.c3.logitrack.model.enums.Role;
import com.c3.logitrack.repository.UserRepository;
import com.c3.logitrack.service.UserService;
import com.c3.logitrack.service.AuditoriaService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuditoriaService auditoriaService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, AuditoriaService auditoriaService) {
        this.userRepository = userRepository;
        this.auditoriaService = auditoriaService;
    }
    

    @Override
    public List<User> listarTodos() {
        return userRepository.findAll();
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
    @Transactional
    public User crearUsuario(User usuario, String creadorUsername) {
        // Validar duplicado
        if (userRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        // Validar permisos
        User creador = userRepository.findByUsername(creadorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado."));

        if (creador.getRole() != Role.ADMIN) {
            throw new SecurityException("Solo los administradores pueden crear nuevos usuarios.");
        }

        // Configurar datos
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        if (usuario.getRole() == null) {
            usuario.setRole(Role.EMPLEADO);
        }

        User guardado = userRepository.save(usuario);

        // Auditoría
        auditoriaService.crearAuditoria(
                "User",
                guardado.getId(),
                creadorUsername,
                "Creación de usuario",
                String.format("{\"username\":\"%s\",\"role\":\"%s\"}", guardado.getUsername(), guardado.getRole()),
                creadorUsername
        );

        return guardado;
    }

    @Override
    @Transactional
    public User actualizarUsuario(Long id, User usuarioDetalles, String usernameEditor) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        existente.setNombre(usuarioDetalles.getNombre());
        existente.setEmail(usuarioDetalles.getEmail());
        existente.setRole(usuarioDetalles.getRole());
        existente.setActivo(usuarioDetalles.isActivo());

        if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
        }

        User actualizado = userRepository.save(existente);

        auditoriaService.crearAuditoria(
                "User",
                id,
                usernameEditor,
                "Actualización de usuario",
                "{\"accion\":\"update\"}",
                usernameEditor
        );

        return actualizado;
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id, String usernameEditor) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        user.setActivo(false);
        userRepository.save(user);

        auditoriaService.crearAuditoria(
                "User",
                id,
                usernameEditor,
                "Desactivación de usuario",
                "{\"activo\":false}",
                usernameEditor
        );
    }

    @Override
    public boolean existePorUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}