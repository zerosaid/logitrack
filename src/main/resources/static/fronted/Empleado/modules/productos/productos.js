// === Gestión de productos ===

const form = document.getElementById("productoForm");
const tablaProductos = document.getElementById("tablaProductos").querySelector("tbody");

// Cargar productos desde localStorage
let productos = JSON.parse(localStorage.getItem("productos")) || [];

// Renderizar la tabla al iniciar
document.addEventListener("DOMContentLoaded", () => {
    renderProductos();
});

// Guardar o actualizar un producto
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nombre = document.getElementById("nombre").value.trim();
    const categoria = document.getElementById("categoria").value.trim();
    const precio = parseFloat(document.getElementById("precio").value);
    const stock = parseInt(document.getElementById("stock").value);

    if (!nombre || !categoria || isNaN(precio) || isNaN(stock)) {
        alert("Por favor, completa todos los campos correctamente.");
        return;
    }

    const id = Date.now();
    const nuevoProducto = { id, nombre, categoria, precio, stock };

    productos.push(nuevoProducto);
    localStorage.setItem("productos", JSON.stringify(productos));
    renderProductos();
    form.reset();
});

// Función para mostrar productos
function renderProductos() {
    tablaProductos.innerHTML = "";

    if (productos.length === 0) {
        tablaProductos.innerHTML = `<tr><td colspan="6">No hay productos registrados</td></tr>`;
        return;
    }

    productos.forEach(prod => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${prod.id}</td>
            <td>${prod.nombre}</td>
            <td>${prod.categoria}</td>
            <td>$${prod.precio.toFixed(2)}</td>
            <td>${prod.stock}</td>
            <td>
                <button onclick="eliminarProducto(${prod.id})">🗑 Eliminar</button>
            </td>
        `;
        tablaProductos.appendChild(fila);
    });
}

// Eliminar un producto
function eliminarProducto(id) {
    if (confirm("¿Seguro que deseas eliminar este producto?")) {
        productos = productos.filter(p => p.id !== id);
        localStorage.setItem("productos", JSON.stringify(productos));
        renderProductos();
    }
}
