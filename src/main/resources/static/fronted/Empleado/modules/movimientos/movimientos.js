// ===============================================
// MOVIMIENTOS.JS - LogiTrack
// ===============================================

// Elementos del DOM
const tablaBody = document.getElementById("tablaBody");
const btnFiltrar = document.getElementById("btnFiltrar");
const tipoMovimiento = document.getElementById("tipoMovimiento");
const fechaInicio = document.getElementById("fechaInicio");
const fechaFin = document.getElementById("fechaFin");
const btnVolver = document.getElementById("btnVolver");

// URL base de la API de Spring Boot
const API_URL = "http://localhost:8080/api/movimientos";

// ===============================================
// EVENTO: Volver al Dashboard Admin
// ===============================================
btnVolver.addEventListener("click", () => {
    window.location.href = "../../user-dashboard.html";
});

// ===============================================
// CARGAR MOVIMIENTOS DESDE EL BACKEND
// ===============================================
async function cargarMovimientos() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Error al obtener movimientos");

        const movimientos = await response.json();
        mostrarMovimientos(movimientos);
    } catch (error) {
        console.error(error);
        tablaBody.innerHTML = `<tr><td colspan="5">Error al cargar movimientos.</td></tr>`;
    }
}

// ===============================================
// MOSTRAR MOVIMIENTOS EN TABLA
// ===============================================
function mostrarMovimientos(movimientos) {
    tablaBody.innerHTML = "";

    if (movimientos.length === 0) {
        tablaBody.innerHTML = `<tr><td colspan="5">No hay movimientos registrados.</td></tr>`;
        return;
    }

    movimientos.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

    movimientos.forEach(mov => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${mov.id}</td>
            <td>${new Date(mov.fecha).toLocaleDateString()}</td>
            <td>${mov.tipo || "—"}</td>
            <td>${mov.cantidad || 0}</td>
            <td>${mov.producto ? mov.producto.nombre : "—"}</td>
        `;
        tablaBody.appendChild(tr);
    });
}

// ===============================================
// FILTRAR MOVIMIENTOS
// ===============================================
btnFiltrar.addEventListener("click", async () => {
    try {
        const response = await fetch(API_URL);
        const movimientos = await response.json();

        let filtrados = movimientos;

        // Filtro por tipo
        const tipo = tipoMovimiento.value;
        if (tipo !== "todos") {
            filtrados = filtrados.filter(m => m.tipo.toLowerCase() === tipo.toLowerCase());
        }

        // Filtro por fechas
        const desde = fechaInicio.value ? new Date(fechaInicio.value) : null;
        const hasta = fechaFin.value ? new Date(fechaFin.value) : null;

        if (desde || hasta) {
            filtrados = filtrados.filter(m => {
                const fechaMov = new Date(m.fecha);
                if (desde && fechaMov < desde) return false;
                if (hasta && fechaMov > hasta) return false;
                return true;
            });
        }

        mostrarMovimientos(filtrados);

    } catch (error) {
        console.error("Error al filtrar movimientos:", error);
    }
});

// ===============================================
// INICIO AUTOMÁTICO
// ===============================================
cargarMovimientos();
