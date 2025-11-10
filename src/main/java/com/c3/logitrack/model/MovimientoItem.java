package com.c3.logitrack.model;


import jakarta.persistence.*;

@Entity
@Table(name = "movimiento_item")
public class MovimientoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;

    // Getters y Setters
    public Movimiento getMovimiento() {
        return movimiento;
    }
    public void setMovimiento(Movimiento movimiento) {
        this.movimiento = movimiento;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}