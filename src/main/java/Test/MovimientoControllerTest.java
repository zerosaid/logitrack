package Test;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.service.MovimientoService;
import com.c3.logitrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000" }, allowCredentials = "true")
@Tag(name = "Movimiento", description = "Gestión de entradas, salidas y transferencias.")
@SecurityRequirement(name = "allowCredentials")
public class MovimientoControllerTest {

    private final MovimientoService movimientoService;
    private final UserService userService;

    public MovimientoControllerTest(MovimientoService movimientoService, UserService userService) {
        this.movimientoService = movimientoService;
        this.userService = userService;
    }

    @GetMapping("/recientes2")
    @Operation(summary = "Últimos 10 movimientos")
    public ResponseEntity<Map<String, Object>> movimientosRecientes() {
        List<Movimiento> recientes = movimientoService.listarUltimos(10);
        recientes.forEach(this::limpiarRelaciones);
        Map<String, Object> response = new HashMap<>();
        response.put("totalRecientes", recientes.size());
        response.put("movimientos", recientes);
        return ResponseEntity.ok(response);
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