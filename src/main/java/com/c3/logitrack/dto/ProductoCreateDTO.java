package com.c3.logitrack.dto;

public class ProductoCreateDTO {
    private String codigo;
    private String nombre;
    private String categoria;
    private Double precio;
    private Integer stockMin;

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStockMin() { return stockMin; }
    public void setStockMin(Integer stockMin) { this.stockMin = stockMin; }
}