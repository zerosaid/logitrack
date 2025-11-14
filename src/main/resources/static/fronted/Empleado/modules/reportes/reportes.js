document.addEventListener("DOMContentLoaded", () => {
    const btnVolver = document.getElementById("btnVolver");
    const btnGenerar = document.getElementById("btnGenerar");
    const tablaReportes = document.getElementById("tablaReportes");

    // Botón para volver al Dashboard
    btnVolver.addEventListener("click", () => {
        window.location.href = "../../user-dashboard.html";
    });

    // Simular generación de reporte
    btnGenerar.addEventListener("click", () => {
        const tipo = document.getElementById("tipoReporte").value;
        const inicio = document.getElementById("fechaInicio").value;
        const fin = document.getElementById("fechaFin").value;

        if (!inicio || !fin) {
            alert("Por favor selecciona un rango de fechas.");
            return;
        }

        const datosSimulados = [
            { id: 1, tipo: tipo, fecha: inicio, detalles: "Datos iniciales del periodo" },
            { id: 2, tipo: tipo, fecha: fin, detalles: "Resumen final del periodo" }
        ];

        renderTabla(datosSimulados);
    });

    function renderTabla(datos) {
        tablaReportes.innerHTML = datos.map(d => `
            <tr>
                <td>${d.id}</td>
                <td>${d.tipo}</td>
                <td>${d.fecha}</td>
                <td>${d.detalles}</td>
            </tr>
        `).join("");
    }
});
