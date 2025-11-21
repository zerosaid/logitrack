package com.c3.logitrack.dto;

import com.c3.logitrack.model.enums.TipoMovimiento;
import java.math.BigDecimal;
import java.util.List;

public class MovimientoCreateDTO {
    private TipoMovimiento tipo;
    private Long bodegaOrigenId;
    private Long bodegaDestinoId;
    private Long usuarioId;
    private List<MovimientoItemDTO> items;

    // Clase interna para los ítems
    public static class MovimientoItemDTO {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    }

    // Getters y Setters
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public Long getBodegaOrigenId() { return bodegaOrigenId; }
    public void setBodegaOrigenId(Long bodegaOrigenId) { this.bodegaOrigenId = bodegaOrigenId; }
    public Long getBodegaDestinoId() { return bodegaDestinoId; }
    public void setBodegaDestinoId(Long bodegaDestinoId) { this.bodegaDestinoId = bodegaDestinoId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public List<MovimientoItemDTO> getItems() { return items; }
    public void setItems(List<MovimientoItemDTO> items) { this.items = items; }
}