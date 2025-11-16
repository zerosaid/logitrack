// === BOTÓN DE REGRESO ===
document.getElementById("backBtn").addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// === FUNCIONES DE UTILIDAD ===
const tableBody = document.querySelector("#auditTable tbody");
const messageDiv = document.getElementById("message");

function showMessage(message, isError = false) {
    messageDiv.textContent = message;
    messageDiv.className = `message ${isError ? "error" : "success"}`;
    messageDiv.style.display = "block";
    setTimeout(() => (messageDiv.style.display = "none"), 3000);
}

async function fetchAuditorias(url = "/api/auditorias") {
    try {
        const token = sessionStorage.getItem("token");
        if (!token) {
            showMessage("No estás autenticado. Redirigiendo al login.", true);
            setTimeout(() => (window.location.href = "/fronted/index.html"), 2000);
            return [];
        }

        const response = await fetch(`http://localhost:8080${url}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) throw new Error(await response.text());
        return await response.json();
    } catch (err) {
        showMessage(`Error al cargar auditorías: ${err.message}`, true);
        return [];
    }
}

function cargarAuditorias(auditorias) {
    tableBody.innerHTML = "";
    if (auditorias.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='6'>No hay auditorías disponibles.</td></tr>";
        return;
    }

    auditorias.forEach(a => {
        const row = document.createElement("tr");
        const tipoClass = 
            a.operacion === "INSERT" ? "tag-insert" :
            a.operacion === "UPDATE" ? "tag-update" :
            "tag-delete";

        row.innerHTML = `
            <td>${a.id || '-'}</td>
            <td>${a.usuario || '-'}</td>
            <td>${a.entidad || '-'}</td>
            <td class="${tipoClass}">${a.operacion || '-'}</td>
            <td>${a.fechaHora ? new Date(a.fechaHora).toLocaleString() : '-'}</td>
            <td>${a.valoresDespues || a.valoresAntes || '-'}</td>
        `;
        tableBody.appendChild(row);
    });
}

// === CARGAR AUDITORÍAS AL INICIAR ===
document.addEventListener("DOMContentLoaded", async () => {
    const auditorias = await fetchAuditorias();
    cargarAuditorias(auditorias);
});

// === FILTROS ===
document.getElementById("filterBtn").addEventListener("click", async () => {
    const usuario = document.getElementById("searchUser").value.toLowerCase();
    const tipo = document.getElementById("filterType").value;
    const from = document.getElementById("fromDate").value;
    const to = document.getElementById("toDate").value;

    let url = "/api/auditorias";
    const params = [];
    if (usuario) params.push(`usuario=${encodeURIComponent(usuario)}`);
    if (tipo) params.push(`operacion=${encodeURIComponent(tipo)}`);
    if (from) params.push(`desde=${encodeURIComponent(from + "T00:00:00")}`);
    if (to) params.push(`hasta=${encodeURIComponent(to + "T23:59:59")}`);

    if (params.length > 0) {
        url += "?" + params.join("&");
    }

    const auditorias = await fetchAuditorias(url);
    cargarAuditorias(auditorias);
});

document.getElementById("clearBtn").addEventListener("click", async () => {
    document.getElementById("searchUser").value = "";
    document.getElementById("filterType").value = "";
    document.getElementById("fromDate").value = "";
    document.getElementById("toDate").value = "";
    const auditorias = await fetchAuditorias();
    cargarAuditorias(auditorias);
});