const UserRegister = require('../models/UserRegister');
const UserUpdate = require('../models/UserUpdate');
const UserResponse = require('../models/UserResponse');
const UserRepository = require('../repositories/userRepository');
const ResponseModel = require('../models/ResponseModel');

class UserControllerDB {

    constructor() {
        this.userRepository = new UserRepository();
    }

    /**
     * Maneja errores específicos del controlador
     * @param {Error} error - Error capturado
     * @returns {ResponseModel} Respuesta formateada según el tipo de error
     */
    _handleControllerError(error) {
        console.error(`❌ [UserControllerDB] Error: ${error.message}`);
        
        // Manejar error de email duplicado
        if (error.statusCode === 409 || error.code === 'EMAIL_DUPLICATE') {
            return ResponseModel.emailDuplicate('El email ya existe');
        }
        
        // Manejar errores de base de datos
        if (error.statusCode === 500) {
            return ResponseModel.databaseError('Error interno del servidor');
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
     * POST /api/users/register
     * Registra un nuevo usuario en la base de datos
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async registerUser(req, res) {
        console.log('🚀 [UserControllerDB] Registrando usuario..');
        
        try {
            // Validar datos de entrada
            const userRegister = new UserRegister(req.body);
            
            console.log(`📝 [UserControllerDB] Validando unicidad de email: ${userRegister.email}`);

            // Intentar crear el usuario en la base de datos
            const createdUser = await this.userRepository.create(userRegister);

            // Crear respuesta exitosa
            const userResponse = UserResponse.fromUser(createdUser);
            
            console.log(`✅ [UserControllerDB] Usuario registrado exitosamente con ID: ${createdUser.id}`);
            
            // Usar el modelo de respuesta estandarizado
            const response = this._createSuccessResponse(
                'Usuario registrado exitosamente', 
                userResponse.toJSON(), 
                201
            );
            
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[UserControllerDB]');
            return response.send(res);
        }
    }

    
    /**
     * PUT /api/users/{id}
     * Actualiza un usuario existente en la base de datos
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async updateUser(req, res) {
        const userId = parseInt(req.params.id);
        console.log(`🚀 [UserControllerDB] Actualizando usuario con ID: ${userId}`);
        
        try {
            // Validar datos de entrada
            const userUpdate = new UserUpdate(req.body);
            
            console.log(`📝 [UserControllerDB] Actualizando usuario: ${userId} con email: ${userUpdate.email}`);

            // Intentar actualizar el usuario en la base de datos
            const updatedUser = await this.userRepository.update(userId, userUpdate);

            if (!updatedUser) {
                const response = ResponseModel.notFound('Usuario no encontrado');
                response.log('[UserControllerDB]');
                return response.send(res);
            }

            // Crear respuesta exitosa
            const userResponse = UserResponse.fromUser(updatedUser);
            
            console.log(`✅ [UserControllerDB] Usuario actualizado exitosamente con ID: ${updatedUser.id}`);
            
            // Usar el modelo de respuesta estandarizado
            const response = this._createSuccessResponse(
                'Usuario actualizado exitosamente', 
                userResponse.toJSON()
            );
            
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[UserControllerDB]');
            return response.send(res);
        }
    }

    
    /**
     * GET /api/users?page=x&size=y
     * Obtiene todos los usuarios paginados
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async getAllUsersPaginated(req, res) {
        console.log('🚀 [UserControllerDB] Obteniendo usuarios paginados..');
        
        try {
            // Obtener parámetros de query con valores por defecto
            const page = parseInt(req.query.page) || 1;
            const size = parseInt(req.query.size) || 10;
            
            console.log(`📝 [UserControllerDB] Parámetros de paginación - Página: ${page}, Tamaño: ${size}`);

            // Validar parámetros de entrada
            if (page < 1) {
                const response = ResponseModel.badRequest('El número de página debe ser mayor a 0');
                response.log('[UserControllerDB]');
                return response.send(res);
            }
            
            if (size < 1 || size > 100) {
                const response = ResponseModel.badRequest('El tamaño de página debe estar entre 1 y 100');
                response.log('[UserControllerDB]');
                return response.send(res);
            }

            // Obtener usuarios paginados del repositorio
            const paginatedUsers = await this.userRepository.findAllPaginated(page, size);
            
            console.log(`✅ [UserControllerDB] Usuarios obtenidos exitosamente - Total: ${paginatedUsers.totalItems}, Páginas: ${paginatedUsers.totalPages}`);
            
            // Usar el modelo de respuesta estandarizado
            const response = this._createSuccessResponse(
                'Usuarios obtenidos exitosamente', 
                paginatedUsers.toJSON()
            );
            
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[UserControllerDB]');
            return response.send(res);
        }
    }


    /**
     * GET /api/users/{id}
     * Obtiene un usuario específico por ID
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async getUserById(req, res) {
        const userId = parseInt(req.params.id);
        console.log(`🚀 [UserControllerDB] Obteniendo usuario con ID: ${userId}`);
        
        try {
            
            // Obtener usuario del repositorio
            const user = await this.userRepository.findById(userId);
            
            if (!user) {
                const response = ResponseModel.notFound('Usuario no encontrado');
                response.log('[UserControllerDB]');
                return response.send(res);
            }

            console.log(`✅ [UserControllerDB] Usuario obtenido exitosamente con ID: ${user.id}`);
            
            // Crear respuesta exitosa con UserResponse
            const userResponse = UserResponse.fromUser(user);
            
            // Usar el modelo de respuesta estandarizado
            const response = this._createSuccessResponse(
                'Usuario obtenido exitosamente', 
                userResponse.toJSON()
            );
            
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[UserControllerDB]');
            return response.send(res);
        }
    }


    /**
     * DELETE /api/users/{id}
     * Elimina lógicamente un usuario (soft delete)
     * @param {Object} req - Request object de Express
     * @param {Object} res - Response object de Express
     */
    async deleteUser(req, res) {
        const userId = parseInt(req.params.id);
        console.log(`🚀 [UserControllerDB] Eliminando usuario con ID: ${userId}`);
        
        try {
           
            // Realizar eliminación lógica
            const deletedUser = await this.userRepository.delete(userId);
            
            if (!deletedUser) {
                const response = ResponseModel.notFound('Usuario no encontrado o ya eliminado');
                response.log('[UserControllerDB]');
                return response.send(res);
            }

            console.log(`✅ [UserControllerDB] Usuario eliminado exitosamente con ID: ${userId}`);
            
            // Usar el modelo de respuesta estandarizado (solo mensaje, sin datos)
            const response = this._createSuccessResponse(
                'El usuario se eliminó satisfactoriamente'
            );
            
            return response.send(res);

        } catch (error) {
            const response = this._handleControllerError(error);
            response.log('[UserControllerDB]');
            return response.send(res);
        }
    }
    
}

module.exports = UserControllerDB;
