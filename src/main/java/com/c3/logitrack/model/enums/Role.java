package com.c3.logitrack.model.enums;

public enum Role {
    ADMIN("Administrador del sistema"),
    EMPLEADO("Empleado operativo");

    private final String descripcion;

    Role(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}