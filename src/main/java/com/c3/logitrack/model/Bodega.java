package com.c3.logitrack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bodega")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    private String encargado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("bodega-stock")
    private List<Stock> stocks;

    @OneToMany(mappedBy = "bodegaOrigen", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("bodega-movimiento-origen")
    private List<Movimiento> movimientosOrigen;

    @OneToMany(mappedBy = "bodegaDestino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("bodega-movimiento-destino")
    private List<Movimiento> movimientosDestino;

    // Constructores
    public Bodega() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEncargado() { return encargado; }
    public void setEncargado(String encargado) { this.encargado = encargado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }

    public List<Movimiento> getMovimientosOrigen() { return movimientosOrigen; }
    public void setMovimientosOrigen(List<Movimiento> movimientosOrigen) { this.movimientosOrigen = movimientosOrigen; }

    public List<Movimiento> getMovimientosDestino() { return movimientosDestino; }
    public void setMovimientosDestino(List<Movimiento> movimientosDestino) { this.movimientosDestino = movimientosDestino; }
}