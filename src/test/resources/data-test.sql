-- Adjust table/column names to match your schema before running.
-- Example seed data for personas and usuarios.
CREATE TABLE IF NOT EXISTS personas(
  id_persona INT PRIMARY KEY,
  edad INT,
  sexo VARCHAR(1),
  confirmado BOOLEAN,
  fecha_registro DATE
);

CREATE TABLE IF NOT EXISTS usuarios(
  id_usuario INT PRIMARY KEY,
  nombre VARCHAR(100),
  username VARCHAR(100),
  password VARCHAR(100)
);

INSERT INTO personas (id_persona, edad, sexo, confirmado, fecha_registro) VALUES
(1, 25, 'M', TRUE, '2024-02-10'),
(2, 43, 'F', FALSE, '2024-04-15'),
(3, 32, 'F', TRUE, '2024-08-20');

INSERT INTO usuarios (id_usuario, nombre, username, password) VALUES
(1, 'Administrador', 'admin', '12345');
