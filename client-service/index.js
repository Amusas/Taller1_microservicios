import axios from "axios";
import dotenv from "dotenv";
import readline from "readline";

// Cargar variables de entorno
dotenv.config();

// Variables de entorno
const AUTH_URL = process.env.AUTH_URL;
const GREETING_URL = process.env.GREETING_URL;

// Interfaz para leer desde consola
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

/**
 * Genera credenciales aleatorias para usuario y contraseña.
 */
function generateRandomCredentials() {
  const username = `user_${Math.random().toString(36).substring(2, 8)}`;
  const password = `pass_${Math.random().toString(36).substring(2, 8)}`;
  return { username, password };
}

/**
 * Solicita un token JWT al servicio de autenticación.
 */
async function getToken(username, password) {
  const response = await axios.post(AUTH_URL, { name: username, password });
  const token = response.data;

  if (!token) {
    throw new Error("No se recibió un token en la respuesta de login");
  }

  return token;
}

/**
 * Llama al servicio de saludo con el token JWT.
 */
async function getGreeting(nombre, token) {
  const response = await axios.get(`${GREETING_URL}?nombre=${nombre}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  return response.data;
}

/**
 * Ejecuta el flujo completo para saludar
 */
async function runGreetingFlow() {
  try {
    // 1️⃣ Generar credenciales
    const { username, password } = generateRandomCredentials();
    console.log(`\n🔑 Usando credenciales: ${username} / ${password}`);

    // 2️⃣ Obtener token
    const token = await getToken(username, password);
    console.log("✅ Token obtenido:");
    console.log(token + "\n");

    // 3️⃣ Llamar al servicio de saludo
    console.log(`📨 Saludando al usuario ${username}...`);
    const saludo = await getGreeting(username, token);

    // 4️⃣ Mostrar resultado
    console.log("📢 Respuesta del servidor:", saludo);

  } catch (error) {
    console.error("❌ Error:", error.message);
  }
}

/**
 * Función principal con ciclo interactivo
 */
async function main() {
  let continuar = true;

  while (continuar) {
    await runGreetingFlow();

    // Preguntar al usuario si quiere seguir
    const answer = await new Promise((resolve) => {
      rl.question("\n¿Quieres generar nuevas credenciales y saludar otra vez? (s/n): ", resolve);
    });

    if (answer.trim().toLowerCase() !== "s") {
      continuar = false;
    }
  }

  console.log("\n👋 Programa finalizado");
  rl.close();
}

main();
