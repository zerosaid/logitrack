package com.c3.logitrack.model;

import com.c3.logitrack.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movimiento")
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false)
    private TipoMovimiento tipo; // Cambiado de String a TipoMovimiento

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoItem> items;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoMovimiento getTipo() { return tipo; } // Cambiado el tipo de retorno
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; } // Cambiado el parámetro
    public Bodega getBodegaOrigen() { return bodegaOrigen; }
    public void setBodegaOrigen(Bodega bodegaOrigen) { this.bodegaOrigen = bodegaOrigen; }
    public Bodega getBodegaDestino() { return bodegaDestino; }
    public void setBodegaDestino(Bodega bodegaDestino) { this.bodegaDestino = bodegaDestino; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public List<MovimientoItem> getItems() { return items; }
    public void setItems(List<MovimientoItem> items) { this.items = items; }
}