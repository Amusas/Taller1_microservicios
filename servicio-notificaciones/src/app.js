import express from "express";
import dotenv from "dotenv";
import sgMail from "@sendgrid/mail";
import twilio from "twilio";


// --- Configuración de variables de entorno ---
dotenv.config();
const app = express();
app.use(express.json());   // Permite recibir datos en formato JSON en el body


// --- Configuración de SendGrid ---
sgMail.setApiKey(process.env.SENDGRID_API_KEY);
// Se obtiene la API Key desde el archivo .env para autenticar las peticiones a SendGrid


// --- Configuración de Twilio ---
const client = twilio(
    process.env.TWILIO_ACCOUNT_SID,
    process.env.TWILIO_AUTH_TOKEN
);
// Se inicializa el cliente de Twilio con el Account SID y Auth Token almacenados en .env


/**
 * Endpoint para enviar correos electrónicos
 * Método: POST
 * Ruta: /send-email
 * Body esperado:
 * {
 *   "to": "correo@destino.com",
 *   "subject": "Asunto del correo",
 *   "text": "Contenido del correo en texto plano"
 * }
 */
app.post("/send-email", async (req, res) => {
    const { to, subject, text } = req.body;

    try {
        // Enviar el correo usando SendGrid
        await sgMail.send({
            to, // destinatario
            from: process.env.SENDGRID_FROM, // remitente verificado en SendGrid
            subject, // asunto
            text, // contenido en texto plano
        });

        res.status(200).json({ message: "Correo enviado ✅" });
    } catch (error) {
        console.error(error);
        res.status(500).json({
            message: "Error enviando correo",
            error: error.message,
        });
    }
});

/**
 * Endpoint para enviar SMS
 * Método: POST
 * Ruta: /send-sms
 * Body esperado:
 * {
 *   "to": "+573001112233",   // Número de teléfono destino (con código de país)
 *   "body": "Contenido del SMS"
 * }
 */
app.post("/send-sms", async (req, res) => {
    const { to, body } = req.body;

    try {
        // Enviar SMS usando Twilio
        const message = await client.messages.create({
            body, // contenido del mensaje
            from: process.env.TWILIO_PHONE, // número telefónico proporcionado por Twilio
            to, // destinatario
        });

        res.status(200).json({
            message: "SMS enviado ✅",
            sid: message.sid, // ID único del mensaje en Twilio (para seguimiento)
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({
            message: "Error enviando SMS",
            error: error.message,
        });
    }
});

// --- Configuración del servidor ---
const PORT = process.env.PORT || 8083;
app.listen(PORT, () => {
    console.log(`✅ Servicio de notificaciones corriendo en puerto ${PORT}`);
});
