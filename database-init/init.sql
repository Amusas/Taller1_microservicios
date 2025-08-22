CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          documento VARCHAR(20) UNIQUE NOT NULL,
                          email VARCHAR(150) UNIQUE NOT NULL,
                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de ejemplo
INSERT INTO usuarios (nombre, documento, email)
VALUES
    ('Anderson Peña', '100200300', 'anderson@example.com'),
    ('Maria Lopez', '200300400', 'maria@example.com');