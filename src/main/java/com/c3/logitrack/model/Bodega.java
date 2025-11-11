package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bodega")
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ubicacion;
    private Double capacidad;
    private String encargado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relación con stock
    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;

    // Relación con movimientos como origen
    @OneToMany(mappedBy = "bodegaOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimiento> movimientosOrigen;

    // Relación con movimientos como destino
    @OneToMany(mappedBy = "bodegaDestino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimiento> movimientosDestino;

    // ===== Getters y Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public List<Movimiento> getMovimientosOrigen() {
        return movimientosOrigen;
    }

    public void setMovimientosOrigen(List<Movimiento> movimientosOrigen) {
        this.movimientosOrigen = movimientosOrigen;
    }

    public List<Movimiento> getMovimientosDestino() {
        return movimientosDestino;
    }

    public void setMovimientosDestino(List<Movimiento> movimientosDestino) {
        this.movimientosDestino = movimientosDestino;
    }
}
