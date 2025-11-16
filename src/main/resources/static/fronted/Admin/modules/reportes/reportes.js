// Elementos DOM
const backBtn = document.getElementById("backBtn");
const tablaBody = document.querySelector("#tablaReportes tbody");
const reporteForm = document.getElementById("form-reporte");

// Verificar elementos DOM
if (!backBtn || !tablaBody || !reporteForm) {
    console.error("Uno o más elementos DOM (backBtn, tablaReportes, form-reporte) no encontrados.");
    return;
}

// Renderizar tabla de reportes
function cargarReportes(reportes = []) {
    tablaBody.innerHTML = "";
    if (reportes.length === 0) {
        tablaBody.innerHTML = '<tr><td colspan="5">No hay reportes disponibles.</td></tr>';
        return;
    }
    reportes.forEach((r) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${r.id || '-'}</td>
            <td>${r.tipo || '-'}</td>
            <td>${r.fecha ? new Date(r.fecha).toLocaleString() : '-'}</td>
            <td>${r.descripcion || '-'}</td>
            <td><button class="btn-descargar" onclick="descargarReporte(${r.id}, '${r.tipo}')">⬇ Descargar</button></td>
        `;
        tablaBody.appendChild(fila);
    });
}

// Descargar reporte (simulación con fetch realista)
async function descargarReporte(id, tipo) {
    const token = sessionStorage.getItem("token");
    if (!token) {
        showMessage("No estás autenticado. Redirigiendo al login.", true);
        setTimeout(() => (window.location.href = "/fronted/index.html"), 2000);
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/reportes/descargar/${tipo}/${id}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error(await response.text());
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `reporte_${tipo}_${id}.pdf`; // Ajustar extensión según el backend
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        showMessage(`Reporte ${tipo} descargado exitosamente`, false);
    } catch (err) {
        console.error("Error al descargar reporte:", err);
        showMessage(`Error al descargar reporte: ${err.message}`, true);
    }
}

// Generar reporte basado en fechas
async function generarReporte(event) {
    event.preventDefault();

    const token = sessionStorage.getItem("token");
    if (!token) {
        showMessage("No estás autenticado. Redirigiendo al login.", true);
        setTimeout(() => (window.location.href = "/fronted/index.html"), 2000);
        return;
    }

    const inicio = document.getElementById("fecha-inicio").value;
    const fin = document.getElementById("fecha-fin").value;
    if (!inicio || !fin) {
        showMessage("Complete las fechas.", true);
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/reportes/movimientos-por-fecha?inicio=${inicio}&fin=${fin}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error(await response.text());
        const reportes = await response.json();
        cargarReportes(reportes);
        showMessage("Reporte generado exitosamente", false);
    } catch (err) {
        console.error("Error al generar reporte:", err);
        showMessage(`Error al generar reporte: ${err.message}`, true);
    }
}

// Volver al dashboard
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// Mostrar mensajes (reutilizable desde movimientos.js)
function showMessage(message, isError = false) {
    const messageDiv = document.getElementById("message");
    if (!messageDiv) {
        console.error("Elemento 'message' no encontrado en el DOM.");
        return;
    }
    messageDiv.textContent = message;
    messageDiv.className = `message ${isError ? "error" : "success"}`;
    messageDiv.style.display = "block";
    setTimeout(() => (messageDiv.style.display = "none"), 3000);
}

// Cargar reportes al iniciar (puedes quitar esto si prefieres solo generarlos)
document.addEventListener("DOMContentLoaded", () => {
    reporteForm.addEventListener("submit", generarReporte);
    // Opcional: Cargar reportes iniciales (descomentar y ajustar endpoint si lo tienes)
    // generarReporte({ preventDefault: () => {} }); // Simulación inicial
});
