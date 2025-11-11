package com.c3.logitrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.c3.logitrack.model.enums.TipoMovimiento;

@Entity
@Table(name = "movimiento")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo; 

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    @ManyToOne
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;

    private String observaciones;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL)
    private List<MovimientoItem> items;

    // Getters y Setters
    public TipoMovimiento getTipo() {
        return tipo;
    }
    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
