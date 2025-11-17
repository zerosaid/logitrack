// Elementos DOM
const backBtn = document.getElementById("backBtn");
const tablaBody = document.querySelector("#tablaReportes tbody");
const reporteForm = document.getElementById("form-reporte");
const messageDiv = document.getElementById("message");

// Verificar elementos DOM
document.addEventListener("DOMContentLoaded", () => {
    if (!backBtn || !tablaBody || !reporteForm || !messageDiv) {
        console.error("Uno o más elementos DOM no encontrados:", {
            backBtn: !!backBtn,
            tablaBody: !!tablaBody,
            reporteForm: !!reporteForm,
            messageDiv: !!messageDiv
        });
        return;
    }

    // Establecer fecha y hora actuales como valores predeterminados
    const now = new Date();
    const defaultDateTime = now.toISOString().slice(0, 16); // Formato "2025-11-16T18:26"
    document.getElementById("fecha-inicio").value = defaultDateTime;
    document.getElementById("fecha-fin").value = defaultDateTime;

    // Registrar eventos
    backBtn.addEventListener("click", handleBackClick);
    reporteForm.addEventListener("submit", generarReporte);
});

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

// Descargar reporte
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

// Manejar clic en el botón "Volver"
function handleBackClick() {
    console.log("Intentando redirigir a /fronted/admin-dashboard.html");
    window.location.href = "/fronted/Admin/admin-dashboard.html"; // Ruta absoluta
}

// Mostrar mensajes
function showMessage(message, isError = false) {
    if (!messageDiv) {
        console.error("Elemento 'message' no encontrado en el DOM.");
        return;
    }
    messageDiv.textContent = message;
    messageDiv.className = `message ${isError ? "error" : "success"}`;
    messageDiv.style.display = "block";
    setTimeout(() => (messageDiv.style.display = "none"), 3000);
}