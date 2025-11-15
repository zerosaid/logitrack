const form = document.getElementById("bodegaForm");
const tableBody = document.querySelector("#bodegaTable tbody");
const backBtn = document.getElementById("backBtn");

let bodegas = []; // Inicialmente vacía, se cargará desde el backend
let editIndex = null;

// ====== Cargar bodegas desde el backend ======
function cargarBodegas() {
    const token = sessionStorage.getItem('token');
    if (!token) {
        alert('No estás autenticado. Redirigiendo al login.');
        window.location.href = '/fronted/index.html';
        return;
    }

    fetch('/api/bodegas', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Error al cargar bodegas: ${response.status} - ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        bodegas = data; // Actualiza el array con datos del backend
        renderTable();
    })
    .catch(error => {
        console.error('Error al cargar bodegas:', error);
        alert('Error al cargar el listado de bodegas: ' + error.message);
        tableBody.innerHTML = '<tr><td colspan="6">Error al cargar datos.</td></tr>';
    });
}

// ====== Renderizar tabla ======
function renderTable() {
    tableBody.innerHTML = '';
    if (bodegas.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6">No hay bodegas registradas.</td></tr>';
        return;
    }
    bodegas.forEach((bodega, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
      <td>${index + 1}</td>
      <td>${bodega.nombre || ''}</td>
      <td>${bodega.ubicacion || ''}</td>
      <td>${bodega.capacidad || 0}</td>
      <td>${bodega.encargado || ''}</td>
      <td>
        <button class="btn-edit" data-index="${index}">✏️</button>
        <button class="btn-delete" data-index="${index}">🗑️</button>
      </td>
    `;
        tableBody.appendChild(row);
    });
    // No guardes en localStorage, usa el backend como fuente de verdad
    // localStorage.setItem("bodegas", JSON.stringify(bodegas));
}

// ====== Guardar o editar bodega ======
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nuevaBodega = {
        nombre: document.getElementById("nombre").value.trim(),
        ubicacion: document.getElementById("ubicacion").value.trim(),
        capacidad: document.getElementById("capacidad").value,
        encargado: document.getElementById("encargado").value.trim(),
    };

    const token = sessionStorage.getItem('token');
    const url = editIndex !== null ? `/api/bodegas/${bodegas[editIndex].id}` : '/api/bodegas';
    const method = editIndex !== null ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuevaBodega)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Error al ${method === 'POST' ? 'crear' : 'actualizar'} bodega: ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        if (editIndex !== null) {
            bodegas[editIndex] = data; // Actualiza con la respuesta del backend
            editIndex = null;
        } else {
            bodegas.push(data); // Añade la nueva bodega
        }
        form.reset();
        renderTable();
    })
    .catch(error => {
        console.error(`Error al ${method === 'POST' ? 'crear' : 'actualizar'} bodega:`, error);
        alert(`Error: ${error.message}`);
    });
});

// ====== Editar bodega ======
tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-edit")) {
        const index = e.target.getAttribute("data-index");
        const bodega = bodegas[index];

        document.getElementById("nombre").value = bodega.nombre || '';
        document.getElementById("ubicacion").value = bodega.ubicacion || '';
        document.getElementById("capacidad").value = bodega.capacidad || 0;
        document.getElementById("encargado").value = bodega.encargado || '';

        editIndex = index;
    }

    if (e.target.classList.contains("btn-delete")) {
        const index = e.target.getAttribute("data-index");
        if (confirm("¿Deseas eliminar esta bodega?")) {
            const token = sessionStorage.getItem('token');
            fetch(`/api/bodegas/${bodegas[index].id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al eliminar bodega: ' + response.statusText);
                }
                bodegas.splice(index, 1);
                renderTable();
            })
            .catch(error => {
                console.error('Error al eliminar bodega:', error);
                alert('Error al eliminar la bodega: ' + error.message);
            });
        }
    }
});

// ====== Botón para volver ======
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// ====== Inicializar ======
document.addEventListener('DOMContentLoaded', cargarBodegas);2