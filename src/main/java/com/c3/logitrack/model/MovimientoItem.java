package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "movimiento_item")
public class MovimientoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_id", nullable = false)
    private Movimiento movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal precioUnitario;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Movimiento getMovimiento() { return movimiento; }
    public void setMovimiento(Movimiento movimiento) { this.movimiento = movimiento; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}