package com.uniquindio.saludonombre.exception;

public class NameIsEmptyException extends RuntimeException {

    public NameIsEmptyException() {
        super("Se requiere un nombre para saludar");
    }

    public NameIsEmptyException(String message) {
        super(message);
    }
}

