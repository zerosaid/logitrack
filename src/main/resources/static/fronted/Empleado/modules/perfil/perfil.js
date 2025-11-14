// === Datos iniciales simulados ===
let usuario = JSON.parse(localStorage.getItem("usuarioActivo")) || {
    nombre: "Juan Pérez",
    correo: "juanperez@logitrack.com",
    rol: "Empleado",
    contraseña: "123456"
};

// === Mostrar datos en la interfaz ===
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("nombre").textContent = usuario.nombre;
    document.getElementById("correo").textContent = usuario.correo;
    document.getElementById("rol").textContent = usuario.rol;
});

// === Actualizar información del perfil ===
document.getElementById("perfilForm").addEventListener("submit", (e) => {
    e.preventDefault();

    const nuevoNombre = document.getElementById("nuevoNombre").value.trim();
    const nuevoCorreo = document.getElementById("nuevoCorreo").value.trim();
    const nuevaContra = document.getElementById("nuevaContra").value.trim();

    if (!nuevoNombre && !nuevoCorreo && !nuevaContra) {
        alert("No se detectaron cambios.");
        return;
    }

    if (nuevoNombre) usuario.nombre = nuevoNombre;
    if (nuevoCorreo) usuario.correo = nuevoCorreo;
    if (nuevaContra) usuario.contraseña = nuevaContra;

    localStorage.setItem("usuarioActivo", JSON.stringify(usuario));

    alert("Perfil actualizado correctamente ✅");
    window.location.reload();
});
