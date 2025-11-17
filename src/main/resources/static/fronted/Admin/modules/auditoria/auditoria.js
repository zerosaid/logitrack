// === FUNCIÓN SEGURA DE FETCH (UNIFICADA) ===
async function apiFetch(url, options = {}) {
    const token = sessionStorage.getItem('token');
    if (!token) {
        showMessage('Sesión expirada. Redirigiendo al login...', true);
        setTimeout(() => window.location.href = '/fronted/index.html', 2000);
        return Promise.reject(new Error('No autenticado'));
    }

    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers
    };

    return fetch(`http://localhost:8080${url}`, { ...options, headers })
        .then(async r => {
            if (r.status === 401) {
                sessionStorage.clear();
                showMessage('Sesión expirada.', true);
                setTimeout(() => window.location.href = '/fronted/index.html', 2000);
                throw new Error('No autorizado');
            }
            if (!r.ok) {
                const text = await r.text();
                throw new Error(text || `Error ${r.status}`);
            }
            return r.json();
        });
}

// === UTILIDADES UI ===
const tableBody = document.querySelector("#auditTable tbody");
const messageDiv = document.getElementById("message");

function showMessage(message, isError = false) {
    messageDiv.textContent = message;
    messageDiv.className = `message ${isError ? "error" : "success"}`;
    messageDiv.style.display = "block";
    setTimeout(() => messageDiv.style.display = "none", 3000);
}

// === RENDERIZAR TABLA ===
function renderAuditorias(auditorias) {
    tableBody.innerHTML = "";
    if (!auditorias || auditorias.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='6'>No hay auditorías disponibles.</td></tr>";
        return;
    }

    auditorias.forEach(a => {
        const row = document.createElement("tr");
        const tipoClass = 
            a.operacion === "INSERT" ? "tag-insert" :
            a.operacion === "UPDATE" ? "tag-update" :
            a.operacion === "DELETE" ? "tag-delete" :
            "tag-default";

        row.innerHTML = `
            <td>${a.id || '-'}</td>
            <td>${a.usuario || '-'}</td>
            <td>${a.entidad || '-'}</td>
            <td class="${tipoClass}">${a.operacion || '-'}</td>
            <td>${a.fechaHora ? new Date(a.fechaHora).toLocaleString('es-CO') : '-'}</td>
            <td class="truncate">${a.valoresDespues || a.valoresAntes || '-'}</td>
        `;
        tableBody.appendChild(row);
    });
}

// === CARGAR AUDITORÍAS (CON FILTROS) ===
async function cargarAuditorias(filtros = {}) {
    try {
        const params = new URLSearchParams();
        
        if (filtros.usuario) params.append('usuario', filtros.usuario);
        if (filtros.operacion) params.append('operacion', filtros.operacion);
        if (filtros.desde) params.append('desde', filtros.desde);
        if (filtros.hasta) params.append('hasta', filtros.hasta);

        const url = `/api/auditorias${params.toString() ? '?' + params : ''}`;
        const data = await apiFetch(url);

        renderAuditorias(data);
        if (data.length === 0) {
            showMessage('No se encontraron auditorías con los filtros aplicados.');
        }
    } catch (err) {
        showMessage(`Error: ${err.message}`, true);
        renderAuditorias([]);
    }
}

// === EVENTOS ===
document.addEventListener("DOMContentLoaded", () => {
    // Cargar todas al inicio
    cargarAuditorias();

    // Botón de regreso
    document.getElementById("backBtn")?.addEventListener("click", () => {
        window.location.href = "../../admin-dashboard.html";
    });

    // Filtro
    document.getElementById("filterBtn")?.addEventListener("click", () => {
        const filtros = {
            usuario: document.getElementById("searchUser").value.trim().toLowerCase(),
            operacion: document.getElementById("filterType").value,
            desde: document.getElementById("fromDate").value ? `${document.getElementById("fromDate").value}T00:00:00` : '',
            hasta: document.getElementById("toDate").value ? `${document.getElementById("toDate").value}T23:59:59` : ''
        };
        cargarAuditorias(filtros);
    });

    // Limpiar filtros
    document.getElementById("clearBtn")?.addEventListener("click", () => {
        document.getElementById("searchUser").value = "";
        document.getElementById("filterType").value = "";
        document.getElementById("fromDate").value = "";
        document.getElementById("toDate").value = "";
        cargarAuditorias();
    });
});