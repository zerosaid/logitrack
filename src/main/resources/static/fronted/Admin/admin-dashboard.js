// Selección de elementos
const menuItems = document.querySelectorAll(".sidebar ul li");
const content = document.getElementById("content");
const logoutBtn = document.getElementById("logoutBtn");

// Manejo del menú lateral
menuItems.forEach((item) => {
    item.addEventListener("click", () => {
        menuItems.forEach((li) => li.classList.remove("active"));
        item.classList.add("active");

        const section = item.getAttribute("data-section");
        updateContent(section);
    });
});

// Función principal de actualización de contenido
function updateContent(section) {
    switch (section) {
        case "inicio":
            content.innerHTML = `
        <div class="card"><h3>Usuarios registrados</h3><p>25 usuarios activos</p></div>
        <div class="card"><h3>Movimientos recientes</h3><p>Última actualización: hoy</p></div>
        <div class="card"><h3>Auditorías pendientes</h3><p>3 revisiones en curso</p></div>
      `;
            break;

        case "usuarios":
            window.location.href = "./modules/usuarios/usuarios.html";
            break;

        case "bodegas":
            window.location.href = "./modules/bodegas/bodega.html";
            break;

        case "movimientos":
            window.location.href = "./modules/movimientos/movimientos.html";
            break;

        case "productos":
            window.location.href = "./modules/productos/productos.html";
            break;

        case "auditoria":
            window.location.href = "./modules/auditoria/auditoria.html";
            break;

        case "reportes":
            window.location.href = "./modules/reportes/reportes.html";
            break;
    }
}

// Cerrar sesión
logoutBtn.addEventListener("click", () => {
    sessionStorage.clear();
    window.location.href = "../index.html"; // ajusta si tu login está en otra ruta
});
