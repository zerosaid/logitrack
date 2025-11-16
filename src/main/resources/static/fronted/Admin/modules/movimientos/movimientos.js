document.addEventListener("DOMContentLoaded", async () => {
    const form = document.getElementById("form-movimiento");
    if (form) {
        form.addEventListener("submit", guardarMovimiento);
    } else {
        console.error("Elemento 'form-movimiento' no encontrado en el DOM.");
    }
    await cargarOpciones();
    await cargarMovimientos();
});

async function cargarOpciones() {
    const token = sessionStorage.getItem("token");
    if (!token) {
        showMessage("No estás autenticado. Redirigiendo al login.", true);
        setTimeout(() => {
            window.location.href = "/fronted/index.html";
        }, 2000);
        return false;
    }

    const productoSelect = document.getElementById("producto");
    const bodegaOrigenSelect = document.getElementById("bodegaOrigen");
    const bodegaDestinoSelect = document.getElementById("bodegaDestino");
    const usuarioSelect = document.getElementById("usuario");

    if (!productoSelect || !bodegaOrigenSelect || !bodegaDestinoSelect || !usuarioSelect) {
        console.error("Uno o más elementos de selección no encontrados en el DOM.");
        showMessage("Error: Los campos de selección no están disponibles.", true);
        return false;
    }

    try {
        const productosRes = await fetch("http://localhost:8080/api/productos", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!productosRes.ok) {
            throw new Error(`Error ${productosRes.status}: ${await productosRes.text()}`);
        }
        const productos = await productosRes.json();
        productoSelect.innerHTML = '<option value="">Seleccione un producto</option>';
        productos.forEach(p => {
            const option = document.createElement("option");
            option.value = p.id;
            option.textContent = p.nombre;
            productoSelect.appendChild(option);
        });

        const bodegasRes = await fetch("http://localhost:8080/api/bodegas", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!bodegasRes.ok) {
            throw new Error(`Error ${bodegasRes.status}: ${await bodegasRes.text()}`);
        }
        const bodegas = await bodegasRes.json();
        bodegaOrigenSelect.innerHTML = '<option value="">Seleccione una bodega</option>';
        bodegaDestinoSelect.innerHTML = '<option value="">Seleccione una bodega</option>';
        bodegas.forEach(b => {
            const optionOrigen = document.createElement("option");
            optionOrigen.value = b.id;
            optionOrigen.textContent = b.nombre;
            bodegaOrigenSelect.appendChild(optionOrigen);
            const optionDestino = document.createElement("option");
            optionDestino.value = b.id;
            optionDestino.textContent = b.nombre;
            bodegaDestinoSelect.appendChild(optionDestino);
        });

        const usuariosRes = await fetch("http://localhost:8080/api/usuarios", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!usuariosRes.ok) {
            throw new Error(`Error ${usuariosRes.status}: ${await usuariosRes.text()}`);
        }
        const usuarios = await usuariosRes.json();
        usuarioSelect.innerHTML = '<option value="">Seleccione un usuario</option>';
        usuarios.forEach(u => {
            const option = document.createElement("option");
            option.value = u.id;
            option.textContent = u.nombre;
            usuarioSelect.appendChild(option);
        });

        return true;
    } catch (err) {
        console.error("Error al cargar opciones:", err);
        showMessage(`Error al cargar opciones: ${err.message}`, true);
        return false;
    }
}

