package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.c3.logitrack.model.enums.Role;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nombre;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean activo = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "usuario")
    private List<Movimiento> movimientos;

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<Movimiento> getMovimientos() { return movimientos; }
    public void setMovimientos(List<Movimiento> movimientos) { this.movimientos = movimientos; }

    public String getRoleDescripcion() {
        return role != null ? role.getDescripcion() : "Sin rol";
    }
}