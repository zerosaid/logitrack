package com.c3.logitrack.dto;

import com.c3.logitrack.model.enums.Role;

public class UserCreateDTO {
    private String username;
    private String password;
    private String nombre;
    private String email;
    private Role role;
    private boolean activo;

    // Constructores
    public UserCreateDTO() {}

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}