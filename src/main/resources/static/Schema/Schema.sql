DROP DATABASE IF EXISTS logitrack;

CREATE DATABASE logitrack
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE logitrack;

-- ===============================
-- USUARIOS
-- ===============================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(150),
    email VARCHAR(150),
    role ENUM('ADMIN', 'EMPLEADO') DEFAULT 'EMPLEADO',
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- BODEGA
-- ===============================
CREATE TABLE bodega (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    capacidad INT NOT NULL CHECK (capacidad >= 0),
    encargado VARCHAR(150),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- PRODUCTO (Versión 2)
-- ===============================
CREATE TABLE producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    categoria VARCHAR(255) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock_min INT NOT NULL DEFAULT 5,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- INDEXES útiles
CREATE INDEX idx_producto_categoria ON producto(categoria);
CREATE INDEX idx_producto_codigo ON producto(codigo);

-- ===============================
-- STOCK POR BODEGA
-- ===============================
CREATE TABLE stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_stock_bodega_producto UNIQUE (bodega_id, producto_id),
    CONSTRAINT fk_stock_bodega FOREIGN KEY (bodega_id) REFERENCES bodega(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE
);

-- ===============================
-- MOVIMIENTOS
-- ===============================
CREATE TABLE movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    usuario_id BIGINT NOT NULL,
    bodega_origen_id BIGINT NULL,
    bodega_destino_id BIGINT NULL,
    observaciones VARCHAR(255),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mov_bod_origen FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id) ON DELETE SET NULL,
    CONSTRAINT fk_mov_bod_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id) ON DELETE SET NULL
);

CREATE INDEX idx_movimiento_fecha ON movimiento(fecha);

-- ===============================
-- DETALLE DE MOVIMIENTOS
-- ===============================
CREATE TABLE movimiento_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(12,2) DEFAULT 0.00,
    CONSTRAINT fk_item_mov FOREIGN KEY (movimiento_id) REFERENCES movimiento(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT
);

-- ===============================
-- AUDITORÍA
-- ===============================
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entidad VARCHAR(100) NOT NULL,
    entidad_id BIGINT NULL,
    operacion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    usuario VARCHAR(100),
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    valores_antes JSON NULL,
    valores_despues JSON NULL
);

CREATE INDEX idx_auditoria_usuario ON auditoria(usuario);
CREATE INDEX idx_auditoria_operacion ON auditoria(operacion);

