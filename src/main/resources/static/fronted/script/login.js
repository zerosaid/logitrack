document.getElementById("loginForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim(); // Nota: El backend ahora usa solo username y password
    const password = document.getElementById("password").value.trim();
    const errorMsg = document.getElementById("error-msg");

    // Petición al backend corregida al endpoint /api/usuarios/login
    fetch('/api/usuarios/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: username, password: password })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text || 'Credenciales incorrectas'); });
            }
            return response.json();
        })
        .then(data => {
            sessionStorage.setItem("userRole", data.role);
            sessionStorage.setItem("userName", data.username);
            sessionStorage.setItem("token", data.token);
            const basePath = './fronted/';
            if (data.role === "ADMIN") {
                window.location.href = basePath + "admin/admin-dashboard.html";
            } else {
                window.location.href = basePath + "empleado/user-dashboard.html";
            }
        })
        .catch(error => {
            errorMsg.textContent = error.message;
            console.error('Error en login:', error);
        });
});

// Mostrar / ocultar contraseña
const togglePassword = document.getElementById("togglePassword");
const passwordInput = document.getElementById("password");
const eyeIcon = document.getElementById("eyeIcon");

togglePassword.addEventListener("click", () => {
    const isPassword = passwordInput.type === "password";
    passwordInput.type = isPassword ? "text" : "password";
    eyeIcon.src = isPassword ? "./icon/ojo-a.png" : "./icon/ojo-c.png";
    eyeIcon.alt = isPassword ? "Ocultar contraseña" : "Mostrar contraseña";
});