package com.c3.logitrack.service;

import com.c3.logitrack.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> listarTodos();

    Optional<User> obtenerPorId(Long id);

    Optional<User> buscarPorUsername(String username);

    User crearUsuario(User usuario, String creadorUsername);

    User actualizarUsuario(Long id, User usuarioDetalles, String usernameEditor);

    void desactivarUsuario(Long id, String usernameEditor);

    boolean existePorUsername(String username);
}