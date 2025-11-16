document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("productoForm");
    const tableBody = document.querySelector("#productoTable tbody");
    const backBtn = document.getElementById("backBtn");
    const messageDiv = document.getElementById("message");
    let productos = [];
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

    // Cargar productos desde el backend
    function cargarProductos() {
        const token = sessionStorage.getItem('token');
        if (!token) {
            showMessage('No estás autenticado. Redirigiendo al login.', true);
            setTimeout(() => {
                window.location.href = '/fronted/index.html';
            }, 2000);
            return;
        }

        fetch('/api/productos', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            console.log('Respuesta del servidor (cargar productos):', response);
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`Error al cargar productos: ${response.status} - ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos recibidos (cargar productos):', data);
            productos = data;
            renderTable();
        })
        .catch(error => {
            console.error('Error al cargar productos:', error);
            showMessage('Error al cargar el listado de productos: ' + error.message, true);
            tableBody.innerHTML = '<tr><td colspan="7">Error al cargar datos.</td></tr>';
        });
    }

    // Renderizar tabla
    function renderTable() {
        tableBody.innerHTML = '';
        if (productos.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7">No hay productos registrados.</td></tr>';
            return;
        }
        productos.forEach((producto, index) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${producto.codigo || ''}</td>
                <td>${producto.nombre || ''}</td>
                <td>${producto.categoria || ''}</td>
                <td>${producto.precio || 0}</td>
                <td>${producto.stockMin || 0}</td>
                <td>
                    <button class="btn-edit" data-id="${producto.id}">✏️</button>
                    <button class="btn-delete" data-id="${producto.id}">🗑️</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    }

    // Manejar el envío del formulario
    form.addEventListener("submit", (e) => {
        e.preventDefault();

        const nuevoProducto = {
            codigo: document.getElementById("codigo").value.trim(),
            nombre: document.getElementById("nombre").value.trim(),
            categoria: document.getElementById("categoria").value.trim(),
            precio: parseFloat(document.getElementById("precio").value),
            stockMin: parseInt(document.getElementById("stockMin").value)
        };

        if (!nuevoProducto.codigo || !nuevoProducto.nombre || !nuevoProducto.categoria || 
            isNaN(nuevoProducto.precio) || nuevoProducto.precio < 0 || 
            isNaN(nuevoProducto.stockMin) || nuevoProducto.stockMin < 0) {
            showMessage("Por favor, completa todos los campos obligatorios y asegúrate de que los valores sean válidos.", true);
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

        const url = editId !== null ? `/api/productos/${editId}` : '/api/productos';
        const method = editId !== null ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoProducto)
        })
        .then(response => {
            console.log('Respuesta del servidor (guardar/editar producto):', response);
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`Error al ${method === 'POST' ? 'crear' : 'actualizar'} producto: ${response.status} - ${text}`);
                });
            }
            return response.json();
        })
        .then(data => {
            if (editId !== null) {
                const index = productos.findIndex(p => p.id === editId);
                productos[index] = data;
                editId = null;
            } else {
                productos.push(data);
            }
            form.reset();
            renderTable();
            showMessage(`Producto ${method === 'POST' ? 'creado' : 'actualizado'} exitosamente`);
        })
        .catch(error => {
            console.error('Error al guardar/editar producto:', error);
            showMessage(`Error: ${error.message}`, true);
        });
    });

    // Manejar edición y eliminación
    tableBody.addEventListener("click", (e) => {
        if (e.target.classList.contains("btn-edit")) {
            const id = parseInt(e.target.getAttribute("data-id"));
            const producto = productos.find(p => p.id === id);

            document.getElementById("codigo").value = producto.codigo || '';
            document.getElementById("nombre").value = producto.nombre || '';
            document.getElementById("categoria").value = producto.categoria || '';
            document.getElementById("precio").value = producto.precio || 0;
            document.getElementById("stockMin").value = producto.stockMin || 0;

            editId = id;
        }

        if (e.target.classList.contains("btn-delete")) {
            const id = parseInt(e.target.getAttribute("data-id"));
            if (confirm("¿Deseas eliminar este producto?")) {
                const token = sessionStorage.getItem('token');
                fetch(`/api/productos/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(`Error al eliminar producto: ${response.status} - ${text}`);
                        });
                    }
                    productos = productos.filter(p => p.id !== id);
                    renderTable();
                    showMessage('Producto eliminado exitosamente');
                })
                .catch(error => {
                    console.error('Error al eliminar producto:', error);
                    showMessage('Error al eliminar el producto: ' + error.message, true);
                });
            }
        }
    });

    // Botón para volver
    if (backBtn) {
        backBtn.addEventListener("click", () => {
            window.location.href = '/fronted/admin/admin-dashboard.html';
        });
    }

    // Inicializar
    cargarProductos();
});