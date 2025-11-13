package com.example.stockify.excepciones;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
