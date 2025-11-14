const form = document.getElementById("productoForm");
const tabla = document.querySelector("#tablaProductos tbody");
const backBtn = document.getElementById("backBtn");

let productos = [];

// Redirección al dashboard del admin
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// Manejar el envío del formulario
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nuevoProducto = {
        codigo: document.getElementById("codigo").value,
        nombre: document.getElementById("nombre").value,
        cantidad: document.getElementById("cantidad").value,
        categoria: document.getElementById("categoria").value,
        precio: document.getElementById("precio").value,
    };

    productos.push(nuevoProducto);
    renderTabla();
    form.reset();
});

// Renderizar tabla
function renderTabla() {
    tabla.innerHTML = "";
    productos.forEach((p, index) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
        <td>${p.codigo}</td>
        <td>${p.nombre}</td>
        <td>${p.cantidad}</td>
        <td>${p.categoria}</td>
        <td>$${parseFloat(p.precio).toFixed(2)}</td>
    <td>
        <button class="action-btn" onclick="editarProducto(${index})">✏️</button>
        <button class="action-btn" onclick="eliminarProducto(${index})">🗑️</button>
    </td>
    `;
        tabla.appendChild(fila);
    });
}

// Editar producto
function editarProducto(index) {
    const producto = productos[index];
    document.getElementById("codigo").value = producto.codigo;
    document.getElementById("nombre").value = producto.nombre;
    document.getElementById("cantidad").value = producto.cantidad;
    document.getElementById("categoria").value = producto.categoria;
    document.getElementById("precio").value = producto.precio;
    productos.splice(index, 1);
    renderTabla();
}

// Eliminar producto
function eliminarProducto(index) {
    productos.splice(index, 1);
    renderTabla();
}
