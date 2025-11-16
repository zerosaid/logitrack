package com.c3.logitrack.service;

import com.c3.logitrack.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<User> listarTodosActivos(); // Solo usuarios activos
    Optional<User> obtenerPorId(Long id);
    Optional<User> buscarPorUsername(String username);
    User crearUsuario(User user, String creadorUsername) throws SecurityException, IllegalArgumentException;
    User actualizarUsuario(Long id, User user, String editorUsername) throws Exception;
    void desactivarUsuario(Long id, String editorUsername) throws SecurityException, Exception;
    void eliminarUsuario(Long id, String editorUsername) throws SecurityException, Exception;
    Optional<User> findByEmail(String email); 
}