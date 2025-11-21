package Test;
import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.service.UserService;
import com.c3.logitrack.service.MovimientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes avanzados de inventario y auditoría")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000" }, allowCredentials = "true")
@Tag(name = "Movimiento", description = "Gestión de reportes en movimientos segun su tipo.")
@SecurityRequirement(name = "allowCredentials")
public class ReporteControllerTest {

    private final MovimientoService movimientoService;
    private final UserService userService;

    public ReporteControllerTest(MovimientoService movimientoService, UserService userService) {
        this.movimientoService = movimientoService;
        this.userService = userService;
    }

    // Reporte de movimientos por rango de fechas
    @GetMapping("/movimientos")
    @Operation(summary = "Filtrar por tipo", description = "ENTRADA, SALIDA, TRANSFERENCIA")
    public ResponseEntity<List<Movimiento>> buscarPorTipo(@PathVariable String tipo) {
        try {
            List<Movimiento> resultados = movimientoService.buscarPorTipo(tipo.toUpperCase());
            resultados.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(resultados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    private void limpiarRelaciones(Movimiento m) {
        if (m == null) return;
        if (m.getBodegaOrigen() != null) {
            m.getBodegaOrigen().setStocks(null);
            m.getBodegaOrigen().setMovimientosOrigen(null);
            m.getBodegaOrigen().setMovimientosDestino(null);
        }
        if (m.getBodegaDestino() != null) {
            m.getBodegaDestino().setStocks(null);
            m.getBodegaDestino().setMovimientosOrigen(null);
            m.getBodegaDestino().setMovimientosDestino(null);
        }
        if (m.getItems() != null) {
            m.getItems().forEach(i -> {
                if (i != null && i.getProducto() != null) {
                    i.getProducto().setStocks(null);
                    i.getProducto().setMovimientoItems(null);
                }
            });
        }
        if (m.getUsuario() != null) {
            m.getUsuario().setMovimientos(null);
        }
    }
}