-- Crear el ENUM para el estado de cuenta
CREATE TYPE account_status_enum AS ENUM ('CREATED', 'DELETED');

-- Crear tabla en inglés con la nueva columna
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_status account_status_enum NOT NULL DEFAULT 'CREATED'
);

-- Insertar datos de ejemplo
INSERT INTO users (name, email, password, account_status)
VALUES
    ('Anderson Peña', 'anderson@example.com', 'hashed_password_123', 'CREATED'),
    ('Maria Lopez', 'maria@example.com', 'hashed_password_456', 'CREATED');
