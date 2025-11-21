package com.c3.logitrack.Tets.dtotest;

import java.util.List;

import com.c3.logitrack.dto.MovimientoCreateDTO.MovimientoItemDTO;
import com.c3.logitrack.Tets.modeltest.enumtest.TipoMovimientosTest;

public class MovimientoDTO {
    private TipoMovimientosTest tipoMovimiento;
    public TipoMovimientosTest getTipoMovimiento() {
        return tipoMovimiento;
    }
    public void setTipoMovimiento(TipoMovimientosTest tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    private Long bodegaOrigenId;
    public Long getBodegaOrigenId() {
        return bodegaOrigenId;
    }
    public void setBodegaOrigenId(Long bodegaOrigenId) {
        this.bodegaOrigenId = bodegaOrigenId;
    }
    private Long bodegaDestinoId;
    public Long getBodegaDestinoId() {
        return bodegaDestinoId;
    }
    public void setBodegaDestinoId(Long bodegaDestinoId) {
        this.bodegaDestinoId = bodegaDestinoId;
    }
    private Long usuarioId;
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    private List<MovimientoItemDTO> items;
    public List<MovimientoItemDTO> getItems() {
        return items;
    }
    public void setItems(List<MovimientoItemDTO> items) {
        this.items = items;
    }
}
