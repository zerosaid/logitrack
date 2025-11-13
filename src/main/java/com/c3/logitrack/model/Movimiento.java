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
    @Column(nullable = false)
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

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoItem> items;

    // ===== Getters y Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Bodega getBodegaOrigen() { return bodegaOrigen; }
    public void setBodegaOrigen(Bodega bodegaOrigen) { this.bodegaOrigen = bodegaOrigen; }

    public Bodega getBodegaDestino() { return bodegaDestino; }
    public void setBodegaDestino(Bodega bodegaDestino) { this.bodegaDestino = bodegaDestino; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public List<MovimientoItem> getItems() { return items; }
    public void setItems(List<MovimientoItem> items) { this.items = items; }
}
