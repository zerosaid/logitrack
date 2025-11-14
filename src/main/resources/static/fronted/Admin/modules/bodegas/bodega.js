const form = document.getElementById("bodegaForm");
const tableBody = document.querySelector("#bodegaTable tbody");
const backBtn = document.getElementById("backBtn");

let bodegas = JSON.parse(localStorage.getItem("bodegas")) || [];
let editIndex = null;

// ====== Renderizar tabla ======
function renderTable() {
    tableBody.innerHTML = "";
    bodegas.forEach((bodega, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
      <td>${index + 1}</td>
      <td>${bodega.nombre}</td>
      <td>${bodega.ubicacion}</td>
      <td>${bodega.capacidad}</td>
      <td>${bodega.encargado}</td>
      <td>
        <button class="btn-edit" data-index="${index}">✏️</button>
        <button class="btn-delete" data-index="${index}">🗑️</button>
      </td>
    `;
        tableBody.appendChild(row);
    });
    localStorage.setItem("bodegas", JSON.stringify(bodegas));
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

    if (editIndex !== null) {
        bodegas[editIndex] = nuevaBodega;
        editIndex = null;
    } else {
        bodegas.push(nuevaBodega);
    }

    form.reset();
    renderTable();
});

// ====== Editar bodega ======
tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-edit")) {
        const index = e.target.getAttribute("data-index");
        const bodega = bodegas[index];

        document.getElementById("nombre").value = bodega.nombre;
        document.getElementById("ubicacion").value = bodega.ubicacion;
        document.getElementById("capacidad").value = bodega.capacidad;
        document.getElementById("encargado").value = bodega.encargado;

        editIndex = index;
    }

    if (e.target.classList.contains("btn-delete")) {
        const index = e.target.getAttribute("data-index");
        if (confirm("¿Deseas eliminar esta bodega?")) {
            bodegas.splice(index, 1);
            renderTable();
        }
    }
});

// ====== Botón para volver ======
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// ====== Inicializar ======
renderTable();
