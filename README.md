# EcoMercado

## Configuración de la Base de Datos
--nota ejecutar parte por parte en dbeaver
CREATE DATABASE IF NOT EXISTS EcoMercadobd;

USE EcoMercadobd;

-- Insertar roles
INSERT INTO rol (nombre, descripcion) VALUES ('admin','Administrador');
INSERT INTO rol (nombre, descripcion) VALUES ('consumidor', 'rol del consumidor');

-- Verifica los roles insertados
SELECT * FROM rol;

-- Insertar usuario con clave foránea a rol
INSERT INTO usuario (nombre, correo, password, estado, id_rol)
VALUES ('Brayan', 'sysadmin@example.com', '$2a$12$r74HSGhuNB5zqfLG3fiao.OlRzKsPv/6R5EuhLNeFqjkKM7BZJ20m', 1, 1);

-- Verificar usuarios
SELECT * FROM usuario;