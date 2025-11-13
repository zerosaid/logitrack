package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public Stock() {}

    public Stock(Bodega bodega, Producto producto, Integer cantidad) {
        this.bodega = bodega;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bodega getBodega() { return bodega; }
    public void setBodega(Bodega bodega) { this.bodega = bodega; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @PreUpdate
    public void preUpdate() { this.fechaActualizacion = LocalDateTime.now(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return Objects.equals(bodega, stock.bodega) && Objects.equals(producto, stock.producto);
    }

    @Override
    public int hashCode() { return Objects.hash(bodega, producto); }
}