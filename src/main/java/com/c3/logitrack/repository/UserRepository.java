package com.c3.logitrack.repository;

import com.c3.logitrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findAllByActivoTrue(); // Añadido para listar solo usuarios activos
    Optional<User> findByEmail(String email);
}