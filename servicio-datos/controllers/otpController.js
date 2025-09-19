const OtpRepository = require('../repositories/otpRepository');
const ResponseModel = require('../models/ResponseModel');
const UserRepository = require("../repositories/userRepository");

class OtpController {

    constructor() {
        this.otpRepository = new OtpRepository();
    }

    /**
     * Maneja errores específicos del controlador para OTP
     * @param {Error} error - Error capturado
     * @returns {ResponseModel} Respuesta formateada según el tipo de error
     */
    _handleControllerError(error) {
        console.error(`❌ [OtpController] Error: ${error.message}`);

        // Manejar error de conflicto (OTP activo existente)
        if (error.statusCode === 409) {
            return ResponseModel.conflict(error.message);
        }

        // Manejar errores de base de datos
        if (error.statusCode === 500) {
            return ResponseModel.databaseError('Error interno del servidor al procesar el OTP');
        }

        // Error genérico
        return ResponseModel.internalError('Ocurrió un error inesperado');
    }

    /**
     * Crea respuesta exitosa estandarizada
     * @param {string} message - Mensaje de éxito
     * @param {Object} data - Datos de la respuesta
     * @param {number} statusCode - Código de estado HTTP
     * @returns {ResponseModel} Respuesta formateada
     */
    _createSuccessResponse(message, data, statusCode = 200) {
        return ResponseModel.success(message, data, statusCode);
    }

    /**
     * POST /api/otp
     * Crea un nuevo OTP para un usuario
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async createOtp(req, res) {
        console.log('🚀 [OtpController] Creando OTP...');

        try {
            const { email } = req.body;

            // Validar que el ID de usuario exista
            if (!email) {
                const response = ResponseModel.badRequest('El email del usuario es obligatorio');
                response.log('[OtpController] El email del usuario esta ausente');
                return response.send(res);
            }

            // Generar un OTP aleatorio de 6 dígitos
            const otp = Math.floor(100000 + Math.random() * 900000).toString();

            // Intentar crear el OTP en la base de datos
            const createdOtp = await this.otpRepository.create({ otp, email });

            const baseUrl = `${req.protocol}://${req.get('host')}`;
            createdOtp.url = `${baseUrl}/api/v1/users/${createdOtp.userId}/password`;

            // Crear respuesta exitosa
            const response = this._createSuccessResponse(
                'OTP creado exitosamente',
                createdOtp.toJSON(),
                201
            );

            console.log(`✅ [OtpController] OTP creado para usuario: ${email}`);
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[OtpController] error');
            return response.send(res);
        }
    }
}

module.exports = OtpController;