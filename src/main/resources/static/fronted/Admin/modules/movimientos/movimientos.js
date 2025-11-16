const form = document.getElementById("movForm");
const tableBody = document.querySelector("#movTable tbody");
const backBtn = document.getElementById("backBtn");

let editId = null;

// ====== RENDERIZAR TABLA ======
async function renderTable() {
    try {
        const res = await fetch("http://localhost:8080/api/movimientos");
        const movimientos = await res.json();

        tableBody.innerHTML = "";
        movimientos.forEach((mov, index) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${mov.tipo}</td>
                <td>${mov.items.map(i => i.producto?.nombre).join(", ")}</td>
                <td>${mov.items.reduce((acc, i) => acc + (i.cantidad || 0), 0)}</td>
                <td>${mov.bodegaOrigen?.nombre || "-"}</td>
                <td>${mov.bodegaDestino?.nombre || "-"}</td>
                <td>${mov.usuario?.nombre || "-"}</td>
                <td>${mov.fecha ? new Date(mov.fecha).toLocaleString("es-CO") : "-"}</td>
                <td>
                    <button class="btn-edit" data-id="${mov.id}">✏️</button>
                    <button class="btn-delete" data-id="${mov.id}">🗑️</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (err) {
        console.error("Error al cargar movimientos:", err);
    }
}

// ====== GUARDAR O EDITAR ======
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const movimientoData = {
        tipo: document.getElementById("tipo").value,
        usuario: { id: parseInt(document.getElementById("usuario").value) || 0 },
        bodegaOrigen: document.getElementById("origen").value
            ? { id: parseInt(document.getElementById("origen").value) }
            : null,
        bodegaDestino: document.getElementById("destino").value
            ? { id: parseInt(document.getElementById("destino").value) }
            : null,
        items: [
            {
                producto: { id: parseInt(document.getElementById("producto").value) || 0 },
                cantidad: parseInt(document.getElementById("cantidad").value) || 0,
            },
        ],
    };

    try {
        if (editId) {
            await fetch(`http://localhost:8080/api/movimientos/${editId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(movimientoData),
            });
            editId = null;
        } else {
            await fetch("http://localhost:8080/api/movimientos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(movimientoData),
            });
        }
        form.reset();
        renderTable();
    } catch (err) {
        console.error("Error al guardar movimiento:", err);
    }
});

// ====== EDITAR / ELIMINAR ======
tableBody.addEventListener("click", async (e) => {
    const id = e.target.getAttribute("data-id");

    if (e.target.classList.contains("btn-edit")) {
        const res = await fetch(`http://localhost:8080/api/movimientos/${id}`);
        const mov = await res.json();

        document.getElementById("tipo").value = mov.tipo || "";
        document.getElementById("producto").value = mov.items[0]?.producto?.id || "";
        document.getElementById("cantidad").value = mov.items[0]?.cantidad || "";
        document.getElementById("origen").value = mov.bodegaOrigen?.id || "";
        document.getElementById("destino").value = mov.bodegaDestino?.id || "";
        document.getElementById("usuario").value = mov.usuario?.id || "";

        editId = id;
    }

    if (e.target.classList.contains("btn-delete")) {
        if (confirm("¿Eliminar este movimiento?")) {
            await fetch(`http://localhost:8080/api/movimientos/${id}`, { method: "DELETE" });
            renderTable();
        }
    }
});

// ====== BOTÓN REGRESO ======
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// ====== INICIALIZAR ======
renderTable();
