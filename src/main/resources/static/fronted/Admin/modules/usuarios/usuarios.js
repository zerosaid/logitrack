const form = document.getElementById("usuarioForm");
const tablaBody = document.querySelector("#tablaUsuarios tbody");
const backBtn = document.getElementById("backBtn");

let usuarios = [];
let editIndex = null;

// Cargar usuarios desde el backend
function cargarUsuarios() {
    const token = sessionStorage.getItem('token');
    if (!token) {
        alert('No estás autenticado. Redirigiendo al login.');
        window.location.href = '/fronted/index.html';
        return;
    }

    fetch('/api/usuarios', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        console.log('Respuesta del servidor (cargar):', response);
        if (!response.ok) {
            throw new Error(`Error al cargar usuarios: ${response.status} - ${response.statusText}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos (cargar):', data);
        if (!Array.isArray(data)) {
            throw new Error('La respuesta no es un array de usuarios');
        }
        usuarios = data;
        renderUsuarios();
    })
    .catch(error => {
        console.error('Error al cargar usuarios:', error);
        alert('Error al cargar el listado de usuarios: ' + error.message);
        tablaBody.innerHTML = '<tr><td colspan="6">Error al cargar datos. Revisa la consola (F12) para más detalles.</td></tr>';
    });
}

// Renderizar tabla
function renderUsuarios() {
    tablaBody.innerHTML = '';
    if (usuarios.length === 0) {
        tablaBody.innerHTML = '<tr><td colspan="6">No hay usuarios registrados.</td></tr>';
        return;
    }
    usuarios.forEach((u, index) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
        <td>${index + 1}</td>
        <td>${u.nombre || u.name || ''}</td>
        <td>${u.email || u.correo || ''}</td>
        <td>${u.username || 'Sin username'}</td>
        <td>${u.role || u.rol || 'Sin rol'}</td>
        <td>${u.activo ? 'Activo' : 'Inactivo'}</td>
        <td>
        <button class="btn-edit" data-index="${index}">✏️</button>
        <button class="btn-delete" data-index="${index}" data-action="desactivar">Desactivar</button>
        <button class="btn-delete" data-index="${index}" data-action="eliminar">Eliminar</button>
        </td>
    `;
        tablaBody.appendChild(fila);
    });
}

// Guardar o editar usuario
form.addEventListener("submit", (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const contrasena = document.getElementById("contrasena").value;
    const confirmarContrasena = document.getElementById("confirmarContrasena").value;

    if (!username) {
        alert('El nombre de usuario es obligatorio.');
        return;
    }
    if (contrasena !== confirmarContrasena) {
        alert('Las contraseñas no coinciden. Por favor, inténtalo de nuevo.');
        return;
    }

    const nuevoUsuario = {
        username: username,
        nombre: document.getElementById("nombre").value.trim(),
        email: document.getElementById("correo").value.trim(),
        role: document.getElementById("rol").value,
        password: contrasena,
        activo: true // Por defecto activo
    };

    const token = sessionStorage.getItem('token');
    const creadorUsername = sessionStorage.getItem('username');
    console.log('Token:', token);
    console.log('Creador Username:', creadorUsername);
    if (!token) {
        alert('No estás autenticado. Inicia sesión nuevamente.');
        window.location.href = '/fronted/index.html';
        return;
    }
    if (!creadorUsername) {
        alert('No se pudo identificar al usuario creador. Inicia sesión nuevamente. Verifica el login.');
        console.error('creadorUsername es null. Asegúrate de que el login guarde "username" en sessionStorage.');
        return;
    }

    console.log('Enviando solicitud POST a:', `/api/usuarios/crear/${creadorUsername}`);
    console.log('Datos enviados:', nuevoUsuario);

    fetch(`/api/usuarios/crear/${creadorUsername}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuevoUsuario)
    })
    .then(response => {
        console.log('Respuesta del servidor (crear):', response);
        if (!response.ok) {
            return response.text().then(text => { throw new Error(`Error al crear usuario: ${response.status} - ${text}`); });
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos (crear):', data);
        usuarios.push(data);
        form.reset();
        renderUsuarios();
    })
    .catch(error => {
        console.error('Error al crear usuario:', error);
        alert('Error al crear el usuario: ' + error.message);
    });
});

// Editar / Eliminar usuario
tablaBody.addEventListener("click", (e) => {
    const index = e.target.getAttribute("data-index");
    const action = e.target.getAttribute("data-action");

    if (e.target.classList.contains("btn-edit")) {
        const u = usuarios[index];
        document.getElementById("username").value = u.username || '';
        document.getElementById("nombre").value = u.nombre || u.name || '';
        document.getElementById("correo").value = u.email || u.correo || '';
        document.getElementById("rol").value = u.role || u.rol || 'EMPLEADO';
        document.getElementById("contrasena").value = '';
        document.getElementById("confirmarContrasena").value = '';
        editIndex = index;
    }

    if (e.target.classList.contains("btn-delete")) {
        if (!confirm(`¿Deseas ${action === 'eliminar' ? 'eliminar permanentemente' : 'desactivar'} este usuario?`)) {
            return;
        }

        const token = sessionStorage.getItem('token');
        const editorUsername = sessionStorage.getItem('username');
        const userId = usuarios[index].id;
        console.log(`Ejecutando ${action} para usuario con ID:`, userId, 'por:', editorUsername);

        if (!editorUsername) {
            alert('No se pudo identificar al usuario editor. Inicia sesión nuevamente.');
            return;
        }
        if (!userId) {
            alert('No se pudo identificar el ID del usuario a ' + action + '.');
            return;
        }

        const url = action === 'eliminar'
            ? `/api/usuarios/eliminar/${userId}/${editorUsername}`
            : `/api/usuarios/${userId}/${editorUsername}`;

        fetch(url, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            console.log(`Respuesta del servidor (${action}):`, response);
            if (!response.ok) {
                return response.text().then(text => { throw new Error(`Error al ${action} usuario: ${response.status} - ${text}`); });
            }
            if (action === 'desactivar') {
                usuarios[index].activo = false; // Actualizar estado local
            } else {
                usuarios.splice(index, 1); // Eliminar de la lista si es eliminación
            }
            renderUsuarios();
        })
        .catch(error => {
            console.error(`Error al ${action} usuario:`, error);
            alert(`Error al ${action} el usuario: ${error.message}`);
            if (action === 'eliminar') {
                usuarios.splice(index, 0, usuarios[index]); // Revertir si falla
            }
            renderUsuarios();
        });
    }
});

// Botón para volver
backBtn.addEventListener("click", () => {
    window.location.href = "../../admin-dashboard.html";
});

// Inicializar
document.addEventListener('DOMContentLoaded', cargarUsuarios);