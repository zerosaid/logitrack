package com.c3.logitrack.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", length = 50, unique = true, nullable = false)
    private String codigo;

    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @Column(name = "categoria", length = 255, nullable = false)
    private String categoria;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock_min", nullable = false)
    private int stockMin = 5;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita serialización circular con Stock
    private List<Stock> stocks;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita serialización circular con MovimientoItem
    private List<MovimientoItem> movimientoItems;

    @Transient
    private int stock; // stock total calculado (no persistido directamente)

    // Constructores
    public Producto() {
    }

    public Producto(String codigo, String nombre, String categoria, BigDecimal precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStockMin() {
        return stockMin;
    }

    public void setStockMin(int stockMin) {
        this.stockMin = stockMin;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public List<MovimientoItem> getMovimientoItems() {
        return movimientoItems;
    }

    public void setMovimientoItems(List<MovimientoItem> movimientoItems) {
        this.movimientoItems = movimientoItems;
    }

    // Getter de stock
    public int getStock() {
        if (stocks != null && !stocks.isEmpty()) {
            return stocks.stream()
                    .mapToInt(Stock::getCantidad) // no hay null, int directo
                    .sum();
        }
        return 0; // si no hay stocks
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
