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

    // CORREGIDO: Usar /api/auth/login
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: username, password: password })
    })
    .then(response => {
        console.log('Respuesta del servidor (login):', response);
        if (!response.ok) {
            return response.text().then(text => { 
                throw new Error(text || 'Credenciales incorrectas'); 
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos (login):', data);
        
        // Guardar datos en localStorage
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", data.username);
        localStorage.setItem("userRole", data.role);
        
        // CORREGIDO: Rutas sin /fronted/
        if (data.role === "ADMIN") {
            window.location.href = "/Admin/admin-dashboard.html";
        } else {
            window.location.href = "/Empleado/user-dashboard.html";
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
        // CORREGIDO: Rutas absolutas
        eyeIcon.src = isPassword ? "/icon/ojo-a.png" : "/icon/ojo-c.png";
        eyeIcon.alt = isPassword ? "Ocultar contraseña" : "Mostrar contraseña";
    });
}