const backBtn = document.getElementById("backBtn");
const tablaBody = document.querySelector("#tablaReportes tbody");

// Simulación de reportes cargados
const reportes = [
    { id: 1, tipo: "Inventario", fecha: "2025-11-10", descripcion: "Reporte de inventario mensual" },
    { id: 2, tipo: "Movimientos", fecha: "2025-11-12", descripcion: "Historial de movimientos recientes" },
    { id: 3, tipo: "Auditoría", fecha: "2025-11-13", descripcion: "Revisión de auditorías pendientes" },
];

// Renderizar tabla
function cargarReportes() {
    tablaBody.innerHTML = "";
    reportes.forEach((r) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
        <td>${r.id}</td>
        <td>${r.tipo}</td>
        <td>${r.fecha}</td>
        <td>${r.descripcion}</td>
        <td><button class="btn-descargar" onclick="descargarReporte('${r.tipo}')">⬇ Descargar</button></td>
    `;
        tablaBody.appendChild(fila);
    });
}

// Simulación descarga
function descargarReporte(tipo) {
    alert(`📊 Descargando reporte de ${tipo}...`);
}

// Volver al dashboard
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

cargarReportes();
