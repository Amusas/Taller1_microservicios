class UserUpdate {
    constructor(data) {
        this.email = data.email;
        this.name = data.name;
    }


    // Convertir a objeto plano
    toJSON() {
        return {
            email: this.email,
            name: this.name
        };
    }
}

module.exports = UserUpdate;
