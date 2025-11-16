package com.c3.logitrack.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN("Administrador"),
    EMPLEADO("Empleado");

    private final String descripcion;

    Role(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }
        return Role.valueOf(value.toUpperCase());
    }
}