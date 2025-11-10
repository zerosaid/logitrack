package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidad;
    private Long entidadId;
    private String operacion; // INSERT, UPDATE, DELETE
    private String usuario;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Lob
    private String valoresAntes;

    @Lob
    private String valoresDespues;

    // Getters y Setters
    public String getEntidad() {
        return entidad;
    }
    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }
    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getOperacion() {
        return operacion;
    }
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getValoresAntes() {
        return valoresAntes;
    }
    public void setValoresAntes(String valoresAntes) {
        this.valoresAntes = valoresAntes;
    }

    public String getValoresDespues() {
        return valoresDespues;
    }
    public void setValoresDespues(String valoresDespues) {
        this.valoresDespues = valoresDespues;
    }
}
