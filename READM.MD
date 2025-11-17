
# 🚛📦 LOGITRACK S.A.
**Sistema de Gestión y Auditoría de Inventarios en Bodegas**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-6DB33F?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Security-000000?style=for-the-badge&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger)
![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk)

Repositorio oficial: https://github.com/zerosaid/logitrack

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                          DESCRIPCIÓN GENERAL
</p>

LogiTrack S.A. es un **backend profesional** para administrar bodegas distribuidas, controlar inventarios, registrar movimientos y auditar todas las operaciones realizadas por los usuarios.

Problemas que resuelve:
 - ❌ Falta de trazabilidad
 - ❌ Manejo manual en hojas de cálculo
 - ❌ Cero auditoría
 - ❌ Sin control de accesos

Soluciones clave:
 - ✔ Backend robusto con Spring Boot
 - ✔ Auditoría automática integrada
 - ✔ Seguridad con JWT + Roles
 - ✔ CRUD completos (bodegas, productos, usuarios)
 - ✔ Reportes avanzados
 - ✔ Documentación con Swagger
 - ✔ Arquitectura escalable y limpia

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         OBJETIVO GENERAL
</p>
Construir un backend **seguro, trazable y escalable** que permita gestionar inventarios y movimientos entre bodegas, con auditoría detallada de cada acción realizada por los usuarios.

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         ESTRUCTURA & ARQUITECTURA
</p>

```
+-----------------------------+
|         FRONTEND            |
|     HTML / CSS / JS         |
+-------------+---------------+
              |
              | HTTP / JSON
              |
+-------------v---------------+
|       SPRING BOOT           |
|    Controladores REST       |
+-------------+---------------+
              |
+-------------v---------------+
|       SERVICE LAYER         |
| Lógica + Auditoría + JWT    |
+-------------+---------------+
              |
+-------------v---------------+
|       JPA REPOSITORY        |
| CRUD + Auditoría JPA         |
+-------------+---------------+
              |
+-------------v---------------+
|           MYSQL             |
| Bodegas / Productos / Logs  |
+-----------------------------+
```

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         ESTRUCTURA DEL PROYECTO
</p>

```
📁 src/
├─ controller/ # Controladores REST
├─ service/ # Lógica de negocio
├─ repository/ # Acceso a datos con JPA
├─ model/ # Entidades y modelos
├─ config/ # Configuraciones generales
├─ security/ # JWT, roles y seguridad
└─ exception/ # Manejo de errores centralizado

📁 frontend/
├─ index.html        # Dashboard principal
├─ login.html        # Página de login
├─ css/              # Estilos
└─ js/               # Scripts
```

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         DIAGRAMA UML SIMPLIFICADO (ASCII)
</p>

```
+----------------+        +----------------+
|    Bodega      | 1..*   |   Producto     |
+----------------+        +----------------+
| id             |        | id             |
| nombre         |        | nombre         |
| ubicacion      |        | categoria      |
| capacidad      |        | precio         |
| encargado      |        | stock          |
+----------------+        +----------------+

            1           1..*
Bodega --------------- Movimiento --------------- ProductoMovimiento
                               |
                               | 1
                     +-----------------------+
                     |      Movimiento       |
                     +-----------------------+
                     | id                    |
                     | tipo                  |
                     | fecha                 |
                     | usuario               |
                     | bodegaOrigen          |
                     | bodegaDestino         |
                     +-----------------------+

+----------------+
|   Auditoria    |
+----------------+
| id             |
| entidad        |
| usuario        |
| fecha          |
| accion         |
| valoresPrev    |
| valoresNuevo   |
+----------------+
```

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         INSTALACIÓN
</p>

1) Clonar el repositorio:  
   `git clone https://github.com/zerosaid/logitrack`

2) Crear base de datos:  
   `CREATE DATABASE logitrack;`

3) Ejecutar scripts SQL:  
   `mysql -u root -p logitrack < schema.sql`  
   `mysql -u root -p logitrack < data.sql`

4) Configurar MySQL:  
   Editar `application.properties` con usuario y contraseña

5) Ejecutar aplicación:  
   `mvn spring-boot:run`

6) Acceder a Swagger:  
   `http://localhost:8080/swagger-ui.html`

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         ENDPOINTS PRINCIPALES
</p>

**Autenticación:**  
 - POST /auth/login  
 - POST /auth/register  

**Bodegas:**  
 - GET /bodegas  
 - POST /bodegas  
 - PUT /bodegas/{id}  
 - DELETE /bodegas/{id}  

**Productos:**  
 - GET /productos  
 - POST /productos  
 - PUT /productos/{id}  
 - DELETE /productos/{id}  

**Movimientos:**  
 - POST /movimientos  

**Auditoría:**  
 - GET /auditorias

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         ENTREGABLES
</p>

 - Backend completo (Spring Boot)  
 - Scripts SQL  
 - Frontend básico (HTML/CSS/JS)  
 - Swagger  
 - Diagramas ASCII y UML  
 - README unificado y llamativo

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         LICENCIA
</p>

Este proyecto es propiedad privada de LogiTrack S.A.  
Prohibida su distribución sin autorización escrita.

<p align="center" style="background-color:#222; color:#fff; padding:8px 4px; border-radius:4px; font-weight:bold; letter-spacing:2px;">
                         FIN DEL README
</p>
