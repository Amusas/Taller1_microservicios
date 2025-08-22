const express = require("express");
const { Pool } = require("pg");

const app = express();
app.use(express.json());

// Config conexión DB
const pool = new Pool({
    host: process.env.DB_HOST || "database",
    port: process.env.DB_PORT || 5432,
    user: process.env.DB_USER || "admin_user",
    password: process.env.DB_PASSWORD || "supersecurepassword",
    database: process.env.DB_NAME || "usuariosdb"
});

// Endpoint healthcheck
app.get("/actuator/health", (req, res) => {
    res.json({ status: "UP" });
});

// Listar usuarios
app.get("/api/usuarios", async (req, res) => {
    try {
        const result = await pool.query("SELECT * FROM usuarios");
        res.json(result.rows);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Crear usuario
app.post("/api/usuarios", async (req, res) => {
    const { nombre, documento, email } = req.body;
    try {
        const result = await pool.query(
            "INSERT INTO usuarios (nombre, documento, email) VALUES ($1, $2, $3) RETURNING *",
            [nombre, documento, email]
        );
        res.json(result.rows[0]);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.listen(8082, () => {
    console.log("✅ Data-service escuchando en puerto 8082");
});