async function cargarMovimientos() {
    const token = sessionStorage.getItem("token");
    if (!token) {
        showMessage("No estás autenticado. Redirigiendo al login.", true);
        setTimeout(() => {
            window.location.href = "/fronted/index.html";
        }, 2000);
        return;
    }

    const tbody = document.getElementById("movimientos-tbody");
    if (!tbody) {
        console.error("Elemento 'movimientos-tbody' no encontrado en el DOM.");
        showMessage("Error: La tabla de movimientos no está disponible.", true);
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/movimientos", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${await response.text()}`);
        }
        const movimientos = await response.json();
        tbody.innerHTML = "";
        if (movimientos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6">No hay movimientos registrados.</td></tr>';
            return;
        }
        movimientos.forEach(m => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${m.id}</td>
                <td>${m.tipo}</td>
                <td>${m.bodegaOrigen ? m.bodegaOrigen.nombre : '-'}</td>
                <td>${m.bodegaDestino ? m.bodegaDestino.nombre : '-'}</td>
                <td>${m.usuario.nombre}</td>
                <td>${new Date(m.fecha).toLocaleString()}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error("Error al cargar movimientos:", err);
        showMessage(`Error al cargar movimientos: ${err.message}`, true);
    }
}

async function guardarMovimiento(event) {
    event.preventDefault();

    const token = sessionStorage.getItem("token");
    if (!token) {
        showMessage("No estás autenticado. Redirigiendo al login.", true);
        setTimeout(() => {
            window.location.href = "/fronted/index.html";
        }, 2000);
        return;
    }

    const tipo = document.getElementById("tipo").value;
    const bodegaOrigenId = document.getElementById("bodegaOrigen").value;
    const bodegaDestinoId = document.getElementById("bodegaDestino").value;
    const usuarioId = document.getElementById("usuario").value;
    const productoId = document.getElementById("producto").value;
    const cantidad = document.getElementById("cantidad").value;

    if (!tipo || !usuarioId || !productoId || !cantidad) {
        showMessage("Por favor, complete todos los campos obligatorios.", true);
        return;
    }
    if (tipo === "ENTRADA" && !bodegaDestinoId) {
        showMessage("La bodega destino es obligatoria para entradas.", true);
        return;
    }
    if (tipo === "SALIDA" && !bodegaOrigenId) {
        showMessage("La bodega origen es obligatoria para salidas.", true);
        return;
    }
    if (tipo === "TRANSFERENCIA" && (!bodegaOrigenId || !bodegaDestinoId)) {
        showMessage("Ambas bodegas son obligatorias para transferencias.", true);
        return;
    }
    if (parseInt(cantidad) <= 0) {
        showMessage("La cantidad debe ser mayor que 0.", true);
        return;
    }

    // Obtener el precio_unitario desde la API de productos
    const productoResponse = await fetch(`http://localhost:8080/api/productos/${productoId}`, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (!productoResponse.ok) {
        throw new Error(`Error al obtener el producto: ${await productoResponse.text()}`);
    }
    const producto = await productoResponse.json();
    const precioUnitario = producto.precio; // Asegúrate de que la API devuelva 'precio'

    const movimientoDTO = {
        tipo: tipo,
        bodegaOrigenId: bodegaOrigenId ? parseInt(bodegaOrigenId) : null,
        bodegaDestinoId: bodegaDestinoId ? parseInt(bodegaDestinoId) : null,
        usuarioId: parseInt(usuarioId),
        items: [
            {
                productoId: parseInt(productoId),
                cantidad: parseInt(cantidad),
                precioUnitario: precioUnitario // Añadido para cumplir con el backend
            }
        ]
    };

    try {
        const response = await fetch("http://localhost:8080/api/movimientos", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(movimientoDTO)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }

        const data = await response.json();
        showMessage("Movimiento registrado exitosamente", false);
        limpiarFormulario();
        await cargarMovimientos();
    } catch (err) {
        console.error("Error al guardar movimiento:", err);
        showMessage(`Error al guardar movimiento: ${err.message}`, true);
    }
}

function limpiarFormulario() {
    document.getElementById("form-movimiento").reset();
}

function showMessage(message, isError) {
    const messageDiv = document.getElementById("message");
    if (messageDiv) {
        messageDiv.textContent = message;
        messageDiv.className = isError ? "error" : "success";
        messageDiv.style.display = "block";
        setTimeout(() => {
            messageDiv.style.display = "none";
        }, 3000);
    } else {
        console.error("Elemento 'message' no encontrado en el DOM.");
    }
}