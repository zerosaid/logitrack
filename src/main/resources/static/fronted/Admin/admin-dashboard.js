// Selección de elementos
const menuItems = document.querySelectorAll(".sidebar ul li");
const content = document.getElementById("content");
const logoutBtn = document.getElementById("logoutBtn");
const welcomeMsg = document.getElementById("welcomeMsg");

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
            loadDashboardCards();
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

// ===============================
// Función para cargar tarjetas con datos reales
// ===============================
async function loadDashboardCards() {
    try {
        // Peticiones al backend (ajusta las URLs según tu API Spring Boot)
        const usuariosRes = await fetch("/api/usuarios/count");
        const movimientosRes = await fetch("/api/movimientos/recent");
        const auditoriasRes = await fetch("/api/auditoria/pending");

        const usuariosData = await usuariosRes.json(); // { total: 25 }
        const movimientosData = await movimientosRes.json(); // { ultimaFecha: "2025-11-15" }
        const auditoriasData = await auditoriasRes.json(); // { pendientes: 3 }

        content.innerHTML = `
            <div class="card">
                <h3>Usuarios registrados</h3>
                <p>${usuariosData.total} usuarios activos</p>
            </div>

            <div class="card">
                <h3>Movimientos recientes</h3>
                <p>Última actualización: ${movimientosData.ultimaFecha}</p>
            </div>

            <div class="card">
                <h3>Auditorías pendientes</h3>
                <p>${auditoriasData.pendientes} revisiones en curso</p>
            </div>
        `;
    } catch (error) {
        console.error("Error cargando datos del dashboard:", error);
        content.innerHTML = `<p>Error al cargar datos. Intenta recargar la página.</p>`;
    }
}

// ===============================
// Cerrar sesión
// ===============================
logoutBtn.addEventListener("click", () => {
    sessionStorage.clear();
    window.location.href = "../index.html"; // ajusta si tu login está en otra ruta
});

// ===============================
// Mensaje de bienvenida dinámico
// ===============================
const usuarioLogeado = sessionStorage.getItem("usuario"); // guardado en login
if (usuarioLogeado) {
    welcomeMsg.textContent = `Bienvenido, ${usuarioLogeado}`;
}
