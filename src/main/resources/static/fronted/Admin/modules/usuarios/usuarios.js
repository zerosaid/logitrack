const form = document.getElementById("usuarioForm");
const tablaBody = document.querySelector("#tablaUsuarios tbody");
const backBtn = document.getElementById("backBtn");

let usuarios = JSON.parse(localStorage.getItem("usuarios")) || [];
let editIndex = null;

// Renderizar tabla
function renderUsuarios() {
    tablaBody.innerHTML = "";
    usuarios.forEach((u, index) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
      <td>${index + 1}</td>
      <td>${u.nombre}</td>
      <td>${u.correo}</td>
      <td>${u.rol}</td>
      <td>
        <button class="btn-edit" data-index="${index}">✏️</button>
        <button class="btn-delete" data-index="${index}">🗑️</button>
      </td>
    `;
        tablaBody.appendChild(fila);
    });
    localStorage.setItem("usuarios", JSON.stringify(usuarios));
}

// Guardar o editar usuario
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const nuevoUsuario = {
        nombre: document.getElementById("nombre").value.trim(),
        correo: document.getElementById("correo").value.trim(),
        rol: document.getElementById("rol").value,
        contrasena: document.getElementById("contrasena").value,
    };

    if (editIndex !== null) {
        usuarios[editIndex] = nuevoUsuario;
        editIndex = null;
    } else {
        usuarios.push(nuevoUsuario);
    }

    form.reset();
    renderUsuarios();
});

// Editar / Eliminar usuario
tablaBody.addEventListener("click", (e) => {
    const index = e.target.getAttribute("data-index");

    if (e.target.classList.contains("btn-edit")) {
        const u = usuarios[index];
        document.getElementById("nombre").value = u.nombre;
        document.getElementById("correo").value = u.correo;
        document.getElementById("rol").value = u.rol;
        document.getElementById("contrasena").value = u.contrasena;
        editIndex = index;
    }

    if (e.target.classList.contains("btn-delete")) {
        if (confirm("¿Deseas eliminar este usuario?")) {
            usuarios.splice(index, 1);
            renderUsuarios();
        }
    }
});

// Botón para volver
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// Inicializar
renderUsuarios();
