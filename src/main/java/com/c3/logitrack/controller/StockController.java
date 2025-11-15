package com.c3.logitrack.controller;

import com.c3.logitrack.model.Stock;
import com.c3.logitrack.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> listarTodos() {
        List<Stock> stocks = stockService.listarTodos();
        stocks.forEach(s -> {
            if (s.getBodega() != null) {
                s.getBodega().setStocks(null);
                s.getBodega().setMovimientosOrigen(null);
                s.getBodega().setMovimientosDestino(null);
            }
            if (s.getProducto() != null) {
                s.getProducto().setStocks(null);
                s.getProducto().setMovimientoItems(null);
            }
        });
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> obtenerPorId(@PathVariable Long id) {
        return stockService.obtenerPorId(id)
                .map(stock -> {
                    if (stock.getBodega() != null) {
                        stock.getBodega().setStocks(null);
                        stock.getBodega().setMovimientosOrigen(null);
                        stock.getBodega().setMovimientosDestino(null);
                    }
                    if (stock.getProducto() != null) {
                        stock.getProducto().setStocks(null);
                        stock.getProducto().setMovimientoItems(null);
                    }
                    return ResponseEntity.ok(stock);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<Stock>> buscarPorBodega(@PathVariable Long bodegaId) {
        List<Stock> stocks = stockService.buscarPorBodega(bodegaId);
        stocks.forEach(s -> {
            if (s.getBodega() != null) s.getBodega().setStocks(null);
            if (s.getProducto() != null) s.getProducto().setStocks(null);
        });
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Stock>> buscarPorProducto(@PathVariable Long productoId) {
        List<Stock> stocks = stockService.buscarPorProducto(productoId);
        stocks.forEach(s -> {
            if (s.getBodega() != null) s.getBodega().setStocks(null);
            if (s.getProducto() != null) s.getProducto().setStocks(null);
        });
        return ResponseEntity.ok(stocks);
    }

    @PostMapping
    public ResponseEntity<Stock> guardarStock(@RequestBody Stock stock) {
        Stock nuevo = stockService.guardar(stock);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/ajustar")
    public ResponseEntity<Stock> ajustarCantidad(
            @RequestParam Long bodegaId,
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @RequestParam boolean sumar) {
        Stock actualizado = stockService.ajustarCantidad(bodegaId, productoId, cantidad, sumar);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarStock(@PathVariable Long id) {
        if (stockService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        stockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}