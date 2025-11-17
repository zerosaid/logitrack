document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("bodegaForm");
    const tableBody = document.querySelector("#bodegaTable tbody");
    const backBtn = document.getElementById("backBtn");
    const messageDiv = document.getElementById("message");
    let bodegas = [];
    let editId = null;

    // Mostrar mensajes de feedback
    function showMessage(text, isError = false) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${isError ? 'error' : 'success'}`;
        messageDiv.style.display = 'block';
        setTimeout(() => {
            messageDiv.style.display = 'none';
        }, 5000);
    }

    // Cargar bodegas desde el backend
    function cargarBodegas() {
        const token = sessionStorage.getItem('token');
        if (!token) {
            showMessage('No estás autenticado. Redirigiendo al login.', true);
            setTimeout(() => {
                window.location.href = '/fronted/index.html';
            }, 2000);
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
                return response.text().then(text => {
                    throw new Error(`Error al cargar bodegas: ${response.status} - ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            bodegas = data;
            renderTable();
        })
        .catch(error => {
            console.error('Error al cargar bodegas:', error);
            showMessage('Error al cargar el listado de bodegas: ' + error.message, true);
            tableBody.innerHTML = '<tr><td colspan="6">Error al cargar datos.</td></tr>';
        });
    }

    // Renderizar tabla
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
                <td>${bodega.encargado || 'Sin encargado'}</td>
                <td>
                    <button class="btn-edit" data-id="${bodega.id}">✏️</button>
                    <button class="btn-delete" data-id="${bodega.id}">🗑️</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    }

    // Manejar el envío del formulario
    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const nuevaBodega = {
            nombre: document.getElementById("nombre").value.trim(),
            ubicacion: document.getElementById("ubicacion").value.trim(),
            capacidad: parseInt(document.getElementById("capacidad").value),
            encargado: document.getElementById("encargado").value.trim() || null
        };

        if (!nuevaBodega.nombre || !nuevaBodega.ubicacion || isNaN(nuevaBodega.capacidad) || nuevaBodega.capacidad < 0) {
            showMessage("Por favor, completa todos los campos obligatorios y asegúrate de que la capacidad sea válida.", true);
            return;
        }

        const token = sessionStorage.getItem('token');
        if (!token) {
            showMessage('No estás autenticado. Redirigiendo al login.', true);
            setTimeout(() => {
                window.location.href = '/fronted/index.html';
            }, 2000);
            return;
        }

        const url = editId !== null ? `/api/bodegas/${editId}` : '/api/bodegas';
        const method = editId !== null ? 'PUT' : 'POST';

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
                return response.text().then(text => {
                    throw new Error(`Error al ${method === 'POST' ? 'crear' : 'actualizar'} bodega: ${response.status} - ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            if (editId !== null) {
                const index = bodegas.findIndex(b => b.id === editId);
                bodegas[index] = data;
                editId = null;
            } else {
                bodegas.push(data);
            }
            form.reset();
            renderTable();
            showMessage(`Bodega ${method === 'POST' ? 'creada' : 'actualizada'} exitosamente`);
        })
        .catch(error => {
            console.error(`Error al ${method === 'POST' ? 'crear' : 'actualizar'} bodega:`, error);
            showMessage(`Error: ${error.message}`, true);
        });
    });

    // Manejar edición y eliminación
    tableBody.addEventListener("click", (e) => {
        if (e.target.classList.contains("btn-edit")) {
            const id = parseInt(e.target.getAttribute("data-id"));
            const bodega = bodegas.find(b => b.id === id);

            document.getElementById("nombre").value = bodega.nombre || '';
            document.getElementById("ubicacion").value = bodega.ubicacion || '';
            document.getElementById("capacidad").value = bodega.capacidad || 0;
            document.getElementById("encargado").value = bodega.encargado || '';

            editId = id;
        }

        if (e.target.classList.contains("btn-delete")) {
            const id = parseInt(e.target.getAttribute("data-id"));
            if (confirm("¿Deseas eliminar esta bodega?")) {
                const token = sessionStorage.getItem('token');
                fetch(`/api/bodegas/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(`Error al eliminar bodega: ${response.status} - ${text}`);
                        });
                    }
                    bodegas = bodegas.filter(b => b.id !== id);
                    renderTable();
                    showMessage('Bodega eliminada exitosamente');
                })
                .catch(error => {
                    console.error('Error al eliminar bodega:', error);
                    showMessage('Error al eliminar la bodega: ' + error.message, true);
                });
            }
        }
    });

    // Botón para volver
    if (backBtn) {
        backBtn.addEventListener("click", () => {
            window.location.href = '/fronted/Admin/admin-dashboard.html';
        });
    }

    // Inicializar
    cargarBodegas();
});