class UserResponse {
    constructor(user) {
        this.id = user.id;
        this.email = user.email;
        this.name = user.name;
        this.phone = user.phone;
    }

    // Convertir a objeto plano para respuesta JSON
    toJSON() {
        return {
            id: this.id,
            email: this.email,
            name: this.name,
            phone: this.phone,
        };
    }

    // Método estático para crear desde un User
    static fromUser(user) {
        return new UserResponse(user);
    }
}

module.exports = UserResponse;
