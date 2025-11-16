const form = document.getElementById("productoForm");
const tablaBody = document.querySelector("#tablaProductos tbody");
const backBtn = document.getElementById("backBtn");
let productos = [];

// Redirección al dashboard del admin
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// Cargar productos desde el backend
function cargarProductos() {
    const token = sessionStorage.getItem('token');
    if (!token) {
        alert('No estás autenticado. Redirigiendo al login.');
        window.location.href = '/fronted/index.html';
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
            throw new Error(`Error al cargar productos: ${response.status} - ${response.statusText}`);
        }
        return response.text().then(text => {
            console.log('Cuerpo de la respuesta (raw):', text);
            try {
                return JSON.parse(text);
            } catch (e) {
                throw new Error('Respuesta no es JSON válido: ' + e.message + '\n' + text);
            }
        });
    })
    .then(data => {
        console.log('Datos recibidos (cargar productos):', data);
        if (!Array.isArray(data)) {
            throw new Error('La respuesta no es un array de productos');
        }
        productos = data;
        renderTabla();
    })
    .catch(error => {
        console.error('Error al cargar productos:', error);
        alert('Error al cargar el listado de productos: ' + error.message);
        tablaBody.innerHTML = '<tr><td colspan="6">Error al cargar datos. Revisa la consola (F12).</td></tr>';
    });
}

// Manejar el envío del formulario (Crear/Editar)
form.addEventListener("submit", (e) => {
    e.preventDefault();
    const codigo = document.getElementById("codigo").value.trim();
    const nombre = document.getElementById("nombre").value.trim();
    const cantidad = document.getElementById("cantidad").value;
    const categoria = document.getElementById("categoria").value.trim();
    const precio = document.getElementById("precio").value;

    if (!nombre) {
        alert('El nombre del producto es obligatorio.');
        return;
    }
    if (!precio || isNaN(precio) || parseFloat(precio) <= 0) {
        alert('El precio debe ser un número positivo.');
        return;
    }
    if (!cantidad || isNaN(cantidad) || parseInt(cantidad) < 0) {
        alert('La cantidad debe ser un número no negativo.');
        return;
    }

    // Obtener el producto a editar si está en modo edición
    const editId = form.dataset.editing === 'true' ? parseInt(form.dataset.editId) : null;
    const productoExistente = editId ? productos.find(p => p.id === editId) : null;
    const fechaRegistro = productoExistente ? productoExistente.fechaRegistro : new Date().toISOString(); // Mantener fecha original o usar actual

    const producto = {
        id: editId || undefined, // Solo incluir id si es edición
        codigo: codigo,
        nombre: nombre,
        cantidad: parseInt(cantidad),
        categoria: categoria,
        precio: parseFloat(precio),
        fechaRegistro: fechaRegistro // Enviar como string ISO
        // Excluir stocks y movimientoItems, ya que se gestionan en el backend
    };

    const token = sessionStorage.getItem('token');
    const method = form.dataset.editing === 'true' ? 'PUT' : 'POST';
    const url = form.dataset.editing === 'true'
        ? `/api/productos/${form.dataset.editId}`
        : '/api/productos';

    fetch(url, {
        method: method,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(producto)
    })
    .then(response => {
        console.log('Respuesta del servidor (guardar/editar producto):', response);
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(`Error al ${method === 'PUT' ? 'actualizar' : 'crear'} producto: ${response.status} - ${text}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Producto guardado/editado:', data);
        if (form.dataset.editing === 'true') {
            const index = productos.findIndex(p => p.id === parseInt(form.dataset.editId));
            if (index !== -1) productos[index] = data;
        } else {
            productos.push(data);
        }
        form.reset();
        delete form.dataset.editing;
        delete form.dataset.editId;
        renderTabla();
    })
    .catch(error => {
        console.error('Error al guardar/editar producto:', error);
        alert(`Error al ${method === 'PUT' ? 'actualizar' : 'crear'} el producto: ${error.message}`);
    });
});

// Renderizar tabla
function renderTabla() {
    tablaBody.innerHTML = "";
    if (productos.length === 0) {
        tablaBody.innerHTML = '<tr><td colspan="6">No hay productos registrados.</td></tr>';
        return;
    }
    productos.forEach((p, index) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${p.codigo || 'Sin código'}</td>
            <td>${p.nombre || 'Sin nombre'}</td>
            <td>${p.cantidad || 0}</td>
            <td>${p.categoria || 'Sin categoría'}</td>
            <td>$${p.precio ? parseFloat(p.precio).toFixed(2) : '0.00'}</td>
            <td>
                <button class="action-btn" onclick="editarProducto(${index})">✏️</button>
                <button class="action-btn" onclick="eliminarProducto(${p.id})">🗑️</button>
            </td>
        `;
        tablaBody.appendChild(fila);
    });
}

// Editar producto
function editarProducto(index) {
    const producto = productos[index];
    document.getElementById("codigo").value = producto.codigo || '';
    document.getElementById("nombre").value = producto.nombre || '';
    document.getElementById("cantidad").value = producto.cantidad || '';
    document.getElementById("categoria").value = producto.categoria || '';
    document.getElementById("precio").value = producto.precio || '';
    form.dataset.editing = 'true';
    form.dataset.editId = producto.id;
}

// Eliminar producto
function eliminarProducto(id) {
    if (!confirm("¿Deseas eliminar este producto?")) return;
    const token = sessionStorage.getItem('token');
    fetch(`/api/productos/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        console.log('Respuesta del servidor (eliminar producto):', response);
        if (!response.ok) {
            throw new Error(`Error al eliminar producto: ${response.status} - ${response.statusText}`);
        }
        productos = productos.filter(p => p.id !== id);
        renderTabla();
    })
    .catch(error => {
        console.error('Error al eliminar producto:', error);
        alert('Error al eliminar el producto: ' + error.message);
    });
}

// Inicializar
document.addEventListener('DOMContentLoaded', cargarProductos);