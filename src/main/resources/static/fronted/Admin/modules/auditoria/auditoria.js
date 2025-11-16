// ==== BOTÓN DE REGRESO ====
document.getElementById("backBtn").addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html"; // Ajuste correcto según estructura
});

// ==== DATOS DE PRUEBA (puedes reemplazar con fetch desde el backend) ====
const auditorias = [
    {
        id: 1,
        usuario: "admin01",
        entidad: "Producto",
        tipo: "INSERT",
        fecha: "2025-11-13",
        detalles: "Se agregó nuevo producto: Laptop Dell"
    },
    {
        id: 2,
        usuario: "empleado12",
        entidad: "Movimiento",
        tipo: "UPDATE",
        fecha: "2025-11-12",
        detalles: "Actualizó cantidad en bodega central"
    },
    {
        id: 3,
        usuario: "admin02",
        entidad: "Bodega",
        tipo: "DELETE",
        fecha: "2025-11-10",
        detalles: "Eliminó bodega temporal #4"
    }
];

// ==== CARGAR TABLA ====
const tableBody = document.querySelector("#auditTable tbody");

function cargarAuditorias(data) {
    tableBody.innerHTML = "";

    data.forEach(a => {
        const row = document.createElement("tr");

        const tipoClass =
            a.tipo === "INSERT" ? "tag-insert" :
                a.tipo === "UPDATE" ? "tag-update" :
                    "tag-delete";

        row.innerHTML = `
      <td>${a.id}</td>
      <td>${a.usuario}</td>
      <td>${a.entidad}</td>
      <td class="${tipoClass}">${a.tipo}</td>
      <td>${a.fecha}</td>
      <td>${a.detalles}</td>
    `;

        tableBody.appendChild(row);
    });
}

cargarAuditorias(auditorias);

// ==== FILTROS ====
document.getElementById("filterBtn").addEventListener("click", () => {
    const usuario = document.getElementById("searchUser").value.toLowerCase();
    const tipo = document.getElementById("filterType").value;
    const from = document.getElementById("fromDate").value;
    const to = document.getElementById("toDate").value;

    const filtradas = auditorias.filter(a => {
        const matchUsuario = usuario ? a.usuario.toLowerCase().includes(usuario) : true;
        const matchTipo = tipo ? a.tipo === tipo : true;
        const matchFecha =
            (!from || a.fecha >= from) &&
            (!to || a.fecha <= to);

        return matchUsuario && matchTipo && matchFecha;
    });

    cargarAuditorias(filtradas);
});

document.getElementById("clearBtn").addEventListener("click", () => {
    document.getElementById("searchUser").value = "";
    document.getElementById("filterType").value = "";
    document.getElementById("fromDate").value = "";
    document.getElementById("toDate").value = "";
    cargarAuditorias(auditorias);
});
