# LogiTrack S.A. - Sistema de Gestión y Auditoría de Bodegas

## Descripción del Proyecto

LogiTrack S.A. administra varias bodegas distribuidas en distintas ciudades, encargadas de almacenar productos y gestionar movimientos de inventario (entradas, salidas y transferencias).
Actualmente, el control se realiza manualmente en hojas de cálculo, sin trazabilidad ni control de accesos.

Este proyecto consiste en un **backend centralizado desarrollado en Spring Boot** que permite:

* Controlar todos los movimientos de inventario entre bodegas.
* Registrar automáticamente los cambios mediante auditorías.
* Proteger la información con autenticación JWT.
* Ofrecer endpoints REST documentados y seguros.

El sistema está diseñado para ser escalable, seguro y auditable, facilitando la gestión de inventarios y la toma de decisiones.

---

## Objetivo General

Desarrollar un sistema de gestión y auditoría de bodegas que permita registrar transacciones de inventario y generar reportes auditables de los cambios realizados por cada usuario.

---

## Requisitos Funcionales

### 1. Gestión de Bodegas

* CRUD completo: registrar, consultar, actualizar y eliminar bodegas.
* Campos: `id`, `nombre`, `ubicacion`, `capacidad`, `encargado`.

### 2. Gestión de Productos

* CRUD completo de productos.
* Campos: `id`, `nombre`, `categoria`, `stock`, `precio`.

### 3. Movimientos de Inventario

* Registrar entradas, salidas y transferencias entre bodegas.
* Cada movimiento almacena:

  * Fecha
  * Tipo de movimiento (`ENTRADA`, `SALIDA`, `TRANSFERENCIA`)
  * Usuario responsable
  * Bodega origen/destino
  * Productos y cantidades

### 4. Auditoría de Cambios

* Registrar todas las operaciones (`INSERT`, `UPDATE`, `DELETE`)
* Guardar información: fecha/hora, usuario, entidad afectada, valores anteriores/nuevos
* Auditoría automática mediante JPA EntityListeners o aspectos con anotaciones personalizadas.

### 5. Autenticación y Seguridad

* Spring Security + JWT
* Endpoints `/auth/login` y `/auth/register`
* Rutas seguras: `/bodegas`, `/productos`, `/movimientos`
* Roles de usuario: `ADMIN` / `EMPLEADO`

### 6. Consultas Avanzadas y Reportes

* Filtros:

  * Productos con stock bajo (< 10 unidades)
  * Movimientos por rango de fechas
  * Auditorías por usuario o tipo de operación
* Reporte REST: stock total por bodega y productos más movidos

### 7. Documentación

* Documentación de API con Swagger/OpenAPI 3
* Pruebas de endpoints protegidos con JWT

### 8. Excepciones y Validaciones

* Manejo global de errores con `@ControllerAdvice`
* Validaciones con `@NotNull`, `@Size`, `@Min`, etc.
* Respuestas JSON personalizadas para errores: 400, 401, 404, 500

### 9. Despliegue

* Base de datos MySQL configurada en `application.properties`
* Scripts SQL: `schema.sql`, `data.sql`
* Ejecución con Tomcat embebido o externo
* Frontend básico en HTML/CSS/JS para login y consultas principales

---

## Estructura del Proyecto

```
src/
 ├─ controller/
 ├─ service/
 ├─ repository/
 ├─ model/
 ├─ config/
 ├─ security/
 └─ exception/
frontend/
 ├─ index.html
 ├─ login.html
 ├─ css/
 └─ js/
```

---

## Instalación y Ejecución

1. Clonar el repositorio:

```bash
git clone https://github.com/usuario/logitrack-backend.git
```

2. Configurar MySQL y crear base de datos:

```sql
CREATE DATABASE logitrack;
```

3. Ejecutar scripts SQL:

```bash
mysql -u root -p logitrack < schema.sql
mysql -u root -p logitrack < data.sql
```

4. Configurar `application.properties` con usuario, contraseña y URL de la base de datos.

5. Ejecutar la aplicación con Maven:

```bash
mvn spring-boot:run
```

6. Acceder a Swagger para probar los endpoints:

```
http://localhost:8080/swagger-ui.html
```

---

## Ejemplos de Endpoints

* **Login:** `POST /auth/login`
* **Registrar usuario:** `POST /auth/register`
* **CRUD Bodegas:** `GET/POST/PUT/DELETE /bodegas`
* **CRUD Productos:** `GET/POST/PUT/DELETE /productos`
* **Registrar Movimiento:** `POST /movimientos`
* **Auditoría:** `GET /auditorias`

---

## Entregables

* Código fuente completo del backend en Spring Boot
* Scripts SQL (`schema.sql`, `data.sql`)
* Documentación Swagger
* Carpeta `frontend/` con HTML/CSS/JS para pruebas
* README.md (este documento)
* Documento explicativo con diagrama de clases, arquitectura, ejemplo de token JWT y uso
* Repositorio en GitHub

---

## Capturas y Pruebas

*(Se pueden incluir imágenes de Swagger y de la interfaz frontend aquí)*

---

## Licencia

Este proyecto es propiedad de LogiTrack S.A. y no debe ser distribuido sin autorización.
