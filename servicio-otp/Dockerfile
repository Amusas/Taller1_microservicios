# Etapa de build
FROM golang:1.21-alpine AS builder

WORKDIR /app

COPY ./src ./src

WORKDIR /app/src

RUN go mod tidy
RUN go build -o otp-service main.go

# Etapa final
FROM alpine:3.18

WORKDIR /app

COPY --from=builder /app/src/otp-service .

EXPOSE 8083

CMD ["./otp-service"]

