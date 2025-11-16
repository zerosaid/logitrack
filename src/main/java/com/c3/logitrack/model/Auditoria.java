package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.c3.logitrack.model.enums.TipoOperacion;  

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidad;
    private Long entidadId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOperacion operacion; // INSERT, UPDATE, DELETE

    private String usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Lob
    private String valoresAntes;

    @Lob
    private String valoresDespues;

    // ===== Constructores =====
    public Auditoria() {}

    public Auditoria(String entidad, Long entidadId, TipoOperacion operacion, String usuario, String valoresAntes, String valoresDespues) {
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.operacion = operacion;
        this.usuario = usuario;
        this.valoresAntes = valoresAntes;
        this.valoresDespues = valoresDespues;
        this.fechaHora = LocalDateTime.now();
    }

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public TipoOperacion getOperacion() { return operacion; }
    public void setOperacion(TipoOperacion operacion) { this.operacion = operacion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getValoresAntes() { return valoresAntes; }
    public void setValoresAntes(String valoresAntes) { this.valoresAntes = valoresAntes; }

    public String getValoresDespues() { return valoresDespues; }
    public void setValoresDespues(String valoresDespues) { this.valoresDespues = valoresDespues; }
}
