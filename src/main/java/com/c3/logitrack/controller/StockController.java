package com.c3.logitrack.controller;

import com.c3.logitrack.model.Stock;
import com.c3.logitrack.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // === LISTAR TODOS LOS REGISTROS DE STOCK ===
    @GetMapping
    public ResponseEntity<List<Stock>> listarTodos() {
        List<Stock> stocks = stockService.listarTodos();

        // Limpiar relaciones para evitar ciclos JSON
        stocks.forEach(s -> {
            if (s.getBodega() != null) s.getBodega().setMovimientosOrigen(null);
            if (s.getBodega() != null) s.getBodega().setMovimientosDestino(null);
            if (s.getProducto() != null) s.getProducto().setStocks(null);
        });

        return ResponseEntity.ok(stocks);
    }

    // === OBTENER STOCK POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<Stock> obtenerPorId(@PathVariable Long id) {
        return stockService.obtenerPorId(id)
                .map(stock -> {
                    if (stock.getBodega() != null) {
                        stock.getBodega().setMovimientosOrigen(null);
                        stock.getBodega().setMovimientosDestino(null);
                        stock.getBodega().setStocks(null);
                    }
                    if (stock.getProducto() != null) {
                        stock.getProducto().setStocks(null);
                    }
                    return ResponseEntity.ok(stock);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === BUSCAR STOCK POR BODEGA ===
    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<Stock>> buscarPorBodega(@PathVariable Long bodegaId) {
        List<Stock> stocks = stockService.buscarPorBodega(bodegaId);
        return ResponseEntity.ok(stocks);
    }

    // === BUSCAR STOCK POR PRODUCTO ===
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Stock>> buscarPorProducto(@PathVariable Long productoId) {
        List<Stock> stocks = stockService.buscarPorProducto(productoId);
        return ResponseEntity.ok(stocks);
    }

    // === CREAR O ACTUALIZAR STOCK ===
    @PostMapping
    public ResponseEntity<Stock> guardarStock(@RequestBody Stock stock) {
        Stock nuevo = stockService.guardar(stock);
        return ResponseEntity.ok(nuevo);
    }

    // === AJUSTAR CANTIDAD DE STOCK ===
    @PutMapping("/ajustar")
    public ResponseEntity<Stock> ajustarCantidad(
            @RequestParam Long bodegaId,
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @RequestParam boolean sumar) {
        Stock actualizado = stockService.ajustarCantidad(bodegaId, productoId, cantidad, sumar);
        return ResponseEntity.ok(actualizado);
    }

    // === ELIMINAR STOCK ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarStock(@PathVariable Long id) {
        if (stockService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}