package com.c3.logitrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "movimiento_item")
public class MovimientoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    @JsonBackReference("movimiento-item")
    private Movimiento movimiento;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonBackReference("producto-item")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    public MovimientoItem() {}

    public MovimientoItem(Movimiento movimiento, Producto producto, Integer cantidad, Double precioUnitario) {
        this.movimiento = movimiento;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public Movimiento getMovimiento() { return movimiento; }
    public void setMovimiento(Movimiento movimiento) { this.movimiento = movimiento; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    @Transient
    public Double getSubtotal() { return precioUnitario * cantidad; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovimientoItem)) return false;
        MovimientoItem that = (MovimientoItem) o;
        return Objects.equals(movimiento, that.movimiento) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() { return Objects.hash(movimiento, producto); }
}