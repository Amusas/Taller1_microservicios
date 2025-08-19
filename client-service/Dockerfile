# Etapa base
FROM node:20-alpine

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de dependencias primero (para aprovechar la cache)
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar código fuente
COPY index.js ./

# No copiamos el .env porque usaremos variables del docker-compose
# Exponer un puerto si fueras a levantar un server, pero aquí es cliente de consola

# Comando por defecto
CMD ["node", "index.js"]
