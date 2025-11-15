document.getElementById("loginForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorMsg = document.getElementById("error-msg");

    // Validar que los campos no estén vacíos
    if (!username || !password) {
        errorMsg.textContent = "Por favor, completa todos los campos.";
        return;
    }

    // Petición al backend corregida al endpoint /api/usuarios/login
    fetch('/api/usuarios/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: username, password: password })
    })
    .then(response => {
        console.log('Respuesta del servidor (login):', response);
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text || 'Credenciales incorrectas'); });
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos (login):', data);
        // Guardar datos en sessionStorage con claves consistentes
        sessionStorage.setItem("token", data.token);
        sessionStorage.setItem("username", data.username); // Corregido a "username"
        sessionStorage.setItem("userRole", data.role);
        const basePath = '/fronted/'; // Ruta absoluta desde la raíz del servidor
        if (data.role === "ADMIN") {
            window.location.href = basePath + "admin/admin-dashboard.html";
        } else {
            window.location.href = basePath + "empleado/user-dashboard.html";
        }
    })
    .catch(error => {
        console.error('Error en login:', error);
        errorMsg.textContent = error.message || "Error al iniciar sesión. Intenta de nuevo.";
    });
});

// Mostrar / ocultar contraseña
const togglePassword = document.getElementById("togglePassword");
const passwordInput = document.getElementById("password");
const eyeIcon = document.getElementById("eyeIcon");

if (togglePassword && passwordInput && eyeIcon) {
    togglePassword.addEventListener("click", () => {
        const isPassword = passwordInput.type === "password";
        passwordInput.type = isPassword ? "text" : "password";
        eyeIcon.src = isPassword ? "./icon/ojo-a.png" : "./icon/ojo-c.png";
        eyeIcon.alt = isPassword ? "Ocultar contraseña" : "Mostrar contraseña";
    });
}