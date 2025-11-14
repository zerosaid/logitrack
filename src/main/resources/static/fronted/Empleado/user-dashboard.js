document.addEventListener("DOMContentLoaded", () => {
    const menuItems = document.querySelectorAll(".sidebar li");
    const logoutBtn = document.getElementById("logoutBtn");

    // Cambiar entre secciones
    menuItems.forEach(item => {
        item.addEventListener("click", () => {
            const section = item.getAttribute("data-section");

            // Quitar activo
            menuItems.forEach(li => li.classList.remove("active"));
            item.classList.add("active");

            // Redirección a módulos
            if (section === "inicio") {
                window.location.href = "./user-dashboard.html";
            } else {
                window.location.href = `./modules/${section}/${section}.html`;
            }
        });
    });

    // Volver al login
    logoutBtn.addEventListener("click", () => {
        window.location.href = "../../index.html";
    });
});
