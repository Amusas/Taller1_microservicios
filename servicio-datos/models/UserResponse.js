class UserResponse {
    constructor(user) {
        this.id = user.id;
        this.email = user.email;
        this.name = user.name;
    }

    // Convertir a objeto plano para respuesta JSON
    toJSON() {
        return {
            id: this.id,
            email: this.email,
            name: this.name
        };
    }

    // Método estático para crear desde un User
    static fromUser(user) {
        return new UserResponse(user);
    }
}

module.exports = UserResponse;
