const form = document.getElementById("movForm");
const tableBody = document.querySelector("#movTable tbody");
const backBtn = document.getElementById("backBtn");

let movimientos = JSON.parse(localStorage.getItem("movimientos")) || [];
let editIndex = null;

// ====== Renderizar tabla ======
function renderTable() {
    tableBody.innerHTML = "";
    movimientos.forEach((mov, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
      <td>${index + 1}</td>
      <td>${mov.tipo}</td>
      <td>${mov.producto}</td>
      <td>${mov.cantidad}</td>
      <td>${mov.origen || "-"}</td>
      <td>${mov.destino || "-"}</td>
      <td>${mov.usuario}</td>
      <td>${mov.fecha}</td>
      <td>
        <button class="btn-edit" data-index="${index}">✏️</button>
        <button class="btn-delete" data-index="${index}">🗑️</button>
      </td>
    `;
        tableBody.appendChild(row);
    });
    localStorage.setItem("movimientos", JSON.stringify(movimientos));
}

// ====== Guardar movimiento ======
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nuevoMovimiento = {
        tipo: document.getElementById("tipo").value,
        producto: document.getElementById("producto").value.trim(),
        cantidad: document.getElementById("cantidad").value,
        origen: document.getElementById("origen").value.trim(),
        destino: document.getElementById("destino").value.trim(),
        usuario: document.getElementById("usuario").value.trim(),
        fecha: new Date().toLocaleString("es-CO"),
    };

    if (editIndex !== null) {
        movimientos[editIndex] = nuevoMovimiento;
        editIndex = null;
    } else {
        movimientos.push(nuevoMovimiento);
    }

    form.reset();
    renderTable();
});

// ====== Editar movimiento ======
tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-edit")) {
        const index = e.target.getAttribute("data-index");
        const mov = movimientos[index];

        document.getElementById("tipo").value = mov.tipo;
        document.getElementById("producto").value = mov.producto;
        document.getElementById("cantidad").value = mov.cantidad;
        document.getElementById("origen").value = mov.origen;
        document.getElementById("destino").value = mov.destino;
        document.getElementById("usuario").value = mov.usuario;

        editIndex = index;
    }

    if (e.target.classList.contains("btn-delete")) {
        const index = e.target.getAttribute("data-index");
        if (confirm("¿Eliminar este movimiento?")) {
            movimientos.splice(index, 1);
            renderTable();
        }
    }
});

// ====== Botón de regreso ======
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// ====== Inicializar ======
renderTable();
